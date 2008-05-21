package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.protege.editor.owl.ui.view.OWLDataPropertyHierarchyViewComponent;
import org.semanticweb.owl.model.OWLDataProperty;

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
public class OWLDataPropertySelectorPanel extends AbstractSelectorPanel<OWLDataProperty> {

    private OWLDataPropertyHierarchyViewComponent view;


    public OWLDataPropertySelectorPanel(OWLEditorKit editorKit) {
        super(editorKit);
    }

    public void setSelection(OWLDataProperty property) {
        view.show(property);
    }

    public OWLDataProperty getSelectedObject() {
        return view.getSelectedDataProperty();
    }

    public Set<OWLDataProperty> getSelectedObjects() {
        return view.getSelectedProperties();
    }


    protected ViewComponentPlugin getViewComponentPlugin() {
        return new ViewComponentPluginAdapter() {
            public String getLabel() {
                return "Data properties";
            }


            public Workspace getWorkspace() {
                return getOWLEditorKit().getOWLWorkspace();
            }


            public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                      InstantiationException {
                view = new OWLDataPropertyHierarchyViewComponent();
                view.setup(this);
                return view;
            }


            public Color getBackgroundColor() {
                return OWLSystemColors.getOWLDataPropertyColor();
            }
        };
    }

    public void dispose() {
        view.dispose();
    }

    /**
     * @deprecated Use <code>setSelection</code>
     * @param property
     */
    public void setSelectedOWLDataProperty(OWLDataProperty property) {
        setSelection(property);
    }

    /**
     * @deprecated Use <code>getSelectedObject</code>
     * @return selected OWLDataProperty
     */
    public OWLDataProperty getSelectedDataProperty() {
        return getSelectedObject();
    }

    /**
     * @deprecated Use <code>getSelectedObjects</code>
     * @return set of selected OWLDataProperties
     */
    public Set<OWLDataProperty> getSelectedOWLDataProperties() {
        return getSelectedObjects();
    }
}
