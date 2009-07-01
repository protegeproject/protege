package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.protege.editor.owl.ui.view.AbstractOWLPropertyHierarchyViewComponent;
import org.protege.editor.owl.ui.view.dataproperty.OWLDataPropertyHierarchyViewComponent;
import org.semanticweb.owlapi.model.OWLDataProperty;

import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 27-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLDataPropertySelectorPanel extends AbstractHierarchySelectorPanel<OWLDataProperty> {

    private AbstractOWLPropertyHierarchyViewComponent<OWLDataProperty> vc;


    public OWLDataPropertySelectorPanel(OWLEditorKit eKit) {
        this(eKit, true);
    }

    public OWLDataPropertySelectorPanel(OWLEditorKit eKit, boolean editable) {
        this(eKit, editable, eKit.getModelManager().getOWLHierarchyManager().getOWLDataPropertyHierarchyProvider());
    }

    public OWLDataPropertySelectorPanel(OWLEditorKit eKit, boolean editable, OWLObjectHierarchyProvider<OWLDataProperty> hp) {
        super(eKit, editable, hp);
    }


    protected ViewComponentPlugin getViewComponentPlugin() {
        return new ViewComponentPluginAdapter() {
            public String getLabel() {
                return "Data properties";
            }


            public Workspace getWorkspace() {
                return getOWLEditorKit().getWorkspace();
            }


            public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException,
                    InstantiationException {
                vc = new OWLDataPropertyHierarchyViewComponent(){
                    protected void performExtraInitialisation() throws Exception {
                        if (isEditable()){
                            super.performExtraInitialisation();
                        }
                    }

                    protected OWLObjectHierarchyProvider<OWLDataProperty> getHierarchyProvider() {
                        return OWLDataPropertySelectorPanel.this.getHierarchyProvider();
                    }
                };
                vc.setup(this);
                return vc;
            }


            public Color getBackgroundColor() {
                return OWLSystemColors.getOWLDataPropertyColor();
            }
        };
    }


    public void setSelection(OWLDataProperty property) {
        vc.setSelectedEntity(property);
    }


    public void setSelection(Set<OWLDataProperty> properties) {
        vc.setSelectedEntities(properties);
    }


    public OWLDataProperty getSelectedObject() {
        return vc.getSelectedEntity();
    }

    public Set<OWLDataProperty> getSelectedObjects() {
        return vc.getSelectedEntities();
    }


    public void addSelectionListener(ChangeListener listener) {
        vc.addChangeListener(listener);
    }

    public void removeSelectionListener(ChangeListener listener) {
        vc.removeChangeListener(listener);
    }

    public void dispose() {
        vc.dispose();
    }
}
