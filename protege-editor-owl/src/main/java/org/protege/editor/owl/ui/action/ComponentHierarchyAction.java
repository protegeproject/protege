package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ProtegeManager;

import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 16/11/15
 *
 * An action that traverses the swing component hierarchy and applies the action to components that implement
 * a given interface.
 */
public abstract class ComponentHierarchyAction<T> extends ProtegeOWLAction {

    private Class<T> targetClass;

    @Override
    public final void actionPerformed(ActionEvent e) {
        Frame frame = ProtegeManager.getInstance().getFrame(getWorkspace());
        for(Component c : frame.getComponents()) {
            processComponent(e, c, targetClass);
        }
    }

    /**
     * Handles the action on the specified target.
     * @param e The action event.
     * @param target The target.  Not null.
     */
    protected abstract void actionPerformedOnTarget(ActionEvent e, T target);

    @Override
    public final void initialise() throws Exception {
        targetClass = initialiseAction();
    }

    protected abstract Class<T> initialiseAction();

    @Override
    public void dispose() throws Exception {

    }

    private void processComponent(ActionEvent e, Component c, Class<T> cls) {
        if(cls.isInstance(c)) {
            T target = cls.cast(c);
            actionPerformedOnTarget(e, target);
        }
        if(c instanceof Container) {
            for(Component childComponent : ((Container) c).getComponents()) {
                processComponent(e, childComponent, cls);
            }
        }
    }
}
