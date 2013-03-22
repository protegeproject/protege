package org.protege.editor.owl.ui.ontology.wizard.move;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.editorkit.EditorKitFactoryPlugin;
import org.protege.editor.core.ui.util.OpenRequestHandler;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.wizard.Wizard;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.OWLEditorKitFactory;
import org.protege.editor.owl.ui.action.OntologyFormatPage;
import org.protege.editor.owl.ui.ontology.wizard.create.PhysicalLocationPanel;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.net.URI;
import java.util.*;

/**
 * User: nickdrummond Date: May 20, 2008
 */
public class MoveAxiomsWizard extends Wizard implements MoveAxiomsModel {

    private OWLEditorKit editorKit;

    private MoveAxiomsKit selectedKit;

    private Set<OWLOntology> sourceOntologies;

    private OWLOntologyID targetOntologyID;

    private List<MoveAxiomsKit> moveAxiomsKits;

    private Map<String, Object> kitId2FirstPanelId;

    private Map<String, Object> kitId2LastPanelId;

    private List<MoveAxiomsKitConfigurationPanel> panels;

    private URI physicalURI;

    private MoveType moveType;

    private OntologyFormatPage ontologyFormatPage;

    private PhysicalLocationPanel ontologyPhysicalLocationPage;


    public MoveAxiomsWizard(OWLEditorKit eKit) {
        setTitle("Copy/move/delete axioms");
        this.editorKit = eKit;

        sourceOntologies = new HashSet<OWLOntology>();
        panels = new ArrayList<MoveAxiomsKitConfigurationPanel>();
        selectedKit = null;
        kitId2FirstPanelId = new HashMap<String, Object>();
        kitId2LastPanelId = new HashMap<String, Object>();
        setupKits();


        // We select the ontologies

        // We select the method

        // We configure the method

        // We confirm the axioms

        // we specify move/copy/delete

        // We specify whether we want to create a new ontology or move to an existing ontology

        // We finish or cancel

//        registerWizardPanel(SelectSourceOntologiesPanel.ID, new SelectSourceOntologiesPanel(editorKit));
        // Always use the active ontologies - it's less confusing
        setSourceOntologies(editorKit.getOWLModelManager().getOntologies());

        registerWizardPanel(SelectKitPanel.ID, new SelectKitPanel(editorKit));

        registerWizardPanel(SelectActionPanel.ID, new SelectActionPanel(editorKit));

        registerWizardPanel(SelectTargetOntologyTypePanel.ID, new SelectTargetOntologyTypePanel(editorKit));

        registerWizardPanel(SelectTargetOntologyPanel.ID, new SelectTargetOntologyPanel(editorKit));

        registerWizardPanel(CreateNewOntologyPanel.ID, new CreateNewOntologyPanel(editorKit));

        registerWizardPanel(PhysicalLocationPanel.ID, ontologyPhysicalLocationPage = new PhysicalLocationPanel(editorKit));

        registerWizardPanel(OntologyFormatPage.ID, ontologyFormatPage = new OntologyFormatPage(editorKit));



        // Setup and init all of the plugin wizard pages - any of them could be used
        for (MoveAxiomsKit kit : moveAxiomsKits) {
            List<MoveAxiomsKitConfigurationPanel> panels = kit.getConfigurationPanels();
            for (int i = 0; i < panels.size(); i++) {
                MoveAxiomsKitConfigurationPanel panel = panels.get(i);
                this.panels.add(panel);
                panel.setup(editorKit, this);
                panel.initialise();
                Object prevId = SelectKitPanel.ID;
                Object nextId = SelectActionPanel.ID;
                if (i == 0) {
                    kitId2FirstPanelId.put(kit.getID(), panel.getID());
                    prevId = SelectKitPanel.ID;
                }
                if (i > 0) {
                    prevId = panels.get(i - 1).getID();
                }
                if (i < panels.size() - 1) {
                    nextId = panels.get(i + 1).getID();
                }
                if (i == panels.size() - 1) {
                    kitId2LastPanelId.put(kit.getID(), panel.getID());
                    nextId = SelectActionPanel.ID;
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
        for (MoveAxiomsKitConfigurationPanel panel : panels) {
            panel.dispose();
        }
        super.dispose();
    }



    ///////////////////////////////////////////////////////////////////////////////
    //
    //  Implementation of the model

    public void applyChanges() throws OWLOntologyCreationException {
        OWLOntology targetOntology = null;
        OWLEditorKit targetEditorKit = editorKit;
        if (moveType == MoveType.COPY || moveType == MoveType.MOVE){
            OWLOntologyManager man = editorKit.getModelManager().getOWLOntologyManager();
            if(man.contains(getTargetOntologyID())) {
                targetOntology = man.getOntology(getTargetOntologyID());
            }
            else {
                try {
                    OWLEditorKit editorKit = (OWLEditorKit) ProtegeManager.getInstance().createAndSetupNewEditorKit(new OWLEditorKitFactory());
                    targetOntology = editorKit.getOWLModelManager().getActiveOntology();
                    targetEditorKit = editorKit;

                }
                catch (Exception e) {
                    ProtegeApplication.getErrorLog().logError(e);
                }

            }
        }

        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        Set<OWLAxiom> axiomsToBeMoved = getAxiomsToBeMoved();

        for(OWLAxiom ax : axiomsToBeMoved) {
            for (OWLOntology ont : getSourceOntologies()) {
                if (ont.containsAxiom(ax)) {
                    if(moveType == MoveType.DELETE || moveType == MoveType.MOVE) {
                        changes.add(new RemoveAxiom(ont, ax));
                    }
                    if(moveType == MoveType.COPY || moveType == MoveType.MOVE) {
                        changes.add(new AddAxiom(targetOntology, ax));
                    }
                }
            }
        }
        targetEditorKit.getOWLModelManager().applyChanges(changes);
        if(editorKit.getOWLModelManager().getOWLOntologyManager().contains(targetOntologyID)) {
            JOptionPane.showMessageDialog(getOwner(), "Axioms successfully " + moveType.getCompletedName(), "Finished", JOptionPane.INFORMATION_MESSAGE);
        }
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


    public OWLOntologyID getTargetOntologyID() {
        return targetOntologyID;
    }


    public void setTargetOntologyID(OWLOntologyID targetOntologyID) {
        this.targetOntologyID = targetOntologyID;
    }

    public void setMoveType(MoveType moveType) {
        this.moveType = moveType;
    }

    //    public void setDeleteFromOriginalOntology(boolean deleteFromOriginalOntology) {
//        this.deleteFromOriginalOntology = deleteFromOriginalOntology;
//    }
//
//
//    public void setAddToTargetOntology(boolean addToTargetOntology) {
//        this.addToTargetOntology = addToTargetOntology;
//    }

}