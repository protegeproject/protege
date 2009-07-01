package org.protege.editor.owl.ui.preferences;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.NamespaceUtil;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.util.*;
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
 * Bio-Health Informatics Group<br>
 * Date: 14-Aug-2007<br><br>
 */
public class AnnotationPreferencesPanel extends OWLPreferencesPanel {

    private Map<JCheckBox, URI> checkBoxURIMap;


    public void initialise() throws Exception {
        setLayout(new BorderLayout());
        Box box = new Box(BoxLayout.Y_AXIS);
        Set<URI> annotationURIs = new TreeSet<URI>();
        for (OWLOntology ont : getOWLModelManager().getOntologies()) {
            annotationURIs.addAll(ont.getAnnotationURIs());
        }
        checkBoxURIMap = new HashMap<JCheckBox, URI>();
        for (URI uri : annotationURIs) {
            NamespaceUtil nsUtil = new NamespaceUtil();
            String[] res = nsUtil.split(uri.toString(), null);
            String presentationText;
            if (res != null) {
                presentationText = nsUtil.getPrefix(res[0]) + ":" + res[1];
            }
            else {
                presentationText = uri.toString();
            }
            JCheckBox cb = new JCheckBox(presentationText,
                                         getOWLEditorKit().getWorkspace().isHiddenAnnotationURI(uri));
            checkBoxURIMap.put(cb, uri);
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
