package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.meta.ClarionStringMixins
import nl.practicom.c4w.txa.model.Common
import nl.practicom.c4w.txa.model.EmbedInstance

class EmbedSectionParserTest extends GroovyTestCase {

    void setUp() {
        super.setUp()
        ClarionStringMixins.initialize()
    }

    void testEmbedWithSingleInstance(){
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
        common.embeds[0].with {
            assert embedPoint == "%ProcedureRoutines"
            assert sourceType == EmbedInstance.SourceType.SOURCE
            assert embedLocation == null
            assert priority == 4000
        }
    }
}
