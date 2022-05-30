package nl.intreq.c4w.txa.transform

import nl.intreq.c4w.txa.meta.ClarionStringMixins

class LineBuilder {

    private List<String> rawLines = []
    def skipBlankLines = false
    def autoConcatenate = true

    private int physicalLineNumber = -1
    private int logicalLineNumber = -1

    static {
        ClarionStringMixins.initialize()
    }

        LineBuilder accept(String line,  Closure rawHandler, Closure logicalHandler) {
            accept(line,-1L,rawHandler,logicalHandler)
        }

        LineBuilder accept(String line, Long lineNumber, Closure rawHandler, Closure logicalHandler) {
        if (lineNumber >= 0){
            logicalLineNumber = lineNumber
            physicalLineNumber = lineNumber
        } else {
            physicalLineNumber++
        }

        rawHandler(physicalLineNumber, line)

        if (line.endsWith('|')) {
            rawLines << line
        } else {
            def logicalLine = rawLines.inject('') { s, rawLine ->
                s +=  rawLine.trim().isEmpty() ? '' :  rawLine.substring(0, rawLine.length() - 1)
            }

            rawLines << line
            logicalLine += line

            if (!logicalLine.trim().isEmpty() || !skipBlankLines) {
                if (autoConcatenate){
                    logicalLine = logicalLine.replaceAll(/'\s*&\s*'/,'')
                }
                
                logicalHandler(logicalLineNumber, logicalLine)
                rawLines = []
                logicalLineNumber += 1
            }
        }

        return this
    }

}
