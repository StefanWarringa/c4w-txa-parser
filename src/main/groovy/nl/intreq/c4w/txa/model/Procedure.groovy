package nl.intreq.c4w.txa.model

/**
 * [PROCEDURE]    optional  repeatable
 *   NAME          optional
 *   PROTOTYPE     optional
 *   [COMMON]
 *    DESCRIPTION  optional
 *    LONG         optional
 *    READONLY     optional
 *    FROM
 *    [DATA]
 *    [FILES]      optional
 *    [PROMPTS]
 *    [EMBED]      optional
 *    [ADDITION]   optional  repeatable
 * [CALLS]
 * [WINDOW]
 * [REPORT]
 * [FORMULA]     optional
 *
 */

class Procedure implements TxaRoot {
    String name
    String prototype
    Common common
    Window window
    Report report
    List<String> calls
    List<Formula> formulas
}
