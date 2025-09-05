package org.protege.editor.owl.ui.renderer;

import com.google.common.base.Optional;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;
import org.semanticweb.owlapi.vocab.DublinCoreVocabulary;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
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
        Map<String, String> namesByLang = new HashMap<String, String>();
        for (OWLAnnotation annot : ont.getAnnotations()) {
            if (annot.getValue().isLiteral()) {
                IRI property = annot.getProperty().getIRI();
                if (property.equals(OWLRDFVocabulary.RDFS_LABEL.getIRI())
                    || property.equals(DublinCoreVocabulary.TITLE.getIRI())
                    || property.equals(DCTERM_TITLE)) {
                    OWLLiteral value = annot.getValue().asLiteral().get();
                    String lang = value.getLang().toLowerCase();
                    if (lang == null || lang.isEmpty()) {
                        namesByLang.put("", value.getLiteral());
                    } else {
                        namesByLang.put(lang, value.getLiteral());
                        int sep = lang.indexOf('-');
                        if (sep != -1) {
                            // This is a language dialect (e.g. "en-GB"); we also store it under the
                            // corresponding language family ("en"), unless we already a title for
                            // that family.
                            String langFamily = lang.substring(0, sep);
                            if (!namesByLang.containsKey(langFamily)) {
                                namesByLang.put(langFamily, value.getLiteral());
                            }
                        }
                    }
                }
            }
        }

        String name = null;
        for (String langPref : OWLRendererPreferences.getInstance().getAnnotationLangs()) {
            name = namesByLang.get(langPref.toLowerCase());
            if (name != null) {
                return name;
            }
        }

        // No lang match, look for a language-neutral title
        name = namesByLang.get("");
        if (name == null && !namesByLang.isEmpty()) {
            // No lang match and no lang-neutral value, pick whatever we have
            name = namesByLang.entrySet().iterator().next().getValue();
        }
        return name;
    }
}
