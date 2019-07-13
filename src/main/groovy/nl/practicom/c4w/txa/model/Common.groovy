package nl.practicom.c4w.txa.model

class Common {
    /**
     * Up to 40 characters of text describing the application, program, module, or procedure (optional).
     *
     * For example:
     *      DESCRIPTION ‘Print dynamic label report’
     */
    String description

    /**
     * Up to 1000 characters of text describing the application, program, module, or procedure (optional).
     * The text is actually split into several lines that are concatenated together as they are read.
     */
    String longDescription

    /**
     * The procedure may be viewed, but not modified from the Clarion for Windows environment (optional).
     * Only allowed in a [PROCEDURE] subsection. READONLY cannot currently be added to a procedure by
     * the environment, but is provided for future use so that SoftVelocity’s developers can implement
     * multi-developer environments that allow a procedure to be “checked out” and “checked in” in order
     * to preserve code integrity.
     *
     * For example:
     *      READONLY
     */
    Boolean isReadOnly

    /**
     * The name of the template class for an application, or the name of the template class and the
     * specific template from which the program, module, or procedure is generated (optional -
     * can only be omitted for a ToDo procedure).
     */
    String from

    /**
     * The date and time the procedure was last modified. For example:
     * MODIFIED ‘1998/07/02’ ‘10:43:32’
     */
    String modified

    /**
     * The data subsection is an optional part of the [COMMON] subsection.
     * It may contain several subsections and keywords that describe each memory variable defined for this procedure,
     * module, program, or application.
     */
    List<VariableDefinition> data
}
