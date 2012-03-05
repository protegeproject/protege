package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.library.OntologyCatalogManager;
import org.protege.editor.owl.ui.library.OntologyLibraryPanel;
import org.protege.editor.owl.ui.ontology.imports.wizard.GetImportsVisitor;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.Entry;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LibraryPage extends OntologyImportPage {

    private static final Logger logger = Logger.getLogger(LibraryPage.class);

    public static final String ID = "LibraryPage";

    private JList importList;
    private DefaultListModel importListModel;


    public LibraryPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import from ontology library", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("The list below shows ontologies that are contained in the available ontology libaries.  Select the ones you want to import.");;
        parent.setLayout(new BorderLayout());
        importListModel = new DefaultListModel();
        importList = new JList(importListModel);
        importList.setCellRenderer(new Renderer());
        calculatePossibleImports();
        importList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    updateNextButtonEnabled();
                }
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        JButton addRepository = new JButton("Edit Repositories");
        addRepository.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
				handleEditRepositories();
			}
		});
        buttonPanel.add(addRepository);
        
		parent.add(buttonPanel, BorderLayout.NORTH);
        parent.add(ComponentFactory.createScrollPane(importList), BorderLayout.CENTER);	
        // no advanced option for this particular type of import
    }
    
    private void handleEditRepositories() {
    	OntologyCatalogManager catalogManager = getOWLModelManager().getOntologyCatalogManager();
    	File activeCatalogFile = OntologyCatalogManager.getCatalogFile(catalogManager.getActiveCatalog());
    	try {
			OntologyLibraryPanel.showDialog(getOWLEditorKit(), activeCatalogFile);
			calculatePossibleImports();
		} catch (Exception e) {
			ProtegeApplication.getErrorLog().logError(e);
		}
    }
    
    private void calculatePossibleImports() {
        GetImportsVisitor getter = new GetImportsVisitor();
        XMLCatalog library = getOWLEditorKit().getOWLModelManager().getOntologyCatalogManager().getActiveCatalog();
        if (library != null) {
        	for (Entry e : library.getEntries()) {
        		e.accept(getter);
        	}
        }
        importListModel.clear();
        for (ImportInfo ii : getter.getImports()) {
            importListModel.addElement(ii);
        }
    }


    public void displayingPanel() {
        updateNextButtonEnabled();
        calculatePossibleImports();
    }

    @Override
    public void aboutToHidePanel() {
        OntologyImportWizard wizard = (OntologyImportWizard) getWizard();
        wizard.setImportsAreFinal(true);
        wizard.setCustomizeImports(false);
        wizard.clearImports();
        for (int index : importList.getSelectedIndices()) {
            wizard.addImport((ImportInfo) importListModel.getElementAt(index));
        }
        ((SelectImportLocationPage) getWizardModel().getPanel(SelectImportLocationPage.ID)).setBackPanelDescriptor(ID);
        ((ImportConfirmationPage) getWizardModel().getPanel(ImportConfirmationPage.ID)).setBackPanelDescriptor(ID);
        super.aboutToHidePanel();
    }

    public Object getBackPanelDescriptor() {
        return ImportTypePage.ID;
    }


    public Object getNextPanelDescriptor() {
        return AnticipateOntologyIdPage.ID;
    }
    
    private void updateNextButtonEnabled() {
        getWizard().setNextFinishButtonEnabled(importList.getSelectedIndex() != -1);
    }
    
    private class Renderer extends DefaultListCellRenderer {
        
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof ImportInfo) {
                ImportInfo ii = (ImportInfo) value;
                label.setText("<html>Import Declaration " + ii.getImportLocation() + "<p>from file " + ii.getPhysicalLocation() + "</p><br/></html>");
            }
            return label;
        }
        
    }
}
