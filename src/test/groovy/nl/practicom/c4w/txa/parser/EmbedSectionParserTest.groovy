package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.meta.ClarionStringMixins
import nl.practicom.c4w.txa.model.Common
import nl.practicom.c4w.txa.model.EmbedInstance

class EmbedSectionParserTest extends GroovyTestCase {

    void setUp() {
        super.setUp()
        ClarionStringMixins.initialize()
    }

    void testEmbedWithSingleSourceInstance() {
        def content = [
                "[EMBED]",
                "EMBED %ProcedureRoutines",
                "[DEFINITION]",
                "[SOURCE]",
                "PROPERTY:BEGIN",
                "PRIORITY 4000",
                "PROPERTY:END",
                "BepalenRoute ROUTINE",
                "[END]"
        ].join('\n')

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
        def content = [
                "[EMBED]",
                "EMBED %ControlEventHandling",
                "[DEFINITION]",
                "[GROUP]",
                "PRIORITY 10000",
                "INSTANCE 5",
                "[END]"
        ].join('\n')

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

    void testEmbedWithSingleProcedureInstance() {
        def content = [
                "[EMBED]",
                "EMBED %ControlEventHandling",
                "[DEFINITION]",
                "[PROCEDURE]",
                "AanmakenBellijst()" ,
                "PRIORITY 2000",
                "[END]"
        ].join('\n')

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

    void testSingleInstance(){
        def content = [
                "[EMBED]" ,
                "EMBED %ControlEventHandling" ,
                "[INSTANCES]" ,
                "WHEN '?OkButton'" ,
                "[DEFINITION]" ,
                "[GROUP]" ,
                "PRIORITY 4100" ,
                "INSTANCE 1",
                "[END]"
        ].join('\n')

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

    void testSingleNestedInstance(){
        def content = [
                "[EMBED]" ,
                "EMBED %ControlEventHandling" ,
                "[INSTANCES]" ,
                "WHEN '?OkButton'" ,
                "[INSTANCES]" ,
                "WHEN 'Accepted'" ,
                "[DEFINITION]" ,
                "[GROUP]" ,
                "PRIORITY 4100" ,
                "INSTANCE 1",
                "[END]"
        ].join('\n')

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

    void testSingleGroupMultipleInstances(){
        def content = [
                "[EMBED]",
                "  EMBED %ControlEventHandling",
                "  [INSTANCES]",
                "    WHEN '?OkButton'",
                "      [INSTANCES]",
                "        WHEN 'Accepted'",
                "        [DEFINITION]",
                "          [GROUP]",
                "          PRIORITY 4100",
                "          INSTANCE 1",
                "        [END]",
                "      [END]",
                "    WHEN '?CancelButton'",
                "      [INSTANCES]",
                "        WHEN 'Accepted'",
                "        [DEFINITION]",
                "          [GROUP]",
                "          PRIORITY 4000",
                "          INSTANCE 2",
                "        [END]",
                "      [END]",
                "  [END]",
                "[END]"
        ].join("\n")

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
}