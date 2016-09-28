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
import org.semanticweb.owlapi.model.OWLAnnotationProperty;

import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 27, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLAnnotationPropertySelectorPanel extends AbstractHierarchySelectorPanel<OWLAnnotationProperty> {
	private static final long serialVersionUID = -5790142068951918908L;
	private AbstractOWLEntityHierarchyViewComponent<OWLAnnotationProperty> vc;


    public OWLAnnotationPropertySelectorPanel(OWLEditorKit editorKit) {
        this(editorKit, true);
    }

    public OWLAnnotationPropertySelectorPanel(OWLEditorKit editorKit, boolean editable) {
        this(editorKit, editable, editorKit.getModelManager().getOWLHierarchyManager().getOWLAnnotationPropertyHierarchyProvider(null));
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
                vc = new OWLAnnotationPropertyHierarchyViewComponent(){
                    public void performExtraInitialisation() throws Exception {
                        if (isEditable()){
                            super.performExtraInitialisation();
                        }
                    }

                    protected OWLObjectHierarchyProvider<OWLAnnotationProperty> getHierarchyProvider() {
                        return OWLAnnotationPropertySelectorPanel.this.getHierarchyProvider();
                    }
                };
                vc.setup(this);
                return vc;
            }


            public Color getBackgroundColor() {
                return OWLSystemColors.getOWLClassColor();
            }


			@Override
			public boolean isEager() {
				// TODO Auto-generated method stub
				return false;
			}
        };
    }

    public void setSelection(OWLAnnotationProperty cls) {
        vc.setSelectedEntity(cls);
    }


    public void setSelection(Set<OWLAnnotationProperty> clses) {
        vc.setSelectedEntities(clses);
    }


    public OWLAnnotationProperty getSelectedObject() {
        return vc.getSelectedEntity();
    }

    public Set<OWLAnnotationProperty> getSelectedObjects() {
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
