package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.Procedure
import nl.practicom.c4w.txa.model.TxaRoot

class ProcedureParser {
    static TxaRoot parse(TxaReader r) {
        def model = new Procedure()
        // COMMON section is required
        def lines = r.readUptoSection(SectionMark.COMMON)
        if ( lines.size() >= 1){
            model.name = lines[0].substring("NAME ".length())
        }
        if ( lines.size() >= 2 ){
            model.prototype = lines[1].substring("PROTOTYPE ".length())
        }
        new CommonSectionParser(model).parse(r)
        r.readUptoNextSection()
        if ( r.currentLine.isSectionStart(SectionMark.CALLS)) {
            r.forward()
            def calls = r.readUptoNextSection()
            model.calls = calls
        }
        if ( r.currentLine.isSectionStart(SectionMark.WINDOW)){
            //new WindowSectionParser(model).parse(r)
            r.readUptoNextSection()
        }
        if ( r.currentLine.isSectionStart(SectionMark.REPORT)){
            //new ReportSectionParser(model).parse(r)
            r.readUptoNextSection()
        }
        if ( r.currentLine.isSectionStart(SectionMark.FORMULA)){
            //new FormulasSectionParser(model).parse(r)
            r.readUptoNextSection()
        }
        return model
    }
}
