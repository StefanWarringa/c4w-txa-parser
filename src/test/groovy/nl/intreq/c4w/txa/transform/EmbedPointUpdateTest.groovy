package nl.intreq.c4w.txa.transform

import groovy.test.GroovyTestCase
import nl.intreq.c4w.txa.test.TxaTestSupport

import static SectionMark.*

class EmbedPointUpdateTest extends GroovyTestCase implements TxaTestSupport {

    void testInstanceLevelIsIncreasedWhenInstanceSectionStarts() {
        TxaContext ctx = new TxaContext()
        ctx.currentLine = INSTANCES // Note the plural form!
        assert ctx.instanceLevel == 0
        new StreamingTxaReader().updateEmbedPointInfo(ctx)
        assert ctx.instanceLevel == 1
    }

    void testInstanceLevelIsDecreasedWhenInstanceSectionEnds() {
        TxaContext ctx = new TxaContext()
        ctx.currentLine = END
        ctx.instanceLevel == 1
        new StreamingTxaReader().updateEmbedPointInfo(ctx)
        assert ctx.instanceLevel == 0
    }

    void testCurrentEmbedPointIsSetInsideEmbedSectionOnly(){
        TxaContext ctx = new TxaContext()
        ctx.currentLine = 'EMBED %Embedpoint1'
        ctx.instanceLevel == 1
        new StreamingTxaReader().updateEmbedPointInfo(ctx)
        assert ctx.currentEmbedPoint == null
        ctx.parentSections = [EMBED]
        new StreamingTxaReader().updateEmbedPointInfo(ctx)
        assert ctx.currentEmbedPoint == '%Embedpoint1'
    }

    void testAddEmbedInstanceAddedWithinInstanceSection(){
        def reader = new StreamingTxaReader()

        // Within first instance section of embedpoint1:
        TxaContext ctx = new TxaContext()
        ctx.parentSections = [EMBED]
        ctx.currentEmbedPoint = '%embedpoint1'
        ctx.currentSection = INSTANCES
        ctx.instanceLevel = 1

        // Embed point instance should be surrounded by single quotes!
        ctx.currentLine = "WHEN 'instance1'"
        reader.updateEmbedPointInfo(ctx)
        assert ctx.currentEmbedInstance.size() == 1
        assert ctx.currentEmbedInstance == ['instance1']

        // Add a nested instance
        ctx.parentSections << ctx.currentSection
        ctx.instanceLevel = 2
        assert ctx.parentSections == [EMBED,INSTANCES]
        assert ctx.currentSection == INSTANCES

        ctx.currentLine = "WHEN 'instance2'"
        reader.updateEmbedPointInfo(ctx)
        assert ctx.currentEmbedInstance.size() == 2
        assert ctx.currentEmbedInstance == ['instance1','instance2']

        // End the last embed instance
        ctx.currentLine = END
        reader.updateEmbedPointInfo(ctx)
        assert ctx.currentEmbedInstance.size() == 1
        assert ctx.currentEmbedInstance == ['instance1']
        assert ctx.parentSections == [EMBED,INSTANCES]

        // Pop the parent instance section
        ctx.currentSection = ctx.parentSections.removeLast()
        assert ctx.parentSections == [EMBED]
        assert ctx.currentSection == INSTANCES
        ctx.currentLine = END
        reader.updateEmbedPointInfo(ctx)
        assert ctx.currentEmbedInstance.size() == 0
        assert ctx.parentSections == [EMBED]
    }

}
