package org.protege.editor.owl.ui.deprecation;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.AbstractOWLWizardPanel;
import org.protege.editor.owl.ui.find.EntityFinderField;
import org.protege.editor.owl.ui.renderer.OWLCellRenderer;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 24 Aug 2017
 */
public class AlternateEntitiesPage extends AbstractOWLWizardPanel {

    public static final String ID = "AlternateEntitiesPage";

    private final JButton addButton = new JButton("Add");

    private final JList<OWLEntity> entityList = new JList<>();

    private final List<OWLEntity> listData = new ArrayList<>();

    private final JButton removeButton = new JButton("Remove");

    private OWLEntity lastFoundEntity = null;

    private final EntityFinderField finderField;

    private final DeprecateEntityWizardState state;

    public AlternateEntitiesPage(@Nonnull OWLEditorKit editorKit,
                                 @Nonnull DeprecateEntityWizardState state) {
        super(ID, "Alternate entities", editorKit);
        this.state = checkNotNull(state);
        setInstructions("You have chosen <emph>not</emph> to replace the deprecated entity with a different entity.  " +
                                "Please enter a list of alternate entities that should be " +
                                "considered by consumers of this ontology.");

        JPanel content = new JPanel(new GridBagLayout());

        finderField = new EntityFinderField(content, getOWLEditorKit());
        finderField.setEntityFoundHandler(entity -> {
            finderField.setText(getOWLModelManager().getRendering(entity));
            lastFoundEntity = entity;
            addButton.setEnabled(true);
        });
        addButton.setEnabled(false);
        finderField.setSearchStartedHandler(() -> {
            lastFoundEntity = null;
            addButton.setEnabled(false);
        });
        Insets insets = new Insets(1, 1, 1, 1);
        content.add(finderField, new GridBagConstraints(0, 0,
                                                        1, 1,
                                                        100, 0,
                                                        GridBagConstraints.BASELINE_LEADING,
                                                        GridBagConstraints.HORIZONTAL,
                                                        insets,
                                                        0, 0));
        content.add(addButton, new GridBagConstraints(1, 0,
                                                      1, 1,
                                                      0, 0,
                                                      GridBagConstraints.BASELINE_LEADING,
                                                      GridBagConstraints.HORIZONTAL,
                                                      insets,
                                                      0, 0));
        addButton.addActionListener(e -> {
            listData.add(lastFoundEntity);
            updateEntityList();
            addButton.setEnabled(false);
            finderField.setText("");
            finderField.requestFocus();
        });
        content.add(new JScrollPane(entityList), new GridBagConstraints(0, 1,
                                                       1, 1,
                                                       100, 100,
                                                       GridBagConstraints.CENTER,
                                                       GridBagConstraints.BOTH,
                                                       insets,
                                                       0, 0));
        entityList.addListSelectionListener(e -> {
            removeButton.setEnabled(entityList.getSelectedValue() != null);
        });
        removeButton.setEnabled(false);
        removeButton.addActionListener(e -> {
            OWLEntity sel = entityList.getSelectedValue();
            if(sel != null) {
                int selIndex = entityList.getSelectedIndex();
                listData.remove(sel);
                updateEntityList();
                if(entityList.getModel().getSize() > selIndex) {
                    entityList.setSelectedIndex(selIndex);
                }
                else if(entityList.getModel().getSize() > selIndex - 1) {
                    entityList.setSelectedIndex(selIndex - 1);
                }
            }
        });
        entityList.setCellRenderer(new OWLCellRenderer(getOWLEditorKit()));
        content.add(removeButton, new GridBagConstraints(1, 1,
                                                         1, 1,
                                                         0, 0,
                                                         GridBagConstraints.BASELINE_LEADING,
                                                         GridBagConstraints.HORIZONTAL,
                                                         insets,
                                                         0, 0));
        content.setPreferredSize(new Dimension(450, 300));
        setContent(content);
    }

    private void updateEntityList() {
        entityList.setListData(listData.toArray(new OWLEntity[listData.size()]));
    }

    @Nullable
    @Override
    public Object getBackPanelDescriptor() {
        return DeprecationReplacementEntityPage.ID;
    }

    @Nullable
    @Override
    public Object getNextPanelDescriptor() {
        return DeprecationSummaryPage.ID;
    }

    @Override
    public void displayingPanel() {
        finderField.requestFocus();
    }

    @Override
    public void aboutToHidePanel() {
        state.setAlternateEntities(listData);
    }
}
