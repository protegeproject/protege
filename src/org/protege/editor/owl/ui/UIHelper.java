package org.protege.editor.owl.ui;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.util.OWLDataTypeUtils;
import org.protege.editor.owl.ui.list.OWLEntityListPanel;
import org.protege.editor.owl.ui.renderer.OWLCellRendererSimple;
import org.protege.editor.owl.ui.selector.*;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 24, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class UIHelper {

    private static Logger logger = Logger.getLogger(UIHelper.class);

    private OWLEditorKit owlEditorKit;

    private Set<String> extensions = new HashSet<String>();


    public UIHelper(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
        extensions.add("owl");
        extensions.add("rdf");
        extensions.add("xml");
        extensions.add("krss");
        extensions.add("obo");
        extensions.add("n3");
        extensions.add("ttl");
        extensions.add("turtle");
        extensions.add("pom");
    }


    private JComponent getParent() {
        return owlEditorKit.getWorkspace();
    }


    private OWLModelManager getOWLModelManager() {
        return owlEditorKit.getModelManager();
    }


    public URI getURI(String title, String message) throws URISyntaxException {
        String uriString = JOptionPane.showInputDialog(getParent(), message, title, JOptionPane.INFORMATION_MESSAGE);
        if (uriString == null) {
            return null;
        }
        return new URI(uriString);
    }


    public OWLClass pickOWLClass() {
        OWLClassSelectorPanel clsPanel = owlEditorKit.getOWLWorkspace().getOWLComponentFactory().getOWLClassSelectorPanel();
        int ret = showDialog("Select a class", clsPanel);
        if (ret == JOptionPane.OK_OPTION) {
            OWLClass cls = clsPanel.getSelectedObject();
            clsPanel.dispose();
            return cls;
        }
        else {
            return null;
        }
    }


    public OWLIndividual pickOWLIndividual() {
        OWLIndividualSelectorPanel indPanel = owlEditorKit.getOWLWorkspace().getOWLComponentFactory().getOWLIndividualSelectorPanel();
        int ret = showDialog("Select an individual", indPanel);
        if (ret == JOptionPane.OK_OPTION) {
            OWLIndividual ind = indPanel.getSelectedObject();
            indPanel.dispose();
            return ind;
        }
        else {
            return null;
        }
    }


    public OWLOntology pickOWLOntology() {
        OWLOntologySelectorPanel ontPanel = new OWLOntologySelectorPanel(owlEditorKit);
        ontPanel.setMultipleSelectionEnabled(false);
        int ret = showDialog("Select an ontology", ontPanel);
        if (ret == JOptionPane.OK_OPTION){
            return ontPanel.getSelectedOntology();
        }
        else{
            return null;
        }
    }


    public Set<OWLOntology> pickOWLOntologies() {
        OWLOntologySelectorPanel ontPanel = new OWLOntologySelectorPanel(owlEditorKit);
        int ret = showDialog("Select ontologies", ontPanel);
        if (ret == JOptionPane.OK_OPTION) {
            return ontPanel.getSelectedOntologies();
        }
        else {
            return Collections.emptySet();
        }
    }


    public int showDialog(String title, JComponent component) {
        return JOptionPaneEx.showConfirmDialog(getParent(),
                                               title,
                                               component,
                                               JOptionPane.PLAIN_MESSAGE,
                                               JOptionPane.OK_CANCEL_OPTION,
                                               null);
    }


    public int showDialog(String title, JComponent component, int options) {
        return JOptionPaneEx.showConfirmDialog(getParent(),
                                               title,
                                               component,
                                               JOptionPane.PLAIN_MESSAGE,
                                               options,
                                               null);
    }


    public int showDialog(String title, JComponent component, JComponent focusedComponent) {
        return JOptionPaneEx.showConfirmDialog(getParent(),
                                               title,
                                               component,
                                               JOptionPane.PLAIN_MESSAGE,
                                               JOptionPane.OK_CANCEL_OPTION,
                                               focusedComponent);
    }


    public int showValidatingDialog(String title, JComponent component, JComponent focusedComponent){
        return JOptionPaneEx.showValidatingConfirmDialog(getParent(),
                                                         title,
                                                         component,
                                                         JOptionPane.PLAIN_MESSAGE,
                                                         JOptionPane.OK_CANCEL_OPTION,
                                                         focusedComponent);
    }


    public OWLObjectProperty pickOWLObjectProperty() {
        OWLObjectPropertySelectorPanel objPropPanel = owlEditorKit.getOWLWorkspace().getOWLComponentFactory().getOWLObjectPropertySelectorPanel();
        if (showDialog("Select an object property", objPropPanel) == JOptionPane.OK_OPTION) {
            return objPropPanel.getSelectedObject();
        }
        else {
            return null;
        }
    }


    public OWLDataProperty pickOWLDataProperty() {
        OWLDataPropertySelectorPanel panel = owlEditorKit.getOWLWorkspace().getOWLComponentFactory().getOWLDataPropertySelectorPanel();
        if (showDialog("Select an object property", panel) == JOptionPane.OK_OPTION) {
            return panel.getSelectedObject();
        }
        else {
            return null;
        }
    }


    public OWLDatatype pickOWLDatatype() {
        OWLDataTypeSelectorPanel panel = new OWLDataTypeSelectorPanel(owlEditorKit);
        if (showDialog("Select a datatype", panel) == JOptionPane.OK_OPTION) {
            return panel.getSelectedObject();
        }
        else{
            return null;
        }
    }


    public <E extends OWLEntity> E pickOWLEntity(String message, Set<E> entities, OWLModelManager owlModelManager) {
        OWLEntityListPanel<E> panel = new OWLEntityListPanel<E>(message, entities, owlEditorKit);
        if (showDialog("Select an entity", panel) == JOptionPane.OK_OPTION) {
            return panel.getSelectedObject();
        }
        else {
            return null;
        }
    }


    public OWLAnnotationProperty pickAnnotationProperty() {
        OWLAnnotationPropertySelectorPanel panel = new OWLAnnotationPropertySelectorPanel(owlEditorKit);
        if (showDialog("Select an annotation property", panel) == JOptionPane.OK_OPTION) {
            return panel.getSelectedObject();
        }
        else{
            return null;
        }
    }


    public String getHTMLOntologyList(Collection<OWLOntology> ontologies) {
        String result = "";
        for (OWLOntology ont : ontologies) {
            if (getOWLModelManager().getActiveOntology().equals(ont)) {
                result += "<font color=\"0000ff\"><b>";
                result += ont.getOntologyID().getDefaultDocumentIRI();
                result += "</font></b>";
            }
            else {
                result += ont.getOntologyID();
            }
            if (!getOWLModelManager().isMutable(ont)) {
                result += "&nbsp;";
                result += " <font color=\"ff0000\">(Not editable)</font>";
            }
            result += "<br>";
        }
        return result;
    }


    public int showOptionPane(String title, String message, int optionType, int messageType) {
        return JOptionPane.showConfirmDialog(getParent(), message, title, optionType, messageType);
    }


    public File chooseOWLFile(String title) {
        JFrame f = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, getParent());
        if (f == null) {
            f = new JFrame();
        }
        return UIUtil.openFile(f, title, extensions);
    }


    public File saveOWLFile(String title) {
        return UIUtil.saveFile((JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, getParent()),
                               title,
                               extensions);
    }


    public OWLLiteral createConstant() {
//        DataValueCreationPanel dataValuePanel = new DataValueCreationPanel(owlEditorKit);
//        if (showDialog("Create a data value", dataValuePanel) == JOptionPane.OK_OPTION) {
//            return dataValuePanel.getDataValue();
//        } else {
//            return null;
//        }
        return null;
    }


    public JComboBox getLanguageSelector() {
        JComboBox c = new JComboBox();
        c.setSelectedItem(null);
        c.setEditable(true);
        c.setModel(new DefaultComboBoxModel(new String[]{null, "en", "de", "es", "fr", "pt"}));
        return c;
    }


    public JComboBox getDatatypeSelector() {
        final OWLModelManager mngr = getOWLModelManager();
        List<OWLDatatype> datatypeList = new ArrayList<OWLDatatype>(new OWLDataTypeUtils(mngr.getOWLOntologyManager()).getKnownDatatypes(mngr.getActiveOntologies()));

        Collections.sort(datatypeList, mngr.getOWLObjectComparator());
        datatypeList.add(0, null);

        JComboBox c = new JComboBox(new DefaultComboBoxModel(datatypeList.toArray()));
        c.setPreferredSize(new Dimension(120, c.getPreferredSize().height));
        c.setRenderer(new OWLCellRendererSimple(owlEditorKit));
        return c;
    }
}
