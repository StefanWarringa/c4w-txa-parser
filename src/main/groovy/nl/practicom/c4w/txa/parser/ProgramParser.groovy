package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.Program
import nl.practicom.c4w.txa.model.TxaRoot
import nl.practicom.c4w.txa.transform.TxaReader

class ProgramParser {
    static TxaRoot parse(TxaReader txaReader) {
        return new Program()
    }
}
