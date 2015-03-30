package org.protege.editor.owl.ui.renderer;

import java.awt.Component;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 13-Sep-2007<br><br>
 */
public class OWLOntologyCellRenderer extends DefaultListCellRenderer {

    private OWLEditorKit editorKit;


    public OWLOntologyCellRenderer(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
    }


    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Pass an empty string as the value.... some toString operations may be expensive.
        JLabel label = (JLabel) super.getListCellRendererComponent(list, "", index, isSelected, cellHasFocus);

        if (value instanceof OWLOntology) {
            label.setText(getOntologyLabelText((OWLOntology) value, editorKit.getModelManager()));
            label.setIcon(editorKit.getWorkspace().getOWLIconProvider().getIcon((OWLOntology) value));
        }
        else if (value instanceof IRI) {
            label.setText(getOntologyLabelText((IRI) value, editorKit.getModelManager()));
        }
        return label;
    }

    // @@TODO move this somewhere more appropriate
    public static String getOntologyLabelText(OWLOntology ont, OWLModelManager mngr) {
        if (ont.getOntologyID().isAnonymous()) {
            return ont.getOntologyID().toString();
        }

        final IRI iri = ont.getOntologyID().getDefaultDocumentIRI().orNull();

        return getOntologyLabelText(iri, mngr);
    }

    public static String getOntologyLabelText(IRI iri, OWLModelManager mngr) {

        String shortForm = new OntologyIRIShortFormProvider().getShortForm(iri);

        if (shortForm != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("<html><body>");
            sb.append(shortForm);
            sb.append(" <font color=\"gray\">(");
            sb.append(iri.toString());
            sb.append(")</font></body></html>");
            return sb.toString();
        }

        return iri.toString();
    }
}
