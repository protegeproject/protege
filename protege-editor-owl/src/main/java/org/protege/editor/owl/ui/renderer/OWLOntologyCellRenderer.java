package org.protege.editor.owl.ui.renderer;

import com.google.common.base.Optional;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;
import org.semanticweb.owlapi.vocab.DublinCoreVocabulary;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.Component;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 13-Sep-2007<br><br>
 */
public class OWLOntologyCellRenderer extends DefaultListCellRenderer {

    private static final IRI DCTERM_TITLE = IRI.create("http://purl.org/dc/terms/title");

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
            label.setText(getOntologyLabelText(Optional.of((IRI)value), editorKit.getModelManager()));
        }
        return label;
    }

    // @@TODO move this somewhere more appropriate
    public static String getOntologyLabelText(OWLOntology ont, OWLModelManager mngr) {
        if (ont.getOntologyID().isAnonymous()) {
            return ont.getOntologyID().toString();
        }

        final Optional<IRI> iri = ont.getOntologyID().getDefaultDocumentIRI();
        String name = getOntologyDisplayName(ont);
        return formatOntologyLabelText(iri, name);
    }
    public static String getOntologyLabelText(Optional<IRI> iri, OWLModelManager mngr) {
        return formatOntologyLabelText(iri, null);
    }

    private static String formatOntologyLabelText(Optional<IRI> iri, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>");
        if (name != null) {
            sb.append(name);
        } else if (iri.isPresent()) {
            sb.append(new OntologyIRIShortFormProvider().getShortForm(iri.get()));
        } else {
            sb.append("Anonymous ontology");
        }
        if (iri.isPresent()) {
            sb.append(" <font color=\"gray\">(");
            sb.append(iri.get().toString());
            sb.append(")</font>");
        }
        sb.append("</body></html>");
        return sb.toString();
    }

    private static String getOntologyDisplayName(OWLOntology ont) {
        String label = null;
        String title = null;
        for (OWLAnnotation annot : ont.getAnnotations()) {
            if (annot.getValue().isLiteral()) {
                IRI property = annot.getProperty().getIRI();
                if (property.equals(OWLRDFVocabulary.RDFS_LABEL.getIRI())) {
                    label = annot.getValue().asLiteral().get().getLiteral();
                } else if (property.equals(DublinCoreVocabulary.TITLE.getIRI())) {
                    title = annot.getValue().asLiteral().get().getLiteral();
                } else if (property.equals(DCTERM_TITLE)) {
                    title = annot.getValue().asLiteral().get().getLiteral();
                }
            }
        }
        return label != null ? label : title;
    }
}
