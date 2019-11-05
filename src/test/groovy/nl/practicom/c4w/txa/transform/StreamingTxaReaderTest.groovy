package nl.practicom.c4w.txa.transform

import nl.practicom.c4w.txa.test.TxaTestSupport
import nl.practicom.c4w.txa.meta.ClarionDateMixins
import nl.practicom.c4w.txa.meta.ClarionStringMixins

import static nl.practicom.c4w.txa.test.TxaTestSupport.*
import static nl.practicom.c4w.txa.transform.SectionMark.*

class StreamingTxaReaderTest extends GroovyTestCase implements TxaTestSupport{

    StreamingTxaReader reader
    TxaContentHandlerStub handler
    TxaContext ctx

    void setUp() throws java . lang . Exception {
        super.setUp()
        ClarionStringMixins.initialize()
        ClarionDateMixins.initialize()
    }

    void testFlatListOfProcedures() {
        def content = '''\
        [PROCEDURE]
        NAME P1
        [PROCEDURE]
        NAME P2
        '''.trimLines()

        (reader,handler, ctx) = setupStreamingTxaReader()
        reader.parse('' << content)
        assert handler.sectionsStarted.size() == 2
        assert handler.sectionsStarted == [PROCEDURE, PROCEDURE]
        assert handler.sectionsClosed.size() == 2
        assert handler.sectionsClosed == [PROCEDURE,PROCEDURE]
    }

    void testSimpleEmbedPoint(){
        def content = '''\
          [EMBED]
            EMBED embedpoint1
            [DEFINITION]
              [SOURCE]
              [GROUP]
              [PROCEDURE]
              [TEMPLATE]
            [END]  
          [END]
        '''.trimLines()

        assertSectionsClosedCorrectly(content)
        (reader, handler, ctx) = setupStreamingTxaReader()
        reader.parse('' << content)
        assert handler.sectionsStarted == [EMBED,DEFINITION,SOURCE,GROUP,PROCEDURE,TEMPLATE]
        // Sections are closed depth-first:
        assert handler.sectionsClosed == [SOURCE, GROUP, PROCEDURE, TEMPLATE,DEFINITION,EMBED]
    }

    void testComplexEmbedPoint(){
        def content = '''\
          [EMBED]
            EMBED embedpoint1
            [DEFINITION]
              [SOURCE]
              [GROUP]
            [END]  
            EMBED embedpoint2
            [INSTANCES]
                WHEN 'instance1\'
                [DEFINITION]
                  [PROCEDURE]
                  [TEMPLATE]
                [END]
            [END]
          [END]
        '''.trimLines()

        assertSectionsClosedCorrectly(content)
        (reader, handler, ctx) = setupStreamingTxaReader()
        reader.parse('' << content)
        assert handler.sectionsStarted == [EMBED,DEFINITION,SOURCE,GROUP,INSTANCES,DEFINITION,PROCEDURE,TEMPLATE]
        assert handler.sectionsClosed == [SOURCE, GROUP,DEFINITION,PROCEDURE, TEMPLATE,DEFINITION,INSTANCES,EMBED]
    }

    void testMultipleEmbedPoints(){
        def content = '''\
          [EMBED]
            EMBED embedpoint2
            [INSTANCES]
              WHEN 'instance1\'
              [INSTANCES]
                WHEN 'instance2\'
                [DEFINITION]
                  [SOURCE]
                  [GROUP]
                  [PROCEDURE]
                  [TEMPLATE]
                [END]
              [END]
            [END]
          [END]
        '''.trimLines()

        assertSectionsClosedCorrectly(content)
        (reader, handler, ctx) = setupStreamingTxaReader()
        reader.parse('' << content)
        assert handler.sectionsStarted == [EMBED,INSTANCES,INSTANCES,DEFINITION,SOURCE,GROUP,PROCEDURE,TEMPLATE]
        assert handler.sectionsClosed == [SOURCE, GROUP, PROCEDURE, TEMPLATE,DEFINITION,INSTANCES,INSTANCES,EMBED]
    }

    void testProcedureWithEmbeddedProcedureCall(){
        def content = '''\
            [MODULE]
                 [PROCEDURE]
                 NAME P1
                 FROM ABC Browse
                 [COMMON]
                     [EMBED]
                       EMBED %Embedpoint1
                       [DEFINITION]
                         [PROCEDURE]
                           P2()
                       [END] 
                     [END]
                 [PROCEDURE]
                 NAME P2
                 FROM ABC Browse
             [END]
        '''.trimLines()

        assertSectionsClosedCorrectly(content)
        (reader, handler, ctx) = setupStreamingTxaReader()
        reader.parse('' << content)
        assert handler.sectionsStarted == [MODULE,PROCEDURE,COMMON,EMBED,DEFINITION,PROCEDURE,PROCEDURE]
        assert handler.sectionsClosed == [PROCEDURE,DEFINITION,EMBED,COMMON,PROCEDURE,PROCEDURE,MODULE]
    }

    void testApplicationSkeleton(){
        def content = '''\
            [APPLICATION]
                 [COMMON]
                    [DATA]
                    [FILES]
                    [PROMPTS]
                    [EMBED]
                    [END]
                    [ADDITION]
                        [FIELDPROMPT]
                        [INSTANCE]
                        [PROMPTS]
                 [PERSIST]
                 [PROJECT]
                 [PROGRAM]
                    [COMMON]
                        [DATA]
                            [SCREENCONTROLS]
                            [REPORTCONTROLS]
                                [QUICKCODE]
                        [EMBED]
                        [END]
                 [END]
                 [MODULE]
                    [COMMON]
                    [PROCEDURE]
                        [COMMON]
                 [END]
            [END]
        '''.trimLines()

        assertSectionsClosedCorrectly(content)
        (reader, handler, ctx) = setupStreamingTxaReader()
        reader.parse('' << content)
        assert handler.sectionsStarted == [
                APPLICATION,
                COMMON,
                DATA,
                FILES,
                PROMPTS,
                EMBED,
                ADDITION,
                FIELDPROMPT,
                INSTANCE,
                PROMPTS,
                PERSIST,
                PROJECT,
                PROGRAM,
                COMMON,
                DATA,
                SCREENCONTROLS,
                REPORTCONTROLS,
                QUICKCODE,
                EMBED,
                MODULE,
                COMMON,
                PROCEDURE,
                COMMON
        ]
        assert handler.sectionsClosed == [
                DATA,
                FILES,
                PROMPTS,
                EMBED,
                FIELDPROMPT,
                INSTANCE,
                PROMPTS,
                ADDITION,
                COMMON,
                PERSIST,
                PROJECT,
                SCREENCONTROLS,
                QUICKCODE,
                REPORTCONTROLS,
                DATA,
                EMBED,
                COMMON,
                PROGRAM,
                COMMON,
                COMMON,
                PROCEDURE,
                MODULE,
                APPLICATION
        ]
    }
}
