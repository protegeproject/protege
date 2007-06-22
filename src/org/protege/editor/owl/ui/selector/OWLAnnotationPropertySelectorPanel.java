package org.protege.editor.owl.ui.selector;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;

import javax.swing.*;
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
 * Medical Informatics Group<br>
 * Date: Apr 27, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLAnnotationPropertySelectorPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(OWLAnnotationPropertySelectorPanel.class);

    private JList propertyList;


    public OWLAnnotationPropertySelectorPanel(OWLEditorKit owlEditorKit, String message, boolean onlyActiveOntology) {
        add(new JLabel("Re-implement me!"));
//        setLayout(new BorderLayout(7, 7));
//        // Encapsulate the message in html tags so that it wraps
//        add(new JLabel("<html><body>" + message + "</body></html>"));
//        Set<OWLAnnotationProperty> props = new TreeSet<OWLAnnotationProperty>(new OWLEntityComparator(owlEditorKit.getOWLModelManager()));
//        for (OWLOntology ont : owlEditorKit.getOWLModelManager().getActiveOntologies()) {
//            if (onlyActiveOntology) {
//                if (ont.equals(owlEditorKit.getOWLModelManager().getActiveOntology())) {
//                    props.addAll(ont.getAnnotationProperties());
//                }
//            } else {
//                props.addAll(ont.getAnnotationProperties());
//            }
//        }
//        // Ensure that built in annotation properties are included.
//        addBuiltInAnnotationProperties(props, owlEditorKit.getOWLModelManager().getOWLDataFactory());
//        propertyList = new JList(props.toArray());
//        propertyList.setCellRenderer(owlEditorKit.getOWLWorkspace().createOWLCellRenderer());
//        add(ComponentFactory.createScrollPane(propertyList));
    }

//    public OWLAnnotationProperty getSelectedProperty() {
//        OWLAnnotationProperty selProp = (OWLAnnotationProperty) propertyList.getSelectedValue();
//        return selProp;
//    }
//
//    private static void addBuiltInAnnotationProperties(Set<OWLAnnotationProperty> properties, OWLDataFactory factory) {
//        for(String s : OWLVocabularyAdapter.INSTANCE.getAnnotationProperties()) {
//            try {
//                OWLAnnotationProperty prop  = factory.getOWLAnnotationProperty(URI.create(s));
//                properties.add(prop);
//            } catch (OWLException e) {
//                logger.error(e);
//            }
//
//        }
//    }


}
