package nl.intreq.c4w.txa.model

abstract class Prompt {
    enum PromptType {
        PICTURE,
        LONG,
        REAL,
        FILE,
        FIELD,
        KEY,
        COMPONENT,
        PROCEDURE,
        DEFAULT
    }

    String name
    PromptType type
}
