package org.protege.editor.owl.ui.selector;

import java.awt.Color;
import java.util.Set;

import javax.swing.event.ChangeListener;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.protege.editor.owl.ui.view.AbstractOWLEntityHierarchyViewComponent;
import org.protege.editor.owl.ui.view.cls.ToldOWLClassHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLClass;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 2, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLClassSelectorPanel extends AbstractHierarchySelectorPanel<OWLClass> {

    private AbstractOWLEntityHierarchyViewComponent<OWLClass> vc;


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
                vc = new ToldOWLClassHierarchyViewComponent(){
                    public void performExtraInitialisation() throws Exception {
                        if (isEditable()){
                            super.performExtraInitialisation();
                        }
                    }

                    protected OWLObjectHierarchyProvider<OWLClass> getOWLClassHierarchyProvider() {
                        return getHierarchyProvider();
                    }
                };
                vc.setup(this);
                return vc;
            }


            public Color getBackgroundColor() {
                return OWLSystemColors.getOWLClassColor();
            }
        };
    }

    public void setSelection(OWLClass cls) {
        vc.setSelectedEntity(cls);
    }


    public void setSelection(Set<OWLClass> clses) {
        vc.setSelectedEntities(clses);
    }


    public OWLClass getSelectedObject() {
        return vc.getSelectedEntity();
    }

    public Set<OWLClass> getSelectedObjects() {
        return vc.getSelectedEntities();
    }

    public void dispose() {
        vc.dispose();
    }

    public void addSelectionListener(ChangeListener listener) {
        vc.addChangeListener(listener);
    }

    public void removeSelectionListener(ChangeListener listener) {
        vc.removeChangeListener(listener);
    }
}
