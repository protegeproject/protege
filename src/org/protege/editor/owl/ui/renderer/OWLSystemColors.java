package org.protege.editor.owl.ui.renderer;

import org.protege.editor.core.PropertyUtil;
import org.protege.editor.core.ProtegeProperties;

import java.awt.*;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 29-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLSystemColors {

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


    public static Color getOWLIndividualColor() {
        return PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.INDIVIDUAL_COLOR_KEY),
                                     Color.GRAY);
    }


    public static Color getOWLOntologyColor() {
        return PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.ONTOLOGY_COLOR_KEY),
                                     Color.GRAY);
    }
}
