package nl.intreq.c4w.txa.model

/**
 * The application section is required and appears only once at the top of each .TXA file
 * (unless only a portion of the application is exported, e.g. a .TXA file may contain a
 * single module or procedure, in which case the file begins with [MODULE] or [PROCEDURE]
 * respectively).
 *
 * This section begins with [APPLICATION] and ends with the beginning of the [PROJECT] section.
 * The application section contains the following keywords and subsections that pertain to the entire application:
 *
 * [APPLICATION]
 *  VERSION      optional
 *  HLP          optional
 *  DICTIONARY   optional
 *  PROCEDURE    optional
 *  [COMMON]
 *    DESCRIPTION optional
 *    LONG        optional
 *    FROM
 *    [DATA]
 *    [FILES]     optional
 *    [PROMPTS]
 *    [EMBED]     optional
 *    [ADDITION]  optional  repeatable
 * [PERSIST]
 *
 * Example:
 *
 *   [APPLICATION]
 *   VERSION 10
 *   HLP ‘C:\C60\.APPS\MY.APP.HLP’
 *   DICTIONARY ‘TUTORIAL.DCT’
 *   PROCEDURE Main
 *   [COMMON]
 *   ...
 */
class Application implements TxaRoot {
    /**
     * The version number of the application (optional).
     * This value reflects the version of generator, and
     * changes when the TXA format changes.
     * Example:
     *
     * VERSION 10.
     */
    String version

    /**
     * The Windows help file called by the application (optional).
     * The file name may be fully qualified or not. If not, Clarion searches
     * in the current directory, the system path, then in paths specified by
     * the redirection file.
     *
     * Example:
     *
     * HLP ‘C:\C60\.APPS\MY.APP.HLP’
     */
    String help

    /**
     * The data dictionary file used by the application (optional).
     * The file name may be fully qualified or not. If not,
     * Clarion searches the paths specified by the redirection file.
     * Example:
     *
     * DICTIONARY ‘TUTORIAL.DCT’
     */
    String dictionary

    /**
     * The name of the first procedure in the application (optional—- no meaning for a .LIB or .DLL).
     * This procedure calls all other procedures in the application, either directly, or indirectly.
     * For example:
     *
     * PROCEDURE Main
     */
    String firstProcedure

    /**
     * A instance of the common subsection (optional).
     * See Common class for a description.
     */
    Common common

    /**
     * Information about the application that is “remembered” across sessions (required).
     * Although these items appear in .TXA [PROMPT] format, they do not appear to the
     * developer as prompts, rather, they are #DECLAREd in the application template with
     * the SAVE attribute, which causes them to be saved in the .APP file so they are
     * available for each new session.
     */
    List<Prompt> persistentData
}
