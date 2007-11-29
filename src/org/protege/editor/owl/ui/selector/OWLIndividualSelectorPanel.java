package org.protege.editor.owl.ui.selector;

import java.awt.Color;
import java.util.Set;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.protege.editor.owl.ui.view.OWLIndividualListViewComponent;
import org.semanticweb.owl.model.OWLIndividual;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 05-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLIndividualSelectorPanel extends AbstractSelectorPanel {

    private OWLIndividualListViewComponent viewComponent;


    public OWLIndividualSelectorPanel(OWLEditorKit owlEditorKit) {
        super(owlEditorKit);
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
                return getOWLEditorKit().getOWLWorkspace();
            }


            public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                      InstantiationException {
                viewComponent = new OWLIndividualListViewComponent();
                viewComponent.setup(this);
                return viewComponent;
            }


            public Color getBackgroundColor() {
                return OWLSystemColors.getOWLIndividualColor();
            }
        };
    }


    public OWLIndividual getSelectedIndividual() {
        return viewComponent.getSelectedIndividual();
    }


    public Set<OWLIndividual> getSelectedIndividuals() {
        return viewComponent.getSelectedIndividuals();
    }


    public void setSelectedIndividual(OWLIndividual ind) {
        if(viewComponent.getView() != null) {
            viewComponent.getView().setPinned(false);
        }
        viewComponent.setSelectedIndividual(ind);

    }
}
