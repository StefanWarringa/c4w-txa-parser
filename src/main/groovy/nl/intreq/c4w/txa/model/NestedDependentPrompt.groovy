package nl.intreq.c4w.txa.model

class NestedDependentPrompt {
    String parentSymbol
    String ancestorSymbol
    List<Tuple<String,String,String>> options

    NestedDependentPrompt(String parentSymbol, String ancestorSymbol){
        this.parentSymbol = parentSymbol
        this.ancestorSymbol = ancestorSymbol
        this.options = new ArrayList<>();
    }

    public void addOption(String parentValue, String ancestorValue, String value){
        options.add(new Tuple(parentValue,ancestorValue,value))
    }
}
