package nl.practicom.c4w.txa.transform

/**
 * The section marks are the pinnacle point for correct functioning of the parser
 * Note that we allow optional whitespace before and after the section marker
 * for more robustness although in the TXA no whitespace seems to be generated.
 */
enum SectionMark {
    ADDITION, APPLICATION, CALLS, COMMON, DATA, DEFINITION, EMBED, FIELDPROMPT, FILES, FORM, FORMULA, GROUP,
    INSTANCE, INSTANCES, KEY, LONGDESC, MODULE, OTHERS, PERSIST, PRIMARY, PROCEDURE, PROGRAM, PROJECT,
    PROMPTS, REPORT, REPORTCONTROLS, SECONDARY, END, SCREENCONTROLS, SOURCE, TEMPLATE, USEROPTION, WINDOW,
    QUICKCODE

    static requiresExplicitEnd = [PROGRAM, MODULE, EMBED, INSTANCES, DEFINITION]

    final static EnumMap<SectionMark,List<SectionMark>> childSections =
            [
                    (APPLICATION): [PROGRAM, PROJECT, MODULE, COMMON, PERSIST],
                    (PROGRAM)    : [COMMON],
                    (PROJECT)    : [],
                    (MODULE)     : [COMMON, PROCEDURE],
                    (PROCEDURE)  : [COMMON, CALLS, WINDOW, REPORT, FORM, FORMULA],
                    (COMMON)     : [DATA, FILES, PROMPTS, EMBED, ADDITION],
                    (DATA)       : [LONGDESC, USEROPTION, SCREENCONTROLS, REPORTCONTROLS],
                    (FILES)      : [PRIMARY, OTHERS],
                    (PRIMARY)    : [INSTANCE, KEY, SECONDARY],
                    (PROMPTS)    : [],
                    (EMBED)      : [INSTANCES, DEFINITION],
                    (INSTANCES)  : [INSTANCES, DEFINITION],
                    (INSTANCE)   : [],
                    (DEFINITION) : [SOURCE, GROUP, PROCEDURE, TEMPLATE],
                    (ADDITION)   : [FIELDPROMPT, INSTANCE, PROMPTS],
                    (SCREENCONTROLS) : [QUICKCODE],
                    (REPORTCONTROLS) : [QUICKCODE]
            ]

    static boolean isSectionMark(String s){
        s== null ? false : s ==~ /^\s*\[[A-Z]+]\s*$/
    }

    def tag
    def matcher

    SectionMark(tag=null, matcher=null) {
        this.tag = tag != null ? tag : "[${this.name() as String}]"
        this.matcher = matcher != null ? matcher : /^\s*\[${this.name()}\]\s*$/
    }

    def hasChild(SectionMark other){
        if ( other == null ){
            return false
        }
        def children = SectionMark.childSections.get(this)
        if ( children == null || children.isEmpty()){
            return false
        }
        return children.contains(other)
    }

    def requiresExplicitEnd(){
        requiresExplicitEnd.contains(this)
    }

    @Override
    String toString() {
        return this.tag
    }
}