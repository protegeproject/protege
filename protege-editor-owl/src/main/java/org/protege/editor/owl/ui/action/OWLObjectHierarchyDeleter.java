package org.protege.editor.owl.ui.action;

import com.google.common.base.Stopwatch;
import org.protege.editor.core.log.LogBanner;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.protege.editor.owl.model.util.OWLEntityDeleter;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.OWLEntitySetProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 02-May-2007<br><br>
 */
public class OWLObjectHierarchyDeleter<E extends OWLEntity> {

    private final Logger logger = LoggerFactory.getLogger(OWLObjectHierarchyDeleter.class);

    private OWLEditorKit owlEditorKit;

    private OWLEntitySetProvider<E> entitySetProvider;

    private OWLObjectHierarchyProvider<E> hierarchyProvider;

    private String pluralName;

    private static final String DELETE_PREFS_KEY = "delete.preferences";

    private static final String ALWAYS_DELETE_CONFIRM = "delete.confirm.always";

    private static final String ALWAYS_CONFIRM_WHEN_DELETE_DESCENDANTS = "delete.confirm.descendants";

    private static final String DELETE_DESCENDANTS = "delete.descendants";



    public OWLObjectHierarchyDeleter(OWLEditorKit owlEditorKit, OWLObjectHierarchyProvider<E> hierarchyProvider,
                                     OWLEntitySetProvider<E> entitySetProvider, String pluralName) {
        this.owlEditorKit = owlEditorKit;
        this.hierarchyProvider = hierarchyProvider;
        this.entitySetProvider = entitySetProvider;
        this.pluralName = pluralName;
    }


    public void dispose() {
    }


    public OWLEditorKit getOWLEditorKit() {
        return owlEditorKit;
    }


    public void performDeletion() {
        Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(DELETE_PREFS_KEY);

        Set<E> selents = entitySetProvider.getEntities();

        String name;
        if (selents.size() == 1) {
            name = owlEditorKit.getModelManager().getRendering(selents.iterator().next());
        }
        else {
            name = "selected " + pluralName;
        }

        final boolean assertedSubsExist = hasAssertedSubs(selents);

        boolean showDialog = prefs.getBoolean(ALWAYS_DELETE_CONFIRM, true);
        if (assertedSubsExist){
            showDialog = prefs.getBoolean(ALWAYS_CONFIRM_WHEN_DELETE_DESCENDANTS, true);
        }

        boolean deleteDescendants = false;
        if (showDialog){
            JComponent panel = new Box(BoxLayout.PAGE_AXIS);
            JLabel label = new JLabel("<html><body>Delete " + name +
                                      "?<p>All references to " + name + " will be removed from the active ontologies.</p></body></html>");
            panel.add(label);
            String confirmLabel = "Always show this confirmation before deleting";

            JRadioButton descendantsRadioButton = null;
            if (assertedSubsExist){
                deleteDescendants = prefs.getBoolean(DELETE_DESCENDANTS, false);
                JRadioButton onlySelectedEntityRadioButton = new JRadioButton("Delete " + name + " only", !deleteDescendants);
                descendantsRadioButton = new JRadioButton("Delete " + name + " and asserted descendant " + pluralName, deleteDescendants);
                ButtonGroup bg = new ButtonGroup();
                bg.add(onlySelectedEntityRadioButton);
                bg.add(descendantsRadioButton);

                panel.add(Box.createRigidArea(new Dimension(0, 20)));
                panel.add(onlySelectedEntityRadioButton);
                panel.add(descendantsRadioButton);
                confirmLabel += " " + pluralName + " with asserted descendants";
            }

            JCheckBox alwaysConfirmCheckbox = new JCheckBox(confirmLabel, true);

            panel.add(Box.createRigidArea(new Dimension(0, 40)));
            panel.add(alwaysConfirmCheckbox);

            int ret = JOptionPane.showConfirmDialog(owlEditorKit.getWorkspace(),
                                                    panel,
                                                    "Delete " + name,
                                                    JOptionPane.OK_CANCEL_OPTION,
                                                    JOptionPane.PLAIN_MESSAGE);
            if (ret != JOptionPane.OK_OPTION){
                return;
            }

            if (assertedSubsExist){
                deleteDescendants = descendantsRadioButton.isSelected();
                prefs.putBoolean(DELETE_DESCENDANTS, deleteDescendants);
                    prefs.putBoolean(ALWAYS_CONFIRM_WHEN_DELETE_DESCENDANTS, alwaysConfirmCheckbox.isSelected());
            }
                prefs.putBoolean(ALWAYS_DELETE_CONFIRM, alwaysConfirmCheckbox.isSelected());

        }

        if (deleteDescendants){
            deleteDescendants(selents);
        }
        else{
            delete(selents);
        }
    }


    private boolean hasAssertedSubs(Set<E> entities) {
        for (E entity : entities){
            if (!hierarchyProvider.getDescendants(entity).isEmpty()){
                return true;
            }
        }
        return false;
    }

    private void delete(Set<E> ents) {
        OWLEntityDeleter.deleteEntities(ents, getOWLEditorKit().getOWLModelManager());
    }


    private void deleteDescendants(Set<E> selectedEntities) {
        logger.info(LogBanner.start("Deleting descendants"));
        logger.info("Deleting descendants of {}", selectedEntities);
        Set<E> ents = new HashSet<>();
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (E ent : selectedEntities) {
            logger.info("Retrieving descendants of {}", ent);
            ents.add(ent);
            ents.addAll(hierarchyProvider.getDescendants(ent));
        }
        stopwatch.stop();
        logger.info("Retrieved {} entities to delete in {} ms", ents.size(), stopwatch.elapsed(TimeUnit.MILLISECONDS));
        delete(ents);
        logger.info(LogBanner.end());
    }
}
