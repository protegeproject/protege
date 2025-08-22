package org.protege.editor.owl.model.entity;

import org.protege.editor.core.ui.menu.MenuBuilder;
import org.protege.editor.core.ui.menu.PopupMenuId;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.view.EntityBannerFormatter;
import org.protege.editor.owl.ui.view.EntityBannerFormatterImpl;
import org.semanticweb.owlapi.model.OWLEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.swing.*;


import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Sep 2017
 */
public class EntityBannerPresenter {

    private static final Logger logger = LoggerFactory.getLogger(EntityBannerPresenter.class);

    private static final String ENTITY_BANNER_MENU_ID = "[EntityBanner]";

    @Nonnull
    private final EntityBannerView view;

    @Nonnull
    private final OWLEditorKit editorKit;

    @Nonnull
    private final EntityBannerFormatter formatter = new EntityBannerFormatterImpl();

    public EntityBannerPresenter(@Nonnull EntityBannerView view, @Nonnull OWLEditorKit editorKit) {
        this.view = checkNotNull(view);
        this.editorKit = checkNotNull(editorKit);
    }

    @Nonnull
    public JComponent getView() {
        return view.asJComponent();
    }

    public void start() {
        editorKit.getOWLWorkspace().getOWLSelectionModel().addListener(this::handleSelectionChanged);
        handleSelectionChanged();
        MenuBuilder menuBuilder = new MenuBuilder(editorKit);
        JPopupMenu popupMenu = menuBuilder.buildPopupMenu(new PopupMenuId(ENTITY_BANNER_MENU_ID));
        view.setPopupMenu(popupMenu);
    }

    private void handleSelectionChanged() {
        view.clear();
        OWLEntity entity = editorKit.getOWLWorkspace().getOWLSelectionModel().getSelectedEntity();
        if(entity == null) {
            view.asJComponent().setVisible(false);
            view.setMenuEnabled(false);
        }
        else {
            view.asJComponent().setVisible(true);
            String formattedBanner = formatter.formatBanner(entity, editorKit);
            view.setText(formattedBanner);
            Icon entityIcon = editorKit.getOWLWorkspace().getOWLIconProvider().getIcon(entity);
            view.setIcon(entityIcon);
            view.setMenuEnabled(true);
        }

    }

    public void dispose() {
        editorKit.getOWLWorkspace().getOWLSelectionModel().removeListener(this::handleSelectionChanged);
    }
}
