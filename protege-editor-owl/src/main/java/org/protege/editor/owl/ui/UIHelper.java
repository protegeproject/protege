package org.protege.editor.owl.ui;

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

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class UIHelper {

    private OWLEditorKit owlEditorKit;

    public final static Set<String> OWL_EXTENSIONS;
    static {
    	Set<String> extensions = new HashSet<>();
        extensions.add("owl");
        extensions.add("ofn");
        extensions.add("omn");
        extensions.add("owx");
        extensions.add("rdf");
        extensions.add("xml");
        extensions.add("obo");
        extensions.add("n3");
        extensions.add("ttl");
        extensions.add("turtle");
        extensions.add("pom");
        OWL_EXTENSIONS = Collections.unmodifiableSet(extensions);
    }


    public UIHelper(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
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
        OWLClassSelectorPanel clsPanel = new OWLClassSelectorPanel(owlEditorKit);
        int ret = showDialog("Select a class", clsPanel);
        clsPanel.dispose();
        if (ret == JOptionPane.OK_OPTION) {
            return clsPanel.getSelectedObject();
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
        OWLEntityListPanel<E> panel = new OWLEntityListPanel<>(message, entities, owlEditorKit);
        if (showDialog("Select an entity", panel) == JOptionPane.OK_OPTION) {
            return panel.getSelectedObject();
        }
        else {
            return null;
        }
    }


    public OWLAnnotationProperty pickAnnotationProperty() {
    	OWLAnnotationPropertySelectorPanel panel = new OWLAnnotationPropertySelectorPanel(owlEditorKit);
    	try {
    		if (showDialog("Select an annotation property", panel) == JOptionPane.OK_OPTION) {
    			return panel.getSelectedObject();
    		}
    		else{
    			return null;
    		}
    	}
    	finally {
    		panel.dispose();
    	}
    }


    public String getHTMLOntologyList(Collection<OWLOntology> ontologies) {
        StringBuilder result = new StringBuilder();
        for (OWLOntology ont : ontologies) {
            com.google.common.base.Optional<IRI> defaultDocumentIRI = ont.getOntologyID().getDefaultDocumentIRI();
            if (defaultDocumentIRI.isPresent()) {
                if (getOWLModelManager().getActiveOntology().equals(ont)) {
                    result.append("<font color=\"0000ff\"><b>");
                    result.append(defaultDocumentIRI.get());
                    result.append("</font></b>");
                }
                else {
                    result.append(defaultDocumentIRI.get());
                }
            }
            if (!getOWLModelManager().isMutable(ont)) {
                result.append("&nbsp;");
                result.append(" <font color=\"ff0000\">(Not editable)</font>");
            }
            result.append("<br>");
        }
        return result.toString();
    }


    public int showOptionPane(String title, String message, int optionType, int messageType) {
        return JOptionPane.showConfirmDialog(getParent(), message, title, optionType, messageType);
    }


    public File chooseOWLFile(String title) {
        JFrame f = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, getParent());
        if (f == null) {
            f = new JFrame();
        }
        return UIUtil.openFile(f, title, "OWL File", OWL_EXTENSIONS);
    }

    public File chooseOWLOrCatalogFile(String title) {
        JFrame f = (JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, getParent());
        if (f == null) {
            f = new JFrame();
        }
        return UIUtil.openFile(f, title, "OWL or Catalog File", OWL_EXTENSIONS);
    }


    public File saveOWLFile(String title) {
        return UIUtil.saveFile((JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, getParent()),
                               title,
                               "OWL File", 
                               OWL_EXTENSIONS);
    }


    public OWLLiteral createConstant() {
        return null;
    }


    public JComboBox<String> getLanguageSelector() {
        JComboBox<String> c = new JComboBox<>();
        c.setSelectedItem(null);
        c.setEditable(true);
        c.setModel(new DefaultComboBoxModel<>(new String[]{null, "en", "de", "es", "fr", "pt"}));
        return c;
    }


    public JComboBox<OWLDatatype> getDatatypeSelector() {
        final OWLModelManager mngr = getOWLModelManager();
        List<OWLDatatype> datatypeList = new ArrayList<>(new OWLDataTypeUtils(mngr.getOWLOntologyManager()).getKnownDatatypes(mngr.getActiveOntologies()));

        Collections.sort(datatypeList, mngr.getOWLObjectComparator());
        datatypeList.add(0, null);

        JComboBox<OWLDatatype> c = new JComboBox<>(new DefaultComboBoxModel<>(datatypeList.toArray(new OWLDatatype [datatypeList.size()])));
        c.setPreferredSize(new Dimension(120, c.getPreferredSize().height));
        c.setRenderer(new OWLCellRendererSimple(owlEditorKit));
        return c;
    }
}
