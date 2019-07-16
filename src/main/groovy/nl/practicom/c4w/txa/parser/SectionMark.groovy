package nl.practicom.c4w.txa.parser

/**
 * The section marks are the pinnacle point for correct functioning of the parser
 * Note that we allow optional whitespace before and after the section marker
 * for more robustness although in the TXA no whitespace seems to be generated.
 */
enum SectionMark {
    APPLICATION("[APPLICATION]",/^\s*\[APPLICATION\\]\s*$/),
    PROGRAM("[PROGRAM]",/^\s*\[PROGRAM\\]\s*$/),
    MODULE("[MODULE",/^\s*\[MODULE\]\s*$/),
    PROCEDURE("[PROCEDURE]",/^\[PROCEDURE\]\s*$/),
    COMMON("[COMMON]",/^\s*\[COMMON\]\s*$/),
    WINDOW("[WINDOW]",/^\s*\[WINDOW\]\s*$/),
    ADDITION("[ADDITION]",/^\s*\[ADDITION\]\s*$/),
    EMBED("[EMBED]",/^\s*\[EMBED\]\s*$/),
    PROMPTS("[PROMPTS]",/^\s*\[PROMPTS\]$/),
    SCREENCONTROLS("[SCREENCONTROLS]",/^\s*\[SCREENCONTROLS\]\s*$/),
    REPORTCONTROLS("[REPORTCONTROLS]",/^\s*\[REPORTCONTROLS\]\s*$/),
    FILES("[FILES]",/^\s*\[FILES\]\s*$/),
    OTHERS("[OTHERS]",/^\s*\[OTHERS\]\s*$/),
    INSTANCES("[INSTANCES]",/^\s*\[INSTANCES\]\s*$/),
    DEFINITION("[DEFINITION]",/^\s*\[DEFINITION\]\s*$/),
    SECTIONEND("[END]",/^\s*\[END\]\s*$/),
    FIELDPROMPT("[FIELDPROMPT]",/^\s*\[FIELDPROMPT\]\s*$/),
    CALLS("[CALLS]",/^\s*\[CALLS\]\s*$/),
    FORM("[FORM]",/^\s*\[FORM\]\s*$/),
    REPORT("[REPORT]",/^\s*\[REPORT\]\s*$/),
    FORMULA("[FORMULA]",/^\s*\[FORMULA\]\s*$/),
    DATA("[DATA]",/^\s*\[DATA\]\s*$/),
    PRIMARY("[PRIMARY]",/^\s*\[PRIMARY\]\s*$/),
    SECONDARY("[SECONDARY]",/^\s*\[SECONDARY\]\s*$/),
    INSTANCE("[INSTANCE]",/^\s*\[INSTANCE\]\s*$/),
    KEY("[KEY]",/^\s*\[KEY\]\s*$/),
    SOURCE("[SOURCE]", /^\s*\[SOURCE\]\s*$/),
    GROUP("[GROUP]", /^\s*\[GROUP\]\s*$/),
    TEMPLATE("[TEMPLATE]", /^\s*\[TEMPLATE\]\s*$/)

    def tag
    def matcher

    SectionMark(tag, matcher) {
        this.tag = tag
        this.matcher = matcher
    }

    @Override
    String toString() {
        return this.tag
    }
}