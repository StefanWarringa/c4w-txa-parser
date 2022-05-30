package nl.intreq.c4w.txa.model

/**
 * The structure and syntax of the module section is identical to that of the program section.
 * The module section is optional and may be repeated as many times as necessary.
 * The section begins with [MODULE] and ends with [END].
 *
 * Although identical in syntax and structure, the module subsection differs in the scope of it’s applicability.
 * The module section is repeated once for each module in the application, and, the information it contains
 * applies only to the source file identified by its NAME keyword, that is, only to those procedures that reside
 * within this module. Data defined in its [DATA] section is “module” data, and is available to all procedures
 * in the module.
 *
 * [MODULE]
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
 */
class Module implements TxaRoot {
}
