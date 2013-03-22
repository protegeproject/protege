package org.protege.editor.owl.ui;

import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.location.PhysicalLocationPanel;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.util.Set;
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
 * Date: May 1, 2009<br><br>
 */
public class SaveConfirmationPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -3494075622397325662L;
    private PhysicalLocationPanel savedOntologiesPanel;

//    private PhysicalLocationPanel dirtyOntologiesPanel;


    public SaveConfirmationPanel(OWLEditorKit editorKit, Set<OWLOntology> ontologies) {
        setLayout(new BorderLayout(12, 12));
        savedOntologiesPanel = new PhysicalLocationPanel(editorKit, ontologies){
            /**
             * 
             */
            private static final long serialVersionUID = 4961022502290441620L;

            public Dimension getPreferredSize() {
                return new Dimension(600, 200);
            }
        };
        add(new JLabel("Saved the following ontologies:"), BorderLayout.NORTH);
        add(savedOntologiesPanel, BorderLayout.CENTER);

//        final Set<OWLOntology> dirtyOntologies = editorKit.getOWLModelManager().getDirtyOntologies();
//        if (dirtyOntologies.size() > 0){
//            dirtyOntologiesPanel = new PhysicalLocationPanel(editorKit, dirtyOntologies){
//                public Dimension getPreferredSize() {
//                    return new Dimension(600, 200);
//                }
//            };
////            add(new JLabel("The following ontologies are still not saved:"), BorderLayout.SOUTH);
//            add(dirtyOntologiesPanel, BorderLayout.SOUTH);
//        }
    }


    public static void showDialog(OWLEditorKit editorKit, Set<OWLOntology> ontologies) {
        SaveConfirmationPanel panel = new SaveConfirmationPanel(editorKit, ontologies);
        JOptionPaneEx.showConfirmDialog(editorKit.getWorkspace(),
                                        "Saved ontologies",
                                        panel,
                                        JOptionPane.PLAIN_MESSAGE,
                                        JOptionPane.DEFAULT_OPTION,
                                        null);
    }

}
