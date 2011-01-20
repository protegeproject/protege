package org.protege.editor.owl.ui.prefix;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.protege.editor.core.PropertyUtil;
import org.protege.editor.core.ProtegeProperties;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.view.ViewBarComponent;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.OWLOntologyCellRenderer;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PrefixMappingPanel extends JPanel {
	private static final long serialVersionUID = 826728863799238361L;
    private JComboBox ontologiesList;
    private PrefixMapperTables tables;
    
    public PrefixMappingPanel(OWLEditorKit owlEditorKit) {
        ViewBarComponent vbc = new ViewBarComponent("Prefix mappings",
                PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.ONTOLOGY_COLOR_KEY), Color.GRAY),
                ComponentFactory.createScrollPane(createIndexedPrefixTables(owlEditorKit)));
        setLayout(new BorderLayout(7, 7));
        add(vbc);
        vbc.addAction(new AddPrefixMappingAction(tables));
        vbc.addAction(new GeneratePrefixFromOntologyAction(owlEditorKit, tables));
        vbc.addAction(new RemovePrefixMappingAction(tables));
    }
    
    public PrefixMapperTable getCurrentPrefixMapperTable() {
        return tables.getPrefixMapperTable();
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 500);
    }
    
    private JPanel createIndexedPrefixTables(final OWLEditorKit owlEditorKit) {
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	createOntologyList(owlEditorKit);
    	panel.add(ontologiesList);
    	tables = new PrefixMapperTables(owlEditorKit.getModelManager());
    	panel.add(tables);
    	ontologiesList.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				OWLOntology ontology = (OWLOntology) ontologiesList.getSelectedItem();
				tables.setOntology(ontology);
			}
		});
    	return  panel;
    }
    
    public JComboBox getOntologyList() {
    	return ontologiesList;
    }
    
    private JComboBox createOntologyList(OWLEditorKit owlEditorKit) {
        ontologiesList = new JComboBox();
        OWLModelManager modelManager = owlEditorKit.getModelManager();
        ontologiesList.setRenderer(new OWLOntologyCellRenderer(owlEditorKit));
        TreeSet<OWLOntology> ts = new TreeSet<OWLOntology>(modelManager.getOWLObjectComparator());
        ts.addAll(modelManager.getOntologies());
        ontologiesList.setModel(new DefaultComboBoxModel(ts.toArray()));
        ontologiesList.setSelectedItem(modelManager.getActiveOntology());
        return ontologiesList;
    }

    public static boolean showDialog(OWLEditorKit owlEditorKit) {
        PrefixMappingPanel panel = new PrefixMappingPanel(owlEditorKit);
        int ret = JOptionPaneEx.showConfirmDialog(owlEditorKit.getWorkspace(),
                                        "Prefix mappings",
                                        panel,
                                        JOptionPane.PLAIN_MESSAGE,
                                        JOptionPane.OK_CANCEL_OPTION,
                                        panel);
        boolean changed = false;
        if(ret == JOptionPane.OK_OPTION) {
        	OWLModelManager modelManager = owlEditorKit.getModelManager();
            for (OWLOntology ontology : modelManager.getOntologies()) {
            	PrefixMapperTable table = panel.tables.getPrefixMapperTable(ontology);
                if (table.getModel().commitPrefixes()) {
                	changed = true;
                	modelManager.setDirty(ontology);
                }
            }
            if (changed) {
            	owlEditorKit.getModelManager().refreshRenderer();
            }
            return true;
        }
        return false;
    }



}
