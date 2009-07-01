package org.protege.editor.owl.ui.axiom;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLAxiomAnnotationsFrame;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.OWLAxiom;

import javax.swing.*;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 3, 2008<br><br>
 */
public class AxiomAnnotationPanel extends JComponent {

    private OWLFrameList<OWLAxiom> axiomAnnotationComponent;

    private DefaultListModel model;

    public AxiomAnnotationPanel(OWLEditorKit eKit) {
        setLayout(new BorderLayout(6, 6));
        setPreferredSize(new Dimension(500, 300));

        // we need to use the OWLCellRenderer, so create a singleton JList
        final OWLCellRenderer ren = new OWLCellRenderer(eKit);
        ren.setHighlightKeywords(true);

        model = new DefaultListModel();
        JList label = new JList(model);
        label.setBackground(getBackground());
        label.setEnabled(false);
        label.setOpaque(true);
        label.setCellRenderer(ren);

        axiomAnnotationComponent = new OWLFrameList<OWLAxiom>(eKit, new OWLAxiomAnnotationsFrame(eKit));
        axiomAnnotationComponent.setAxiomSelectionSyncronized(false);

        final JScrollPane scroller = new JScrollPane(axiomAnnotationComponent);

        add(label, BorderLayout.NORTH);
        add(scroller, BorderLayout.CENTER);

        setVisible(true);
    }


    public void setAxiom(OWLAxiom ax){
        model.clear();
        model.addElement(ax);
        axiomAnnotationComponent.setRootObject(ax);
    }


    public void dispose(){
        axiomAnnotationComponent.dispose();
    }
}
