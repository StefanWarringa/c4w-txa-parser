package nl.practicom.c4w.txa.meta

import nl.practicom.c4w.txa.parser.SectionMark

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
            if ( s == null ) return false
            return s ==~ /^\s*\[[A-Z]+\]\s*$/
        }

        static boolean isSectionStart(String s, SectionMark mark) {
            if ( s == null ) return false
            if ( !s.isSectionMark() ) return false
            if ( mark.matcher != null) {
                return s ==~ mark.matcher
            } else {
                return s == mark.tag
            }
        }

        static boolean isSectionEnd(String s){
            if ( s == null ) return false
            return s == "[END]"
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

        static void initialize(){
            String.mixin ClarionStringMixins
        }
}
