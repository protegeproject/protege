package org.protege.editor.owl.model.entity;

import javax.annotation.Nonnull;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Sep 2017
 */
public interface EntityBannerView {

    /**
     * Get this view as a JComponent
     */
    @Nonnull
    JComponent asJComponent();

    void setIcon(@Nonnull Icon icon);

    void setText(@Nonnull String text);

    void clear();

    void setMenuVisible(boolean visible);

    void setMenuEnabled(boolean enabled);

    void setPopupMenu(@Nonnull JPopupMenu popupMenu);
}
