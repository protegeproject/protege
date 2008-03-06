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
public class OWLDataPropertySelectorPanel extends AbstractSelectorPanel {

    private OWLDataPropertyHierarchyViewComponent view;


    public OWLDataPropertySelectorPanel(OWLEditorKit editorKit) {
        super(editorKit);
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


    public OWLDataProperty getSelectedDataProperty() {
        return view.getSelectedDataProperty();
    }

    public Set<OWLDataProperty> getSelectedOWLDataProperties() {
        return view.getSelectedProperties();
    }

    public void dispose() {
        view.dispose();
    }

    public void setSelectedOWLDataProperty(OWLDataProperty property) {
        view.show(property);
    }
}
