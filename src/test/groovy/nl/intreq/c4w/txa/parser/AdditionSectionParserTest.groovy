package nl.intreq.c4w.txa.parser

import nl.intreq.c4w.txa.meta.ClarionStringMixins
import nl.intreq.c4w.txa.model.Addition
import nl.intreq.c4w.txa.model.Common
import nl.intreq.c4w.txa.transform.TxaReader

class AdditionSectionParserTest extends GroovyTestCase {
    void setUp() {
        super.setUp()
        ClarionStringMixins.initialize()
    }

    void testMinimalAddition() {
        def content = '''
            [ADDITION]
            NAME MyGreatAdditions addition1
            [INSTANCE]
            INSTANCE 3
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()
        def model = new Common()
        new AdditionSectionParser(model).parse(reader)

        assert model.additions != null
        assert model.additions.size() == 1
        (model.additions[0] as Addition).with {
            assert templateClass == "MyGreatAdditions"
            assert templateName == "addition1"
            assert instanceId == 3
            assert parentId == null
            assert fieldPrompts == null
            assert prompts == null
            assert showPrompts == false
        }
    }

    void testAdditionAttributesAreParsedOK() {
        def content = '''
            [ADDITION]
            NAME MyGreatAdditions addition1
            [INSTANCE]
            INSTANCE 2
            PARENT 9
            OWNER 1
            PROCPROP
        '''

        def reader = new TxaReader('' << content)
        reader.readLine()
        def model = new Common()
        new AdditionSectionParser(model).parse(reader)

        assert model.additions != null
        assert model.additions.size() == 1
        (model.additions[0] as Addition).with {
            assert templateClass == "MyGreatAdditions"
            assert templateName == "addition1"
            assert instanceId == 2
            assert parentId == 9
            assert fieldPrompts == null
            assert prompts == null
            assert showPrompts == true
        }
    }

//    @Ignore("Needs further revision")
//    void testAdditionWithPrompts() {
//        def content = '''
//            [ADDITION]
//            NAME MyGreatAdditions addition1
//            [FIELDPROMPT]
//            %ResizeAnchorTop DEPEND %Control LONG TIMES 0
//
//            %ResizeAnchorLeft DEPEND %Control LONG TIMES 0
//
//            [INSTANCE]
//            INSTANCE 3
//            [PROMPTS]
//            %UpdateProcedure PROCEDURE  (UpdateCBLsubgroep)
//            %ClassItem UNIQUE DEFAULT  ('BrowseEIPManager', 'EIP_CBLS:CBLnummer', 'EIP_CBLS:Omschrijving')
//            %DefaultBaseClassType DEPEND %ClassItem DEFAULT TIMES 1
//            WHEN  ('BrowseEIPManager') ('BrowseEIPManager')
//
//        '''
//
//        def reader = new TxaReader('' << content)
//        reader.readLine()
//        def model = new Common()
//        new AdditionSectionParser(model).parse(reader)
//
//        assert model.additions != null
//        assert model.additions.size() == 1
//        (model.additions[0] as Addition).with {
//            assert templateClass == "MyGreatAdditions"
//            assert templateName == "addition1"
//            assert instanceId == 3
//
//            assert fieldPrompts != null
//            assert fieldPrompts.size() == 2
//            (fieldPrompts[0] as DependentPrompt).with {
//                assert name == "ResizeAnchorTop"
//                assert type == Prompt.PromptType.LONG
//                assert parentSymbol == "Control"
//            }
//
//            assert prompts != null
//            assert prompts.size() == 3
//            assert prompts[0].name == "UpdateProcedure"
//            assert prompts[2].name == "DefaultBaseClassType"
//        }
//    }
}
