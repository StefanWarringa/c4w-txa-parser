package nl.intreq.c4w.txa.model

/**
 * For each memory variable defined, there is a series of optional subsections and keywords that fully describe
 * the variable, as well as any default formatting conventions the Application Generator is expected to follow.
 * The complete list of possible subsections and keywords is:
 *
 * [DATA]
 *  [LONGDESC]         optional
 *  [USEROPTION]       optional
 *  [SCREENCONTROLS]   optional
 *  [REPORTCONTROLS]   optional
 *  field definition
 *  keyword list        optional
 *
 *  Example:
 *
 *  [DATA]
 *        [SCREENCONTROLS]
 *        ! PROMPT(‘CurrentTab:’),USE(?CurrentTab:Prompt)
 *        ! ENTRY(@s80),USE(CurrentTab)
 *        [REPORTCONTROLS]
 *        ! STRING(@s80),USE(CurrentTab)
 *        CurrentTab               STRING(80) ! Tab selected by user
 *        !!> IDENT(4294967206),PROMPT(‘CurrentTab:’),HEADER(‘CurrentTab’),
 *        PICTURE(@s80)
 *        [SCREENCONTROLS]
 */
class VariableDefinition {

    /**
     * Up to thirteen (13) lines of text, up to seventy-five (75) characters in length each (optional).
     * Each line of text begins with an exclamation point ( ! ). Comes from the Comments tab of the
     * Field Properties dialog.
     *
     * For example:
     *        [LONGDESC]
     *        !CurrentTab is used internally by the template !generated code to store
     *        the number/id of the !TAB control that has focus.
     */
    String longDescription

    /**
     * Up to thirteen (13) lines of text up to seventy-five (75) characters in length each (optional).
     * Each line of text begins with an exclamation point ( ! ). The text is available to templates.
     * See the EXTRACT procedure in the Template Language Reference.
     * Comes from the Options tab of the Field Properties dialog.
     *
     * For example:
     *        [USEROPTION]
     *        !ThirdPartyTemplateAttribute:Details(on)
     *        !ThirdPartyTemplateAttribute:WizardHelp(off)
     */
    List<String> userOptions

    /**
     * The beginning of the subsection that describes the default window controls used to manage the memory variable
     * (optional). Use the Window tab of the Field Properties dialog to set the default controls.
     * Following [SCREENCONTROLS], an exclamation point ( ! ) marks the beginning of a control declaration.
     * The control declaration is the Clarion language statement that defines a window control.
     * There may be several controls associated with the memory variable, so there may be several control declarations,
     * beginning immediately after [SCREENCONTROLS] and continuing until the next subsection, usually [REPORTCONTROLS].
     *
     * For example:
     *        [SCREENCONTROLS]
     *        ! PROMPT(‘CurrentTab:’),USE(?CurrentTab:Prompt)
     *        ! ENTRY(@s80),USE(CurrentTab)
     */
    List<String> screenControls

    /**
     * The beginning of the subsection that describes the default report controls used to manage the memory variable
     * (optional). Use the Report tab of the Field Properties dialog to set the default controls.
     * Following [REPORTCONTROLS], an exclamation point ( ! ) marks the beginning of a control declaration. The control
     * declaration is the Clarion language statement that defines a report control. There may be several controls
     * associated with the memory variable, so there may be several control declarations, beginning immediately after
     * [REPORTCONTROLS] and continuing until the field definition begins.
     *
     * For example:
     *        [REPORTCONTROLS]
     *        ! STRING(@s80),USE(CurrentTab)
     */
    List<String> reportControls

    /**
     * The keywords that specify various attributes of the memory variable (optional). The list begins with !!>.
     * The keywords in this list are set using the Field Properties dialog. Many of the keywords correspond directly
     * to Clarion language keywords.
     */
    List<String> keywords


    // -- Start of field definition --

    /**
     * Lists the Clarion language field declaration (required). Optionally includes a text description of up to
     * 40 characters. The text description begins with an exclamation point ( ! ).
     *
     * For example:
     *  CurrentTab   STRING(80) !user selected tab
     */

    String name
    String type
    String description

    // -- End of field definition --

}
