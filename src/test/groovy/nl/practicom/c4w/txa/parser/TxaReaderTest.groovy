package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.meta.ClarionStringMixins;
import org.junit.Before
import org.junit.Test

class TxaReaderTest {

    @Before
    void setUp() throws Exception {
        ClarionStringMixins.initialize()
    }

    @Test
    void readLineFromEmptyFileShouldReturnNull() {
        def reader = new TxaReader(''<<'')
        assert reader.readLine() == null
    }

    @Test
    void splitLinesWillBeReadAsSingleLine() {
        def reader = new TxaReader(
                "This is the |" << "\n" << "first line" << "\n"
            << "This is the |" << "\n" << "second line"
        )
        assert "This is the first line" == reader.readLine()
        assert "This is the second line" == reader.readLine()
    }

    @Test
    void readUptoMatchingExcludesMatchOfCurrentLine() {
        def contents = ["first","MARK","second"].join("\n")
        def reader = new TxaReader("" << contents)
        def chunk1 = reader.readUptoMatching(/^MARK/)
        assert chunk1.size() == 1
        assert chunk1.first() == "first"
        assert "MARK" == reader.currentLine
        def chunk2 = reader.readUptoMatching(/^MARK/)
        assert chunk2.size() == 1
        assert chunk2.first() == "second"
    }

    @Test
    void readUptoMatchingStopsAtFirstMatch() {
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

    @Test
    void readUptoMatchingReadsAllRemainingLinesIfNoMatch() {
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

    @Test
    void readWhileMatching() {
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
        assert reader.currentLine == "%DefaultBaseClassType DEPEND %ClassItem DEFAULT TIMES 2"
        def whenblock2 = reader.readWhileMatching(/^WHEN\W.*/)
        assert whenblock2.size() == 2
    }

    @Test
    void readUptoNextSectionReturnsAllLinesBeforeSectionMark(){
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
        assert reader.currentLine == "[SEC1]"
    }

    @Test
    void readUptoNextSectionStopsAfterExplicitEnd(){
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
        assert reader.currentLine == "third"
    }

    @Test
    void readUptoSectionSlurpsIntermediateSections() {
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
        assert reader.currentLine.asSectionMark() == SectionMark.CALLS
    }
}