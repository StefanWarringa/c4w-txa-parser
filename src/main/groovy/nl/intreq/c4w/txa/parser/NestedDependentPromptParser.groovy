package nl.intreq.c4w.txa.parser

import nl.intreq.c4w.txa.model.TemplatePrompts
import nl.intreq.c4w.txa.transform.TxaReader

class NestedDependentPromptParser {
    TemplatePrompts parent

    NestedDependentPromptParser(TemplatePrompts parent) {
        this.parent = parent
    }

    def parse(TxaReader reader) {
        if ( r.currentLine ==~
                /^%(\w*)\s+DEPEND\s+%(\w+)\s+DEPEND\s+%(\w+)\s+(UNIQUE|MULTI)?\s?(%picture|LONG|REAL|STRING|FILE|FIELD|KEY|COMPONENT|PROCEDURE|DEFAULT)\s+TIMES\s+(.*)$/){
            def conditionals = r.readWhileMatching(/^WHEN\W.*/)
        } else {
            r.readLine()
        }
    }
}
