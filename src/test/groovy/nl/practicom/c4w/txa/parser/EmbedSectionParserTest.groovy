package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.meta.ClarionStringMixins
import nl.practicom.c4w.txa.model.Common
import nl.practicom.c4w.txa.model.EmbedInstance
import nl.practicom.c4w.txa.transform.TxaReader

class EmbedSectionParserTest extends GroovyTestCase {

    void setUp() {
        super.setUp()
        ClarionStringMixins.initialize()
    }

    void testEmbedWithSingleSourceInstance() {
        def content = '''
            [EMBED]
              EMBED %ProcedureRoutines
              [DEFINITION]
                [SOURCE]
                  PROPERTY:BEGIN
                  PRIORITY 4000
                  PROPERTY:END
                  BepalenRoute ROUTINE
              [END]
            [END]
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()

        def common = new Common()
        new EmbedSectionParser(common).parse(reader)
        assert common.embeds != null
        assert common.embeds.size() == 1
        (common.embeds[0] as EmbedInstance).with {
            assert embedPoint == "%ProcedureRoutines"
            assert sourceType == EmbedInstance.SourceType.SOURCE
            assert instanceId == null
            assert procedureName == null
            assert procedureParams == null
            assert embedLocation == null
            assert priority == 4000
        }
    }

    void testEmbedWithSingleGroupInstance() {
        def content = '''
            [EMBED]
              EMBED %ControlEventHandling
              [DEFINITION]
                [GROUP]
                  PRIORITY 10000
                  INSTANCE 5
               [END]
            [END]
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()

        def common = new Common()
        new EmbedSectionParser(common).parse(reader)
        assert common.embeds != null
        assert common.embeds.size() == 1
        (common.embeds[0] as EmbedInstance).with {
            assert embedPoint == "%ControlEventHandling"
            assert sourceType == EmbedInstance.SourceType.GROUP
            assert instanceId == 5
            assert procedureName == null
            assert procedureParams == null
            assert embedLocation == null
            assert priority == 10000
        }
    }

    /**
     * A very basic embed point with a single procedure instance
     */
    void testEmbedWithSingleProcedureInstance() {
        def content = '''
            [EMBED]
              EMBED %ControlEventHandling
              [DEFINITION]
                [PROCEDURE]
                  AanmakenBellijst()
                  PRIORITY 2000
               [END]
            [END]
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()

        def common = new Common()
        new EmbedSectionParser(common).parse(reader)
        assert common.embeds != null
        assert common.embeds.size() == 1
        (common.embeds[0] as EmbedInstance).with {
            assert embedPoint == "%ControlEventHandling"
            assert sourceType == EmbedInstance.SourceType.PROCEDURE
            assert procedureName == "AanmakenBellijst"
            assert procedureParams == "()"
            assert instanceId == null
            assert embedLocation == null
            assert priority == 2000
        }
    }

    /**
     * A single embed point can contain multiple definitions
     * even of the same type. Each definition should result
     * in a seperate EmbedInstance instance.
     */
    void testSingleEmbedWithMultipleDefinitions(){
        def content =  ''''
            [EMBED]
              EMBED %ControlEventHandling
              [DEFINITION]
                [PROCEDURE]
                   AanmakenBellijst(123,'abc')
                   PRIORITY 2000
                [SOURCE]
                   PROPERTY:BEGIN
                   PRIORITY 7001
                   PROPERTY:END
                   !Verwijderen ikonatabel
                   Remove(IkonaTabel)
               [SOURCE]
                   PROPERTY:BEGIN
                   PRIORITY 4000
                   PROPERTY:END
                   BepalenRoute ROUTINE
               [GROUP]
                   PRIORITY 4100
                   INSTANCE 5
               [GROUP]
                   PRIORITY 5000
                   INSTANCE 7
              [END]
            [END]
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()

        def common = new Common()
        new EmbedSectionParser(common).parse(reader)
        assert common.embeds != null
        assert common.embeds.size() == 5
        (common.embeds[0] as EmbedInstance).with {
            assert embedPoint == "%ControlEventHandling"
            assert sourceType == EmbedInstance.SourceType.PROCEDURE
            assert procedureName == "AanmakenBellijst"
            assert procedureParams == "(123,'abc')"
            assert instanceId == null
            assert embedLocation == null
            assert priority == 2000
        }
        (common.embeds[1] as EmbedInstance).with {
            assert sourceType == EmbedInstance.SourceType.SOURCE
        }
        (common.embeds[2] as EmbedInstance).with {
            assert sourceType == EmbedInstance.SourceType.SOURCE
        }
        (common.embeds[3] as EmbedInstance).with {
            assert sourceType == EmbedInstance.SourceType.GROUP
        }
        (common.embeds[4] as EmbedInstance).with {
            assert sourceType == EmbedInstance.SourceType.GROUP
        }
    }

    /**
     * An instance can be embedded at a certain position/location within
     * an embed point. This test covers a simple one-level location
     * specifier
     */
    void testSingleInstanceWithLocation(){
        def content = '''
            [EMBED]
              EMBED %ControlEventHandling
                [INSTANCES]
                   WHEN '?OkButton'
                      [DEFINITION]
                         [GROUP]
                         PRIORITY 4100
                         INSTANCE 1
                      [END]
                 [END]
            [END]
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()

        def common = new Common()
        new EmbedSectionParser(common).parse(reader)
        assert common.embeds != null
        assert common.embeds.size() == 1
        (common.embeds[0] as EmbedInstance).with {
            assert embedPoint == "%ControlEventHandling"
            assert sourceType == EmbedInstance.SourceType.GROUP
            assert procedureName == null
            assert procedureParams == null
            assert instanceId == 1
            assert embedLocation == ["?OkButton"]
            assert priority == 4100
        }
    }

    /**
     * Location specifiers can be more complex like the control/event
     * location in this test
     */
    void testSingleInstanceWithNestedLocation(){
        def content = '''
            [EMBED]
                EMBED %ControlEventHandling
                [INSTANCES]
                  WHEN '?OkButton'
                    [INSTANCES]
                       WHEN 'Accepted'
                         [DEFINITION]
                            [GROUP]
                               PRIORITY 4100
                               INSTANCE 1
                         [END]
                    [END]
                [END]
            [END]         
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()

        def common = new Common()
        new EmbedSectionParser(common).parse(reader)
        assert common.embeds != null
        assert common.embeds.size() == 1
        (common.embeds[0] as EmbedInstance).with {
            assert embedPoint == "%ControlEventHandling"
            assert sourceType == EmbedInstance.SourceType.GROUP
            assert procedureName == null
            assert procedureParams == null
            assert instanceId == 1
            assert embedLocation == ["?OkButton","Accepted"]
            assert priority == 4100
        }
    }

    /**
     * Different instances can be nested at different locations
     * inside the embed point
     */
    void testSingleGroupMultipleInstances(){
        def content = '''
            [EMBED]
              EMBED %ControlEventHandling
              [INSTANCES]
                WHEN '?OkButton'
                  [INSTANCES]
                    WHEN 'Accepted'
                    [DEFINITION]
                      [GROUP]
                        PRIORITY 4100
                        INSTANCE 1
                    [END]
                  [END]
                WHEN '?CancelButton'
                  [INSTANCES]
                    WHEN 'Accepted'
                    [DEFINITION]
                      [GROUP]
                        PRIORITY 4000
                        INSTANCE 2
                    [END]
                  [END]
              [END]
            [END]
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()

        def common = new Common()
        new EmbedSectionParser(common).parse(reader)
        assert common.embeds != null
        assert common.embeds.size() == 2
        (common.embeds[0] as EmbedInstance).with {
            assert embedPoint == "%ControlEventHandling"
            assert sourceType == EmbedInstance.SourceType.GROUP
            assert procedureName == null
            assert procedureParams == null
            assert instanceId == 1
            assert embedLocation == ["?OkButton","Accepted"]
            assert priority == 4100
        }
        (common.embeds[1] as EmbedInstance).with {
            assert embedPoint == "%ControlEventHandling"
            assert sourceType == EmbedInstance.SourceType.GROUP
            assert procedureName == null
            assert procedureParams == null
            assert instanceId == 2
            assert embedLocation == ["?CancelButton","Accepted"]
            assert priority == 4000
        }
    }

    /**
     * An embed point can have multiple locations
     * where multiple instances can be added
     */
    void testMultipleInstancesMultipleLocations(){
        def content = '''
            [EMBED]
              EMBED %ControlEventHandling
              [INSTANCES]
                WHEN '?OkButton'
                  [INSTANCES]
                    WHEN 'Accepted'
                    [DEFINITION]
                      [GROUP]
                        PRIORITY 4100
                        INSTANCE 1
                      [SOURCE]
                        PROPERTY:BEGIN
                        PRIORITY 4000
                        PROPERTY:END
                        BepalenRoute ROUTINE
                      [GROUP]
                        PRIORITY 4200
                        INSTANCE 8
                    [END]
                  [END]
                WHEN '?CancelButton'
                  [INSTANCES]
                    WHEN 'Accepted'
                        [DEFINITION]
                           [PROCEDURE]
                              AanmakenBellijst(123,'abc')
                              PRIORITY 2000
                           [GROUP]
                             PRIORITY 4000
                             INSTANCE 2
                        [END]
                  [END]
              [END]
            [END]
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()

        def common = new Common()
        new EmbedSectionParser(common).parse(reader)
        assert common.embeds != null
        assert common.embeds.size() == 5
        (common.embeds[2] as EmbedInstance).with {
            assert embedPoint == "%ControlEventHandling"
            assert sourceType == EmbedInstance.SourceType.GROUP
            assert procedureName == null
            assert procedureParams == null
            assert instanceId == 8
            assert embedLocation == ["?OkButton","Accepted"]
            assert priority == 4200
        }
    }

    void testSingleEmbedMultipleInstances() {
        def content = '''
        [EMBED]
        EMBED %ControlEventHandling
            [INSTANCES]
                WHEN '?RelatiebeheerPrintendebiteurenOpDebiteurnummer'
                [INSTANCES]
                    WHEN 'Accepted'
                    [DEFINITION]
                        [SOURCE]
                        PROPERTY:BEGIN
                        PRIORITY 4000
                        PROPERTY:END
                        !Keuze 'Debiteurnummer'
                        GloPrintDebiteuren = 'Debiteurnummer'
                    [END]
                [END]
                WHEN '?PrintDEB:KeyZoekcode'
                [INSTANCES]
                    WHEN 'Accepted'
                    [DEFINITION]
                        [SOURCE]
                        PROPERTY:BEGIN
                        PRIORITY 4000
                        PROPERTY:END
                        !Keuze 'Zoekcode'
                        GloPrintDebiteuren = 'Zoekcode'
                    [END]
                [END]
            [END]
        [END]
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()

        def common = new Common()
        new EmbedSectionParser(common).parse(reader)
        assert common.embeds != null
        assert common.embeds.size() == 2

        (common.embeds[0] as EmbedInstance).with {
            assert embedPoint == "%ControlEventHandling"
            assert sourceType == EmbedInstance.SourceType.SOURCE
            assert procedureName == null
            assert procedureParams == null
            assert instanceId == null
            assert embedLocation == ["?RelatiebeheerPrintendebiteurenOpDebiteurnummer","Accepted"]
            assert priority == 4000
        }
        (common.embeds[1] as EmbedInstance).with {
            assert embedPoint == "%ControlEventHandling"
            assert sourceType == EmbedInstance.SourceType.SOURCE
            assert procedureName == null
            assert procedureParams == null
            assert instanceId == null
            assert embedLocation == ["?PrintDEB:KeyZoekcode","Accepted"]
            assert priority == 4000
        }
    }


    /**
     * The embed section will usually contain multiple
     * embed points.
     */
    void testMultipleEmbedsWithoutInstances(){
        def content = '''
            [EMBED]
              EMBED %ProcedureRoutines
                  [DEFINITION]
                    [SOURCE]
                      PROPERTY:BEGIN
                      PRIORITY 4000
                      PROPERTY:END
                      BepalenRoute ROUTINE
                  [END]
              EMBED %ControlEventHandling
                  [DEFINITION]
                    [PROCEDURE]
                      AanmakenBellijst()
                      PRIORITY 2000
                   [END]
            [END]
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()

        def common = new Common()
        new EmbedSectionParser(common).parse(reader)
        assert common.embeds != null
        assert common.embeds.size() == 2
        (common.embeds[0] as EmbedInstance).with {
            assert embedPoint == "%ProcedureRoutines"
            assert sourceType == EmbedInstance.SourceType.SOURCE
            assert procedureName == null
            assert procedureParams == null
            assert instanceId == null
            assert embedLocation == null
            assert priority == 4000
        }
        (common.embeds[1] as EmbedInstance).with {
            assert embedPoint == "%ControlEventHandling"
            assert sourceType == EmbedInstance.SourceType.PROCEDURE
            assert procedureName == "AanmakenBellijst"
            assert procedureParams == "()"
            assert instanceId == null
            assert embedLocation == null
            assert priority == 2000
        }
    }

    void testMultipleEmbedsWithSingleInstance(){
        def content =  '''
        [EMBED]
        EMBED %ControlEventHandling
            [INSTANCES]
                WHEN '?RelatiebeheerPrintendebiteurenOpDebiteurnummer'
                [DEFINITION]
                     [GROUP]
                        PRIORITY 4200
                        INSTANCE 8  
                [END]
            [END]
        EMBED %WindowManagerMethodCodeSection
            [INSTANCES]
                WHEN 'Init'
                [DEFINITION]
                     [SOURCE]
                        PROPERTY:BEGIN
                        PRIORITY 450
                        PROPERTY:END 
                [END]
            [END]
        [END]
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()

        def common = new Common()
        new EmbedSectionParser(common).parse(reader)
        assert common.embeds != null
        assert common.embeds.size() == 2

    }
    void testMultipleEmbedsWithMultipleNestedInstances(){
        def content =  '''
        [EMBED]
        EMBED %ControlEventHandling
            [INSTANCES]
                WHEN '?RelatiebeheerPrintendebiteurenOpDebiteurnummer'
                [DEFINITION]
                     [GROUP]
                        PRIORITY 4200
                        INSTANCE 8  
                [END]
                WHEN '?PrintDEB:KeyZoekcode'
                [DEFINITION]
                    [PROCEDURE]
                        AanmakenBellijst()
                        PRIORITY 2000    
                [END]
            [END]
        EMBED %WindowManagerMethodCodeSection
            [INSTANCES]
                WHEN 'Init'
                [INSTANCES]
                    WHEN '(),BYTE'
                    [DEFINITION]
                         [SOURCE]
                            PROPERTY:BEGIN
                            PRIORITY 450
                            PROPERTY:END 
                    [END]
                [END]
             [END]
        [END]
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()

        def common = new Common()
        new EmbedSectionParser(common).parse(reader)
        assert common.embeds != null
        assert common.embeds.size() == 3

    }
}