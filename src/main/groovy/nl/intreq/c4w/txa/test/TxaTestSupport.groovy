package nl.intreq.c4w.txa.test

import nl.intreq.c4w.txa.transform.SectionMark

import nl.intreq.c4w.txa.transform.StreamingTxaReader
import nl.intreq.c4w.txa.transform.TxaContentHandler
import nl.intreq.c4w.txa.transform.TxaRawContentHandler
import nl.intreq.c4w.txa.transform.TxaContext
import nl.intreq.c4w.txa.transform.TxaLogicalContentHandler
import nl.intreq.c4w.txa.transform.TxaSectionHandler

import java.nio.file.Paths

trait TxaTestSupport {

    // In tests all content uses unix line endings
    final static EOL = '\n'

    //Cross-platform content comparison of stringbuffers
    def assertContentEquals(StringBuffer sb1, StringBuffer sb2) {
        def sbx1 = sb1.replaceAll(System.lineSeparator(), EOL)
        def sbx2 = sb2.replaceAll(System.lineSeparator(), EOL)
        assert sbx1.contentEquals(sbx2)
    }

    def txaContent(String s){
        s.replaceAll(System.lineSeparator(), EOL).trimLines(EOL)
    }

    def assertStructuresAtLine(String content, int lineno, List<String>... structures){
        def n = lineno
        for (s in structures){
            assertStructureAtLine(content, n, s)
            n += s.size()
        }
    }

    def assertStructureAtLine(String content, int lineno, structure){
        def lines = content.toLineArray()
        assert lines.size() > 0
        assert lines.size() >= lineno + structure.size()
        def bodySection = lines[lineno..lineno+structure.size()-1]
        assert bodySection*.trim() == structure*.trim()
    }

    def assertSectionsClosedCorrectly(String s){
        def level = 0
        for (line in s.toLineArray(EOL)*.trim()){
            if(line.isSectionMark()){
                SectionMark section = line.asSectionMark()
                if ( section.requiresExplicitEnd()){
                    level++
                } else if ( section == SectionMark.END){
                    level--
                }
            }
        }
        assert level == 0
    }

    def assertAllSectionsWithoutIndents(String content){
        def unalignedSections = content.toLineArray().findAll { it ==~ /^\s+\[[A-Z]+\]\s*$/}
        assert unalignedSections.size() == 0
    }

    File openTxaFile(String resourcePath){
        def resourceURL = getClass().getClassLoader().getResource(resourcePath)
        return new File(Paths.get(resourceURL.toURI()).toString())
    }

    Tuple setupStreamingTxaReader(parentSections=[], currentSection=null) {
        TxaContext ctx = new TxaContext()
        ctx.parentSections = parentSections
        ctx.currentSection = currentSection
        def reader = new StreamingTxaReader()
        def h = new TxaContentHandlerStub()
        reader.registerHandler(h)
        [reader, h, ctx]
    }

    static class TxaContentHandlerStub implements TxaContentHandler, TxaSectionHandler, TxaRawContentHandler, TxaLogicalContentHandler {
        def sectionsStarted = []
        def sectionsClosed = []
        def rawContents = [:]
        def logicalContents = [:]

        @Override
        void onProcessingStart(TxaContext context) {}

        @Override
        void onSectionStart(TxaContext context, SectionMark section) {
            sectionsStarted << section
        }

        @Override
        void onSectionContent(TxaContext context, SectionMark section, String content) {
            if ( !rawContents.containsKey(section)){
                rawContents.put(section,[])
            }
            rawContents[section] << content
        }

        @Override
        void onSectionContent(TxaContext context, SectionMark section, Long logicalLineNo, String content) {
            if ( !logicalContents.containsKey(section)){
                logicalContents.put(section,[])
            }
            logicalContents[section] << content
        }

        @Override
        void onSectionEnd(TxaContext context, SectionMark section) {
            sectionsClosed << section
        }

        void onProcessingFinished(TxaContext context) {}
    }
}