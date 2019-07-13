package nl.practicom.c4w.txa.parser;

import org.junit.Before
import org.junit.Test

import static org.junit.Assert.*

public class TxaReaderTest {

    @Before
    void setUp() throws Exception {
    }

    @Test
    void readLineFromEmptyFileShouldReturnNull() {
        def reader = new TxaReader(''<<'')
        assert reader.readLine() == null
    }

    @Test
    void multipleReadLinesWithoutForwardWillReturnSameLine() {
        def reader = new TxaReader(
                "first line\n" << "second line"
        )
        assert "first line" == reader.readLine(false)
        assert "first line" == reader.readLine(false)
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
    void readUptoMatchingReturnsNothingIfCurrentLineMatches() {
        def contents = ["first","MARK","second"].join("\n")
        def reader = new TxaReader("" << contents)
        def chunk1 = reader.readUptoMatching(/^MARK/)
        assert chunk1.size() == 1
        assert chunk1.first() == "first"
        assert "MARK" == reader.readLine(false)
        def chunk2 = reader.readUptoMatching(/^MARK/)
        assert chunk2.size() == 0
    }

    @Test
    void readUptoMatchingStopsAtFirstMatch() {
        def contents = ["first","second","third","MARK1","fourth","fifth","MARK2"].join("\n")
        def reader = new TxaReader("" << contents)
        def chunk1 = reader.readUptoMatching(/^MARK.$/)
        assert chunk1.size() == 3
        assert chunk1.first() == "first"
        assert chunk1.last() == "third"
        def chunk2 = reader.readUptoMatching(/^MARK2$/)
        assert chunk2.size() == 3
        assert chunk2.first() == "MARK1"
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
        assert chunk2.size() == 3
        assert chunk2.first() == "MARK2"
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
        assert reader.readLine() == "%DefaultBaseClassType DEPEND %ClassItem DEFAULT TIMES 2"
        def whenblock2 = reader.readWhileMatching(/^WHEN\W.*/)
        assert whenblock2.size() == 2
    }
}