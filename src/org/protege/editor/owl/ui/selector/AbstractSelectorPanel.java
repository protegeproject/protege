package org.protege.editor.owl.ui.selector;

import org.protege.editor.core.ui.view.View;
import org.protege.editor.core.ui.view.ViewComponentPlugin;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLObject;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;
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
public abstract class AbstractSelectorPanel<O extends OWLObject> extends JPanel {

    private OWLEditorKit editorKit;

    private View view;


    public AbstractSelectorPanel(OWLEditorKit editorKit) {
        this.editorKit = editorKit;
        createUI();
    }


    public OWLEditorKit getOWLEditorKit() {
        return editorKit;
    }


    public Dimension getPreferredSize() {
        return new Dimension(300, 500);
    }


    public OWLModelManager getOWLModelManager() {
        return editorKit.getOWLModelManager();
    }


    private void createUI() {
        setLayout(new BorderLayout());
        ViewComponentPlugin plugin = getViewComponentPlugin();
        View v = new View(plugin, editorKit.getOWLWorkspace());
        v.setPinned(true);
        v.setSyncronizing(false);
        add(v);
        v.createUI();
        view = v;
        view.setShowViewBanner(false);
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                                                     BorderFactory.createEmptyBorder(2, 2, 2, 2)));
    }


    public abstract void setSelection(O entity);


    public abstract O getSelectedObject();


    public abstract Set<O> getSelectedObjects();


    public boolean requestFocusInWindow() {
        return view.requestFocusInWindow();
    }


    protected abstract ViewComponentPlugin getViewComponentPlugin();


    // cheating because we know this gets fired when selection changed
    public abstract void addSelectionListener(ChangeListener listener);

    
    public abstract void removeSelectionListener(ChangeListener listener);
}
