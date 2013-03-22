package org.protege.editor.owl.ui.renderer.styledstring;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 25/09/2012
 */
public enum CSSConstants {

    TEXT_DECORATION("text-decoration"),

    FONT_WEIGHT("font-weight"),

    FONT_SIZE("font-size"),

    BACKGROUND("background"),

    COLOR("color"),

    BOLD("bold"),

    NORMAL("normal"),

    LINE_THROUGH("line-through"),

    UNDERLINE("underline"),

    NONE("none");


    String name;

    private CSSConstants(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
