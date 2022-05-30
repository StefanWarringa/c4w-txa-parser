package nl.intreq.c4w.txa.parser

import nl.intreq.c4w.txa.model.Prompt
import nl.intreq.c4w.txa.model.TemplatePrompts
import nl.intreq.c4w.txa.model.SimplePrompt
import nl.intreq.c4w.txa.transform.TxaReader

class SimplePromptParser {

    /*
     *   1           2         3       4
     * <name> (UNIQUE|MULTI) <type> <values
     */
    static promptPattern =
            /^\s*%(\w*)\s+(UNIQUE\s|MULTI\s)?\s*(%picture|LONG|REAL|STRING|FILE|FIELD|KEY|COMPONENT|PROCEDURE|DEFAULT)\s+(\(.*\))\s*$/

    TemplatePrompts model

    SimplePromptParser(TemplatePrompts model) {
        this.model = model
    }

    def parse(TxaReader r) {
        def promptMatcher = (r.currentLine() =~ promptPattern)

        if ( promptMatcher.matches()){
            def prompt = new SimplePrompt()
            prompt.name = promptMatcher[0][1] as String
            if ( promptMatcher[0][2] != null ){
                prompt.isUnique = (promptMatcher[0][2] as String).equalsIgnoreCase("unique")
            }
            prompt.type = promptMatcher[0][3] as Prompt.PromptType
            prompt.values = (promptMatcher[0][4] as String).fromClarionStringList()

            if ( model.prompts == null){
                model.prompts = [prompt]
            } else {
                model.prompts.add(prompt)
            }
        }

        r.readLine()
    }
}
