package nl.practicom.c4w.txa.model

/**
 * The addition subsection is an optional part of the common subsection and appears once for each code template used.
 * It may contain several subsections and keywords that describe each control, code, and extension template defined
 * for this procedure, module, or program. See Using Control, Code, and Extension Templates in the User’s Guide.
 *
 * The [ADDITION] subsection may contain the following subsections and keywords:
 *
 * [ADDITION]        repeatable
 *   NAME
 *   [FIELDPROMPT]   optional
 *   [INSTANCE]      repeatable
 *     INSTANCE
 *   PARENT          optional
 *   OWNER           optional
 *   PROCPROP        optional
 *   [PROMPTS]       optional
 *
 *  Example:
 *
 *  [ADDITION]
 *        NAME Clarion BrowseUpdateButtons
 *        [FIELDPROMPT]
 *        %MadeItUp LONG  (1)
 *        [INSTANCE]
 *        INSTANCE 2
 *        PARENT 1
 *        OWNER 9
 *        PROCPROP
 *        [PROMPTS]
 *        %UpdateProcedure PROCEDURE  (UpdatePhones)
 *        %EditViaPopup LONG  (1)
 *              .
 *        [INSTANCE]
 *        INSTANCE 4
 *        PARENT 3
 *        .
 */
class Addition extends TemplatePrompts {

    /**
     * Identifies the template class and the specific template invoked (required).
     * Appears once for each [ADDITION] subsection.
     * For example:
     *    NAME Clarion BrowseUpdateButtons
     */
    String templateClass
    String templateName

    /**
     * Indicates a prompt and its associated type and value (optional).
     * This is only generated if you use a #FIELD statement in your templates.
     * The prompts begin on the following line.
     *
     * Example:
     *
     * [FIELDPROMPT]
     *        %MadeItUp LONG  (1)
     */
    List<Prompt> fieldPrompts

    /**
     * Indicates the instance number (identification number) of this particular template addition.
     * For example:
     *    [INSTANCE]
     *    INSTANCE 2
     */
    Long instanceId

    /**
     * Indicates this control template depends on another control template (optional).
     * PARENT is followed by the instance number of the control template upon which this control template depends.
     * For example:
     *   PARENT 1
     */
    Long parentId

    /**
     * NOT DOCUMENTED
     */
    long ownerId

    /**
     * Means the prompts for this control template are shown in the Procedure Properties dialog (optional).
     * If PROCPROP is absent, the prompts will not be displayed in the Procedure Properties dialog.
     * For example:
     *   PROCPROP
     */
    Boolean showPrompts

}
