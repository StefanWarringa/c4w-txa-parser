package nl.practicom.c4w.txa.parser

import java.nio.file.Paths

/**
 * The TXA reader is a forward-only scanner for TXA files
 * It provides sugar on top of a raw text reader for automatically
 * concatenating lines that have been split using line continuation '|',
 * recognizing section marks and regex reads.
 */
class TxaReader {

    // The raw text reader
    private Reader source

    // The last logical line that was read by readLine
    String currentLine

    // The PHYSICAL line number at which the reader is currently positioned
    // A logical line may span several physical lines so the number of logical
    // lines read may be lower
    long currentLineNumber = 0

    TxaReader(String txaFilepath){
        this.source = new File(Paths.get(txaFilepath).normalize().toString()).newReader()
    }

    TxaReader(StringBuffer contents){
        this.source = new StringReader(contents.toString())
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize()
        if ( this.source != null ){
            this.source.close()
        }
    }

    /**
     * Read a single line, concatenating split lines
     * @param autoforward
     * @return
     */
    String readLine(autoforward = true){
        def sb = '' << ''
        while (true) {
            def line = this.source.readLine()
            this.currentLineNumber++
            if ( line == null){
                return null
            }
            if (line.endsWith('|')) {
                sb << line.substring(0,line.length()-1)
            } else {
                sb << line
                break
            }
        }

        this.currentLine = sb.toString()
    }

    /**
     * Read a specific number of lines
     * @param lineCount
     * @return
     */
    List<String> readLines(int lineCount) {
        def lines = []
        lineCount.times { lines << this.readLine()}
        return lines
    }

    /**
     * Read lines until a line matching a given pattern occurs
     * @param pattern - regular expression
     * @return
     */
    List<String> readUptoMatching(pattern){
        def lines = []
        def match = false

        while (!match){
            def line = this.readLine()
            if ( line == null) break
            if (line ==~ pattern) {
                match = true
            } else {
                lines << line
            }
        }

        return lines
    }

    /**
     * Read lines as long as the line matches the given pattern
     * @param pattern - regular expression
     * @return
     */
    List<String> readWhileMatching(pattern){
        def lines = []

        while (true){
            def line = this.readLine()
            if ( line == null ) break
            if (line ==~ pattern) {
                lines << line
            } else {
                break
            }
        }

        return lines
    }

    List<String> readUptoNextSection(){
        def lines = readUptoMatching(/^\[.+\]$/)
        if ( this.currentLine != null && this.currentLine.isSectionEnd() ) {
            this.readLines(2)
        }
        return lines
    }

    List<String> readUptoSection(SectionMark sectionMark){
        return readUptoMatching(sectionMark.matcher)
    }

    boolean atEOF() {
        this.currentLineNumber > 0 && this.currentLine == null
    }

    boolean at(SectionMark mark){
        !this.atEOF() && this.currentLine.isSectionStart(mark)
    }

}
