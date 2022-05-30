package nl.intreq.c4w.txa.transform

interface TxaSectionHandler {
    /**
     * Handle start of section
     * @param context current section context
     * @param section the current section read from the txa
     */
    void onSectionStart(TxaContext context, SectionMark section)
    /**
     * Handle end of section
     * @param context the context of the session being terminated
     * @param section the section being ended
     */
    void onSectionEnd(TxaContext context, SectionMark section)

}