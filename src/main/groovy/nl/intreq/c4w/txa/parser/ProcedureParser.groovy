package nl.intreq.c4w.txa.parser

import nl.intreq.c4w.txa.model.Procedure
import nl.intreq.c4w.txa.model.TxaRoot
import nl.intreq.c4w.txa.transform.SectionMark
import nl.intreq.c4w.txa.transform.TxaReader

class ProcedureParser {
    static TxaRoot parse(TxaReader r) {
        def model = new Procedure()

        // COMMON section is required
        def lines = r.readUptoSection(SectionMark.COMMON)

        // Common section attributes
        if ( lines.size() >= 1){
            model.name = lines[0].substring("NAME ".length())
        }
        if ( lines.size() >= 2 ){
            model.prototype = lines[1].substring("PROTOTYPE ".length())
        }

        // Process common section subsections. Assumption is
        // that these are generated in the order below

        new CommonSectionParser(model).parse(r)
        r.readUptoNextSection()

        if ( r.at(SectionMark.CALLS)) {
            def calls = r.readUptoNextSection()
            model.calls = calls
        }

        if ( r.at(SectionMark.WINDOW)){
            new WindowSectionParser(model).parse(r)
        }

        if ( r.at(SectionMark.REPORT)){
            //new ReportSectionParser(model).parse(r)
            r.readUptoNextSection()
        }

        if ( r.at(SectionMark.FORMULA)){
            //new FormulasSectionParser(model).parse(r)
            r.readUptoNextSection()
        }

        return model
    }
}
