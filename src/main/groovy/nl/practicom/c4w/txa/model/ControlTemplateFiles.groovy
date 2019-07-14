package nl.practicom.c4w.txa.model;

/**
 * This model class describes a single file tree for a specific control template instance.
 *
 * The complete list of possible subsections and keywords is:
 *
 * [PRIMARY]
 * [INSTANCE]
 * [KEY]
 * [SECONDARY]  optional, repeatable
 * [OTHERS]      optional
 *
 * Example 1: single primary file
 *
 * [PRIMARY]
 * Herkomst
 * [INSTANCE]
 * 18
 * [KEY]
 * HER:KeyHerkomst
 *
 * Example 2: primary with a secondary
 *
 * [PRIMARY]
 * ProdDebiteuren
 * [INSTANCE]
 * 12
 * [KEY]
 * PDEB:KeyGroepNummerDebiteur
 * [SECONDARY]
 * Debiteuren ProdDebiteuren
 *
 * Example 3: multi-level tree
 *
 * [PRIMARY]
 * Producten
 * [INSTANCE]
 * 2
 * [SECONDARY]
 * Voorraadlocaties Producten
 * ProdAfdelingen Producten
 * Bedrijfsafdelingen ProdAfdelingen

 *
 * Example 4: multi-level tree, inner join
 * [PRIMARY]
 * VerkRegels
 * [INSTANCE]
 * 0
 * [SECONDARY]
 * Verkopen VerkRegels INNER
 * Debiteuren Verkopen

 */
class ControlTemplateFiles {

    /**
     * The beginning of the subsection which describes a single primary file tree for a control template in this procedure (required).
     * There may be a [PRIMARY] subsection for each control template in the procedure.
     * Primary simply means that this is the main file processed by the associated control template.
     * Any other files processed by the control template are dependent files.
     */
    def primaryFile

    /**
     * Introduces the instance number of the control template for which this file is primary (required).
     * See Common Subsections—[ADDITION] for more information on instance numbers.
     */
    def controlTemplateInstance

    /**
     * the key used to access this file (optional).
     */
    def accessKey

    /**
     * Introduces the child and the parent file accessed by this control template (optional).
     * This subsection is repeated for each related file.
     * The tuples contain: (secondary file, primary file, join type)
     */
    List<Tuple> secondaryFiles

}
