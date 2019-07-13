package nl.practicom.c4w.txa.model

abstract class Prompt {
    enum PromptType {
        PICTURE,
        LONGFORMAT,
        REALFORMAT,
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
