package org.protege.editor.owl.ui.preferences;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.*;
/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


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
        Set<OWLAnnotationProperty> annotationProperties = new TreeSet<OWLAnnotationProperty>();
        for (OWLOntology ont : getOWLModelManager().getOntologies()) {
            annotationProperties.addAll(ont.getAnnotationPropertiesInSignature());
        }
        checkBoxURIMap = new HashMap<JCheckBox, URI>();
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
        Set<URI> hiddenURIs = new HashSet<URI>();
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
