package nl.intreq.c4w.txa.parser

import groovy.test.GroovyTestCase
import nl.intreq.c4w.txa.meta.ClarionStringMixins
import nl.intreq.c4w.txa.model.Procedure
import nl.intreq.c4w.txa.model.Window
import nl.intreq.c4w.txa.transform.TxaReader

class WindowSectionParserTest extends GroovyTestCase {
    void setUp() {
        super.setUp()
        ClarionStringMixins.initialize()
    }

    void testParserFindsApplicationFrame() {
        def content = [
                "AppFrame   APPLICATION('My application'),AT(,,527,245),FONT('MS Sans Serif',8),RESIZE, |",
                "          CENTERED,HVSCROLL,ICON('Udea.ico'),MAX,STATUS(-1,140),SYSTEM,TIMER(1000)"
        ].join('\n')

        def reader = new TxaReader('' << content)

        def procedure = new Procedure()
        new WindowSectionParser(procedure).parse(reader)
        assert procedure.window != null
        (procedure.window as Window).with {
            assert text == "My application"
            assert isMdiFrame == true
            assert use == "AppFrame"
            assert menuBar == null
        }
    }

    void testParserFindsDialogWindow() {
        def content = [
                "QuickWindow WINDOW('Periode Gegevens Muteren'),AT(,,221,148),FONT('MS Sans Serif',8),DOUBLE, |",
                "GRAY,IMM,MDI,HLP('UpdateTitulatuur'),SYSTEM"
        ].join('\n')

        def reader = new TxaReader('' << content)

        def procedure = new Procedure()
        new WindowSectionParser(procedure).parse(reader)
        assert procedure.window != null
        (procedure.window as Window).with {
            assert text == "Periode Gegevens Muteren"
            assert isMdiFrame == false
            assert use == "QuickWindow"
            assert menuBar == null
        }
    }

    void testEmptyMenubar() {
        def content = [
                "appframe APPLICATION('My window')",
                "MENUBAR,USE(?MENUBAR1),#ORDINAL(1)"
        ].join('\n')

        def reader = new TxaReader('' << content)
        def procedure = new Procedure()
        new WindowSectionParser(procedure).parse(reader)
        assert procedure.window != null
        assert procedure.window.menuBar != null
        procedure.window.menuBar.with {
            assert use == "?MENUBAR1"
            assert ordinalPosition == 1
        }
    }

    void testMenuMenuBarWithoutSubmenus(){
        def content = [
                "appframe APPLICATION('My window')",
                "          MENUBAR,USE(?MENUBAR1),#ORDINAL(1)",
                "              ITEM('Afdeling wijzigen'),USE(?1BestandAfdelingwijzigen),#ORDINAL(2)",
                "              ITEM('&Opnieuw aanmelden'),USE(?BestandOpnieuwaanmelden),MSG('Opnieuw aanmelden'), |",
                "                #ORDINAL(3)"
        ].join('\n')

        def reader = new TxaReader('' << content)
        def procedure = new Procedure()
        new WindowSectionParser(procedure).parse(reader)
        assert procedure.window != null
        assert procedure.window.menuBar != null
        procedure.window.menuBar.with {
            assert use == "?MENUBAR1"
            assert ordinalPosition == 1
            assert menus.size() == 2
            assert menus[0].text == "Afdeling wijzigen"
            assert menus[0].use == "?1BestandAfdelingwijzigen"
            assert menus[0].ordinalPosition == 2
            assert menus[1].text == "&Opnieuw aanmelden"
            assert menus[1].use == "?BestandOpnieuwaanmelden"
            assert menus[1].ordinalPosition == 3

        }
    }
}
