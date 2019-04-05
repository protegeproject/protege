package org.protege.editor.owl.ui.merge;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.core.ui.workspace.WorkspaceFrame;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.find.EntityFinderField;
import org.protege.editor.owl.ui.find.EntityFoundHandler;
import org.protege.editor.owl.ui.find.SearchStartedHandler;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-05
 */
public class MergeEntitiesPanel extends JPanel implements VerifiedInputEditor {

    @Nonnull
    private Optional<OWLEntity> selectedEntity = Optional.empty();

    private final EntityFinderField entityFinderField;

    private InputVerificationStatusChangedListener listener = newState -> {};

    public MergeEntitiesPanel(OWLEditorKit editorKit) {
        PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        panel.addGroup("Target Entity");
        entityFinderField = new EntityFinderField(this, editorKit);
        entityFinderField.setEntityFoundHandler(entity -> {
            this.selectedEntity = Optional.of(entity);
            entityFinderField.setText(editorKit.getModelManager().getRendering(entity));
            this.listener.verifiedStatusChanged(true);
        });
        entityFinderField.setSearchStartedHandler(() -> {
            this.listener.verifiedStatusChanged(false);
        });
        panel.addGroupComponent(entityFinderField);
        panel.addHelpText("The target entity is the entity that the selected entity will be merged into");
        setLayout(new BorderLayout());
        add(panel);
    }

    @Override
    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        this.listener = checkNotNull(listener);
    }

    @Override
    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        this.listener = newState -> {};
    }

    private EntityFinderField getEntityFinderField() {
        return entityFinderField;
    }

    public Optional<OWLEntity> getTargetEntity() {
        return this.selectedEntity;
    }

    public static Optional<OWLEntity> showDialog(OWLEditorKit editorKit) {
        WorkspaceFrame parent = ProtegeManager.getInstance().getFrame(editorKit.getWorkspace());
        MergeEntitiesPanel mergeEntitiesPanel = new MergeEntitiesPanel(editorKit);
        JOptionPaneEx.showValidatingConfirmDialog(parent,
                                                  "Select target entity",
                                                  mergeEntitiesPanel,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  mergeEntitiesPanel.getEntityFinderField());
        return mergeEntitiesPanel.getTargetEntity();
    }
}
