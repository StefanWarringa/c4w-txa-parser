package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.Common
import nl.practicom.c4w.txa.model.Prompt
import nl.practicom.c4w.txa.model.SimplePrompt

class SimplePromptParser {

    /*
     *   1           2         3       4
     * <name> (UNIQUE|MULTI) <type> <values
     */
    static promptPattern =
            /^%(\w*)\s+(UNIQUE|MULTI)?\s+(%picture|LONG|REAL|STRING|FILE|FIELD|KEY|COMPONENT|PROCEDURE|DEFAULT)\s+(\(.*\))$/

    Common parent

    SimplePromptParser(Common parent) {
        this.parent = parent
    }

    def parse(TxaReader r) {
        def promptMatcher = (r.currentLine =~ promptPattern)

        if ( promptMatcher.matches()){
            def prompt = new SimplePrompt()
            prompt.name = promptMatcher[0][1] as String
            if ( promptMatcher[0][2] != null ){
                prompt.isUnique == (promptMatcher[0][2] as String).equalsIgnoreCase("unique")
            }
            prompt.type = promptMatcher[0][3] as Prompt.PromptType
            prompt.values = promptMatcher[0][4] as List
        } else {
            r.readLine()
        }
    }
}
