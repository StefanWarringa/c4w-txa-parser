package nl.practicom.c4w.txa.model

class DependentPrompt extends Prompt {
    String parentSymbol
    List<Tuple<String,String>> options

    public DependentPrompt(String parentSymbol){
        this.parentSymbol = parentSymbol
        this.options = new ArrayList<>()
    }

    public DependentPrompt addOption(String parentValue, String value){
        this.options.add(new Tuple(parentValue,value))
    }
}
