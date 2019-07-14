package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.Common
import nl.practicom.c4w.txa.model.TxaRoot

class CommonSectionParser {
    private TxaRoot parent

    CommonSectionParser(TxaRoot parent){
        this.parent = parent
    }

    void parse(TxaReader r){
        def model = new Common()
        if (r.currentLine.isSectionStart(SectionMark.COMMON)){
            //r.readLine()
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

        if (r.currentLine.isSectionStart(SectionMark.DATA)){
            new DataSectionParser(model).parse(r)
        }
        if (r.currentLine.isSectionStart(SectionMark.FILES)){
            new FilesSectionParser(model).parse(r)
        }
        if (r.currentLine.isSectionStart(SectionMark.PROMPTS)){
            new PromptsSectionParser(model).parse(r)
        }
        if (r.currentLine.isSectionStart(SectionMark.EMBED)){
            //new EmbedSectionParser(model).parse(r)
            r.readUptoNextSection()
        }
        while (r.currentLine.isSectionStart(SectionMark.ADDITION)){
            //new AdditionSectionReader(model).parse(r)
            r.readUptoSection(SectionMark.ADDITION)
        }

        this.parent.common = model
    }
}
