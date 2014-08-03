package org.protege.editor.owl.ui.action;

import javax.swing.JComponent;

import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.ui.wizard.AbstractWizardPanel;
import org.protege.editor.core.ui.wizard.WizardPanel;
import org.protege.editor.owl.ui.OntologyFormatPanel;
import org.protege.editor.owl.ui.ontology.wizard.create.PhysicalLocationPanel;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

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
 * Date: Jan 6, 2009<br><br>
 */
public class OntologyFormatPage extends AbstractWizardPanel {
	private static final long serialVersionUID = -3310342304172021246L;

	public static final String ID = "ONTOLOGY_FORMAT_PANEL";

    private OntologyFormatPanel panel;


    public OntologyFormatPage(EditorKit editorKit) {
        super(ID, "Ontology Format", editorKit);
    }


    @Override
    protected void createUI(JComponent parent) {
        setInstructions("Please select the format in which the ontology will be saved (by default).\n\n" +
        		        "Note that the Manchester OWL Syntax does not support all OWL constructs (e.g. GCI's and\n" +
        		        "annotations of undeclared entities) and the Latex format cannot be reloaded");
         
        panel = new OntologyFormatPanel();
        
        parent.add(panel);
    }


    public OWLDocumentFormat getFormat() {
        return panel.getSelectedFormat();
    }


    @Override
    public Object getBackPanelDescriptor() {
        return PhysicalLocationPanel.ID;
    }


    @Override
    public Object getNextPanelDescriptor() {
        return WizardPanel.FINISH;
    }
}
