package org.protege.editor.owl.ui.ontology.imports.wizard;

import java.awt.BorderLayout;
import java.io.File;
import java.net.URI;
import java.util.Collections;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.protege.editor.core.editorkit.EditorKitDescriptor;
import org.protege.editor.core.editorkit.RecentEditorKitManager;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.FilePathPanel;
import org.protege.editor.core.ui.wizard.AbstractWizardPanel;
import org.protege.editor.owl.OWLEditorKit;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class LocalFilePage extends AbstractWizardPanel {

    public static final String ID = "LocalFilePage";

    private FilePathPanel filePathPanel;


    public LocalFilePage(OWLEditorKit owlEditorKit) {
        super(ID, "Import from local file", owlEditorKit);
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please specify the path to a file that contains an ontology.  You can use the browse " + "button to show a file chooser dialog.");
        filePathPanel = new FilePathPanel("Please select a file", Collections.EMPTY_SET);
        filePathPanel.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                File f = filePathPanel.getFile();
                getWizard().setNextFinishButtonEnabled(f.exists() && f.isDirectory() == false);
            }
        });
        filePathPanel.setBorder(ComponentFactory.createTitledBorder("Path"));

        JList recentList = createRecentList();
        JPanel recentListHolder = new JPanel(new BorderLayout());
        recentListHolder.setBorder(ComponentFactory.createTitledBorder("Recent locations"));
        recentListHolder.add(new JScrollPane(recentList), BorderLayout.CENTER);

        parent.setLayout(new BorderLayout(6, 6));
        parent.add(filePathPanel, BorderLayout.NORTH);
        parent.add(recentListHolder, BorderLayout.CENTER);
    }

    @Override
    public void aboutToHidePanel() {
    	OntologyImportWizard wizard = (OntologyImportWizard) getWizard();
    	wizard.clearImports();
    	ImportInfo parameters = new ImportInfo();
    	parameters.setPhysicalLocation(filePathPanel.getFile().toURI());
    	wizard.addImport(parameters);
    	((SelectImportLocationPage) getWizardModel().getPanel(SelectImportLocationPage.ID)).setBackPanelDescriptor(ID);
    	super.aboutToHidePanel();
    }

    private JList createRecentList() {
        DefaultListModel model = new DefaultListModel();
        RecentEditorKitManager man = RecentEditorKitManager.getInstance();
        for (EditorKitDescriptor descriptor : man.getDescriptors()) {
            final URI uri = descriptor.getURI(OWLEditorKit.URI_KEY);
            if (uri.getScheme() != null && uri.getScheme().equals("file")){
                model.addElement(descriptor);
            }
        }

        final JList list = new JList(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.addListSelectionListener(new ListSelectionListener(){
            public void valueChanged(ListSelectionEvent event) {
                String path = ((EditorKitDescriptor)list.getSelectedValue()).getLabel();
                filePathPanel.setPath(path);
            }
        });
        return list;
    }


    public Object getBackPanelDescriptor() {
        return ImportTypePage.ID;
    }


    public Object getNextPanelDescriptor() {
        return AnticipateOntologyIdPage.ID;
    }


    public void displayingPanel() {
        getWizard().setNextFinishButtonEnabled(false);
        filePathPanel.requestFocus();
    }
}
