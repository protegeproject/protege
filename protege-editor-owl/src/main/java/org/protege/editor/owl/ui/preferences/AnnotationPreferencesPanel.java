package org.protege.editor.owl.ui.preferences;

import java.awt.BorderLayout;
import java.net.URI;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 14-Aug-2007<br><br>
 */
public class AnnotationPreferencesPanel extends OWLPreferencesPanel {

    private Map<JCheckBox, URI> checkBoxURIMap;


    public void initialise() throws Exception {
        setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        Set<OWLAnnotationProperty> annotationProperties = new TreeSet<>();
        for (OWLOntology ont : getOWLModelManager().getOntologies()) {
            annotationProperties.addAll(ont.getAnnotationPropertiesInSignature());
        }
        checkBoxURIMap = new HashMap<>();
        for (OWLAnnotationProperty property : annotationProperties) {
            JCheckBox cb = new JCheckBox(getOWLModelManager().getRendering(property),
                                         getOWLEditorKit().getWorkspace().isHiddenAnnotationURI(property.getIRI().toURI()));
            checkBoxURIMap.put(cb, property.getIRI().toURI());
            box.add(cb);
            box.add(Box.createVerticalStrut(4));
            cb.setOpaque(false);
        }
        JPanel holder = new JPanel(new BorderLayout());
        holder.setBorder(ComponentFactory.createTitledBorder("Hidden annotation URIs"));
        holder.add(new JScrollPane(box));
        add(holder);
    }


    public void applyChanges() {
        Set<URI> hiddenURIs = new HashSet<>();
        for (JCheckBox cb : checkBoxURIMap.keySet()) {
            if (cb.isSelected()) {
                hiddenURIs.add(checkBoxURIMap.get(cb));
            }
        }
        getOWLEditorKit().getWorkspace().setHiddenAnnotationURIs(hiddenURIs);
    }


    public void dispose() throws Exception {
    }
}
