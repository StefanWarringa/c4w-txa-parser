package nl.practicom.c4w.txa.meta

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ClarionDateMixins {

    final static clarionDateFormat = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    final static clarionTimeFormat = DateTimeFormatter.ofPattern("HH:mm:ss")

    static String toClarionDateTimeString(LocalDateTime self){
        return "'${self.format(clarionDateFormat)}' ' ${self.format(clarionTimeFormat)}'"
    }

    static void initialize(){
        LocalDateTime.mixin ClarionDateMixins
    }
}
