package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.Addition
import nl.practicom.c4w.txa.model.Common
import nl.practicom.c4w.txa.transform.SectionMark
import nl.practicom.c4w.txa.transform.TxaReader

class AdditionSectionParser {
    def static namePattern = /^\s*NAME\s+(\w+)\s+(\w+)\s*$/
    def static instancePattern = /^\s*INSTANCE\s+([0-9]+)\s*$/
    def static parentPattern = /^\s*PARENT\s+([0-9]+)\s*$/
    def static ownerPattern = /^\s*OWNER\s+([0-9]+)\s*$/
    def static procpropPattern = /^\s*PROCPROP\s*$/

    Common parent

    AdditionSectionParser(Common parent) {
        this.parent = parent
    }

    void parse(TxaReader reader) {

        if ( !reader.at(SectionMark.ADDITION)){
            reader.readUptoSection(SectionMark.ADDITION)
        }

        // Only when there's a [ADDITION] section it will be initialize
        if ( reader.at(SectionMark.ADDITION)){
            this.parent.additions= []
        }

        while ( !reader.atEOF() && reader.at(SectionMark.ADDITION)){
            def addition = new Addition()

            reader.readLine()

            def nameMatcher = (reader.currentLine() =~ namePattern)
            if (nameMatcher.matches()) {
                addition.templateClass = nameMatcher[0][1]
                addition.templateName = nameMatcher[0][2]
            }

            reader.readUptoNextSection()

            // keep processing any sections
            while (!reader.atEOF() && reader.atAnySectionStart() && !reader.at(SectionMark.ADDITION)) {
                switch (reader.currentLine().asSectionMark()) {
                    case SectionMark.FIELDPROMPT:
                        parseFieldPrompts(addition, reader)
                        break
                    case SectionMark.INSTANCE:
                        parseInstance(addition, reader)
                        break
                    case SectionMark.PROMPTS:
                        parsePrompts(addition, reader)
                        break
                    case SectionMark.ADDITION:
                        //Implicit end of current addition section
                        break
                }
            }

            this.parent.additions.add(addition)
        }
    }

    void parseFieldPrompts(Addition addition, TxaReader r) {
        // The prompts parser will only update the prompts field
        // of the model object. Therefore we create a simple object
        // and add the prompts to via the trait it so we can pass
        // it to the parser
        new PromptsSectionParser(addition).parse(r, SectionMark.FIELDPROMPT)
    }

    def parsePrompts(Addition addition, TxaReader r) {
        new PromptsSectionParser(addition).parse(r, SectionMark.PROMPTS)
    }


    void parseInstance(Addition addition, TxaReader r) {
        def lines = r.readUptoNextSection()

        // showPrompts set to true when PROCPROP line present
        addition.showPrompts = false

        lines.each { line ->
            if ( line ==~ instancePattern ){
                def m = (line =~ instancePattern)
                addition.instanceId = m[0][1] as Long
            }
            if ( line ==~ parentPattern ){
                def m = (line =~ parentPattern)
                addition.parentId = m[0][1] as Long
            }
            if ( line ==~ ownerPattern ){
                def m = (line =~ ownerPattern)
                addition.ownerId = m[0][1] as Long
            }
            if ( line ==~ procpropPattern ){
                addition.showPrompts = true
            }
        }
    }
}
