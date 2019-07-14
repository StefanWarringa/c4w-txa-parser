package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.Common
import nl.practicom.c4w.txa.model.ControlTemplateFiles

class ControlTemplateFilesParser {
    Common parent

    ControlTemplateFilesParser(Common parent) {
        this.parent = parent
    }

    def parse(TxaReader r) {
        def model = new ControlTemplateFiles()

        if ( r.at(SectionMark.PRIMARY) ){
            model.primaryFile = r.readLine()
        }

        if ( r.at(SectionMark.INSTANCE) ) {
            model.controlTemplateInstance = r.readLine()
        }

        if ( r.at(SectionMark.KEY) ) {
            model.accessKey = r.readLine()
        }

        if ( r.at(SectionMark.SECONDARY) ) {
            def lines = r.readUptoNextSection()
            lines.each { line ->
                def elems = line.trim().split(/\W/)
                if (elems.size() == 2) {
                    model.secondaryFiles.add(new Tuple(elems[0],elems[1],"LEFT"))
                }
                if (elems.size() == 3) {
                    model.secondaryFiles.add(new Tuple(elems[0],elems[1],elems[2]))
                }
            }
        }

    }
}
