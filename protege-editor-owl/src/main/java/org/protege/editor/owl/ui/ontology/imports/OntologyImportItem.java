package org.protege.editor.owl.ui.ontology.imports;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import javax.swing.JOptionPane;

import org.protege.editor.core.ui.list.MListButton;
import org.protege.editor.core.ui.list.MListItem;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.RemoveImport;

import com.google.common.base.Optional;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 28, 2009<br><br>
 */
class OntologyImportItem implements MListItem {

    private OWLOntology ont;

    private OWLImportsDeclaration decl;

    private OWLEditorKit eKit;


    private MListButton fixImportsButton = new FixImportsButton(e -> {
        handleImportsFix();
    });

    public OntologyImportItem(OWLOntology ont, OWLImportsDeclaration decl, OWLEditorKit eKit) {
        this.ont = ont;
        this.decl = decl;
        this.eKit = eKit;
    }


    public List<MListButton> getAdditionalButtons() {
        OWLOntology ont = eKit.getOWLModelManager().getOWLOntologyManager().getImportedOntology(decl);
        if(ont == null) {
            return Collections.emptyList();
        }
        Optional<IRI> defaultDocumentIRI = ont.getOntologyID().getDefaultDocumentIRI();
        if (Optional.of(decl.getIRI()).equals(defaultDocumentIRI)) {
            return Collections.emptyList();
        }
        return Collections.singletonList(fixImportsButton);
    }


    private void handleImportsFix() {
        int ret = JOptionPane.showConfirmDialog(eKit.getWorkspace(),
                                                getMismatchedImportMessage(),
                                                "Mismatched import",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE);

        if(ret == JOptionPane.YES_OPTION) {
            List<OWLOntologyChange> changes = new ArrayList<>();
            changes.add(new RemoveImport(ont, decl));
            final OWLModelManager mngr = eKit.getOWLModelManager();
            OWLOntology impOnt = mngr.getOWLOntologyManager().getImportedOntology(decl);
            if (impOnt != null) {
                Optional<IRI> defaultDocumentIRI = impOnt.getOntologyID().getDefaultDocumentIRI();
                if (defaultDocumentIRI.isPresent()) {
                    changes.add(new AddImport(ont, mngr.getOWLDataFactory().getOWLImportsDeclaration(defaultDocumentIRI.get())));
                    mngr.applyChanges(changes);
                }
            }
        }
    }


    private String getMismatchedImportMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        sb.append("The imports URI:<br>");
        sb.append("<font color=\"blue\">");
        sb.append(decl.getIRI());
        sb.append("</font>");
        sb.append("<br>");
        sb.append("does not match the URI of the ontology that has been imported:<br>");
        sb.append("<font color=\"blue\">");
        OWLOntology ont = eKit.getOWLModelManager().getOWLOntologyManager().getImportedOntology(decl);
        // @@TODO what about anonymous ontologies?
        sb.append(ont == null ? "(Not loaded)" : ont.getOntologyID().getDefaultDocumentIRI().get());
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
