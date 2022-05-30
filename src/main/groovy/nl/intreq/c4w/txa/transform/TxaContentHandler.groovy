package nl.intreq.c4w.txa.transform

abstract interface TxaContentHandler {
    /**
     * Hook to perform initialization
     * @param context initial processing context
     */
    void onProcessingStart(TxaContext context)

    /**
     * Hook to perform processing when complete file has been processed
     * @param context the final processing context
     */
    void onProcessingFinished(TxaContext context)
}