package org.protege.editor.owl.ui.merge;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.BorderLayout;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.core.ui.util.VerifiedInputEditor;
import org.protege.editor.core.ui.workspace.WorkspaceFrame;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.find.EntityFinderField;
import org.semanticweb.owlapi.model.OWLEntity;

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
        entityFinderField = new EntityFinderField(this, editorKit);
        entityFinderField.setEntityFoundHandler(entity -> {
            this.selectedEntity = Optional.of(entity);
            entityFinderField.setText(editorKit.getModelManager().getRendering(entity));
            this.listener.verifiedStatusChanged(true);
        });
        entityFinderField.setSearchStartedHandler(() -> {
            this.listener.verifiedStatusChanged(false);
        });
        setLayout(new BorderLayout(7, 7));
        add(new JLabel("<html><body>Please specify the <b>target entity</b> in the field below.<br>" +
                               "This is the entity that the selected entity will be merged into.</body></html>"),
            BorderLayout.NORTH);
        add(entityFinderField, BorderLayout.SOUTH);
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
                                                  "Specify target entity",
                                                  mergeEntitiesPanel,
                                                  JOptionPane.PLAIN_MESSAGE,
                                                  JOptionPane.OK_CANCEL_OPTION,
                                                  mergeEntitiesPanel.getEntityFinderField());
        return mergeEntitiesPanel.getTargetEntity();
    }
}
