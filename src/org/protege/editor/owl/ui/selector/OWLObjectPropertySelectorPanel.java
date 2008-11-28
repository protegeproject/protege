package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.protege.editor.owl.ui.view.AbstractOWLPropertyHierarchyViewComponent;
import org.protege.editor.owl.ui.view.OWLObjectPropertyHierarchyViewComponent;
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

    private AbstractOWLPropertyHierarchyViewComponent<OWLObjectProperty> view;


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

                    view = new OWLObjectPropertyHierarchyViewComponent(){
                        protected void performExtraInitialisation() throws Exception {
                            if (isEditable()){
                                super.performExtraInitialisation();
                            }
                        }

                        protected OWLObjectHierarchyProvider<OWLObjectProperty> getHierarchyProvider() {
                            return OWLObjectPropertySelectorPanel.this.getHierarchyProvider();
                        }
                    };
                view.setup(this);
                return view;
            }


            public Color getBackgroundColor() {
                return OWLSystemColors.getOWLObjectPropertyColor();
            }
        };
    }


    public void setSelection(OWLObjectProperty property) {
        view.setSelectedProperty(property);
    }


    public void setSelection(Set<OWLObjectProperty> properties) {
        view.setSelectedProperties(properties);
    }


    public OWLObjectProperty getSelectedObject() {
        return view.getSelectedProperty();
    }

    public Set<OWLObjectProperty> getSelectedObjects() {
        return view.getSelectedProperties();
    }

    
    public void dispose() {
        view.dispose();
    }


    public void addSelectionListener(ChangeListener listener) {
        view.addChangeListener(listener);
    }

    public void removeSelectionListener(ChangeListener listener) {
        view.removeChangeListener(listener);
    }

    /**
     * @deprecated Use <code>setSelection</code>
     * @param property
     */
    public void setSelectedOWLObjectProperty(OWLObjectProperty property) {
        setSelection(property);
    }

    /**
     * @deprecated Use <code>getSelectedObject</code>
     * @return selected OWLObjectProperty
     */
    public OWLObjectProperty getSelectedOWLObjectProperty() {
        return getSelectedObject();
    }

    /**
     * @deprecated Use <code>getSelectedObjects</code>
     * @return set of selected OWLObjectProperties
     */
    public Set<OWLObjectProperty> getSelectedOWLObjectProperties() {
        return getSelectedObjects();
    }
}
