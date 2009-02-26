package org.protege.editor.owl.ui.editor;

import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.InputVerificationStatusChangedListener;
import org.protege.editor.owl.ui.selector.AbstractHierarchySelectorPanel;
import org.protege.editor.owl.ui.selector.AbstractSelectorPanel;
import org.semanticweb.owl.model.OWLDataFactory;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLProperty;

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
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 11, 2008<br><br>
 */
public abstract class AbstractRestrictionCreatorPanel<P extends OWLProperty, F extends OWLObject> extends AbstractOWLDescriptionEditor {

    private JPanel panel;

    private AbstractHierarchySelectorPanel<P> propertySelectorPanel;

    private AbstractSelectorPanel<F> fillerSelectorPanel;

    private JSpinner cardinalitySpinner;

    private JComboBox typeCombo;


    private boolean currentStatus = false;

    private Set<InputVerificationStatusChangedListener> listeners = new HashSet<InputVerificationStatusChangedListener>();

    private ChangeListener selListener = new ChangeListener(){
        public void stateChanged(ChangeEvent event) {
            checkStatus();
        }
    };


    public void initialise() throws Exception {
        panel = new JPanel();

        propertySelectorPanel = createPropertySelectorPanel();
        propertySelectorPanel.addSelectionListener(selListener);
        propertySelectorPanel.setBorder(ComponentFactory.createTitledBorder("Restricted property"));

        fillerSelectorPanel = createFillerSelectorPanel();
        propertySelectorPanel.addSelectionListener(selListener);
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


    protected abstract java.util.List<RestrictionCreator<P, F>> createTypes();


    protected abstract AbstractSelectorPanel<F> createFillerSelectorPanel();


    protected abstract AbstractHierarchySelectorPanel<P> createPropertySelectorPanel();


    public JComponent getComponent() {
        return panel;
    }


    public Set<OWLDescription> getDescriptions(){
        Set<OWLDescription> result = new HashSet<OWLDescription>();
        RestrictionCreator<P, F> creator = (RestrictionCreator<P, F>) typeCombo.getSelectedItem();
        if (creator == null) {
            return Collections.emptySet();
        }
        creator.createRestrictions(propertySelectorPanel.getSelectedObjects(),
                                   new HashSet<F>(fillerSelectorPanel.getSelectedObjects()),
                                   result);
        return result;
    }


    /**
     * @deprecated use <code>getDescriptions()</code>
     * @return a set of restrictions described by this editor
     */
    public Set<OWLDescription> createRestrictions() {
        return getDescriptions();
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


        abstract void createRestrictions(Set<P> properties, Set<F> fillers, Set<OWLDescription> result);
    }


    protected abstract class CardinalityRestrictionCreator<P extends OWLProperty, F extends OWLObject> extends RestrictionCreator<P, F> {


        protected CardinalityRestrictionCreator(String name) {
            super(name);
        }


        public void createRestrictions(Set<P> properties, Set<F> fillers,
                                       Set<OWLDescription> result) {
            for (P prop : properties) {
                for (F desc : fillers) {
                    result.add(createRestriction(prop, desc, (Integer) cardinalitySpinner.getValue()));
                }
            }
        }


        public abstract OWLDescription createRestriction(P prop, F filler, int card);
    }
}
