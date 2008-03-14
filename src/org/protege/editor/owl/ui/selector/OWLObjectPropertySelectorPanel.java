package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
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
public class OWLObjectPropertySelectorPanel extends AbstractSelectorPanel {

    private OWLObjectPropertyHierarchyViewComponent view;


    public OWLObjectPropertySelectorPanel(OWLEditorKit editorKit) {
        super(editorKit);
    }


    protected ViewComponentPlugin getViewComponentPlugin() {

        return new ViewComponentPluginAdapter() {
            public String getLabel() {
                return "Object properties";
            }


            public Workspace getWorkspace() {
                return getOWLEditorKit().getOWLWorkspace();
            }


            public ViewComponent newInstance() throws ClassNotFoundException, IllegalAccessException,
                                                      InstantiationException {
                view = new OWLObjectPropertyHierarchyViewComponent();
                view.setup(this);
                return view;
            }


            public Color getBackgroundColor() {
                return OWLSystemColors.getOWLObjectPropertyColor();
            }
        };
    }


    public OWLObjectProperty getSelectedOWLObjectProperty() {
        return view.getSelectedProperty();
    }


    public Set<OWLObjectProperty> getSelectedOWLObjectProperties() {
        return view.getSelectedProperties();
    }


    public void setSelectedOWLObjectProperty(OWLObjectProperty p){
        view.show(p);
    }


    public void dispose() {
        view.dispose();
    }


    public void addSelectionListener(ChangeListener listener) {
        view.addChangeListener(listener); // cheating because we know this gets called when the selection changes
    }
}
