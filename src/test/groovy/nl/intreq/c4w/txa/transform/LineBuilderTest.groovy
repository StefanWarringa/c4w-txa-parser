package nl.intreq.c4w.txa.transform

import nl.intreq.c4w.txa.meta.ClarionStringMixins
import nl.intreq.c4w.txa.test.TxaTestSupport

class LineBuilderTest extends GroovyTestCase implements TxaTestSupport {

    LineBuilder lineBuilder
    def logicalLines
    def rawLines
    def currentPhysicalLineNo
    def currentLogicalLineNo

    static {
        ClarionStringMixins.initialize()
    }

    final def rawHandler = { rawLineNo, rawLine ->
        currentPhysicalLineNo = rawLineNo
        rawLines.push(rawLine)
    }

    final def logicalHandler = {logicalLineNo, logicalLine ->
        currentLogicalLineNo = logicalLineNo
        logicalLines.push(logicalLine)
    }

    void setUp() {
        super.setUp()
        initializeBuilder()
    }

    def initializeBuilder(){
        logicalLines = []
        rawLines = []
        currentPhysicalLineNo
        currentLogicalLineNo
        lineBuilder = new LineBuilder()
    }

    void testSingleLine() {
        lineBuilder.accept("Hello",10, rawHandler, logicalHandler)

        assert logicalLines == ['Hello']
        assert rawLines == ['Hello']
        assert currentPhysicalLineNo == 10
        assert currentLogicalLineNo == 10
    }

    /**
     * Skipping blank lines is not applied to raw lines only
     * to logical ones
     */
    void testSkipBlankLines() {
        lineBuilder.skipBlankLines = true
        lineBuilder
            .accept('',2, rawHandler, logicalHandler)
            .accept("Hello", rawHandler, logicalHandler)
            .accept('', rawHandler, logicalHandler)
            .accept("World", rawHandler, logicalHandler)
            .accept('', rawHandler, logicalHandler)

        assert logicalLines == ['Hello','World']
        assert rawLines == ['','Hello','','World','']
        assert currentPhysicalLineNo == 6
        assert currentLogicalLineNo == 3
    }

    void testContinuation() {
        lineBuilder.skipBlankLines = true
        lineBuilder
                .accept('Hello |',2,rawHandler,logicalHandler)
                .accept('|',rawHandler,logicalHandler)
                .accept("World!",rawHandler,logicalHandler)

        assert logicalLines == ['Hello World!']
        assert rawLines == ['Hello |','|','World!']
        assert currentPhysicalLineNo == 4
        assert currentLogicalLineNo == 2
    }

    void testWindowStructure(){
        def content = txaContent("""\
            [WINDOW]
            AppFrame APPLICATION('INkoop VERkoop VOorraad')
                      MENUBAR,USE(?MENUBAR1),#ORDINAL(1)
                        ITEM('Aanmelden'),USE(?Aanmelden),#ORDINAL(1)
                        MENU('Groothandel bestellingen'),USE(?VerkopenModembestellingen),MSG('Inlezen, p' & |
                            'rinten en verwerking van modembestellingen'),#ORDINAL(22)
                            ITEM('Opdrachtbevestigingen aanmaken'),USE(?OpdrachtbevestigingenAanmaken), |
                              #ORDINAL(2)
                        END
                      END\
        """)

        content.split(EOL).each { line ->
            lineBuilder.accept(line,rawHandler,logicalHandler)
        }

        assert logicalLines == [
                "[WINDOW]",
                "AppFrame APPLICATION('INkoop VERkoop VOorraad')",
                "MENUBAR,USE(?MENUBAR1),#ORDINAL(1)",
                "ITEM('Aanmelden'),USE(?Aanmelden),#ORDINAL(1)",
                "MENU('Groothandel bestellingen'),USE(?VerkopenModembestellingen),MSG('Inlezen, printen en verwerking van modembestellingen'),#ORDINAL(22)",
                "ITEM('Opdrachtbevestigingen aanmaken'),USE(?OpdrachtbevestigingenAanmaken), #ORDINAL(2)",
                "END",
                "END"
        ]

        assert rawLines == content.split(EOL)
    }
}
