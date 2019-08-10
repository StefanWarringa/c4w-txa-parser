package nl.practicom.c4w.txa.transform

interface TxaContentHandler {

    /**
     * Hook to perform initialization
     * @param context initial processing context
     */
    void onProcessingStart(TxaContext context)

    /**
     * Handle start of section
     * @param context current section context
     * @param section the current section read from the txa
     */
    void onSectionStart(TxaContext context, SectionMark section)

    /**
     * Handle a single line of the txa within the current section
     * @param context the current context of the line being read
     * @param section the current section this line belongs to
     * @param content the line read from the txa
     */
    void onSectionContent(TxaContext context, SectionMark section, String content)

    /**
     * Handle end of section
     * @param context the context of the session being terminated
     * @param section the section being ended
     */
    void onSectionEnd(TxaContext context, SectionMark section)

    /**
     * Hook to perform processing when complete file has been processed
     * @param context the final processing context
     */
    void onProcessingFinished(TxaContext context)
}