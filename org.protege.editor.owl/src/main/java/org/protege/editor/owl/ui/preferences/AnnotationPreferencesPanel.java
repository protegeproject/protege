package org.protege.editor.owl.ui.preferences;

import java.awt.BorderLayout;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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
import org.semanticweb.owlapi.model.parameters.Imports;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 14-Aug-2007<br><br>
 */
public class AnnotationPreferencesPanel extends OWLPreferencesPanel {

    private Map<JCheckBox, URI> checkBoxURIMap;


    @Override
    public void initialise() throws Exception {
        setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        Set<OWLAnnotationProperty> annotationProperties = new TreeSet<OWLAnnotationProperty>();
        for (OWLOntology ont : getOWLModelManager().getOntologies()) {
            annotationProperties.addAll(ont
                    .getAnnotationPropertiesInSignature(Imports.EXCLUDED));
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


    @Override
    public void applyChanges() {
        Set<URI> hiddenURIs = new HashSet<URI>();
        for (JCheckBox cb : checkBoxURIMap.keySet()) {
            if (cb.isSelected()) {
                hiddenURIs.add(checkBoxURIMap.get(cb));
            }
        }
        getOWLEditorKit().getWorkspace().setHiddenAnnotationURIs(hiddenURIs);
    }


    @Override
    public void dispose() throws Exception {
    }
}
