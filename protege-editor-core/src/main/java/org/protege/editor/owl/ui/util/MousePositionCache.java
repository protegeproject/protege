package org.protege.editor.owl.ui.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-01-17
 */
public class MousePositionCache {

    @Nonnull
    private final Component component;

    private boolean stale = true;

    private Point cachedPosition = null;

    private MousePositionCache(@Nonnull Component component) {
        this.component = checkNotNull(component);
    }

    public static MousePositionCache createAndInstall(@Nonnull Component component) {
        MousePositionCache cache = new MousePositionCache(component);
        cache.attacheListeners();
        return cache;
    }

    private void attacheListeners() {
        component.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                markAsStale();
            }
        });
    }

    private void markAsStale() {
        stale = true;
    }

    @Nullable
    public Point getMousePosition() {
        if(stale) {
            cachedPosition = component.getMousePosition();
            stale = false;
        }
        return cachedPosition;
    }
}
