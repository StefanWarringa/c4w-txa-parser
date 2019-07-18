package nl.practicom.c4w.txa.parser

import java.nio.file.Paths

/**
 * The TXA reader is a forward-only scanner for TXA files
 * It provides sugar on top of a raw text reader for automatically
 * concatenating lines that have been split using line continuation '|',
 * recognizing section marks and regex reads.
 */
class TxaReader {

    private static anySectionMarkPattern = /^\s*\[.+\]\s*$/

    // The raw text reader
    private Reader _source

    // The last logical line that was read by readLine
    private String _currentLine

    // End Of File flag
    private boolean _atEOF = false

    // The PHYSICAL line number at which the reader is currently positioned
    // A logical line may span several physical lines so the number of logical
    // lines read may be lower
    private long _currentLineNumber = 0

    TxaReader(String txaFilepath){
        this._source = new File(Paths.get(txaFilepath).normalize().toString()).newReader()
    }

    TxaReader(StringBuffer contents){
        this._source = new StringReader(contents.toString())
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize()
        if ( this._source != null ){
            this._source.close()
        }
    }

    /**
     * Read a single line, concatenating split lines
     * @param autoforward
     * @return
     */
    String readLine(skipBlank = true, ignoreContinuation = false){
        def sb = '' << ''

        def linesRead = 0

        while (!this.atEOF()) {
            def line = this._source.readLine()
            if ( line == null){
                this._atEOF = true
            }
            else {
                this._currentLineNumber++
                linesRead++
                if (!ignoreContinuation && line.endsWith('|')) {
                    sb << line.substring(0, line.length() - 1)
                } else {
                    if ( !line.trim().isEmpty() || !skipBlank ) {
                        sb << line
                        break
                    }
                }
            }
        }

        // Reading past the end of the file will return a null
        // but current line unaffected
        if ( linesRead > 0 ){
            this._currentLine = sb.toString()
            this._currentLine
        } else {
            return null
        }

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
    List<String> readUptoMatching(pattern, skipBlank = true){
        def lines = []
        def match = false

        while (!this.atEOF() && !match){
            def line = this.readLine(skipBlank)
            if ( line == null) break // EOF now
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
    List<String> readWhileMatching(pattern, skipBlank = true){
        def lines = []

        while (!this.atEOF()){
            def line = this.readLine(skipBlank)
            if ( line == null ) break // EOF now
            if (line ==~ pattern) {
                lines << line
            } else {
                break
            }
        }

        return lines
    }

    List<String> readUptoNextSection(){
        def lines = readUptoMatching(anySectionMarkPattern)
        if ( !this.atEOF() && this._currentLine != null && this._currentLine.isSectionEnd() ) {
            this.readLines(2)
        }
        return lines
    }

    List<String> readUptoSection(SectionMark sectionMark){
        return readUptoMatching(sectionMark.matcher)
    }

    /**
     * Reads upto the start of the next section (implicit end) or
     * upto explicit section ending ([END])or upto EOF
     * Post condition: current at [END], at section mark of next section or at last line
     *
     * @return the lines read between current position and end of section.
     */
    List<String> readUptoSectionEnd(){
        return readUptoMatching(anySectionMarkPattern)
    }

    String currentLine(){
        this._currentLine
    }

    long currentLineNumber(){
        this._currentLineNumber
    }

    // write protect the crucial EOF flag
    boolean atEOF() {
        this._atEOF
    }

    boolean at(SectionMark mark){
        this._currentLine.isSectionStart(mark)
    }

    boolean atAnySectionStart() {
        this._currentLine.isSectionMark() && !this.atSectionEndMark()
    }

    boolean atSectionEndMark(){
        this.at(SectionMark.SECTIONEND)
    }
}
