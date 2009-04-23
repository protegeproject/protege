package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.protege.editor.owl.ui.view.AbstractOWLEntityHierarchyViewComponent;
import org.protege.editor.owl.ui.view.annotationproperty.OWLAnnotationPropertyHierarchyViewComponent;
import org.semanticweb.owl.model.OWLAnnotationProperty;

import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 27, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLAnnotationPropertySelectorPanel extends AbstractHierarchySelectorPanel<OWLAnnotationProperty> {


    private AbstractOWLEntityHierarchyViewComponent<OWLAnnotationProperty> viewComponent;


    public OWLAnnotationPropertySelectorPanel(OWLEditorKit editorKit) {
        this(editorKit, true);
    }

    public OWLAnnotationPropertySelectorPanel(OWLEditorKit editorKit, boolean editable) {
        this(editorKit, editable, editorKit.getModelManager().getOWLHierarchyManager().getOWLAnnotationPropertyHierarchyProvider());
    }

    public OWLAnnotationPropertySelectorPanel(OWLEditorKit editorKit, boolean editable, OWLObjectHierarchyProvider<OWLAnnotationProperty> hp) {
        super(editorKit, editable, hp);
    }


    protected ViewComponentPlugin getViewComponentPlugin() {

        return new ViewComponentPluginAdapter() {
            public String getLabel() {
                return "Annotation Properties";
            }


            public Workspace getWorkspace() {
                return getOWLEditorKit().getWorkspace();
            }


            public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException,
                    InstantiationException {
                viewComponent = new OWLAnnotationPropertyHierarchyViewComponent(){
                    public void performExtraInitialisation() throws Exception {
                        if (isEditable()){
                            super.performExtraInitialisation();
                        }
                    }

                    protected OWLObjectHierarchyProvider<OWLAnnotationProperty> getHierarchyProvider() {
                        return OWLAnnotationPropertySelectorPanel.this.getHierarchyProvider();
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

    public void setSelection(OWLAnnotationProperty cls) {
        viewComponent.setSelectedEntity(cls);
    }


    public void setSelection(Set<OWLAnnotationProperty> clses) {
        viewComponent.setSelectedEntities(clses);
    }


    public OWLAnnotationProperty getSelectedObject() {
        return viewComponent.getSelectedEntity();
    }

    public Set<OWLAnnotationProperty> getSelectedObjects() {
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
