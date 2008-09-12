package org.protege.editor.owl.ui;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.util.OWLDataTypeUtils;
import org.protege.editor.owl.ui.frame.AnnotationURIList;
import org.protege.editor.owl.ui.list.OWLEntityListPanel;
import org.protege.editor.owl.ui.renderer.OWLCellRendererSimple;
import org.protege.editor.owl.ui.selector.*;
import org.semanticweb.owl.model.*;

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


    public UIHelper(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
    }


    private JComponent getParent() {
        return owlEditorKit.getWorkspace();
    }


    private OWLModelManager getOWLModelManager() {
        return owlEditorKit.getModelManager();
    }


    /**
     * Gets a URI whose base is the URI of the active ontology.  This
     * method prompts the user to enter a local name (for a new entity
     * for example) and then derived an absolute URI based on the URI
     * of the active ontology.
     * @param title   The title to display on the input dialog.
     * @param message The message to display on the input dialog
     * @return A <code>URI</code> or <code>null</code> if there was a
     *         problem creating the URI.
     */
    public URI getURIForActiveOntology(String title, String message) throws URISyntaxException, OWLException {
        return getURIForActiveOntology(title, message, null);
    }


    public URI getURIForActiveOntology(String title, String message, String initialValue) throws URISyntaxException,
            OWLException {
        // Prompt the user for a local name
        String name = (String) JOptionPane.showInputDialog(getParent(),
                                                           message,
                                                           title,
                                                           JOptionPane.INFORMATION_MESSAGE,
                                                           null,
                                                           null,
                                                           initialValue);
        if (name == null) {
            return null;
        }
        OWLOntology activeOntology = getOWLModelManager().getActiveOntology();
        if (activeOntology == null) {
            return null;
        }
        String base = activeOntology.getURI().toString();
        if (base.endsWith("#") == false && base.endsWith("/") == false) {
            base += "#";
        }
        return new URI(base + name);
    }


    public URI getURI(String title, String message) throws URISyntaxException {
        String uriString = JOptionPane.showInputDialog(getParent(), message, title, JOptionPane.INFORMATION_MESSAGE);
        if (uriString == null) {
            return null;
        }
        URI uri = new URI(uriString);
        return uri;
    }


    public OWLClass pickOWLClass() {
        OWLClassSelectorPanel clsPanel = new OWLClassSelectorPanel(owlEditorKit);
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
        OWLIndividualSelectorPanel indPanel = new OWLIndividualSelectorPanel(owlEditorKit);
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
        OWLObjectPropertySelectorPanel objPropPanel = new OWLObjectPropertySelectorPanel(owlEditorKit);
        if (showDialog("Select an object property", objPropPanel) == JOptionPane.OK_OPTION) {
            return objPropPanel.getSelectedObject();
        }
        else {
            return null;
        }
    }


    public OWLDataProperty pickOWLDataProperty() {
        OWLDataPropertySelectorPanel panel = new OWLDataPropertySelectorPanel(owlEditorKit);
        if (showDialog("Select an object property", panel) == JOptionPane.OK_OPTION) {
            return panel.getSelectedObject();
        }
        else {
            return null;
        }
    }


    public OWLDataType pickOWLDataType() {
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


    public URI pickAnnotationURI() {
        AnnotationURIList uriList = new AnnotationURIList(owlEditorKit);
        uriList.rebuildAnnotationURIList();

        if (showDialog("Select an annotation URI", new JScrollPane(uriList)) == JOptionPane.OK_OPTION){
            return uriList.getSelectedURI();
        }
        return null;
    }


    public String getHTMLOntologyList(Collection<OWLOntology> ontologies) {
        String result = "";
        for (OWLOntology ont : ontologies) {
            if (getOWLModelManager().getActiveOntology().equals(ont)) {
                result += "<font color=\"0000ff\"><b>";
                result += ont.getURI();
                result += "</font></b>";
            }
            else {
                result += ont.getURI();
            }
            if (getOWLModelManager().isMutable(ont) == false) {
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
        Set<String> extensions = new HashSet<String>();
        extensions.add("owl");
        extensions.add("rdf");
        extensions.add("xml");
        extensions.add("pom");
        JFrame f = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, getParent());
        if (f == null) {
            f = new JFrame();
        }
        return UIUtil.openFile(f, title, extensions);
    }


    public File saveOWLFile(String title) {
        Set<String> extensions = new HashSet<String>();
        extensions.add("owl");
        extensions.add("rdf");
        extensions.add("xml");
        return UIUtil.saveFile((JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, getParent()),
                               title,
                               extensions);
    }


    public OWLConstant createConstant() {
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
        c.setEditable(true);
        c.setModel(new DefaultComboBoxModel(new String[]{null, "en", "de", "es", "fr", "pt"}));
        return c;
    }


    public JComboBox getDatatypeSelector() {
        final OWLModelManager mngr = getOWLModelManager();
        List<OWLDataType> datatypeList = new ArrayList<OWLDataType>(new OWLDataTypeUtils(mngr.getOWLOntologyManager()).getBuiltinDatatypes());

        Collections.sort(datatypeList, mngr.getOWLObjectComparator());
        datatypeList.add(0, null);

        JComboBox c = new JComboBox(new DefaultComboBoxModel(datatypeList.toArray()));
        c.setPreferredSize(new Dimension(120, c.getPreferredSize().height));
        c.setRenderer(new OWLCellRendererSimple(owlEditorKit));
        return c;
    }
}
