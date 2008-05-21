package org.protege.editor.owl.ui.frame;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.clsdescriptioneditor.ExpressionEditor;
import org.protege.editor.owl.ui.clsdescriptioneditor.OWLDescriptionChecker;
import org.protege.editor.owl.ui.selector.OWLClassSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectProperty;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 15-Feb-2007<br><br>
 */
public class OWLClassDescriptionEditor extends AbstractOWLFrameSectionRowObjectEditor<OWLDescription>
        implements VerifiedInputEditor {

    private static final String CLASS_EXPRESSION_EDITOR_LABEL = "Class expression editor";
    private static final String CLASS_TREE_LABEL = "Class tree";
    private static final String RESTRICTION_CREATOR_LABEL = "Restriction creator";

    private OWLEditorKit editorKit;

    private OWLDescriptionChecker checker;

    private ExpressionEditor<OWLDescription> editor;

    private JComponent editingComponent;

    private JTabbedPane tabbedPane;

    private OWLClassSelectorPanel classSelectorPanel;

    private ObjectRestrictionCreatorPanel restrictionCreatorPanel;

    private OWLDescription initialDescription;

    private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();

    private ChangeListener changeListener = new ChangeListener(){
        public void stateChanged(ChangeEvent changeEvent) {
            handleVerifyEditorContents();
        }
    };

    public OWLClassDescriptionEditor(OWLEditorKit editorKit, OWLDescription description) {
        this.editorKit = editorKit;
        this.initialDescription = description;
        checker = new OWLDescriptionChecker(editorKit);
        editor = new ExpressionEditor<OWLDescription>(editorKit, checker);
        editor.setExpressionObject(description);


        tabbedPane = new JTabbedPane();
        tabbedPane.setFocusable(false);
        editingComponent = new JPanel(new BorderLayout());
        editingComponent.add(tabbedPane);
        editingComponent.setPreferredSize(new Dimension(500, 400));
        tabbedPane.add(CLASS_EXPRESSION_EDITOR_LABEL, new JScrollPane(editor));

        if (description == null || !description.isAnonymous()) {
            classSelectorPanel = new OWLClassSelectorPanel(editorKit);
            tabbedPane.add(CLASS_TREE_LABEL, classSelectorPanel);
            if (description != null) {
                classSelectorPanel.setSelection(description.asOWLClass());
            }
            classSelectorPanel.addSelectionListener(changeListener);

            restrictionCreatorPanel = new ObjectRestrictionCreatorPanel();
            tabbedPane.add(RESTRICTION_CREATOR_LABEL, restrictionCreatorPanel);
            restrictionCreatorPanel.classSelectorPanel.addSelectionListener(changeListener);
            restrictionCreatorPanel.objectPropertySelectorPanel.addSelectionListener(changeListener);

            tabbedPane.addChangeListener(changeListener);
        }
    }

    private void handleVerifyEditorContents() {
        for (InputVerificationStatusChangedListener l : listeners){
            l.verifiedStatusChanged(isValidated());
        }
    }


    private boolean isValidated() {
        boolean validated = false;
        final String selectedTabTitle = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
        if (selectedTabTitle.equals(CLASS_EXPRESSION_EDITOR_LABEL)){
            validated = editor.isWellFormed();
        }
        else if (selectedTabTitle.equals(CLASS_TREE_LABEL)){
            validated = classSelectorPanel.getSelectedObject() != null;
        }
        else if (selectedTabTitle.equals(RESTRICTION_CREATOR_LABEL)){
            validated = restrictionCreatorPanel.classSelectorPanel.getSelectedObject() != null &&
                    restrictionCreatorPanel.objectPropertySelectorPanel.getSelectedObject() != null;
        }
        return validated;
    }


    public JComponent getInlineEditorComponent() {
        // Same as general editor component
        return editingComponent;
    }


    /**
     * Gets a component that will be used to edit the specified
     * object.
     * @return The component that will be used to edit the object
     */
    public JComponent getEditorComponent() {
        return editingComponent;
    }


    public void clear() {
        initialDescription = null;
        editor.setText("");
    }


    public Set<OWLDescription> getEditedObjects() {
        if (tabbedPane.getSelectedComponent() == classSelectorPanel) {
            return new HashSet<OWLDescription>(classSelectorPanel.getSelectedObjects());
        }
        else if (tabbedPane.getSelectedComponent() == restrictionCreatorPanel) {
            return restrictionCreatorPanel.createRestrictions();
        }
        return super.getEditedObjects();
    }


    /**
     * Gets the object that has been edited.
     * @return The edited object
     */
    public OWLDescription getEditedObject() {
        try {
            if (!editor.isWellFormed()) {
                return null;
            }
            String expression = editor.getText();
            if (editor.isWellFormed()) {
                return editorKit.getOWLModelManager().getOWLDescriptionParser().createOWLDescription(expression);
            }
            else {
                return null;
            }
        }
        catch (OWLException e) {
            return null;
        }
    }


    public void dispose() {
        if (classSelectorPanel != null) {
            classSelectorPanel.dispose();
        }
        if (restrictionCreatorPanel != null) {
            restrictionCreatorPanel.dispose();
        }
    }


    private OWLDataFactory getDataFactory() {
        return editorKit.getOWLModelManager().getOWLDataFactory();
    }

    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.add(listener);
        editor.addStatusChangedListener(listener);
        listener.verifiedStatusChanged(isValidated());
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.remove(listener);
        editor.removeStatusChangedListener(listener);
    }


    private class ObjectRestrictionCreatorPanel extends JPanel {

        private OWLObjectPropertySelectorPanel objectPropertySelectorPanel;

        private OWLClassSelectorPanel classSelectorPanel;

        private JSpinner cardinalitySpinner;

        private JComboBox typeCombo;


        public ObjectRestrictionCreatorPanel() {
            objectPropertySelectorPanel = new OWLObjectPropertySelectorPanel(editorKit);
            objectPropertySelectorPanel.setBorder(ComponentFactory.createTitledBorder("Restricted properties"));
            cardinalitySpinner = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
            JComponent cardinalitySpinnerEditor = cardinalitySpinner.getEditor();
            Dimension prefSize = cardinalitySpinnerEditor.getPreferredSize();
            cardinalitySpinnerEditor.setPreferredSize(new Dimension(50, prefSize.height));
            classSelectorPanel = new OWLClassSelectorPanel(editorKit);
            classSelectorPanel.setBorder(ComponentFactory.createTitledBorder("Restriction fillers"));
            setLayout(new BorderLayout());
            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
            splitPane.setResizeWeight(0.5);
            splitPane.setLeftComponent(objectPropertySelectorPanel);
            splitPane.setRightComponent(classSelectorPanel);
            add(splitPane);
            splitPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            List<RestrictionCreator> types = new ArrayList<RestrictionCreator>();
            types.add(new RestrictionCreator("Some (existential)") {
                public void createRestrictions(Set<OWLObjectProperty> properties, Set<OWLDescription> fillers,
                                               Set<OWLDescription> result) {
                    for (OWLObjectProperty prop : properties) {
                        for (OWLDescription filler : fillers) {
                            result.add(getDataFactory().getOWLObjectSomeRestriction(prop, filler));
                        }
                    }
                }
            });
            types.add(new RestrictionCreator("Only (universal)") {
                public void createRestrictions(Set<OWLObjectProperty> properties, Set<OWLDescription> fillers,
                                               Set<OWLDescription> result) {
                    for (OWLObjectProperty prop : properties) {
                        if (fillers.isEmpty()) {
                            return;
                        }
                        OWLDescription filler;
                        if (fillers.size() > 1) {
                            filler = getDataFactory().getOWLObjectUnionOf(fillers);
                        }
                        else {
                            filler = fillers.iterator().next();
                        }
                        result.add(getDataFactory().getOWLObjectAllRestriction(prop, filler));
                    }
                }
            });
            types.add(new CardinalityRestrictionCreator("Min (min cardinality)", cardinalitySpinner) {
                public OWLDescription createRestriction(OWLObjectProperty prop, OWLDescription filler, int card) {
                    return getDataFactory().getOWLObjectMinCardinalityRestriction(prop, card, filler);
                }
            });
            types.add(new CardinalityRestrictionCreator("Exactly (exact cardinality)", cardinalitySpinner) {
                public OWLDescription createRestriction(OWLObjectProperty prop, OWLDescription filler, int card) {
                    return getDataFactory().getOWLObjectExactCardinalityRestriction(prop, card, filler);
                }
            });
            types.add(new CardinalityRestrictionCreator("Max (max cardinality)", cardinalitySpinner) {
                public OWLDescription createRestriction(OWLObjectProperty prop, OWLDescription filler, int card) {
                    return getDataFactory().getOWLObjectMaxCardinalityRestriction(prop, card, filler);
                }
            });
            typeCombo = new JComboBox(types.toArray());


            final JPanel typePanel = new JPanel();
            typePanel.setBorder(ComponentFactory.createTitledBorder("Restriction type"));
            add(typePanel, BorderLayout.SOUTH);
            typePanel.add(typeCombo);
            typeCombo.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    cardinalitySpinner.setEnabled(typeCombo.getSelectedItem() instanceof CardinalityRestrictionCreator);
                }
            });
            JPanel spinnerHolder = new JPanel(new BorderLayout(4, 4));
            spinnerHolder.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
            spinnerHolder.add(new JLabel("Cardinality"), BorderLayout.WEST);
            spinnerHolder.add(cardinalitySpinner, BorderLayout.EAST);
            JPanel spinnerAlignmentPanel = new JPanel(new BorderLayout());
            spinnerAlignmentPanel.add(spinnerHolder, BorderLayout.WEST);
            typePanel.add(spinnerAlignmentPanel);
            cardinalitySpinner.setEnabled(typeCombo.getSelectedItem() instanceof CardinalityRestrictionCreator);
        }


        public Set<OWLDescription> createRestrictions() {
            Set<OWLDescription> result = new HashSet<OWLDescription>();
            RestrictionCreator creator = (RestrictionCreator) typeCombo.getSelectedItem();
            if (creator == null) {
                return Collections.emptySet();
            }
            creator.createRestrictions(objectPropertySelectorPanel.getSelectedObjects(),
                                       new HashSet<OWLDescription>(classSelectorPanel.getSelectedObjects()),
                                       result);
            return result;
        }


        public void dispose() {
            objectPropertySelectorPanel.dispose();
            classSelectorPanel.dispose();
        }
    }


    private abstract class RestrictionCreator {

        private String name;


        protected RestrictionCreator(String name) {
            this.name = name;
        }


        public String toString() {
            return name;
        }


        abstract void createRestrictions(Set<OWLObjectProperty> properties, Set<OWLDescription> fillers,
                                         Set<OWLDescription> result);
    }


    private abstract class CardinalityRestrictionCreator extends RestrictionCreator {

        private JSpinner cardinalitySpinner;


        protected CardinalityRestrictionCreator(String name, JSpinner cardinalitySpinner) {
            super(name);
            this.cardinalitySpinner = cardinalitySpinner;
        }


        public void createRestrictions(Set<OWLObjectProperty> properties, Set<OWLDescription> fillers,
                                       Set<OWLDescription> result) {
            for (OWLObjectProperty prop : properties) {
                for (OWLDescription desc : fillers) {
                    result.add(createRestriction(prop, desc, (Integer) cardinalitySpinner.getValue()));
                }
            }
        }


        public abstract OWLDescription createRestriction(OWLObjectProperty prop, OWLDescription filler, int card);
    }
}
