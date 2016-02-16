package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.owl.ui.selector.AbstractHierarchySelectorPanel;
import org.protege.editor.owl.ui.selector.AbstractSelectorPanel;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLProperty;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 11, 2008<br><br>
 */
public abstract class AbstractRestrictionCreatorPanel<P extends OWLProperty, F extends OWLObject> extends AbstractOWLClassExpressionEditor {

    private JPanel panel;

    private AbstractHierarchySelectorPanel<P> propertySelectorPanel;

    private AbstractSelectorPanel<F> fillerSelectorPanel;

    private JSpinner cardinalitySpinner;

    private JComboBox typeCombo;


    private boolean currentStatus = false;

    private Set<InputVerificationStatusChangedListener> listeners = new HashSet<>();

    private ChangeListener selListener = event -> checkStatus();


    public void initialise() throws Exception {
        panel = new JPanel();

        propertySelectorPanel = createPropertySelectorPanel();
        propertySelectorPanel.addSelectionListener(selListener);
        propertySelectorPanel.setBorder(ComponentFactory.createTitledBorder("Restricted property"));

        fillerSelectorPanel = createFillerSelectorPanel();
        fillerSelectorPanel.addSelectionListener(selListener);
        fillerSelectorPanel.setBorder(ComponentFactory.createTitledBorder("Restriction filler"));

        panel.setLayout(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, false);
        splitPane.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        splitPane.setResizeWeight(0.5);
        splitPane.setLeftComponent(propertySelectorPanel);
        splitPane.setRightComponent(fillerSelectorPanel);
        panel.add(splitPane);

        java.util.List<RestrictionCreator<P, F>> types = createTypes();

        typeCombo = new JComboBox(types.toArray());
        cardinalitySpinner = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));
        JComponent cardinalitySpinnerEditor = cardinalitySpinner.getEditor();
        Dimension prefSize = cardinalitySpinnerEditor.getPreferredSize();
        cardinalitySpinnerEditor.setPreferredSize(new Dimension(50, prefSize.height));


        final JPanel typePanel = new JPanel();
        typePanel.setBorder(ComponentFactory.createTitledBorder("Restriction type"));
        panel.add(typePanel, BorderLayout.SOUTH);
        typePanel.add(typeCombo);
        typeCombo.addActionListener(e -> {
            cardinalitySpinner.setEnabled(typeCombo.getSelectedItem() instanceof CardinalityRestrictionCreator);
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


    protected abstract java.util.List<RestrictionCreator<P, F>> createTypes();


    protected abstract AbstractSelectorPanel<F> createFillerSelectorPanel();


    protected abstract AbstractHierarchySelectorPanel<P> createPropertySelectorPanel();


    public JComponent getComponent() {
        return panel;
    }


    public Set<OWLClassExpression> getClassExpressions(){
        Set<OWLClassExpression> result = new HashSet<>();
        RestrictionCreator<P, F> creator = (RestrictionCreator<P, F>) typeCombo.getSelectedItem();
        if (creator == null) {
            return Collections.emptySet();
        }
        creator.createRestrictions(propertySelectorPanel.getSelectedObjects(),
                                   new HashSet<>(fillerSelectorPanel.getSelectedObjects()),
                                   result);
        return result;
    }


    protected final OWLDataFactory getDataFactory(){
        return getOWLEditorKit().getModelManager().getOWLDataFactory();
    }


    public void dispose() {
        propertySelectorPanel.dispose();
        fillerSelectorPanel.dispose();
        panel = null;
    }


    private void checkStatus() {
        boolean newStatus = isValidInput();
        if (currentStatus != newStatus){
            currentStatus = newStatus;
            for (InputVerificationStatusChangedListener l : listeners){
                l.verifiedStatusChanged(newStatus);
            }
        }
    }

    protected void setType(RestrictionCreator type){
        typeCombo.setSelectedItem(type);
    }


    protected void setProperty(P property) {
        propertySelectorPanel.setSelection(property);
    }


    protected void setFiller(F filler) {
        fillerSelectorPanel.setSelection(filler);
    }


    protected void setCardinality(int cardinality){
        cardinalitySpinner.setValue(cardinality);
    }


    public boolean isValidInput() {
        return propertySelectorPanel.getSelectedObject() != null && fillerSelectorPanel.getSelectedObject() != null;
    }


    public void addStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.add(listener);
        listener.verifiedStatusChanged(currentStatus);
    }


    public void removeStatusChangedListener(InputVerificationStatusChangedListener listener) {
        listeners.remove(listener);
    }


    protected abstract class RestrictionCreator<P extends OWLProperty, F extends OWLObject> {

        private String name;


        protected RestrictionCreator(String name) {
            this.name = name;
        }


        public String toString() {
            return name;
        }


        abstract void createRestrictions(Set<P> properties, Set<F> fillers, Set<OWLClassExpression> result);
    }


    protected abstract class CardinalityRestrictionCreator<P extends OWLProperty, F extends OWLObject> extends RestrictionCreator<P, F> {


        protected CardinalityRestrictionCreator(String name) {
            super(name);
        }


        public void createRestrictions(Set<P> properties, Set<F> fillers,
                                       Set<OWLClassExpression> result) {
            for (P prop : properties) {
                for (F desc : fillers) {
                    result.add(createRestriction(prop, desc, (Integer) cardinalitySpinner.getValue()));
                }
            }
        }


        public abstract OWLClassExpression createRestriction(P prop, F filler, int card);
    }
}
