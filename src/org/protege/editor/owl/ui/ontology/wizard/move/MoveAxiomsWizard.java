package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.*;

import java.net.URI;
import java.util.*;

/**
 * User: nickdrummond Date: May 20, 2008
 */
public class MoveAxiomsWizard extends Wizard implements MoveAxiomsModel {

    private OWLEditorKit editorKit;

    private MoveAxiomsKit selectedKit;

    private Set<OWLOntology> sourceOntologies;

    private URI targetOntologyURI;

    private List<MoveAxiomsKit> moveAxiomsKits;

    private Map<String, Object> kitId2FirstPanelId;

    private Map<String, Object> kitId2LastPanelId;

    private Set<OWLAxiom> axioms;

    private List<MoveAxiomsKitConfigurationPanel> panels;

    private boolean copyAxioms;

    private URI physicalURI;


    public MoveAxiomsWizard(OWLEditorKit eKit) {
        setTitle("Move axioms to ontology");
        this.editorKit = eKit;

        sourceOntologies = new HashSet<OWLOntology>();
        axioms = new HashSet<OWLAxiom>();
        panels = new ArrayList<MoveAxiomsKitConfigurationPanel>();
        selectedKit = null;
        kitId2FirstPanelId = new HashMap<String, Object>();
        kitId2LastPanelId = new HashMap<String, Object>();
        setupKits();

        // We select the type of move/copy

        // We select the ontologies

        // We configure the move/copy

        // we confirm the move/copy

        // We specify whether we want to create a new ontology or move to an existing ontology

        // We finish or cancel

        registerWizardPanel(SelectKitPanel.ID, new SelectKitPanel(editorKit));

        registerWizardPanel(SelectSourceOntologiesPanel.ID, new SelectSourceOntologiesPanel(editorKit));

        registerWizardPanel(SelectTargetOntologyTypePanel.ID, new SelectTargetOntologyTypePanel(editorKit));

        registerWizardPanel(SelectTargetOntologyPanel.ID, new SelectTargetOntologyPanel(editorKit));

        registerWizardPanel(CreateNewOntologyPanel.ID, new CreateNewOntologyPanel(editorKit));

        registerWizardPanel(NewOntologyPhysicalLocationPanel.ID, new NewOntologyPhysicalLocationPanel(editorKit));

        registerWizardPanel(SelectMoveOrCopyPanel.ID, new SelectMoveOrCopyPanel(editorKit));
        

        // Setup and init all of the plugin wizard pages - any of them could be used
        for (MoveAxiomsKit kit : moveAxiomsKits) {
            List<MoveAxiomsKitConfigurationPanel> panels = kit.getConfigurationPanels();
            for (int i = 0; i < panels.size(); i++) {
                MoveAxiomsKitConfigurationPanel panel = panels.get(i);
                this.panels.add(panel);
                panel.setup(editorKit, this);
                panel.initialise();
                Object prevId = SelectSourceOntologiesPanel.ID;
                Object nextId = SelectTargetOntologyTypePanel.ID;
                if (i == 0) {
                    kitId2FirstPanelId.put(kit.getID(), panel.getID());
                    prevId = SelectSourceOntologiesPanel.ID;
                }
                if (i > 0) {
                    prevId = panels.get(i - 1).getID();
                }
                if (i < panels.size() - 1) {
                    nextId = panels.get(i + 1).getID();
                }
                if (i == panels.size() - 1) {
                    kitId2LastPanelId.put(kit.getID(), panel.getID());
                    nextId = SelectTargetOntologyTypePanel.ID;
                }
                MoveAxiomsWizardKitConfigurationPanel configPanel = new MoveAxiomsWizardKitConfigurationPanel(prevId,
                                                                                                              nextId,
                                                                                                              panel,
                                                                                                              editorKit);
                registerWizardPanel(panel.getID(), configPanel);
            }
        }
        setCurrentPanel(SelectKitPanel.ID);
    }


    public Object getFirstPanelIDForKit() {
        return kitId2FirstPanelId.get(selectedKit.getID());
    }


    public Object getLastPanelIDForKit() {
        return kitId2LastPanelId.get(selectedKit.getID());
    }


    public List<MoveAxiomsKit> getMoveAxiomsKits() {
        return moveAxiomsKits;
    }


    private void setupKits() {
        moveAxiomsKits = new ArrayList<MoveAxiomsKit>();

//        moveAxiomsKits.add(new MoveAxiomKitImpl(editorKit, new AnnotationAxiomsStrategy()));
//        moveAxiomsKits.add(new MoveAxiomKitImpl(editorKit, new AllAxiomsStrategy()));

        MoveAxiomsKitPluginLoader loader = new MoveAxiomsKitPluginLoader(editorKit);
        for (MoveAxiomsKitPlugin plugin : loader.getPlugins()) {
            try {
                MoveAxiomsKit moveAxiomsKit = plugin.newInstance();
                moveAxiomsKits.add(moveAxiomsKit);
            }
            catch (Exception e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }

        for (MoveAxiomsKit kit : moveAxiomsKits) {
            try {
                kit.initialise();
            }
            catch (Exception e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
        if (!moveAxiomsKits.isEmpty()) {
            Collections.sort(moveAxiomsKits, new Comparator<MoveAxiomsKit>(){
                public int compare(MoveAxiomsKit kit1, MoveAxiomsKit kit2) {
                    return kit1.getName().compareTo(kit2.getName());
                }
            });
            selectedKit = moveAxiomsKits.get(0);
        }
    }


    public MoveAxiomsKit getSelectedKit() {
        return selectedKit;
    }


    public void setSelectedKit(MoveAxiomsKit selectedKit) {
        this.selectedKit = selectedKit;
    }


    public void dispose() {
        super.dispose();
        for (MoveAxiomsKitConfigurationPanel panel : panels) {
            panel.dispose();
        }
    }



    ///////////////////////////////////////////////////////////////////////////////
    //
    //  Implementation of the model

    public List<OWLOntologyChange> getChanges() throws OWLOntologyCreationException {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        Set<OWLAxiom> axiomsToBeMoved = getAxiomsToBeMoved();
        OWLOntology targetOntology;
        OWLOntologyManager man = editorKit.getModelManager().getOWLOntologyManager();
        if(man.contains(getTargetOntologyURI())) {
            targetOntology = man.getOntology(getTargetOntologyURI());
        }
        else {
            targetOntology = editorKit.getModelManager().createNewOntology(getTargetOntologyURI(), getTargetOntologyPhysicalURI());
        }

        for(OWLAxiom ax : axiomsToBeMoved) {
            for (OWLOntology ont : getSourceOntologies()) {
                if (ont.containsAxiom(ax)) {
                    if(!isCopyAxioms()) {
                        changes.add(new RemoveAxiom(ont, ax));
                    }
                    changes.add(new AddAxiom(targetOntology, ax));
                }
            }
        }
        return changes;
    }


    public Set<OWLAxiom> getAxiomsToBeMoved() {
        return selectedKit.getAxioms(sourceOntologies);
    }


    public Set<OWLOntology> getSourceOntologies() {
        return sourceOntologies;
    }


    public void setSourceOntologies(Set<OWLOntology> sourceOntologies) {
        this.sourceOntologies = sourceOntologies;
    }


    public URI getTargetOntologyURI() {
        return targetOntologyURI;
    }


    public void setTargetOntologyURI(URI targetOntologyURI) {
        this.targetOntologyURI = targetOntologyURI;
    }


    public void setTargetOntologyPhysicalURI(URI uri) {
        this.physicalURI = uri;
    }


    public URI getTargetOntologyPhysicalURI() {
        return physicalURI;
    }


    public void setCopyAxioms(boolean b) {
        this.copyAxioms = b;
    }


    public boolean isCopyAxioms() {
        return copyAxioms;
    }
}
