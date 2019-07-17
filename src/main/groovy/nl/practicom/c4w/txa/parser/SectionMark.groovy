package nl.practicom.c4w.txa.parser

/**
 * The section marks are the pinnacle point for correct functioning of the parser
 * Note that we allow optional whitespace before and after the section marker
 * for more robustness although in the TXA no whitespace seems to be generated.
 */
enum SectionMark {
    ADDITION("[ADDITION]",/^\s*\[ADDITION\]\s*$/),
    APPLICATION("[APPLICATION]",/^\s*\[APPLICATION\\]\s*$/),
    CALLS("[CALLS]",/^\s*\[CALLS\]\s*$/),
    COMMON("[COMMON]",/^\s*\[COMMON\]\s*$/),
    DATA("[DATA]",/^\s*\[DATA\]\s*$/),
    DEFINITION("[DEFINITION]",/^\s*\[DEFINITION\]\s*$/),
    EMBED("[EMBED]",/^\s*\[EMBED\]\s*$/),
    FIELDPROMPT("[FIELDPROMPT]",/^\s*\[FIELDPROMPT\]\s*$/),
    FILES("[FILES]",/^\s*\[FILES\]\s*$/),
    FORM("[FORM]",/^\s*\[FORM\]\s*$/),
    FORMULA("[FORMULA]",/^\s*\[FORMULA\]\s*$/),
    GROUP("[GROUP]", /^\s*\[GROUP\]\s*$/),
    INSTANCE("[INSTANCE]",/^\s*\[INSTANCE\]\s*$/),
    INSTANCES("[INSTANCES]",/^\s*\[INSTANCES\]\s*$/),
    KEY("[KEY]",/^\s*\[KEY\]\s*$/),
    MODULE("[MODULE",/^\s*\[MODULE\]\s*$/),
    OTHERS("[OTHERS]",/^\s*\[OTHERS\]\s*$/),
    PRIMARY("[PRIMARY]",/^\s*\[PRIMARY\]\s*$/),
    PROCEDURE("[PROCEDURE]",/^\[PROCEDURE\]\s*$/),
    PROGRAM("[PROGRAM]",/^\s*\[PROGRAM\\]\s*$/),
    PROMPTS("[PROMPTS]",/^\s*\[PROMPTS\]$/),
    REPORT("[REPORT]",/^\s*\[REPORT\]\s*$/),
    REPORTCONTROLS("[REPORTCONTROLS]",/^\s*\[REPORTCONTROLS\]\s*$/),
    SECONDARY("[SECONDARY]",/^\s*\[SECONDARY\]\s*$/),
    SECTIONEND("[END]",/^\s*\[END\]\s*$/),
    SCREENCONTROLS("[SCREENCONTROLS]",/^\s*\[SCREENCONTROLS\]\s*$/),
    SOURCE("[SOURCE]", /^\s*\[SOURCE\]\s*$/),
    TEMPLATE("[TEMPLATE]", /^\s*\[TEMPLATE\]\s*$/),
    WINDOW("[WINDOW]",/^\s*\[WINDOW\]\s*$/)

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