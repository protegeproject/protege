package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.view.cls.ToldOWLClassHierarchyViewComponent;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.protege.editor.owl.ui.view.AbstractOWLEntityHierarchyViewComponent;
import org.semanticweb.owl.model.OWLClass;

import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLClassSelectorPanel extends AbstractHierarchySelectorPanel<OWLClass> {

    private AbstractOWLEntityHierarchyViewComponent<OWLClass> viewComponent;


    public OWLClassSelectorPanel(OWLEditorKit editorKit) {
        this(editorKit, true);
    }

    public OWLClassSelectorPanel(OWLEditorKit editorKit, boolean editable) {
        this(editorKit, editable, editorKit.getModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider());
    }

    public OWLClassSelectorPanel(OWLEditorKit editorKit, boolean editable, OWLObjectHierarchyProvider<OWLClass> hp) {
        super(editorKit, editable, hp);
    }


    protected ViewComponentPlugin getViewComponentPlugin() {

        return new ViewComponentPluginAdapter() {
            public String getLabel() {
                return "Asserted Class Hierarchy";
            }


            public Workspace getWorkspace() {
                return getOWLEditorKit().getWorkspace();
            }


            public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException,
                    InstantiationException {
                viewComponent = new ToldOWLClassHierarchyViewComponent(){
                    public void performExtraInitialisation() throws Exception {
                        if (isEditable()){
                            super.performExtraInitialisation();
                        }
                    }

                    protected OWLObjectHierarchyProvider<OWLClass> getOWLClassHierarchyProvider() {
                        return getHierarchyProvider();
                    }
                };
                viewComponent.setup(this);
                return viewComponent;
            }


            public Color getBackgroundColor() {
                return OWLSystemColors.getOWLClassColor();
            }
        };
    }

    public void setSelection(OWLClass cls) {
        viewComponent.setSelectedEntity(cls);
    }


    public void setSelection(Set<OWLClass> clses) {
        viewComponent.setSelectedEntities(clses);
    }


    public OWLClass getSelectedObject() {
        return viewComponent.getSelectedEntity();
    }

    public Set<OWLClass> getSelectedObjects() {
        return viewComponent.getSelectedEntities();
    }

    public void dispose() {
        viewComponent.dispose();
    }

    public void addSelectionListener(ChangeListener listener) {
        viewComponent.addChangeListener(listener);
    }

    public void removeSelectionListener(ChangeListener listener) {
        viewComponent.removeChangeListener(listener);
    }
}
