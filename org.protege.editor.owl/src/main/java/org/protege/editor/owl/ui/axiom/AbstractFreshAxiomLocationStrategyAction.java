package org.protege.editor.owl.ui.axiom;

import org.protege.editor.owl.model.axiom.FreshAxiomLocation;
import org.protege.editor.owl.model.axiom.FreshAxiomLocationPreferences;
import org.protege.editor.owl.ui.action.ProtegeOWLRadioButtonAction;

import java.awt.event.ActionEvent;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class AbstractFreshAxiomLocationStrategyAction extends ProtegeOWLRadioButtonAction {

    private FreshAxiomLocation freshAxiomLocation;

    public AbstractFreshAxiomLocationStrategyAction(FreshAxiomLocation freshAxiomLocation) {
        this.freshAxiomLocation = freshAxiomLocation;
    }

    @Override
    protected void update() {
        setSelected(FreshAxiomLocationPreferences.getPreferences().getFreshAxiomLocation() == freshAxiomLocation);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FreshAxiomLocationPreferences.getPreferences().setFreshAxiomLocation(freshAxiomLocation);
    }

    @Override
    public void initialise() throws Exception {
        update();
    }

    @Override
    public void dispose() throws Exception {

    }
}
