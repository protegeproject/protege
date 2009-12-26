package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.library.OntologyLibrary;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.ontology.imports.wizard.GetImportsVisitor;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
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
public class LibraryPage extends AbstractOWLWizardPanel {

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
        parent.add(ComponentFactory.createScrollPane(importList), BorderLayout.CENTER);
        importList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    getWizard().setNextFinishButtonEnabled(importList.getSelectedIndex() != -1);
                }
            }
        });
    }
    
    private void calculatePossibleImports() {
        GetImportsVisitor getter = new GetImportsVisitor();
        for (OntologyLibrary library : getOWLEditorKit().getOWLModelManager().getOntologyLibraryManager().getLibraries()) {
            for (Entry e : library.getXmlCatalog().getEntries()) {
                e.accept(getter);
            }
        }
        importListModel.clear();
        for (ImportInfo ii : getter.getImports()) {
            importListModel.addElement(ii);
        }
    }


    public void displayingPanel() {
        getWizard().setNextFinishButtonEnabled(false);
        calculatePossibleImports();
    }

    @Override
    public void aboutToHidePanel() {
        OntologyImportWizard wizard = (OntologyImportWizard) getWizard();
        wizard.clearImports();
        for (int index : importList.getSelectedIndices()) {
            wizard.addImport((ImportInfo) importListModel.getElementAt(index));
        }
        ((SelectImportLocationPage) getWizardModel().getPanel(SelectImportLocationPage.ID)).setBackPanelDescriptor(ID);
    }

    public Object getBackPanelDescriptor() {
        return ImportTypePage.ID;
    }


    public Object getNextPanelDescriptor() {
        return AnticipateOntologyIdPage.ID;
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
