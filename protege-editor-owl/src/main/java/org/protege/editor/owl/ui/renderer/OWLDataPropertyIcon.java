package org.protege.editor.owl.ui.renderer;

import java.awt.*;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/02/2012
 */
public class OWLDataPropertyIcon extends OWLPropertyIcon {

    public static final Color COLOR = OWLSystemColors.getOWLDataPropertyColor();

    public OWLDataPropertyIcon() {
        this(FillType.FILLED);
    }

    public OWLDataPropertyIcon(FillType fillType) {
        super(COLOR, fillType);
    }

    @Override
    public Color getEntityColor() {
        return COLOR;
    }
}
