package org.protege.editor.owl.ui.selector;

import java.awt.Color;
import java.util.Set;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clshierarchy.ToldOWLClassHierarchyViewComponent;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLClassSelectorPanel extends AbstractSelectorPanel {

    private ToldOWLClassHierarchyViewComponent viewComponent;


    public OWLClassSelectorPanel(OWLEditorKit editorKit) {
        super(editorKit);
    }


    protected ViewComponentPlugin getViewComponentPlugin() {

        return new ViewComponentPluginAdapter() {
            public String getLabel() {
                return "OWL Asserted Class Hierarchy";
            }


            public Workspace getWorkspace() {
                return getOWLEditorKit().getOWLWorkspace();
            }


            public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                      InstantiationException {
                viewComponent = new ToldOWLClassHierarchyViewComponent();
                viewComponent.setup(this);
                return viewComponent;
            }


            public Color getBackgroundColor() {
                return OWLSystemColors.getOWLClassColor();
            }
        };
    }


    public void setSelectedClass(OWLClass cls) {
        viewComponent.setSelectedClass(cls);
    }


    public OWLClass getSelectedClass() {
        return viewComponent.getSelectedClass();
    }


    public void dispose() {
        viewComponent.dispose();
    }


    public Set<OWLDescription> getSelectedClasses() {
        return viewComponent.getSelectedClasses();
    }
}
