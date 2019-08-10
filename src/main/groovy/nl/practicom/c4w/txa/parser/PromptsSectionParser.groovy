package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.TemplatePrompts
import nl.practicom.c4w.txa.transform.SectionMark
import nl.practicom.c4w.txa.transform.TxaReader

class PromptsSectionParser {
    TemplatePrompts model

    PromptsSectionParser(TemplatePrompts model) {
        this.model = model
    }

    def parse(TxaReader r, SectionMark promptsSectionMark = SectionMark.PROMPTS) {
        if ( !r.at(promptsSectionMark) ){
            r.readUptoSection(promptsSectionMark)
        }

        r.readLine()

        while(r.currentLine().trim().startsWith('%')){
           def depcount = r.currentLine().findAll("DEPEND").size()
           switch (depcount){
               case 0 :
                   new SimplePromptParser(this.model).parse(r)
                   break
               case 1 :
                   new DependentPromptParser(this.model).parse(r)
                   break
               case 2 :
                   new NestedDependentPromptParser(this.model).parse(r)
                   break
           }
        }
    }
}
