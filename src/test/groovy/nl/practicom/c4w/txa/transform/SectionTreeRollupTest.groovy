package nl.practicom.c4w.txa.transform

import nl.practicom.c4w.txa.TxaTestSupport
import static nl.practicom.c4w.txa.TxaTestSupport.*

import static nl.practicom.c4w.txa.parser.SectionMark.*

class SectionTreeRollupTest extends GroovyTestCase implements TxaTestSupport {

    StreamingTxaReader reader
    TxaContentHandlerStub handler
    TxaContext ctx

    void testRollupEmptySectionTreeHasNoEffect() {
        (reader, handler , ctx) = setupStreamingTxaReader([],null)
        reader.rollupSectionTree(ctx, APPLICATION)
        assert ctx.parentSections.isEmpty()
        assert ctx.currentSection == null
        assert handler.sectionsClosed.isEmpty()
    }

    void testRollupCurrentSectionWithExplicitEnd(){
        (reader, handler, ctx) = setupStreamingTxaReader([APPLICATION, PROGRAM],MODULE)
        ctx.currentLine = END
        reader.rollupSectionTree(ctx, END)
        assert ctx.parentSections == [APPLICATION,PROGRAM]
        assert ctx.currentSection == null
        assert handler.sectionsClosed == [MODULE]
    }

    void testCurrentSectionIsClearedOnExplicitEnd(){
        (reader, handler, ctx) = setupStreamingTxaReader([], PROCEDURE)
        ctx.currentLine = END
        reader.rollupSectionTree(ctx, END)
        assert ctx.parentSections.isEmpty()
        assert ctx.currentSection == null
        assert handler.sectionsClosed == [PROCEDURE]
    }

    void testCurrentSectionIsClearedOnImplicitEnd(){
        (reader, handler, ctx) = setupStreamingTxaReader([MODULE], PROCEDURE)
        ctx.currentLine = PROCEDURE
        reader.rollupSectionTree(ctx, PROCEDURE)
        assert ctx.parentSections == [MODULE]
        assert ctx.currentSection == null
        assert handler.sectionsClosed == [PROCEDURE]
    }

    void testSectionTreeIsRolledUptoFirstParentWithExplicitEnd(){
        (reader, handler, ctx) = setupStreamingTxaReader([PROCEDURE,COMMON,EMBED,INSTANCES,DEFINITION], SOURCE)
        ctx.currentLine = END
        reader.rollupSectionTree(ctx, END)
        assert ctx.parentSections == [PROCEDURE,COMMON,EMBED,INSTANCES]
        assert ctx.currentSection == null
        assert handler.sectionsClosed == [SOURCE, DEFINITION]
    }

    void testSectionTreeIsRolledUpToParentSection(){
        (reader, handler, ctx) = setupStreamingTxaReader([PROCEDURE,COMMON,FILES], OTHERS)
        ctx.currentLine = PROMPTS
        reader.rollupSectionTree(ctx, PROMPTS)
        assert ctx.parentSections == [PROCEDURE,COMMON]
        assert ctx.currentSection == null
        assert handler.sectionsClosed == [OTHERS, FILES]
    }

    void testCurrentSectionReplacedBySiblingSection(){
        (reader, handler, ctx) = setupStreamingTxaReader([PROCEDURE,COMMON,FILES], PRIMARY)
        ctx.currentLine = OTHERS
        reader.rollupSectionTree(ctx, OTHERS)
        assert ctx.parentSections == [PROCEDURE,COMMON,FILES]
        assert ctx.currentSection == null
        assert handler.sectionsClosed == [PRIMARY]
    }
}
