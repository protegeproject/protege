package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.protege.editor.owl.ui.view.AbstractOWLPropertyHierarchyViewComponent;
import org.protege.editor.owl.ui.view.objectproperty.OWLObjectPropertyHierarchyViewComponent;
import org.semanticweb.owl.model.OWLObjectProperty;

import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLObjectPropertySelectorPanel extends AbstractHierarchySelectorPanel<OWLObjectProperty> {

    private AbstractOWLPropertyHierarchyViewComponent<OWLObjectProperty> vc;


    public OWLObjectPropertySelectorPanel(OWLEditorKit eKit) {
        this(eKit, true);
    }

    public OWLObjectPropertySelectorPanel(OWLEditorKit eKit, boolean editable) {
        this(eKit, editable, eKit.getModelManager().getOWLHierarchyManager().getOWLObjectPropertyHierarchyProvider());
    }

    public OWLObjectPropertySelectorPanel(OWLEditorKit eKit, boolean editable, OWLObjectHierarchyProvider<OWLObjectProperty> hp) {
        super(eKit, editable, hp);
    }

    protected ViewComponentPlugin getViewComponentPlugin() {

        return new ViewComponentPluginAdapter() {
            public String getLabel() {
                return "Object properties";
            }


            public Workspace getWorkspace() {
                return getOWLEditorKit().getWorkspace();
            }


            public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException,
                    InstantiationException {

                    vc = new OWLObjectPropertyHierarchyViewComponent(){
                        protected void performExtraInitialisation() throws Exception {
                            if (isEditable()){
                                super.performExtraInitialisation();
                            }
                        }

                        protected OWLObjectHierarchyProvider<OWLObjectProperty> getHierarchyProvider() {
                            return OWLObjectPropertySelectorPanel.this.getHierarchyProvider();
                        }
                    };
                vc.setup(this);
                return vc;
            }


            public Color getBackgroundColor() {
                return OWLSystemColors.getOWLObjectPropertyColor();
            }
        };
    }


    public void setSelection(OWLObjectProperty property) {
        vc.setSelectedEntity(property);
    }


    public void setSelection(Set<OWLObjectProperty> properties) {
        vc.setSelectedEntities(properties);
    }


    public OWLObjectProperty getSelectedObject() {
        return vc.getSelectedEntity();
    }

    public Set<OWLObjectProperty> getSelectedObjects() {
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
