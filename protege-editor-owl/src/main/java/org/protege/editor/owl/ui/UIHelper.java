package org.protege.editor.owl.ui;

import java.awt.Dimension;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.lang.LangCode;
import org.protege.editor.owl.model.lang.LangCodeRegistry;
import org.protege.editor.owl.model.util.OWLDataTypeUtils;
import org.protege.editor.owl.ui.lang.LangCodeEditor;
import org.protege.editor.owl.ui.lang.LangCodeRenderer;
import org.protege.editor.owl.ui.list.OWLEntityListPanel;
import org.protege.editor.owl.ui.renderer.OWLCellRendererSimple;
import org.protege.editor.owl.ui.selector.OWLAnnotationPropertySelectorPanel;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLDataPropertySelectorPanel;
import org.protege.editor.owl.ui.selector.OWLDataTypeSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLIndividualSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.protege.editor.owl.ui.selector.OWLOntologySelectorPanel;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import com.google.common.collect.ImmutableList;


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

    public final static Set<String> OWL_EXTENSIONS = Collections.unmodifiableSet(new HashSet<>(Extensions.getExtensions()));


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


    public File saveOWLFile(String title) {
        return saveOWLFile(title, OWL_EXTENSIONS);
    }


    public File saveOWLFile(String title, Set<String> extensions) {
        return UIUtil.saveFile((JFrame) SwingUtilities.getAncestorOfClass(JFrame.class, getParent()),
                               title,
                               "OWL File",
                               extensions);
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

    public JComboBox<LangCode> getLangCodeSelector() {
        LangCodeRegistry registry = LangCodeRegistry.get();
        JComboBox<LangCode> comboBox = new JComboBox<>();
        comboBox.setSelectedItem(null);
        comboBox.setEditable(true);
        ImmutableList<LangCode> langCodes = registry.getLangCodes();
        List<LangCode> langCodeList = new ArrayList<>(langCodes);
        langCodeList.add(0, null);
        comboBox.setModel(new DefaultComboBoxModel(
                langCodeList.toArray()));
        comboBox.setEditor(new LangCodeEditor(registry));
        comboBox.setRenderer(new LangCodeRenderer());
        return comboBox;
    }


    public JComboBox<OWLDatatype> getDatatypeSelector() {
        OWLModelManager mngr = getOWLModelManager();
        OWLDataFactory dataFactory = mngr.getOWLDataFactory();
        Set<OWLDatatype> datatypes = new OWLDataTypeUtils(mngr.getOWLOntologyManager()).getKnownDatatypes(mngr.getActiveOntologies());


        OWLDatatype decimal = OWL2Datatype.XSD_DECIMAL.getDatatype(dataFactory);
        datatypes.remove(decimal);
        OWLDatatype integer = OWL2Datatype.XSD_INTEGER.getDatatype(dataFactory);
        datatypes.remove(integer);
        OWLDatatype string = OWL2Datatype.XSD_STRING.getDatatype(dataFactory);
        datatypes.remove(string);
        OWLDatatype bool = OWL2Datatype.XSD_BOOLEAN.getDatatype(dataFactory);
        datatypes.remove(bool);
        OWLDatatype datetime = OWL2Datatype.XSD_DATE_TIME.getDatatype(dataFactory);
        datatypes.remove(datetime);

        List<OWLDatatype> datatypeList = new ArrayList<>(datatypes);
        datatypeList.sort(mngr.getOWLObjectComparator());
        datatypeList.add(0, null);
        datatypeList.add(0, bool);
        datatypeList.add(0, datetime);
        datatypeList.add(0, integer);
        datatypeList.add(0, string);
        datatypeList.add(0, decimal);
        datatypeList.add(0, null);

        JComboBox<OWLDatatype> c = new JComboBox<>(new DefaultComboBoxModel<>(datatypeList.toArray(new OWLDatatype[0])));
        c.setPreferredSize(new Dimension(120, c.getPreferredSize().height));
        c.setRenderer(new OWLCellRendererSimple(owlEditorKit));
        return c;
    }
}
