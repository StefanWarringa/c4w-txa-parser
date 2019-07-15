package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.Common
import nl.practicom.c4w.txa.model.DependentPrompt
import nl.practicom.c4w.txa.model.Prompt

class DependentPromptParser {
    Common parent

    /*
     *    1               2            (3)        4          5
     * %<name> DEPEND %<parent> <MULTI|UNIQUE> <type> TIMES <n>
     */
    private static promptPattern =
            /^%(\w*)\s+DEPEND\s+%(\w+)\s+(UNIQUE|MULTI)?\s?(%picture|LONG|REAL|STRING|FILE|FIELD|KEY|COMPONENT|PROCEDURE|DEFAULT)\s+TIMES\s+(.*)$/

    /*
     *
     * WHEN (<parent value>) (<values>)
     */
    private static whenPattern = /^WHEN\s+\((.*)\)\s+\((.*)\)$/

    DependentPromptParser(Common parent) {
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

            //def conditionals = r.readWhileMatching(/^WHEN\s.*/, false)
            r.readLine()

            numConditionals.times {
                def whenMatcher = (r.readLine(false) =~ whenPattern)
                if (whenMatcher.matches()) {
                    prompt.addOption(whenMatcher[0][1] as String, whenMatcher[0][2] as String)
                }
            }
//
//            conditionals.forEach { c ->
//                def whenMatcher = (c =~ whenPattern)
//                if (whenMatcher.matches()) {
//                    prompt.addOption(whenMatcher[0][1] as String, whenMatcher[0][2] as String)
//                }
//            }

            if (this.parent.prompts == null) {
                this.parent.prompts = [prompt]
            } else {
                this.parent.prompts.add(prompt)
            }
        }

        // WHEN block is followed by blank line(s)
        while ( !r.atEOF() && r.currentLine().trim().isEmpty() ){
            r.readLine()
        }
    }
}
