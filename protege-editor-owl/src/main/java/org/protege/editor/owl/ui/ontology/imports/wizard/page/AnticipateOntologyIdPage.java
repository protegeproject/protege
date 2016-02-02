package org.protege.editor.owl.ui.ontology.imports.wizard.page;

import com.google.common.base.Optional;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.library.folder.XmlBaseAlgorithm;
import org.protege.editor.owl.model.repository.MasterOntologyIDExtractor;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.ontology.imports.wizard.ImportInfo;
import org.protege.editor.owl.ui.ontology.imports.wizard.OntologyImportWizard;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
public class AnticipateOntologyIdPage extends AbstractOWLWizardPanel {

    private static final long serialVersionUID = -1944232166721256262L;

    public static final String ID = "AnticipateOntologyIdPage";

    private JProgressBar progressBar;

    private Runnable checker;

    public AnticipateOntologyIdPage(OWLEditorKit owlEditorKit) {
        super(ID, "Import verification", owlEditorKit);
        checker = () -> checkImport();
    }


    public Object getNextPanelDescriptor() {
        return needsImportPage() ? SelectImportLocationPage.ID : ImportConfirmationPage.ID;
    }

    private boolean needsImportPage() {
        OntologyImportWizard wizard = (OntologyImportWizard) getWizard();
        Set<ImportInfo> imports = wizard.getImports();
        if (imports == null || imports.size() != 1) { /* size > 1 means  we are in the library imports - no manual step */
            return false;
        }
        ImportInfo parameters = imports.iterator().next();

        List<IRI> importOptions = new ArrayList<>();

        OWLOntologyID id = parameters.getOntologyID();
        if (id != null && !id.isAnonymous()) {
            importOptions.add(id.getOntologyIRI().get());
            if (id.getVersionIRI().isPresent() && !importOptions.contains(id.getVersionIRI().get())) {
                importOptions.add(id.getVersionIRI().get());
            }
        }
        IRI physicalLocation = IRI.create(parameters.getPhysicalLocation());
        if (!UIUtil.isLocalFile(physicalLocation.toURI()) && !importOptions.contains(physicalLocation)) {
            importOptions.add(IRI.create(physicalLocation.toString()));
        }
        if (UIUtil.isLocalFile(physicalLocation.toURI()) && importOptions.isEmpty()) {
            File f = new File(physicalLocation.toURI());

            Set<URI> bases = new XmlBaseAlgorithm().getSuggestions(f);
            if (bases.size() == 1) {
                importOptions.add(IRI.create(bases.iterator().next()));
            }
        }

        if (!wizard.isImportsAreFinal() && !wizard.isCustomizeImports() && importOptions.size() > 0) {
            parameters.setImportLocation(importOptions.get(0));
            return false;
        }
        else {
            return !wizard.isImportsAreFinal();
        }
    }

    protected void createUI(final JComponent parent) {
        JPanel panel = new JPanel(new BorderLayout(7, 7));
        panel.add(new JLabel("Please wait.  Verifying import..."), BorderLayout.NORTH);
        progressBar = new JProgressBar(0, 100);
        progressBar.setIndeterminate(true);
        panel.add(progressBar, BorderLayout.SOUTH);
        parent.setLayout(new BorderLayout());
        parent.add(panel, BorderLayout.NORTH);
    }


    protected void checkImport() {
        for (ImportInfo parameters : ((OntologyImportWizard) getWizard()).getImports()) {
            if (parameters.getOntologyID() != null) {
                continue;
            }
            try {
                MasterOntologyIDExtractor extractor = new MasterOntologyIDExtractor();
                Optional<OWLOntologyID> id = extractor.getOntologyId(parameters.getPhysicalLocation());
                if (id.isPresent()) {
                    parameters.setOntologyID(id.get());
                }
                else {
                    parameters.setOntologyID(null);
                }
            } catch (Throwable t) {
                LoggerFactory.getLogger(AnticipateOntologyIdPage.class)
                        .error("An error occurred whilst extracting the Ontology Id from the imported ontology: {}", t);
            }
        }
        SwingUtilities.invokeLater(() -> {
            getWizard().setCurrentPanel(getNextPanelDescriptor());
        });
    }


    public void displayingPanel() {
        getWizard().setNextFinishButtonEnabled(false);
        Thread t = new Thread(checker);
        t.start();
    }
}
