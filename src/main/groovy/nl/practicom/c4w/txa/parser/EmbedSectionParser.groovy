package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.Common
import nl.practicom.c4w.txa.model.EmbedInstance

class EmbedSectionParser {

    static embedPattern = /^\s*EMBED\s*(%?\w+)$/
    static priorityPattern = /^\s*PRIORITY\s+([0-9]+)\s*$/
    static instancePattern = /^\s*INSTANCE\s+([0-9]+)\s*$/
    static whenPattern = /^\s*WHEN\s*'(\??[:\w\(\)\,]+)'\s*$/

    Common parent

    EmbedSectionParser(Common parent) {
        this.parent = parent
    }

    def parse(TxaReader r) {

        if ( !r.at(SectionMark.EMBED) ){
            r.readUptoSection(SectionMark.EMBED)
        }

        if ( !r.atEOF() && r.at(SectionMark.EMBED)) {
            r.readLine()

            if ( this.parent.embeds == null ){
                this.parent.embeds = []
            }

            while (!r.atEOF() && r.currentLine() ==~ embedPattern) {

                def embedMatcher = (r.currentLine() =~ embedPattern)
                def embedPoint = embedMatcher[0][1] as String

                r.readUptoNextSection()

                def instanceLocation = []

                switch ( r.currentLine().asSectionMark() ){
                    case SectionMark.INSTANCES : // Note the plural form!
                        this.parseInstances(embedPoint, instanceLocation, r)
                        break

                    case SectionMark.DEFINITION :
                        this.parseDefinitions(embedPoint, instanceLocation, r)
                        break
                }
            }
        } else {
            r.readLine()
        }

        // Discard [END] of [EMBED]
        if ( r.atSectionEndMark()){
            r.readLine()
        }
    }

    private void parseInstances(String embedPoint, List parentLocation, TxaReader r) {

        // discard [INSTANCES] marker
        if ( r.at(SectionMark.INSTANCES)){
            r.readLine()
        }

        while ( !r.atSectionEndMark() && r.currentLine() ==~ whenPattern){
            def instanceLocation = [] << parentLocation << parseInstanceLocation(r.currentLine())
            instanceLocation = instanceLocation.flatten()

            r.readUptoNextSection()
            switch ( r.currentLine().asSectionMark()){
                case SectionMark.INSTANCES:
                    parseInstances(embedPoint, instanceLocation, r)
                    break;
                case SectionMark.DEFINITION:
                    parseDefinitions(embedPoint,instanceLocation, r)
            }
        }

        if ( r.atSectionEndMark()){
            // discard [END] of [INSTANCES]
            r.readLine()
        }
    }

    private void parseDefinitions(String embedPoint, List instanceLocation, TxaReader r) {
        r.readUptoNextSection()

        EmbedInstance embedInstance = null

        while( !r.atEOF() && !r.atSectionEndMark()) {
            switch (r.currentLine().asSectionMark()) {
                case SectionMark.SOURCE:
                    embedInstance = parseSourceDefinition(r)
                    break
                case SectionMark.TEMPLATE:
                    embedInstance = parseTemplateDefinition(r)
                    break
                case SectionMark.PROCEDURE:
                    embedInstance = parseProcedureDefinition(r)
                    break
                case SectionMark.GROUP:
                    embedInstance = parseGroupDefinition(r)
                    break
                default:
                    println("${this.getClass().getSimpleName()}:parseDefinitions:")
                    println("ERROR at line ${r.currentLineNumber()}, While parsing definition for embedpoint ${embedPoint}/${instanceLocation.join('/')}:")
                    println("--> Expected [SOURCE],[GROUP],[PROCEDURE] or [TEMPLATE] but found: ${r.currentLine()} ")
                    //r.readUptoNextSection()
                    r.readLine()
                    break
            }

            if (embedInstance != null) {
                embedInstance.embedPoint = embedPoint
                if (instanceLocation.size() > 0) {
                    embedInstance.embedLocation = instanceLocation
                }
                this.parent.embeds.add(embedInstance)
            }
        }

        // discard [END] of [DEFINITION] line
        if ( r.atSectionEndMark()){
            r.readLine()
        }
    }

    EmbedInstance parseSourceDefinition(TxaReader r) {
        def embed =  new EmbedInstance(sourceType: EmbedInstance.SourceType.SOURCE)

        // For sourcecode read lines as is
        // ToDo: remove eager line read
        def source = r.readLine(false, true) << '\n'
        r.readLine(false, true)

        // Source block implicitly ends at [END] of [DEFINITION] (in case this
        // is the last definition) or by the start of any other definition
        // ([GROUP], [PROCEDURE],[SOURCE] or [TEMPLATE]) or if we reach the end of
        // the file (which should not happen)
        while ( !( r.atEOF() || r.atSectionEndMark() || r.atAnySectionStart()) ){
            source << r.currentLine() << '\n'
            // Prevent unnecessary pattern matching
            if ( embed.priority == null ){
                embed.priority = this.parsePriority(r.currentLine())
            }
            r.readLine(false, true)
        }
        embed.source = source

        return embed
    }

    EmbedInstance parseTemplateDefinition(TxaReader r) {
        return new EmbedInstance(sourceType: EmbedInstance.SourceType.TEMPLATE)
        r.readUptoNextSection()
    }

    EmbedInstance parseProcedureDefinition(TxaReader r) {
        def embed =  new EmbedInstance(sourceType: EmbedInstance.SourceType.PROCEDURE)
        def embeddedCall = r.readLine().trim()
        embed.procedureName = embeddedCall.substring(0,embeddedCall.indexOf('('))
        embed.procedureParams = embeddedCall.substring(embed.procedureName.length())
        embed.priority = this.parsePriority(r.readLine())
        r.readUptoSectionEnd()
        return embed
    }

    EmbedInstance parseGroupDefinition(TxaReader r) {
        def embed =  new EmbedInstance(sourceType: EmbedInstance.SourceType.GROUP)

        // Assumption: priority and instance generated in fixed sequence!
        embed.priority = parsePriority(r.readLine())
        embed.instanceId = parseInstanceId(r.readLine())
        r.readUptoSectionEnd()
        return embed
    }


    Object parseInstanceLocation(String line) {
        def location
        if ( line ==~ whenPattern){
            def whenMatcher = (line =~ whenPattern)
            if ( whenMatcher.matches() ){
                location = whenMatcher[0][1]
            }
        }
        location
    }

    private parsePriority(String line) {
        def priority = null
        if (line ==~ priorityPattern) {
            def priorityMatcher = (line =~ priorityPattern)
            if (priorityMatcher.matches()) {
                priority = priorityMatcher[0][1] as int
            }
        }
        priority
    }

    private parseInstanceId(String line) {
        def instanceId = null
        if (line ==~ instancePattern) {
            def instanceMatcher = (line =~ instancePattern)
            if (instanceMatcher.matches()) {
                instanceId = instanceMatcher[0][1] as int
            }
        }
        instanceId
    }

}
