package org.protege.editor.owl.ui.renderer;

import java.awt.Color;

import org.protege.editor.core.PropertyUtil;
import org.protege.editor.core.ProtegeProperties;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 29-May-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLSystemColors {

    private static final Color FOREGROUND_COLOR = new Color(110, 110, 110);

    private static final Color ROLLOVER_FOREGROUND_COLOR = new Color(50, 50, 50);

    public static Color getOWLClassColor() {
        return PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.CLASS_COLOR_KEY),
                                     Color.GRAY);
    }


    public static Color getOWLObjectPropertyColor() {
        return PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.OBJECT_PROPERTY_COLOR_KEY),
                                     Color.GRAY);
    }


    public static Color getOWLDataPropertyColor() {
        return PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.DATA_PROPERTY_COLOR_KEY),
                                     Color.GRAY);
    }

    public static Color getOWLAnnotationPropertyColor() {
        return PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.ANNOTATION_PROPERTY_COLOR_KEY),
                Color.GRAY);
    }

    public static Color getOWLDatatypeColor() {
        return PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.DATATYPE_COLOR_KEY),
                Color.GRAY);
    }


    public static Color getOWLIndividualColor() {
        return PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.INDIVIDUAL_COLOR_KEY),
                                     Color.GRAY);
    }


    public static Color getOWLOntologyColor() {
        return PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.ONTOLOGY_COLOR_KEY),
                                     Color.GRAY);
    }

    public static Color getForegroundColor() {
        return FOREGROUND_COLOR;
    }

    public static Color getRolloverForegroundColor() {
        return ROLLOVER_FOREGROUND_COLOR;
    }
}
