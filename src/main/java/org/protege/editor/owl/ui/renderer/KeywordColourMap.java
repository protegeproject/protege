package org.protege.editor.owl.ui.renderer;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntax;

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
	private static final long serialVersionUID = -7509556871169641464L;

	public KeywordColourMap() {
        Color restrictionColor = new Color(178, 0, 178);
        Color logicalOpColor = new Color(0, 178, 178);
        Color axiomColor = new Color(10, 94, 168);
        Color typeColor = new Color(178, 178, 178);

        for (ManchesterOWLSyntax keyword : ManchesterOWLSyntax.values()){
            if (keyword.isAxiomKeyword()){
                put(keyword.toString(), axiomColor);
                put(keyword.toString() + ":", axiomColor);
            }
            else if (keyword.isClassExpressionConnectiveKeyword()){
                put(keyword.toString(), logicalOpColor);
            }
            else if (keyword.isClassExpressionQuantiferKeyword()){
                put(keyword.toString(), restrictionColor);
            }
            else if (keyword.isSectionKeyword()){
                put(keyword.toString(), typeColor);
                put(keyword.toString() + ":", typeColor);
            }
        }

        put("o", axiomColor);
//        put("\u279E", axiomColor);
//        put("\u2192", axiomColor);
//        put("\u2227", axiomColor);
    }
}
