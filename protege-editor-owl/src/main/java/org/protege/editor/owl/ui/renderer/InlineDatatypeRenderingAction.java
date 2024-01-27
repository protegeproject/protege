package org.protege.editor.owl.ui.renderer;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.ui.action.ProtegeOWLRadioButtonAction;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 28/05/2014
 */
public class InlineDatatypeRenderingAction extends ProtegeOWLRadioButtonAction {

    @Override
    protected void update() {
        setSelected(OWLRendererPreferences.getInstance().isDisplayLiteralDatatypesInline());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OWLRendererPreferences.getInstance().setDisplayLiteralDatatypesInline(isSelected());
    }

    @Override
    public void initialise() throws Exception {

    }

    @Override
    public void dispose() throws Exception {

    }
}
