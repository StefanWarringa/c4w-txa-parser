package nl.practicom.c4w.txa.model

class MenuBar {

    // Menu composition classes
    abstract class Menu {
        def use
        def text
        def ordinalPosition
    }
    class MenuItem extends Menu {}
    class SubMenu extends Menu {
        List<Menu> children
    }

    // Top-level menu elements
    List<Menu> menus

    // Menubar control identifier
    def use

    // Sequence number for menubar control
    int ordinalPosition
}
