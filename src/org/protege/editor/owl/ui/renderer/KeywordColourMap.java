package org.protege.editor.owl.ui.renderer;

import java.awt.*;
import java.util.HashMap;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 23, 2008<br><br>
 */
public class KeywordColourMap extends HashMap<String, Color> {

    public KeywordColourMap() {
        Color restrictionColor = new Color(178, 0, 178);
        Color logicalOpColor = new Color(0, 178, 178);
        Color axiomColor = new Color(10, 94, 168);
        Color queryColor = new Color(100, 15, 120);
        put("some", restrictionColor);
        put("only", restrictionColor);
        put("value", restrictionColor);
        put("exactly", restrictionColor);
        put("min", restrictionColor);
        put("max", restrictionColor);
        put("inv", logicalOpColor);
        put("and", logicalOpColor);
        put("that", logicalOpColor);
        put("or", logicalOpColor);
        put("not", logicalOpColor);
        put("subClassOf", axiomColor);
        put("SubClassOf", axiomColor);
        put("disjointWith", axiomColor);
        put("DisjointWith", axiomColor);
        put("equivalentTo", axiomColor);
        put("EquivalentTo", axiomColor);
        put("domain", axiomColor);
        put("range", axiomColor);
        put("instanceOf", axiomColor);
        put("types", axiomColor);
        put("InstanceOf", axiomColor);
        put("minus", queryColor);
        put("plus", queryColor);
        put("possibly", queryColor);
        put("inverseOf", axiomColor);
        put("DifferentIndividuals:", axiomColor);
        put("SameIndividuals:", axiomColor);
        put("Functional:", axiomColor);
        put("InverseFunctional:", axiomColor);
        put("Symmetric:", axiomColor);
        put("AntiSymmetric:", axiomColor);
        put("Reflexive:", axiomColor);
        put("Irreflexive:", axiomColor);
        put("Transitive:", axiomColor);
        put("subPropertyOf", axiomColor);
        put("disjointUnionOf", axiomColor);
        put("o", axiomColor);
        put("\u279E", axiomColor);
        put("\u2192", axiomColor);
        put("\u2227", axiomColor);
    }
}
