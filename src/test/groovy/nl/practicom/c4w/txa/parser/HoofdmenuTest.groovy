package nl.practicom.c4w.txa.parser

import nl.practicom.c4w.txa.model.DependentPrompt
import nl.practicom.c4w.txa.model.Procedure
import nl.practicom.c4w.txa.model.Prompt
import org.junit.Test

import java.nio.file.Paths

import static org.junit.Assert.*

public class HoofdmenuTest {

    @Test
    void testHoofdmenu(){
        def txaFile = getClass().getClassLoader().getResource("nl/practicom/c4w/txa/parser/procedure/Hoofdmenu.txa")
        def model = new TxaParser().parse(txaFile)
        assert model instanceof Procedure
        assert model.name == "Hoofdmenu"
        assert model.prototype == null

        assert model.common != null
        model.common.with {
            assert description == "'Hoofdmenu Invervo'"
            assert longDescription == null
            assert from == "ABC Frame"
            assert modified == "'2019/06/21' '13:58:37'"
            assert templateFiles == null
            assert otherFiles == [
                "Bedrijfsversies",
                "LogInlogApplicaties",
                "IPKeepAlive",
                "Bedrijfsgegevens",
                "Stamgegevens",
                "Medewerkers"
            ]
            assert prompts.size() == 166
            (prompts[0] as DependentPrompt).with {
                assert name == "ButtonAction"
                assert type == Prompt.PromptType.DEFAULT
                assert options.size() == 333
            }

        }

        assert model.window != null
        model.window.with {
            assert text == "INkoop VERkoop VOorraad"
            assert use == "AppFrame"
            assert isMdiFrame == true

            assert menuBar != null
            menuBar.with {
                assert use == "?MENUBAR1"
                assert ordinalPosition == 1
                assert menus != null
                assert menus.size() == 333
            }
        }
    }
}
