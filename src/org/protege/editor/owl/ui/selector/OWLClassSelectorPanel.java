package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.core.ui.view.ViewComponentPluginAdapter;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.ui.clshierarchy.AbstractOWLClassHierarchyViewComponent;
import org.protege.editor.owl.ui.clshierarchy.ToldOWLClassHierarchyViewComponent;
import org.protege.editor.owl.ui.editor.OWLDescriptionEditor;
import org.protege.editor.owl.ui.renderer.OWLSystemColors;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDescription;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.HashSet;
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
public class OWLClassSelectorPanel extends AbstractHierarchySelectorPanel<OWLClass> implements OWLDescriptionEditor {

    private static final String TITLE = "OWL Asserted Class Hierarchy";

    private AbstractOWLClassHierarchyViewComponent viewComponent;


    public OWLClassSelectorPanel(OWLEditorKit editorKit) {
        this(editorKit, true);
    }

    public OWLClassSelectorPanel(OWLEditorKit editorKit, boolean editable) {
        this(editorKit, editable, editorKit.getModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider());
    }

    public OWLClassSelectorPanel(OWLEditorKit editorKit, boolean editable, OWLObjectHierarchyProvider<OWLClass> hp) {
        super(editorKit, editable, hp);
    }

    public String getEditorName() {
        return TITLE;
    }


    public JComponent getComponent() {
        return this;
    }


    public boolean isValidInput() {
        return getSelectedObjects() != null && !getSelectedObjects().isEmpty();
    }


    public boolean setDescription(OWLDescription description) {
        if (description == null){
            return true;
        }
        if (!description.isAnonymous()){
            setSelection(description.asOWLClass());
            return true;
        }
        return false;
    }


    public Set<OWLDescription> getDescriptions() {
        return new HashSet<OWLDescription>(getSelectedObjects());
    }


    protected ViewComponentPlugin getViewComponentPlugin() {

        return new ViewComponentPluginAdapter() {
            public String getLabel() {
                return getEditorName();
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
        viewComponent.setSelectedClass(cls);
    }


    public void setSelection(Set<OWLClass> clses) {
        viewComponent.setSelectedClasses(clses);
    }


    public OWLClass getSelectedObject() {
        return viewComponent.getSelectedClass();
    }

    public Set<OWLClass> getSelectedObjects() {
        return viewComponent.getSelectedClasses();
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

    /**
     * @deprecated Use <code>setSelection()</code>
     * @param cls
     */
    public void setSelectedClass(OWLClass cls) {
        setSelection(cls);
    }

    /**
     * @deprecated Use <code>getSelectedObject()</code>
     * @return OWLClass
     */
    public OWLClass getSelectedClass() {
        return getSelectedObject();
    }

    /**
     * @deprecated Use <code>getSelectedObjects</code>
     * @return set of selected classes
     */
    public Set<OWLClass> getSelectedClasses() {
        return viewComponent.getSelectedClasses();
    }
}
