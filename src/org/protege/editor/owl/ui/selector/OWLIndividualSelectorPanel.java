package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.protege.editor.owl.ui.view.OWLIndividualListViewComponent;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 05-Jul-2006<br>
 * <br>
 * <p/> matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br>
 * <br>
 */
public class OWLIndividualSelectorPanel extends AbstractSelectorPanel<OWLIndividual> {

    private OWLIndividualListViewComponent viewComponent;

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
        this.viewComponent.setSelectionMode(selectionMode);
    }

    public void setSelection(OWLIndividual ind) {
        if (viewComponent.getView() != null) {
            viewComponent.getView().setPinned(false);
        }
        viewComponent.setSelectedIndividual(ind);
    }


    public void setSelection(Set<OWLIndividual> entities) {
        viewComponent.setSelectedIndividuals(entities);
    }


    public OWLIndividual getSelectedObject() {
        return viewComponent.getSelectedIndividual();
    }

    public Set<OWLIndividual> getSelectedObjects() {
        return viewComponent.getSelectedIndividuals();
    }


    public void dispose() {
        viewComponent.dispose();
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
                viewComponent = new OWLIndividualListViewComponent(){
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
                viewComponent.setup(this);
                return viewComponent;
            }

            public Color getBackgroundColor() {
                return OWLSystemColors.getOWLIndividualColor();
            }
        };
    }

    public void setOntologies(Set<OWLOntology> ontologies) {

    }

    public void addSelectionListener(ChangeListener listener) {
        viewComponent.addChangeListener(listener);
    }

    public void removeSelectionListener(ChangeListener listener) {
        viewComponent.removeChangeListener(listener);
    }

    /**
     * @deprecated Use <code>getSelectedObject</code>
     * @return
     */
    public OWLIndividual getSelectedIndividual() {
        return getSelectedObject();
    }

    /**
     * @deprecated Use <code>getSelectedObjects</code>
     * @return
     */
    public Set<OWLIndividual> getSelectedIndividuals() {
        return getSelectedObjects();
    }

    /**
     * @deprecated Use <code>setSelection</code>
     * @param ind
     */
    public void setSelectedIndividual(OWLIndividual ind) {
        setSelection(ind);
    }
}
