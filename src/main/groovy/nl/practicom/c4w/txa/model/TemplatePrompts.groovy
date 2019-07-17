package nl.practicom.c4w.txa.model

/**
 * The list of prompts and the values supplied by the developer for each prompt (optional) for a template.
 * The prompts begin on the following line and continue until the beginning of the next .TXA subsection.
 * For example:
 *    [PROMPTS]
 *     %UpdateProcedure PROCEDURE  (UpdatePhones)
 *     %EditViaPopup LONG  (1)
 */
trait TemplatePrompts {

    List<Prompt> prompts

}