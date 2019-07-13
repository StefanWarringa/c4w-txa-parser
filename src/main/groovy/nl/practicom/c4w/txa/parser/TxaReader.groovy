package nl.practicom.c4w.txa.parser

import java.nio.file.Path

class TxaReader {

    Reader source
    String currentLine

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
        if ( currentLine == null) {
            def sb = '' << ''
            while (true) {
                def line = this.source.readLine()
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

        if (autoforward){
            return forward()
        } else {
            return this.currentLine
        }
    }

    // Marks the current line as processed and moves pointer forward
    private String forward() {
        def tmp = this.currentLine
        this.currentLine = null
        return tmp
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
            def line = this.readLine(false)
            if ( line == null) break
            if (line ==~ pattern) {
                match = true
            } else {
                lines << line
                forward()
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
            def line = this.readLine(false)
            if ( line == null ) break
            if (line ==~ pattern) {
                lines << line
                forward()
            } else {
                break
            }
        }

        return lines
    }
}
