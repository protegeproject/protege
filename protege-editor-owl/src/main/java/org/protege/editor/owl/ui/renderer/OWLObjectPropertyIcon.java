package org.protege.editor.owl.ui.renderer;

import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/02/2012
 */
public class OWLObjectPropertyIcon extends OWLPropertyIcon {

    public static final Color COLOR = OWLSystemColors.getOWLObjectPropertyColor();

    public OWLObjectPropertyIcon() {
        this(FillType.FILLED);
    }

    public OWLObjectPropertyIcon(FillType fillType) {
        super(COLOR, fillType);
    }

    @Override
    public Color getEntityColor() {
        return COLOR;
    }
}
