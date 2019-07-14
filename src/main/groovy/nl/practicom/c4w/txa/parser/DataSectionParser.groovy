package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.Common

class DataSectionParser {
    private Common parent

    DataSectionParser(Common parent) {
        this.parent = parent
    }

    /**
     * The data section structure to be parsed:
     *
     * [DATA]
     *  [LONGDESC]         optional
     *  [USEROPTION]       optional
     *  [SCREENCONTROLS]   optional
     *  [REPORTCONTROLS]   optional
     *  field definition
     *  keyword list
     *
     * @param r
     * @return
     */
    def parse(TxaReader r) {
        if ( r.currentLine.isSectionStart(SectionMark.DATA)){
            r.readLine()
        }

        while ( r.at(SectionMark.SCREENCONTROLS) || r.at(SectionMark.REPORTCONTROLS)){
            //ToDo : process data var
            // skip to after keyword indicator
            r.readUptoMatching(/^!!.*$/)
            r.readLine() // move past it
        }

        this.parent.data = []
    }
}
