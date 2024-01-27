package org.protege.editor.owl.ui.selector;

import java.awt.Color;
import java.util.Set;

import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeListener;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.protege.editor.owl.ui.view.individual.OWLIndividualListViewComponent;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 05-Jul-2006<br>
 * <br>
  matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br>
 * <br>
 */
public class OWLIndividualSelectorPanel extends AbstractSelectorPanel<OWLNamedIndividual> {

    private OWLIndividualListViewComponent vc;

    private Set<OWLOntology> ontologies;

    public OWLIndividualSelectorPanel(OWLEditorKit eKit) {
        this(eKit, true);
    }

    public OWLIndividualSelectorPanel(OWLEditorKit eKit, boolean editable) {
        this(eKit, editable, eKit.getModelManager().getActiveOntologies());
    }

    public OWLIndividualSelectorPanel(OWLEditorKit eKit, boolean editable, Set<OWLOntology> ontologies) {
        this(eKit, editable, ontologies, ListSelectionModel.SINGLE_SELECTION);
    }

    public OWLIndividualSelectorPanel(OWLEditorKit eKit, int selectionMode) {
        this(eKit, true, null, selectionMode);
    }

    /**
     * Builds an OWLIndividualSelectorPanel with the input selection mode. The
     * valid values are the same described in the constants in
     * javax.swing.ListSelectionModel (the default is
     * ListSelectionModel.SINGLE_SELECTION)
     *
     * @param eKit
     * @param selectionMode
     */
    public OWLIndividualSelectorPanel(OWLEditorKit eKit, boolean editable, Set<OWLOntology> ontologies, int selectionMode) {
        super(eKit, editable, false);
        this.ontologies = ontologies;
        createUI();
        this.vc.setSelectionMode(selectionMode);
    }

    public void setSelection(OWLNamedIndividual ind) {
        if (vc.getView() != null) {
            vc.getView().setPinned(false);
        }
        vc.setSelectedIndividual(ind);
    }


    public void setSelection(Set<OWLNamedIndividual> entities) {
        vc.setSelectedIndividuals(entities);
    }


    public OWLNamedIndividual getSelectedObject() {
        return vc.getSelectedIndividual();
    }

    public Set<OWLNamedIndividual> getSelectedObjects() {
        return vc.getSelectedIndividuals();
    }


    public void dispose() {
        vc.dispose();
    }

    protected ViewComponentPlugin getViewComponentPlugin() {
        return new ViewComponentPluginAdapter() {
            public String getLabel() {
                return "Individuals";
            }

            public Workspace getWorkspace() {
                return getOWLEditorKit().getWorkspace();
            }

            public ViewComponent newInstance() throws ClassNotFoundException,
                    IllegalAccessException, InstantiationException {
                vc = new OWLIndividualListViewComponent(){
                    protected void setupActions() {
                        if (isEditable()){
                            super.setupActions();
                        }
                    }

                    protected Set<OWLOntology> getOntologies() {
                        if (ontologies != null){
                            return ontologies;
                        }
                        return super.getOntologies();
                    }
                };
                vc.setup(this);
                return vc;
            }

            public Color getBackgroundColor() {
                return OWLSystemColors.getOWLIndividualColor();
            }
        };
    }

    public void setOntologies(Set<OWLOntology> ontologies) {

    }

    public void addSelectionListener(ChangeListener listener) {
        vc.addChangeListener(listener);
    }

    public void removeSelectionListener(ChangeListener listener) {
        vc.removeChangeListener(listener);
    }
}
