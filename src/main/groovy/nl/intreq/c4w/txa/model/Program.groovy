package nl.intreq.c4w.txa.model

/**
 * The program section is required and appears only once, after the [APPLICATION] section of each .TXA file.
 * The program section begins with [PROGRAM] and ends with [END].
 * This section contains the following keywords and subsections that pertain to the source file that
 * contains the PROGRAM statement:
 *
 * [PROGRAM]
 *   NAME           optional
 *   INCLUDE        optional
 *   NOPOPULATE     optional
 *   [COMMON]
 *     DESCRIPTION   optional
 *     LONG
 *     FROM
 *     [DATA]
 *     [FILES]
 *     [PROMPTS]
 *     [EMBED]
 *     [ADDITION]    optional  repeatable
 *   [PROCEDURE]    optional  repeatable
 * [END]
 *
 *
 */
class Program implements TxaRoot {

    /**
     * The name of the source file that contains the PROGRAM statement for this application (optional).
     * If omitted it defaults to the application name.
     * For example:
     *   NAME ‘TUTORIAL.CLW’
     */
    String sourceFilename

    /**
     * The name of a source file that is included in the data declaration section of the
     * program source file (optional).
     * For example:
     *    INCLUDE ‘EQUATES.CLW’
     */
    String includeFilename

    /**
     * The Application Generator may not store procedures in this source file (optional).
     * This is typically present for external modules.
     * For example:
     *     NOPOPULATE
     */
    Boolean noPopulate

    /**
     * Information that fully defines a procedure (optional).
     * The procedure subsection may be repeated for each procedure in the module.
     * The information stored in this subsection applies only to the procedure identified by the NAME keyword.
     *
     */
    List<Procedure> procedures
}
