package org.protege.editor.owl.ui.ontology.migration;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;

import javax.swing.*;
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
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MigrationStartPanel extends AbstractOWLWizardPanel {

    public static final String ID = MigrationStartPanel.class.getName();


    public MigrationStartPanel(OWLEditorKit owlEditorKit) {
        super(ID, "Ontology migration", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("This wizard will migrate the classes, properties and individuals that " + "are contained in the active ontology (" + getOWLModelManager().getActiveOntology() + ") to " + "a new ontology.  This operation is typically used to create a new version of an ontology. However, " + "the current version of the ontology is left untouched.\n" + "Press 'Contine' to perform the migration.");
    }
}
