package org.protege.editor.owl.ui.renderer;

import java.awt.Color;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/02/2012
 */
public class OWLAnnotationPropertyIcon extends OWLPropertyIcon {

    public static final Color COLOR = OWLSystemColors.getOWLAnnotationPropertyColor();

    public OWLAnnotationPropertyIcon() {
        this(FillType.FILLED);
    }

    public OWLAnnotationPropertyIcon(FillType fillType) {
        super(COLOR, fillType);
    }

    @Override
    public Color getEntityColor() {
        return COLOR;
    }
}
