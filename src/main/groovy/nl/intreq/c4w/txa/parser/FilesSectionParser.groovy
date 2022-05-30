package nl.intreq.c4w.txa.parser

import nl.intreq.c4w.txa.model.Common
import nl.intreq.c4w.txa.transform.SectionMark
import nl.intreq.c4w.txa.transform.TxaReader

class FilesSectionParser {
    private Common parent

    FilesSectionParser(Common parent) {
        this.parent = parent
    }

    def parse(TxaReader r) {
        if ( r.at(SectionMark.FILES)){
            r.readUptoNextSection()
        }

        while( r.at(SectionMark.PRIMARY)){
            new ControlTemplateFilesParser(this.parent).parse(r)
        }

        if ( r.at(SectionMark.OTHERS)) {
            this.parent.otherFiles = r.readUptoNextSection()
        }
    }
}
