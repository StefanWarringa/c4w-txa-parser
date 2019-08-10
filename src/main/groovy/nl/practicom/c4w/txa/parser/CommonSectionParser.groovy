package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.Common
import nl.practicom.c4w.txa.model.TemplatePrompts
import nl.practicom.c4w.txa.model.TxaRoot
import nl.practicom.c4w.txa.transform.SectionMark
import nl.practicom.c4w.txa.transform.TxaReader

class CommonSectionParser {
    private TxaRoot parent

    CommonSectionParser(TxaRoot parent){
        this.parent = parent
    }

    void parse(TxaReader r){
        def model = new Common()

        if (!r.at(SectionMark.COMMON)){
            r.readUptoSection(SectionMark.COMMON)
            r.readLine()
        }

        def attrs = r.readUptoNextSection()
        attrs.each { attr ->
            def t = attr.asAttribute()
            switch ( t.first()) {
                case "DESCRIPTION":
                    model.description = t.last()
                    break
                case "LONG":
                    model.longDescription = t.last()
                    break
                case "FROM":
                    model.from = t.last()
                    break
                case "MODIFIED":
                    model.modified = t.last()
                    break
            }
        }

        if (r.at(SectionMark.DATA)){
            new DataSectionParser(model).parse(r)
        }
        if (r.at(SectionMark.FILES)){
            new FilesSectionParser(model).parse(r)
        }
        if (r.at(SectionMark.PROMPTS)){
            new PromptsSectionParser(model as TemplatePrompts).parse(r)
        }

        if (r.at(SectionMark.EMBED)){
            new EmbedSectionParser(model).parse(r)
        }

        // ADDITION is repeatable
        while (!r.atEOF() && r.at(SectionMark.ADDITION)){
            new AdditionSectionParser(model).parse(r)
        }

        this.parent.common = model
    }
}
