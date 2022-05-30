package nl.intreq.c4w.txa.model

/**
 * The project section is always present and appears only once, after the [APPLICATION] section of each .TXA file.
 * The project section begins with [PROJECT] and ends with [PROGRAM]. It is generated (exported) automatically
 * for each application.
 * This section contains the project system settings specified for this application.
 * It is provided so that project system settings are preserved when you export an application to a .TXA file,
 * then import the .TXA back into an .APP file.
 *
 * Example:
 *
 * [PROJECT]
 *  -- Generator
 *  #noedit
 *  #system win
 *  #model clarion dll
 *  #pragma debug(vid=>full)
 *  #compile BIG_RD.CLW /define(GENERATED=>on)-- GENERATED
 *  #compile BIG_RU.CLW /define(GENERATED=>on)-- GENERATED
 *  #compile BIG_SF.CLW /define(GENERATED=>on)-- GENERATED
 *  #compile ResCode.Clw /define(GENERATED=>on)-- GENERATED
 *  #compile BIG.clw /define(GENERATED=>on)-- GENERATED
 *  #compile BIG001.clw /define(GENERATED=>on)-- GENERATED
 *  #compile BIG002.clw /define(GENERATED=>on)-- GENERATED
 *  #pragma link(C%L%2AS4%S%.LIB)-- GENERATED
 *  #link BIG.EXE
 *  [PROGRAM]
 */
class Project {
}
