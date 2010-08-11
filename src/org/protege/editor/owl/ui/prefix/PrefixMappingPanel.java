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

    private Map<OWLOntologyID, PrefixMapperTable> prefixManagers = new HashMap<OWLOntologyID, PrefixMapperTable>();
    private JComboBox ontologiesList;
    private CardLayout cards;
    
    public PrefixMappingPanel(OWLEditorKit owlEditorKit) {
        ViewBarComponent vbc = new ViewBarComponent("Prefix mappings",
                PropertyUtil.getColor(ProtegeProperties.getInstance().getProperty(ProtegeProperties.ONTOLOGY_COLOR_KEY), Color.GRAY),
                ComponentFactory.createScrollPane(createIndexedPrefixTables(owlEditorKit)));
        setLayout(new BorderLayout(7, 7));
        add(vbc);
        vbc.addAction(new AddPrefixMappingAction(this));
        vbc.addAction(new GeneratePrefixFromOntologyAction(owlEditorKit, this));
        vbc.addAction(new RemovePrefixMappingAction(this));
    }
    
    public PrefixMapperTable getCurrentPrefixMapperTable() {
        OWLOntology ontology  = (OWLOntology) ontologiesList.getSelectedItem();
        return prefixManagers.get(ontology.getOntologyID());
    }

    public Dimension getPreferredSize() {
        return new Dimension(800, 500);
    }
    
    private JPanel createIndexedPrefixTables(final OWLEditorKit owlEditorKit) {
    	JPanel panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	JComboBox ontologiesList = createOntologyList(owlEditorKit);
    	panel.add(ontologiesList);
    	final JPanel prefixTable = createPrefixTables(owlEditorKit.getModelManager());
    	panel.add(prefixTable);
    	cards.show(prefixTable, owlEditorKit.getModelManager().getActiveOntology().getOntologyID().getOntologyIRI().toString());
    	ontologiesList.addItemListener(new ItemListener() {
			
			public void itemStateChanged(ItemEvent e) {
				OWLOntology ontology = owlEditorKit.getModelManager().getActiveOntology();
				String name = ontology.getOntologyID().getOntologyIRI().toString();
				cards.show(prefixTable, name);
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
    
    private JPanel createPrefixTables(OWLModelManager modelManager) {
        JPanel tables = new JPanel();
        cards = new CardLayout();
        tables.setLayout(cards);
        for (OWLOntology ontology : modelManager.getActiveOntologies()) {
        	PrefixMapperManager prefixManager = new OntologyPrefixMapperManager(ontology);
        	PrefixMapperTable table = new PrefixMapperTable(prefixManager);
        	prefixManagers.put(ontology.getOntologyID(), table);
        	tables.add(table, ontology.getOntologyID().getOntologyIRI().toString());
        }
        return tables;
    }

    public static boolean showDialog(OWLEditorKit owlEditorKit) {
        PrefixMappingPanel panel = new PrefixMappingPanel(owlEditorKit);
        int ret = JOptionPaneEx.showConfirmDialog(owlEditorKit.getWorkspace(),
                                        "Prefix mappings",
                                        panel,
                                        JOptionPane.PLAIN_MESSAGE,
                                        JOptionPane.OK_CANCEL_OPTION,
                                        panel);
        if(ret == JOptionPane.OK_OPTION) {
            for (PrefixMapperTable table : panel.prefixManagers.values()) {
                table.getPrefixMapperManager().save();
            }
            // Reset the renderer to force an update - there should
            // probably be an easier way to do this.
            owlEditorKit.getModelManager().setOWLEntityRenderer(owlEditorKit.getModelManager().getOWLEntityRenderer());
            return true;
        }
        return false;
    }



}
