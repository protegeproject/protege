package org.protege.editor.owl.ui.view;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.FilteringOWLOntologyChangeListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
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
 * Bio-Health Informatics Group<br>
 * Date: 21-Feb-2007<br><br>
 */
public class OWLDataPropertyCharacteristicsViewComponent extends AbstractOWLDataPropertyViewComponent {

    private static final Logger logger = Logger.getLogger(OWLDataPropertyCharacteristicsViewComponent.class);


    private JCheckBox checkBox;

    private OWLDataProperty prop;

    private OWLOntologyChangeListener listener;


    protected OWLDataProperty updateView(OWLDataProperty property) {
        prop = property;
        checkBox.setSelected(false);
        for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
            if (ont.getFunctionalDataPropertyAxiom(prop) != null) {
                checkBox.setSelected(true);
                break;
            }
        }
        return property;
    }


    public void disposeView() {
        super.disposeView();
        getOWLModelManager().removeOntologyChangeListener(listener);
    }


    public void initialiseView() throws Exception {
        setLayout(new BorderLayout());
        checkBox = new JCheckBox("Functional");
        add(checkBox, BorderLayout.NORTH);
        listener = new FilteringOWLOntologyChangeListener() {
            public void visit(OWLFunctionalDataPropertyAxiom axiom) {
                updateView(prop);
            }
        };
        getOWLModelManager().addOntologyChangeListener(listener);
        checkBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateOntology();
            }
        });
    }


    private void updateOntology() {
        if (prop == null) {
            return;
        }
        OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
        OWLAxiom ax = df.getOWLFunctionalDataPropertyAxiom(prop);
        if (checkBox.isSelected()) {
            OWLOntology ont = getOWLModelManager().getActiveOntology();
            getOWLModelManager().applyChange(new AddAxiom(ont, ax));
        }
        else {
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            for (OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
                changes.add(new RemoveAxiom(ont, ax));
            }
            getOWLModelManager().applyChanges(changes);
        }
    }
}
