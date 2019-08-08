package nl.practicom.c4w.txa

import nl.practicom.c4w.txa.parser.SectionMark
import nl.practicom.c4w.txa.transform.ProcedureExtractor
import nl.practicom.c4w.txa.transform.StreamingTxaReader
import nl.practicom.c4w.txa.transform.TxaContentHandler
import nl.practicom.c4w.txa.transform.TxaContext

import java.nio.file.Paths

trait TxaTestSupport {

    def assertStructuresAtLine(ProcedureExtractor.Procedure p, int lineno, List<String>... structures){
        def n = lineno
        for (s in structures){
            assertStructureAtLine(p, n, s)
            n += s.size()
        }
    }

    def assertStructureAtLine(ProcedureExtractor.Procedure p, int lineno, structure){
        def lines = p.body.toString().toLineArray()
        assert lines.size() > 0
        assert lines.size() >= lineno + structure.size()
        def bodySection = lines[lineno..lineno+structure.size()-1]
        assert bodySection*.trim() == structure*.trim()
    }

    def assertSectionsClosedCorrectly(ProcedureExtractor.Procedure p) {
        assertSectionsClosedCorrectly(p.body.toString())
    }

    def assertSectionsClosedCorrectly(String s){
        def level = 0
        for (line in s.toLineArray()*.trim()){
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

    def assertAllSectionsWithoutIndents(ProcedureExtractor.Procedure p){
        def unalignedSections = p.body.toString().toLineArray().findAll { it ==~ /^\s+\[[A-Z]+\]\s*$/}
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

    static class TxaContentHandlerStub implements TxaContentHandler {
        def sectionsStarted = []
        def sectionsClosed = []
        def sectionContents = [:]

        @Override
        void onProcessingStart(TxaContext context) {

        }

        @Override
        void onSectionStart(TxaContext context, SectionMark section) {
            sectionsStarted << section
        }

        @Override
        void onSectionContent(TxaContext context, SectionMark section, String content) {
            if ( !sectionContents.containsKey(section)){
                sectionContents.put(section,[])
            }
            sectionContents[section] << content
        }

        @Override
        void onSectionEnd(TxaContext context, SectionMark section) {
            sectionsClosed << section
        }

        @Override
        void onProcessingFinished(TxaContext context) {

        }
    }
}