package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.Common
import nl.practicom.c4w.txa.model.EmbedInstance

class EmbedSectionParser {

    static embedPattern = /^\s*EMBED\s*(%?\w+)$/
    static priorityPattern = /^\s*PRIORITY\s+([0-9]+)\s*$/

    Common parent

    EmbedSectionParser(Common parent) {
        this.parent = parent
    }

    def parse(TxaReader r) {

        if ( !r.at(SectionMark.EMBED) ){
            r.readUptoSection(SectionMark.EMBED)
        }

        if ( !r.atEOF() && r.at(SectionMark.EMBED)) {
            r.readLine()

            if ( this.parent.embeds == null ){
                this.parent.embeds = []
            }

            while (!r.atEOF() && r.currentLine() ==~ embedPattern) {
                def embedMatcher = (r.currentLine() =~ embedPattern)
                def embedPoint = embedMatcher[0][1] as String

                r.readUptoNextSection()

                if (r.at(SectionMark.DEFINITION)) {
                    r.readUptoNextSection()

                    EmbedInstance embedInstance = null

                    switch (r.currentLine().asSectionMark()) {
                        case SectionMark.SOURCE:
                            embedInstance = parseSourceDefinition(r)
                            break
                        case SectionMark.TEMPLATE:
                            embedInstance = parseTemplateDefinition(r)
                            break
                        case SectionMark.PROCEDURE:
                            embedInstance = parseProcedureDefinition(r)
                            break
                        case SectionMark.GROUP:
                            embedInstance = parseGroupDefinition(r)
                    }

                    if (embedInstance != null) {
                        embedInstance.embedPoint = embedPoint
                        this.parent.embeds.add(embedInstance)
                    }
                }
            }
        } else {
            r.readLine()
        }
    }

    EmbedInstance parseSourceDefinition(TxaReader r) {
        def embed =  new EmbedInstance(sourceType: EmbedInstance.SourceType.SOURCE)
        def source = r.readLine() << '\n'
        while ( !( r.atEOF() || r.at(SectionMark.SECTIONEND) || r.currentLine().isSectionStart()) ){
            source << r.readLine() << '\n'
            if ( embed.priority == 0 ){
                if ( r.currentLine() ==~ priorityPattern ){
                    def priorityMatcher = ( r.currentLine() =~ priorityPattern )
                    if ( priorityMatcher.matches() ){
                        embed.priority = priorityMatcher[0][1] as int
                    }
                }
            }
        }

        embed.source = source

        r.readUptoNextSection()
        return embed
    }

    EmbedInstance parseTemplateDefinition(TxaReader r) {
        return new EmbedInstance(sourceType: EmbedInstance.SourceType.TEMPLATE)
        r.readUptoNextSection()
    }

    EmbedInstance parseProcedureDefinition(TxaReader r) {
        return new EmbedInstance(sourceType: EmbedInstance.SourceType.PROCEDURE)
        r.readUptoNextSection()
    }

    EmbedInstance parseGroupDefinition(TxaReader r) {
        return new EmbedInstance(sourceType: EmbedInstance.SourceType.GROUP)
        r.readUptoNextSection()
    }
}
