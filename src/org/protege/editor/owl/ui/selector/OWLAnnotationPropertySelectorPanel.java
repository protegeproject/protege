package org.protege.editor.owl.ui.selector;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;


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
