package nl.practicom.c4w.txa.meta

import nl.practicom.c4w.txa.parser.SectionMark
import static nl.practicom.c4w.txa.parser.SectionMark.*

class ClarionStringMixins {

    static SectionMark asSectionMark(String s) {
        if (s == null ) return null
        def mark = s.trim()
        if ( mark.startsWith('[')) mark = mark.substring(1)
        if ( mark.endsWith(']')) mark = mark.substring(0,mark.length()-1)
        try {
            return mark as SectionMark
        } catch ( Exception x){
            return null
        }
    }

    static isContinued(String s) {
        return s == null ? false : s.endsWith('|')
    }

    static join(String self, String other) {
        if ( self.isContinued() ) {
            return self.substring(0,self.length()-1) + other
        } else {
            self + other
        }
    }

    static boolean isSectionMark(String s){
        SectionMark.isSectionMark(s)
    }

    static boolean isSectionStart(String s, SectionMark mark) {
        if ( s == null ) return false
        if ( mark == null ) return isSectionMark(s)
        if ( !s.isSectionMark() ) return false
        if ( mark.matcher != null) {
            return s ==~ mark.matcher
        } else {
            return s == mark.tag
        }
    }

    static boolean isSectionEnd(String s){
        s == null ? false : s ==~ END.matcher
    }

    static Tuple asAttribute(String s) {
        def matcher = ( s =~ /(\w+)\W(.*)/ )
        if (matcher.hasGroup()){
            return new Tuple(matcher[0][1] as String , matcher[0][2] as String )
        } else {
            return null
        }
    }

    static fromClarionStringList(String s){
        return s.replace('(','').replace(')','').split(',')
    }

    static trimLines(String s){
        s.toLineArray()*.trim().join(System.lineSeparator())
    }

    static List<String> toLineArray(String s){
        s == null ? [] : s.split(System.lineSeparator())
    }

    static trimEOL(String s){
        def content = '' << s.reverse()
        // On windoze EOL is 2 chars: \cr\lf!
        def EOL = System.lineSeparator().reverse()

        while ( content[0..EOL.size()-1] == EOL){
            content.delete(0,EOL.size())
        }
        content.trimToSize()
        content.reverse().toString()
    }

    static lineCount(String s){
        s.split(System.lineSeparator()).size()
    }

    static ux2dos(String s) {
        return s == null ? null : s.replaceAll(~/\.*(?<!\r)\n/, '\r\n')
    }

    static void initialize(){
        String.mixin ClarionStringMixins
    }
}