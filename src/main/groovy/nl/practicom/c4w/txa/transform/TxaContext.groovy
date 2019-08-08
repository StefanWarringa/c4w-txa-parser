package nl.practicom.c4w.txa.transform

import nl.practicom.c4w.txa.parser.SectionMark

/**
 * This class stores context the hierarchical context
 * of the current line in the TXA structure.
 */
final class TxaContext {
    // Current line number being read
    Long currentLineNumber = null

    // Current line contents
    String currentLine = null

    /* The parent sections of the current section*/
    List<SectionMark> parentSections = []

    // The current section
    SectionMark currentSection = null

    // The name of current procedure being processed
    String currentProcedureName

    // Holds the current %embedpoint defined by EMBED %embedpoint
    String currentEmbedPoint = null

    // Stores the name in WHEN '<name>' to figure out in which
    // instance of the embed-point we are. Can hold an array
    // because WHEN statements can be nested
    List<String> currentEmbedInstance = []

    // is increased on every WHEN ... inside EMBED
    int instanceLevel = 0

    // Tests if current section hierarchy contains the provided sections in the given order
    boolean within(SectionMark... sections){
        if ( sections == null ) return false
        if ( sections.size() == 0) return false
        if ( sections.size() == 1) return this.currentSection == sections[0] || this.parentSections.contains(sections[0])

        List<SectionMark> sectionContext = this.parentSections + this.currentSection
        for ( s in sections){
            if (sectionContext.indexOf(s) == -1){
                return false
            } else {
                sectionContext = sectionContext.drop(sectionContext.indexOf(s)+1)
            }
        }
        return true
    }
}