package nl.intreq.c4w.txa.parser

import nl.intreq.c4w.txa.model.DependentPrompt
import nl.intreq.c4w.txa.model.Prompt
import nl.intreq.c4w.txa.model.TemplatePrompts
import nl.intreq.c4w.txa.transform.TxaReader

class DependentPromptParser {
    TemplatePrompts parent

    /*
     *    1               2            (3)        4          5
     * %<name> DEPEND %<parent> <MULTI|UNIQUE> <type> TIMES <n>
     */
    private static promptPattern =
            /^\s*%(\w*)\s+DEPEND\s+%(\w+)\s+(UNIQUE|MULTI)?\s?(%picture|LONG|REAL|STRING|FILE|FIELD|KEY|COMPONENT|PROCEDURE|DEFAULT)\s+TIMES\s+(.*)\s*$/

    /*
     *
     * WHEN (<parent value>) (<values>)
     */
    private static whenPattern = /^\s*WHEN\s+\((.*)\)\s+\((.*)\)\s*$/

    DependentPromptParser(TemplatePrompts parent) {
        this.parent = parent
    }

    def parse(TxaReader r) {
        def promptMatcher = (r.currentLine() =~ promptPattern)
        if ( promptMatcher.matches() ) {
            def prompt = new DependentPrompt()
            prompt.name = promptMatcher[0][1] as String
            prompt.parentSymbol = promptMatcher[0][2] as String
            if (promptMatcher[0][3] != null) {

            }
            prompt.type = (promptMatcher[0][4] as String).toUpperCase() as Prompt.PromptType

            def numConditionals = promptMatcher[0][5] as Integer

            numConditionals.times {
                r.readLine()
                if ( !r.atEOF()) {
                    def whenMatcher = (r.currentLine() =~ whenPattern)
                    if (whenMatcher.matches()) {
                        prompt.addOption(whenMatcher[0][1] as String, whenMatcher[0][2] as String)
                    }
                }
            }

            if (this.parent.prompts == null) {
                this.parent.prompts = [prompt]
            } else {
                this.parent.prompts.add(prompt)
            }
        }

        // WHEN block is followed by blank line(s)
//        while ( !r.atEOF() && r.currentLine().trim().isEmpty() ){
            r.readLine()
//        }
    }
}
