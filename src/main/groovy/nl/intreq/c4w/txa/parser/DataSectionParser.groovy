package nl.intreq.c4w.txa.parser

import nl.intreq.c4w.txa.model.Common
import nl.intreq.c4w.txa.transform.SectionMark
import nl.intreq.c4w.txa.transform.TxaReader

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

        if ( !r.at(SectionMark.DATA)){
            r.readUptoSection(SectionMark.DATA)
            r.readLine()
        }

        r.readUptoNextSection()

        while ( r.at(SectionMark.SCREENCONTROLS) || r.at(SectionMark.REPORTCONTROLS)){
            //ToDo : process data var
            // skip to after keyword indicator
            r.readUptoMatching(/^!!.*$/)
            r.readLine() // move past it
        }

        this.parent.data = []
    }
}
