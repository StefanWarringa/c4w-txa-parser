package nl.practicom.c4w.txa.parser


enum SectionMark {
    APPLICATION("[APPLICATION]",/^\[APPLICATION\\]$/),
    PROGRAM("[PROGRAM]",/^\[PROGRAM\\]$/),
    MODULE("[MODULE",/^\[MODULE\]$/),
    PROCEDURE("[PROCEDURE]",/^\[PROCEDURE\]$/),
    COMMON("[COMMON]",/^\[COMMON\]$/),
    WINDOW("[WINDOW]",/^\[WINDOW\]$/),
    ADDITION("[ADDITION]",/^\[ADDITION\]$/),
    EMBED("[EMBED]",/^\[EMBED\]$/),
    PROMPTS("[PROMPTS]",/^\[PROMPTS\]$/),
    SCREENCONTROLS("[SCREENCONTROLS]",/^\[SCREENCONTROLS\]$/),
    REPORTCONTROLS("[REPORTCONTROLS]",/^\[REPORTCONTROLS\]$/),
    FILES("[FILES]",/^\[FILES\]$/),
    OTHERS("[OTHERS]",/^\[OTHERS\]$/),
    INSTANCES("[INSTANCES]",/^\[INSTANCES\]$/),
    DEFINITION("[DEFINITION]",/^\[DEFINITION\]$/),
    SECTIONEND("[END]",/^\[END\]$/),
    FIELDPROMPT("[FIELDPROMPT]",/^\[FIELDPROMPT\]$/),
    CALLS("[CALLS]",/^\[CALLS\]$/),
    FORM("[FORM]",/^\[FORM\]$/),
    REPORT("[REPORT]",/^\[REPORT\]$/),
    FORMULA("[FORMULA]",/^\[FORMULA\]$/),
    DATA("[DATA]",/^\[DATA\]$/),
    PRIMARY("[PRIMARY]",/^\[PRIMARY\]$/),
    SECONDARY("[SECONDARY]",/^\[SECONDARY\]$/),
    INSTANCE("[INSTANCE]",/^\[INSTANCE\]$/),
    KEY("[KEY]",/^\[KEY\]$/),
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