package org.protege.editor.owl.ui.ontology.imports.wizard;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.library.OntologyLibraryPanel;
import org.semanticweb.owlapi.model.IRI;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LibraryPage extends AbstractImportSourcePage {

    private static final Logger logger = Logger.getLogger(LibraryPage.class);


    public static final String ID = "LibraryPage";

    private LibraryOntologiesList list;


    public LibraryPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import from ontology library", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("The list below shows ontologies that are contained in the available ontology " + "libraries.  To add or remove an ontology library, click the \"Ontology libraries...\" button.");
        parent.setLayout(new BorderLayout());
        list = new LibraryOntologiesList(getOWLModelManager());
        JPanel listPanel = new JPanel(new BorderLayout(7, 7));
        listPanel.add(ComponentFactory.createScrollPane(list), BorderLayout.NORTH);
        listPanel.add(new JButton(new AbstractAction("Ontology libraries...") {
            public void actionPerformed(ActionEvent e) {
                OntologyLibraryPanel.showDialog(getOWLEditorKit());
                // Rebuild the list
                list.rebuildList();
            }
        }), BorderLayout.EAST);
        parent.add(listPanel, BorderLayout.NORTH);
        list.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    getWizard().setNextFinishButtonEnabled(list.getSelectedIndex() != -1);
                }
            }
        });
    }


    public ImportVerifier getImportVerifier() {
        Set<IRI> selectedIRIs = new HashSet<IRI>();
        Object [] selObjs = list.getSelectedValues();
        for (Object o : selObjs) {
            selectedIRIs.add((IRI) o);
        }
        return new LibraryVerifier(getOWLModelManager(), selectedIRIs);
    }


    public void displayingPanel() {
        getWizard().setNextFinishButtonEnabled(false);
        list.rebuildList();
        list.requestFocus();
    }


    public Object getBackPanelDescriptor() {
        return ImportTypePage.ID;
    }


    public Object getNextPanelDescriptor() {
        return ImportVerificationPage.ID;
    }
}
