package org.protege.editor.owl.model.entity;

import org.protege.editor.core.ui.menu.MenuButton;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.awt.BorderLayout.WEST;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Sep 2017
 */
public class EntityBannerViewImpl implements EntityBannerView {

    private final MenuButton menuButton = new MenuButton();

    private final JLabel entityLabel = new JLabel();

    private final JPanel container = new JPanel(new BorderLayout(7, 7));

    private Optional<JPopupMenu> popupMenu = Optional.empty();

    public EntityBannerViewImpl() {
        container.add(menuButton, WEST);
        menuButton.setVerticalAlignment(SwingConstants.CENTER);
        container.add(entityLabel);
        entityLabel.setVerticalAlignment(SwingConstants.CENTER);
        menuButton.addActionListener(this::handleMenuButtonClicked);
    }

    @Nonnull
    @Override
    public JComponent asJComponent() {
        return container;
    }

    @Override
    public void setIcon(@Nonnull Icon icon) {
        entityLabel.setIcon(checkNotNull(icon));
    }

    @Override
    public void setText(@Nonnull String text) {
        entityLabel.setText(checkNotNull(text));
    }

    @Override
    public void clear() {
        entityLabel.setIcon(null);
        entityLabel.setText("");
    }

    @Override
    public void setMenuVisible(boolean visible) {
        menuButton.setVisible(visible);
    }

    @Override
    public void setMenuEnabled(boolean enabled) {
        menuButton.setEnabled(enabled);
    }

    @Override
    public void setPopupMenu(@Nonnull JPopupMenu popupMenu) {
        this.popupMenu = Optional.of(popupMenu);
    }

    private void handleMenuButtonClicked(ActionEvent e) {
        popupMenu.ifPresent(menu -> menu.show(menuButton, 0, menuButton.getHeight() + 2));
    }

}
