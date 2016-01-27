package org.protege.editor.owl.ui.view;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.view.ViewComponent;
import org.protege.editor.core.ui.view.ViewMode;

import java.util.*;
import javax.smartcardio.Card;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Predicate;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 26/01/16
 */
public class ViewModeComponent<T extends JComponent> extends JPanel implements Disposable {

    private Map<Optional<ViewMode>, T> mode2Component = new LinkedHashMap<>();

    private Optional<ViewMode> currentMode = Optional.empty();

    private Disposable disposeHook = () -> {};

    public ViewModeComponent() {
        setLayout(new CardLayout());
    }

    public void setDisposeHook(Disposable disposeHook) {
        this.disposeHook = disposeHook;
    }

    public void setViewMode(Optional<ViewMode> viewMode) {
        currentMode = viewMode;
        CardLayout cardLayout = (CardLayout) getLayout();
        if (viewMode.isPresent()) {
            cardLayout.show(this, viewMode.get().getName());
        }
        else {
            cardLayout.first(this);
        }
        T component = mode2Component.get(currentMode);
        if(component != null) {
            component.requestFocus();
        }
    }

    public T getComponentForCurrentViewMode() {
        return mode2Component.get(currentMode);
    }

    public void add(T component, ViewMode viewMode, boolean scrollPane) {
        if(mode2Component.isEmpty()) {
            mode2Component.put(Optional.empty(), component);
        }
        mode2Component.put(Optional.of(viewMode), component);
        if (!scrollPane) {
            super.add(component, viewMode.getName());
        }
        else {
            super.add(ComponentFactory.createScrollPane(component), viewMode.getName());
        }
    }


    @Deprecated
    @Override
    public Component add(Component comp) {
        throw new RuntimeException("Use add(Component, ViewMode, boolean)");
    }

    @Deprecated
    @Override
    public Component add(String name, Component comp) {
        throw new RuntimeException("Use add(Component, ViewMode, boolean)");
    }

    @Deprecated
    @Override
    public Component add(Component comp, int index) {
        throw new RuntimeException("Use add(Component, ViewMode, boolean)");
    }

    @Deprecated
    @Override
    public void add(Component comp, Object constraints) {
        throw new RuntimeException("Use add(Component, ViewMode, boolean)");
    }

    @Deprecated
    @Override
    public void add(Component comp, Object constraints, int index) {
        throw new RuntimeException("Use add(Component, ViewMode)");
    }


    public T getComponentForViewMode(Optional<ViewMode> viewMode) {
        return mode2Component.get(viewMode);
    }

    @Override
    public void dispose() throws Exception {
        disposeHook.dispose();
    }
}
