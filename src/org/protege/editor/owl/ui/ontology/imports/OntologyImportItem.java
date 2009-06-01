package org.protege.editor.owl.ui.ontology.imports;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 28, 2009<br><br>
 */
class OntologyImportItem implements MListItem {

    private OWLOntology ont;

    private OWLImportsDeclaration decl;

    private OWLEditorKit eKit;


    private MListButton fixImportsButton = new FixImportsButton(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            handleImportsFix();
        }
    });

    public OntologyImportItem(OWLOntology ont, OWLImportsDeclaration decl, OWLEditorKit eKit) {
        this.ont = ont;
        this.decl = decl;
        this.eKit = eKit;
    }


    public List<MListButton> getAdditionalButtons() {
        OWLOntology ont = eKit.getOWLModelManager().getOWLOntologyManager().getImportedOntology(decl);
        // @@TODO what about anonymous ontologies?
        if (ont != null && !decl.getIRI().equals(ont.getOntologyID().getOntologyIRI())) {
            return Collections.singletonList(fixImportsButton);
        }
        return Collections.EMPTY_LIST;
    }


    private void handleImportsFix() {
        int ret = JOptionPane.showConfirmDialog(eKit.getWorkspace(),
                                                getMismatchedImportMessage(),
                                                "Mismatched import",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE);

        if(ret == JOptionPane.YES_OPTION) {
            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
            changes.add(new RemoveImport(ont, decl));
            final OWLModelManager mngr = eKit.getOWLModelManager();
            OWLOntology impOnt = mngr.getOWLOntologyManager().getImportedOntology(decl);
            // @@TODO what about anonymous ontologies?
            changes.add(new AddImport(ont, mngr.getOWLDataFactory().getOWLImportsDeclaration(impOnt.getOntologyID().getOntologyIRI())));
            mngr.applyChanges(changes);
        }
    }


    private String getMismatchedImportMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("The imports URI:<br>");
        sb.append("<font color=\"blue\">");
        sb.append(decl.getURI());
        sb.append("</font>");
        sb.append("<br>");
        sb.append("does not match the URI of the ontology that has been imported:<br>");
        sb.append("<font color=\"blue\">");
        OWLOntology ont = eKit.getOWLModelManager().getOWLOntologyManager().getImportedOntology(decl);
        // @@TODO what about anonymous ontologies?
        sb.append(ont == null ? "(Not loaded)" : ont.getOntologyID().getOntologyIRI());
        sb.append("</font><br><br>");
        sb.append("Do you want to fix the mismatch by modifying the imports statement?");
        sb.append("</body></html>");

        return sb.toString();
    }


    public boolean isEditable() {
        return false;
    }


    public void handleEdit() {
        // do nothing
    }


    public boolean isDeleteable() {
        return true;
    }


    public boolean handleDelete() {
        eKit.getModelManager().applyChange(new RemoveImport(ont, decl));
        return true;
    }


    public String getTooltip() {
        return "";
    }


    public OWLImportsDeclaration getImportDeclaration() {
        return decl;
    }


    private class FixImportsButton extends MListButton {

        public FixImportsButton(ActionListener actionListener) {
            super("Mismatched import!", Color.ORANGE, actionListener);
        }


        public void paintButtonContent(Graphics2D g) {
            Rectangle bounds = getBounds();
            g.translate(bounds.x, bounds.y - 1);
            g.drawLine(bounds.width / 2, 4, 4, bounds.height - 4);
            g.drawLine(bounds.width / 2, 4, bounds.width - 4, bounds.height - 4);
            g.drawLine(4, bounds.height - 4, bounds.width - 4, bounds.height - 4);
            g.translate(-bounds.x, -bounds.y + 1);
        }


        public Color getBackground() {
            return Color.ORANGE;
        }
    }
}