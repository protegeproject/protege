package org.protege.editor.owl.ui.ontology.wizard.move.byannotation;

import org.protege.editor.core.ui.list.RemovableObjectList;
import org.protege.editor.owl.ui.frame.AnnotationURIList;
import org.protege.editor.owl.ui.ontology.wizard.move.MoveAxiomsKitConfigurationPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URI;
import java.util.HashSet;
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
 * Date: Nov 28, 2008<br><br>
 */
public class AnnotationSelectorPanel extends MoveAxiomsKitConfigurationPanel {

    private MoveAnnotationAxiomsByURIKit kit;

    private AnnotationURIList uriSource;

    private JButton button;

    private RemovableObjectList<URI> uriSelection;


    public AnnotationSelectorPanel(MoveAnnotationAxiomsByURIKit kit) {
        this.kit = kit;
    }


    public void initialise() {
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        uriSource = new AnnotationURIList(getEditorKit());

        uriSelection = new RemovableObjectList<URI>();
        uriSelection.setCellRenderer(new DefaultListCellRenderer(){
            public Component getListCellRendererComponent(JList jList, Object o, int i, boolean b, boolean b1) {
                if (o instanceof URI){
                    o = getEditorKit().getOWLModelManager().getURIRendering((URI)o);
                }
                return super.getListCellRendererComponent(jList, o, i, b, b1);
            }
        });

        button = new JButton(new AbstractAction(">>") {
            public void actionPerformed(ActionEvent e) {
                addSelectedItems();
            }
        });

        JComponent buttonPanel = new Box(BoxLayout.PAGE_AXIS);
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(button);
        buttonPanel.add(Box.createVerticalGlue());

        add(Box.createHorizontalGlue());
        add(new JScrollPane(uriSource));
        add(buttonPanel);
        add(new JScrollPane(uriSelection));
        add(Box.createHorizontalGlue());
    }


    private void addSelectedItems() {
        Set<URI> selectedTypes = uriSource.getSelectedURIs();
        selectedTypes.removeAll(uriSelection.getListItems()); // don't allow duplicates
        uriSelection.addObject(selectedTypes);
    }


    private Set<URI> getSelection(){
        return new HashSet<URI>(uriSelection.getListItems());
    }


    public void dispose() {
        // do nothing
    }


    public String getID() {
        return getClass().getName();
    }


    public String getTitle() {
        return "Annotation axioms by URI";
    }


    public String getInstructions() {
        return "Please select the URIs of annotations.";
    }


    public void update() {
        uriSource.rebuildAnnotationURIList(getWizard().getSourceOntologies());
    }


    public void commit() {
        kit.setURIs(getSelection());
    }
}