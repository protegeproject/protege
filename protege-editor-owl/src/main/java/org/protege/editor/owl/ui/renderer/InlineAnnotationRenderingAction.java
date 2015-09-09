package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.ui.action.ProtegeOWLRadioButtonAction;

import java.awt.event.ActionEvent;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 28/05/2014
 */
public class InlineAnnotationRenderingAction extends ProtegeOWLRadioButtonAction {

    @Override
    protected void update() {
        setSelected(OWLRendererPreferences.getInstance().isDisplayAnnotationAnnotationsInline());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OWLRendererPreferences.getInstance().setDisplayAnnotationAnnotationsInline(isSelected());
    }

    @Override
    public void initialise() throws Exception {
        update();
    }

    @Override
    public void dispose() throws Exception {

    }
}
