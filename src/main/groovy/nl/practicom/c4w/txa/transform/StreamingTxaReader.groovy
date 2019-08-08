package nl.practicom.c4w.txa.transform

import groovy.transform.PackageScope
import nl.practicom.c4w.txa.meta.ClarionDateMixins

import java.nio.file.Paths
import nl.practicom.c4w.txa.parser.SectionMark
import nl.practicom.c4w.txa.meta.ClarionStringMixins

import java.util.regex.Matcher

import static nl.practicom.c4w.txa.parser.SectionMark.*

/**
 * Extracts specific content from a txa file
 */
class StreamingTxaReader {
    private Reader source

    private List<TxaContentHandler> handlers = []

    static {
        ClarionStringMixins.initialize()
        ClarionDateMixins.initialize()
    }

    def parse(String txaFile){
        this.source = new File(Paths.get(txaFile).normalize().toString()).newReader()
        doParse()
    }

    def parse(File txaFile) {
        this.source = txaFile.newReader()
        doParse()
    }

    def parse(StringBuffer contents){
        this.source = new StringReader(contents.toString())
        doParse()
    }

    def registerHandler(TxaContentHandler handler){
        handlers.push(handler)
    }

    /**
     * Extracts the individual procedures from a txa file
     * @param txaFile - TXA File instance
     * @return List of Procedure instances
     */
    private void doParse() {

        if ( handlers.isEmpty()) return

        def ctx = new TxaContext()

        // Current line number being read
        def lnr = 0

        this.source.eachLine { line ->

            ctx.currentLineNumber = lnr++
            ctx.currentLine = line

            if ( line.isSectionMark()){
                if ( line.isSectionEnd() ){
                    rollupSectionTree(ctx, END)
                    if ( !ctx.parentSections.isEmpty()){
                        ctx.currentSection = ctx.parentSections.pop()
                    }
                } else {
                    SectionMark section = line.asSectionMark()
                    if ( ctx.currentSection != null) {
                        if ( ctx.currentSection.hasChild(section)) {
                            ctx.parentSections.push(ctx.currentSection)
                        } else {
                            // Roll up to the parent section for this section marker
                            rollupSectionTree(ctx, section)
                        }
                    }

                    processSectionStart(ctx, section)
                    ctx.currentSection = section
                }
            } else {
                processLine(ctx)
            }
        }

        // Roll up remaining section tree
        if ( ctx.currentSection ){
            processSectionEnd(ctx, ctx.currentSection)
        }
        while(!ctx.parentSections.isEmpty()){
            def s = ctx.parentSections.pop()
            processSectionEnd(ctx, s)
        }
    }

    /**
     * Handles the start of a new section, notifying handlers so they can
     * perform their processing logic. This method will be called just before
     * the new section will be added to the section tree.
     *
     * @param ctx - the current txa processing context
     * @param section - the new section to be added to the section tree
     */
    private void processSectionStart(TxaContext ctx, SectionMark section){
        updateEmbedPointInfo(ctx)
        if ( isProcedureDeclaration(ctx, section)){
            ctx.currentProcedureName = null
        }
        handlers.each { h -> h.onSectionStart(ctx, section)}
    }

    /**
     * Handles the closing of a section, notifying handlers so they can
     * perform their processing logic.
     * When this method is called just after the section has been removed
     * from the section tree.
     *
     * @param ctx - the current txa processing context
     * @param section - the section to be closed.
     */
    private void processSectionEnd(TxaContext ctx, SectionMark section){
        updateEmbedPointInfo(ctx)
        handlers.each { h -> h.onSectionEnd(ctx, section)}
    }

    /**
     * Handles the processing of a line of content within the current section.
     *
     * @param ctx
     */
    private void processLine(TxaContext ctx){
        final NAME_DECL = ~/^NAME\s+(.*)\s*$/
        final TEMPLATE_DECL = ~/^FROM\s+(\w[\w\s]+)\s*$/

        if (!ctx.currentProcedureName && ctx.currentSection == PROCEDURE && !ctx.parentSections.contains(DEFINITION)){
            (ctx.currentLine =~ NAME_DECL).each {
                _, procedureName -> ctx.currentProcedureName = procedureName
            }
        }

        if ( ctx.within(EMBED)){
            updateEmbedPointInfo(ctx)
        }

        handlers.each {h -> h.onSectionContent(ctx, ctx.currentSection, ctx.currentLine)}
    }

    /* Check if current section mark starts a procedure declaration */
    def isProcedureDeclaration(TxaContext context, SectionMark section) {
        section == PROCEDURE && !context.within(DEFINITION)
    }

    /**
     * Remove all sections that need to be closed for the current [END] or [SECTION]  marker.
     * In case the passed in section mark is an [END] marker, the section hierarchy is rolled
     * up to and including the first section that requires an explicit [END] mark. The parent
     * of that section becomes the current section.
     * In case the section mark is a regular section mark the section hierarchy is rolled
     * up to the parent section of that marker.
     * The section hierarchy consists of the current section + the ctx.parentSections. Since
     * the current section is the first to be closed the current section will be set to null
     * after the method completes.
     *
     * @param ctx - the txa context, current section should not be null. The current section
     *              and parentSections of the context are modified to reflect the roll-up
     * @param section - the section mark that was read from the txa, including [END]
     */
    @PackageScope // for testing
    void rollupSectionTree(TxaContext ctx, SectionMark section) {

        if ( section == END) {
            if (ctx.currentSection != null) {
                processSectionEnd(ctx, ctx.currentSection)
                if ( ctx.currentSection.requiresExplicitEnd()){
                    ctx.currentSection = null
                    return
                } else {
                    ctx.currentSection = null
                }
            }

            // Roll up sections until we hit a section requiring an explicit [END]
            while (!ctx.parentSections.isEmpty()) {
                def parentSection = ctx.parentSections.pop()
                processSectionEnd(ctx, parentSection)
                if (parentSection.requiresExplicitEnd()) {
                    break
                }
            }

        } else {
            if ( ctx.currentSection != null ) {
                processSectionEnd(ctx, ctx.currentSection)
                ctx.currentSection = null
            }
            // Roll up sections until we hit the parent of the passed in section
            while (!ctx.parentSections.isEmpty() && !ctx.parentSections.last().hasChild(section)) {
                processSectionEnd(ctx,ctx.parentSections.pop())
            }
        }
    }

    /**
     * Update the current embed point information based on the current line/section
     * @param ctx the current processing context
     * @param section the section mark on the current line, null if not not a section mark
     */
    @PackageScope
    void updateEmbedPointInfo(TxaContext ctx){
        // EMBED embedpoint
        final embedPointPattern = /^\s*EMBED\s+(%?\w+)\s*/

        // WHEN 'embedinstance'
        final embedInstancePattern = /^\s*WHEN\s+'([\(\)\,\w]+)'\s*/

        if (ctx.currentLine.isSectionMark()){
            switch (ctx.currentLine.asSectionMark()) {
                case INSTANCES:
                    ctx.instanceLevel++
                    break
                case END:
                    if (ctx.within(DEFINITION)) {
                        // [END] of [DEFINITION]
                        if (ctx.instanceLevel == 0) {
                            // EMBED without [INSTANCE]'s
                            ctx.currentEmbedPoint = null
                            ctx.currentEmbedInstance = []
                        }
                    } else {
                        // [END] of [INSTANCES]
                        if (ctx.instanceLevel > 1) {
                            if (ctx.currentEmbedInstance.size() > 0) {
                                ctx.currentEmbedInstance.pop()
                                ctx.instanceLevel--
                            }
                        } else {
                            // Dropping out of the embed point
                            ctx.currentEmbedInstance = []
                            ctx.currentEmbedPoint = null
                            ctx.instanceLevel = 0
                        }
                    }
            }
        } else {
            /* Keeping track of the relevant embed points */
            if (ctx.within(EMBED) ) {
                Matcher m = ctx.currentLine =~ embedPointPattern
                if (m.matches()) {
                    ctx.currentEmbedPoint = m[0][1] as String
                    ctx.currentEmbedInstance = []
                    ctx.instanceLevel = 0
                }

                m = ctx.currentLine =~ embedInstancePattern
                if (m.matches()) {
                    if ( ctx.currentEmbedInstance.isEmpty()){
                        ctx.currentEmbedInstance.add(m[0][1] as String)
                    } else if (ctx.instanceLevel <= ctx.currentEmbedInstance.size()) {
                        // Sibling WHEN within [INSTANCE] section
                        ctx.currentEmbedInstance = ctx.currentEmbedInstance.take(ctx.instanceLevel)
                        ctx.currentEmbedInstance[ctx.instanceLevel - 1] = m[0][1] as String
                    } else {
                        // Child WHEN for nested [INSTANCE]
                        ctx.currentEmbedInstance.add(m[0][1] as String)
                    }
                }
            }
        }
    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize()
        if ( this.source != null ){
            this.source.close()
        }
    }
}

