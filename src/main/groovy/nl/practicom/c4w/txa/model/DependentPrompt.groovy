package nl.practicom.c4w.txa.model

class DependentPrompt extends Prompt {
    String parentSymbol

    Map<String,List<String>> options

    DependentPrompt(){
        this.options = [:]
    }

    DependentPrompt addOption(String parentValue, String value){
        this.options.put(parentValue, [value])
        return this
    }
}
