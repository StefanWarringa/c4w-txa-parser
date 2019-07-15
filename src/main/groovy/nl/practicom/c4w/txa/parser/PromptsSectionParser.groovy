package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.Common

class PromptsSectionParser {
    Common parent

    PromptsSectionParser(Common parent) {
        this.parent = parent
    }

    def parse(TxaReader r) {
        if ( !r.at(SectionMark.PROMPTS)){
            r.readUptoSection(SectionMark.PROMPTS)
        }

        r.readLine()

        while(r.currentLine().startsWith('%')){
           def depcount = r.currentLine().findAll("DEPEND").size()
           switch (depcount){
               case 0 :
                   new SimplePromptParser(this.parent).parse(r)
                   break
               case 1 :
                   new DependentPromptParser(this.parent).parse(r)
                   break
               case 2 :
                   new NestedDependentPromptParser(this.parent).parse(r)
                   break
           }
        }
    }
}
