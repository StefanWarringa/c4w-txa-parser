package nl.intreq.c4w.txa.transform

/**
 * The TXA format and the Clarion language allow long lines to be
 * split into multiple lines using the line continuation marker '|'.
 * Therefore we can distinguish between the physical (raw) lines
 * read from the txa and the 'logical' lines that are reconstructed
 * by rejoining physical lines.
 * Content handlers that want to receive physical lines instead of
 * raw lines should implement this interface.
 */
interface TxaRawContentHandler {

    /**
     * Handle a single raw line of the txa within the current section
     * @param context the current context of the line being read
     * @param section the current section this line belongs to
     * @param content the line read from the txa
     */
    void onSectionContent(TxaContext context, SectionMark section, String content)


}