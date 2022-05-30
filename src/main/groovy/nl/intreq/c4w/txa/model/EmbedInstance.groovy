package nl.intreq.c4w.txa.model

/**
 * The embed subsection is an optional part of the common subsection.
 * It may contain several subsections and keywords that describe each embed point defined for this procedure,
 * module, or program with the Embedded Source dialog.
 *
 * The [EMBED] subsection may contain the following subsections and keywords:
 *
 * [EMBED]
 * EMBED
 *   [INSTANCES]
 *     WHEN
 *      [DEFINITION]
 *        [SOURCE]
 *        [TEMPLATE]
 *        [PROCEDURE]  optional, repeatable
 *        [GROUP]      optional, repeatable
 *        INSTANCE 4
 *      [END]
 *   [END]
 * [END]
 *
 * This model class represents a single instance within the definition section an the embed point.
 *
 */
class EmbedInstance {

    enum SourceType {
        SOURCE,   // free-form clarion sourcecode
        TEMPLATE, // free-form text including template statements
        GROUP,    // reference to code template that will generate the source
        PROCEDURE // embedded procedure call
    }

    /**
     * Identifies the embed point (required).
     * Example:
     * EMBED %ControlPreEventHandling
     */
    String embedPoint

    /**
     * The sourceType of instance (required)
     */
    SourceType sourceType

    /**
     * Priority of the instance within the definition.
     * For GROUP and PROCEDURE this is specified as a seperate attribute, for example:
     *
     * [PROCEDURE]
     * BrowseKlachtenDebiteur()
     * PRIORITY 3950
     *
     * [GROUP]
     * PRIORITY 4000
     * INSTANCE 6
     * [END]
     *
     * For TEMPLATE and SOURCE this is included as part of the sourcecode, for example:
     * [SOURCE]
     * PROPERTY:BEGIN
     * PRIORITY 8001
     * PROPERTY:END
     */
    Integer priority

    /**
     * Indicates the location within the embed point where this instance is to be embedded (required).
     *
     * For example:
     *   [INSTANCES]
     *   WHEN ‘?Change:2’
     *      [INSTANCES]
     *         WHEN 'Accept'
     *
     * results in embedLocation = ['?Change:2','Accept']
     */
    List<String> embedLocation

    /**
     * Identifier of the code template (addition) for a GROUP source sourceType.
     * For example:
     *
     * [GROUP]
     * INSTANCE 4
     */
    Integer instanceId

    /**
     * Name of the procedure for a PROCEDURE source sourceType.
     * Example:
     *
     * [PROCEDURE]
     * AanmakenCrediteurenBellijst()
     */
    String procedureName

    /**
     * Parameters passed in the embedded procedure call of a PROCEDURE source sourceType
     */
    String procedureParams

    /**
     * Holds the statements for SOURCE and TEMPLATE embeds
     * These statements are the raw data as present in the TXA
     */
    StringBuffer source
}
