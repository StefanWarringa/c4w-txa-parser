package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.meta.ClarionStringMixins;

class TxaReaderTest extends GroovyTestCase {

    void setUp() {
        super.setUp()
        ClarionStringMixins.initialize()
    }

    void testReadlineFromEmptyFileShouldReturnNull() {
        def reader = new TxaReader(''<<'')
        assert reader.atEOF() == false
        assert reader.currentLine() == null
        assert reader.currentLineNumber() == 0
        assert reader.readLine() == null
        assert reader.atEOF() == true
        assert reader.currentLine() == null
        assert reader.currentLineNumber() == 0
    }

    void testReadlineAtEndOfFileDoesHasNoEffect(){
        def contents = 'the only line'
        def reader = new TxaReader(''<< contents)
        assert reader.atEOF() == false
        assert reader.currentLine() == null
        assert reader.currentLineNumber() == 0
        // Read single line. EOF not set because we have not yet attempted
        // to read past the end
        assert reader.readLine() == contents
        assert reader.atEOF() == false
        assert reader.currentLine() == contents
        assert reader.currentLineNumber() == 1
        // Attempt to read past the end: EOF flag is set and nothing returned
        assert reader.readLine() == null
        assert reader.atEOF() == true
        assert reader.currentLine() == contents
        assert reader.currentLineNumber() == 1
        // Another attempt has no effect
        assert reader.readLine() == null
        assert reader.atEOF() == true
        assert reader.currentLine() == contents
        assert reader.currentLineNumber() == 1
    }

    void testSplitLinesWillBeReadAsSingleLine() {
        def reader = new TxaReader(
                "This is the |" << "\n" << "first line" << "\n"
            << "This is the |" << "\n" << "second line"
        )
        assert "This is the first line" == reader.readLine()
        assert "This is the second line" == reader.readLine()
    }

    void testEmptyLinesAreIgnoredByDefault() {
        def reader = new TxaReader('' << [
              "first line","  ","third line"
        ].join("\n"))
        assert "first line" == reader.readLine()
        assert "third line" == reader.readLine()
    }

    void testSingleContinuationIsNotAnEmptyLine() {
        def reader = new TxaReader('' << [
                "first line |","|","third line"
        ].join("\n"))
        assert "first line third line" == reader.readLine()
    }

    void testSingleContinuationBeforeLineIsIgnored(){
        def reader = new TxaReader('' << [
                "first line","|","third line"
        ].join("\n"))
        assert "first line" == reader.readLine()
        assert "third line" == reader.readLine()
    }

    void testReadUptoMatchingExcludesMatchOfCurrentLine() {
        def contents = ["first","MARK","second"].join("\n")
        def reader = new TxaReader("" << contents)
        def chunk1 = reader.readUptoMatching(/^MARK/)
        assert chunk1.size() == 1
        assert chunk1.first() == "first"
        assert "MARK" == reader.currentLine()
        def chunk2 = reader.readUptoMatching(/^MARK/)
        assert chunk2.size() == 1
        assert chunk2.first() == "second"
    }

    void testReadUptoMatchingStopsAtFirstMatch() {
        def contents = ["first","second","third","MARK1","fourth","fifth","MARK2"].join("\n")
        def reader = new TxaReader("" << contents)
        def chunk1 = reader.readUptoMatching(/^MARK.$/)
        assert chunk1.size() == 3
        assert chunk1.first() == "first"
        assert chunk1.last() == "third"
        def chunk2 = reader.readUptoMatching(/^MARK.$/)
        assert chunk2.size() == 2
        assert chunk2.first() == "fourth"
        assert chunk2.last() == "fifth"
    }

    void testReadUptoMatchingReadsAllRemainingLinesIfNoMatch() {
        def contents = ["first","second","MARK1","third","fourth","MARK2","fifth","sixth"].join("\n")
        def reader = new TxaReader("" << contents)
        def chunk1 = reader.readUptoMatching(/^MARK2$/)
        assert chunk1.size() == 5
        assert chunk1.first() == "first"
        assert chunk1.last() == "fourth"
        def chunk2 = reader.readUptoMatching(/^MARK3$/)
        assert chunk2.size() == 2
        assert chunk2.first() == "fifth"
        assert chunk2.last() == "sixth"
    }
    void testReadWhileMatching() {
        def contents = [
                "%ButtonName DEPEND %Buttons DEFAULT TIMES 1",
                "WHEN  ('Default')TIMES 4",
                "WHEN  (1) ('UploadButton')",
                "WHEN  (2) ('LookupButton')",
                "WHEN  (3) ('SaveButton')",
                "WHEN  (4) ('CancelButton')",
                "%DefaultBaseClassType DEPEND %ClassItem DEFAULT TIMES 2",
                "WHEN  ('4') ('vsRegistryClass')",
                "WHEN  ('Default') ('vsRegistryClass')",
                ""
            ].join("\n")
        def reader = new TxaReader("" << contents)
        assert reader.readLine() == "%ButtonName DEPEND %Buttons DEFAULT TIMES 1"
        def whenblock1 = reader.readWhileMatching(/^WHEN\W.*/)
        assert whenblock1.size() == 5
        assert reader.currentLine() == "%DefaultBaseClassType DEPEND %ClassItem DEFAULT TIMES 2"
        def whenblock2 = reader.readWhileMatching(/^WHEN\W.*/)
        assert whenblock2.size() == 2
    }

    void testReadUptoNextSectionReturnsAllLinesBeforeSectionMark(){
        def contents = [
                "first",
                "second",
                "[SEC1]",
                "third"
        ].join("\n")
        def reader = new TxaReader("" << contents)
        def lines = reader.readUptoNextSection()
        assert lines.size() == 2
        assert lines.last() == "second"
        assert reader.currentLine() == "[SEC1]"
    }

    void testReadUptoNextSectionStopsAfterExplicitEnd(){
        def contents = [
                "first",
                "second",
                "[END]",
                "third"
        ].join("\n")
        def reader = new TxaReader("" << contents)
        def lines = reader.readUptoNextSection()
        assert lines.size() == 2
        assert lines.last() == "second"
        // Note that the end marker is skipped!
        assert reader.currentLine() == "third"
    }

    void testReadUptoSectionSlurpsIntermediateSections() {
        def contents = [
                "first",
                "second",
                SectionMark.COMMON,
                "third",
                SectionMark.CALLS,
                "fifth"
        ].join("\n")
        def reader = new TxaReader("" << contents)
        def lines = reader.readUptoSection(SectionMark.CALLS)
        assert lines.size() == 4
        assert lines.last() == "third"
        assert reader.currentLine().asSectionMark() == SectionMark.CALLS
    }
}