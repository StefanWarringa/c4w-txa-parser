package nl.practicom.c4w.txa.parser


import nl.practicom.c4w.txa.model.MenuBar
import nl.practicom.c4w.txa.model.Procedure
import nl.practicom.c4w.txa.model.Window
import nl.practicom.c4w.txa.transform.TxaReader

class WindowSectionParser {

    static windowPattern = /^([^\s]+)\s+(APPLICATION|WINDOW)\('([^']*)\'\).*$/
    static menubarPattern = /^\s*MENUBAR,\s*USE\((\?[^)]+)\),.*#ORDINAL\(([0-9]+)\)$/
    static menuPattern = /^\s*MENU,\s*USE\((\?[^)]+)\),.*#ORDINAL\(([0-9]+)\)$/
    static itemPattern = /^\s*ITEM\('([^']+)'\),\s*USE\((\?[^)]+)\),.*#ORDINAL\(([0-9]+)\)$/
    static endPattern = /^\s*END\s*$/

    Procedure parent

    WindowSectionParser(Procedure parent){
        this.parent = parent
    }

    void parse(TxaReader r){
        if ( r.currentLine() !=~ windowPattern){
            r.readUptoMatching(windowPattern)
        }

        if ( !r.atEOF() ){
            def windowMatcher = ( r.currentLine() =~ windowPattern )
            if ( windowMatcher.matches()){
                def window = new Window()
                window.use = windowMatcher[0][1]
                window.isMdiFrame = (windowMatcher[0][2] as String) == "APPLICATION"
                window.text = windowMatcher[0][3]

                def line = r.readLine()

                while ( !r.atEOF() && !r.currentLine().isSectionMark() ) {
                    if (line.indexOf('MENUBAR') >= 0) {
                        this.parseMenubar(window, r)
                    }
                    if (line.indexOf('TOOLBAR') >= 0) {
                        this.parseToolbar(window, r)
                    }

                    line = r.readLine()
                }

                this.parent.window = window
            }
        }
    }

    void parseMenubar(Window window, TxaReader r){
        def menubarMatcher = ( r.currentLine() =~ menubarPattern )
        if ( menubarMatcher.matches() ){
            def menubar = new MenuBar()
            menubar.use = menubarMatcher[0][1] as String
            menubar.ordinalPosition = menubarMatcher[0][2] as int
            menubar.menus = []

            r.readLine()

            while( !(r.atEOF() || r.currentLine() ==~ endPattern ) ){
                if ( r.currentLine() ==~ menuPattern ){
                    menubar.menus.add( this.parseSubMenu(r) )
                } else if ( r.currentLine() ==~ itemPattern ){
                    menubar.menus.add( this.parseMenuItem(r) )
                } else {
                    r.readLine()
                }
            }

            window.menuBar = menubar
        }
    }

    MenuBar.Menu parseSubMenu(TxaReader r){
        def menuMatcher = ( r.currentLine() =~ menuPattern )

        def m = null

        if ( menuMatcher.matches()) {
            m = new MenuBar.SubMenu()
            m.text = menuMatcher[0][1] as String
            m.use = menuMatcher[0][2] as String
            m.ordinalPosition = menuMatcher[0][3] as int

            while( !(r.atEOF() || r.currentLine() ==~ endPattern) ) {
                MenuBar.Menu child = null

                if ( r.currentLine() ==~ menuPattern ){
                    child = this.parseSubMenu(r)
                } else if ( r ==~ itemPattern ){
                    child = this.parseMenuItem(r)
                } else {
                    r.readLine()
                }

                if ( child != null ){
                    m.children.add(child)
                }
            }
        }

        // forward reader to continue processing
        r.readLine()

        return m

    }

    MenuBar.Menu parseMenuItem(TxaReader r){
        def m = null

        def itemMatcher = ( r.currentLine() =~ itemPattern )
        if ( itemMatcher.matches() ){
            m = new MenuBar.MenuItem()
            m.text = itemMatcher[0][1] as String
            m.use = itemMatcher[0][2] as String
            m.ordinalPosition = itemMatcher[0][3] as int

        }

        // forward reader to continue processing
        r.readLine()

        return m
    }

    void parseToolbar(Window window, TxaReader r){

    }
}
