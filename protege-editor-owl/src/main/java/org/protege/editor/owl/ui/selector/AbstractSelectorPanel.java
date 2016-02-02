package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.core.ui.view.View;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLObject;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 03-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A common base class for selector panels, so that they
 * all have the same preferred size etc.
 */
public abstract class AbstractSelectorPanel<O extends OWLObject> extends JPanel
        implements OWLObjectSelector<O>, VerifiedInputEditor {
	private static final long serialVersionUID = -3436408401382241385L;

	private OWLEditorKit editorKit;

    private View view;

    private boolean editable;

    private List<InputVerificationStatusChangedListener> validateListeners = new ArrayList<InputVerificationStatusChangedListener>();

    public boolean isValid = false;

    private boolean registeredListener = false;


    public AbstractSelectorPanel(OWLEditorKit editorKit) {
        this(editorKit, true);
    }

    public AbstractSelectorPanel(OWLEditorKit editorKit, boolean editable) {
        this(editorKit, editable, true);
    }

    public AbstractSelectorPanel(OWLEditorKit editorKit, boolean editable, boolean autoCreateUI) {
        this.editorKit = editorKit;
        this.editable = editable;
        if (autoCreateUI){
            createUI();
        }
    }

    public OWLEditorKit getOWLEditorKit() {
        return editorKit;
    }


    public Dimension getPreferredSize() {
        return new Dimension(300, 500);
    }


    public OWLModelManager getOWLModelManager() {
        return editorKit.getModelManager();
    }


    protected void createUI() {
        setLayout(new BorderLayout());
        ViewComponentPlugin plugin = getViewComponentPlugin();
        view = new View(plugin, editorKit.getWorkspace());
        view.setPinned(true);
        view.setSyncronizing(false);
        view.createUI();
        view.setShowViewBanner(false);
        add(view);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                                                     BorderFactory.createEmptyBorder(2, 2, 2, 2)));

        // only attach change listeners once the component is shown
        // (as those that use a view component are lazilly created)
        view.addHierarchyListener(event -> {
            if (!registeredListener){
                addSelectionListener(new ChangeListener(){
                    public void stateChanged(ChangeEvent event) {
                        boolean valid = getSelectedObjects() != null && !getSelectedObjects().isEmpty();
                        for (InputVerificationStatusChangedListener l : validateListeners){
                            l.verifiedStatusChanged(valid);
                        }
                        isValid = valid;
                    }
                });
                registeredListener = true;
            }
        });
    }


    public void clearSelection(){
        setSelection((O)null);
    }
    

    public abstract void setSelection(O entity);


    public abstract void setSelection(Set<O> entities);


    public abstract O getSelectedObject();


    public abstract Set<O> getSelectedObjects();


    public boolean requestFocusInWindow() {
        return view.requestFocusInWindow();
    }

    protected final boolean isEditable(){
        return editable;
    }

    protected abstract ViewComponentPlugin getViewComponentPlugin();


    // cheating because we know this gets fired when selection changed
    public abstract void addSelectionListener(ChangeListener listener);


    public abstract void removeSelectionListener(ChangeListener listener);


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener){
        validateListeners.add(listener);
        listener.verifiedStatusChanged(isValid);
    }

    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener){
        validateListeners.remove(listener);
    }


    public void dispose() {
    	if (view != null) {
    		view.dispose();
    	}
    }
}
