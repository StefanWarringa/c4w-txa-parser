package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.Common

class NestedDependentPromptParser {
    Common parent

    NestedDependentPromptParser(Common parent) {
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
