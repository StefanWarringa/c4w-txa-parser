package nl.intreq.c4w.txa.parser

import nl.intreq.c4w.txa.model.Program
import nl.intreq.c4w.txa.model.TxaRoot
import nl.intreq.c4w.txa.transform.TxaReader

class ProgramParser {
    static TxaRoot parse(TxaReader txaReader) {
        return new Program()
    }
}
