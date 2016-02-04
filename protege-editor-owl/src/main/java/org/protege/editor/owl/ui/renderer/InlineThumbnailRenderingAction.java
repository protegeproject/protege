package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.ui.action.ProtegeOWLRadioButtonAction;

import java.awt.event.ActionEvent;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/02/16
 */
public class InlineThumbnailRenderingAction extends ProtegeOWLRadioButtonAction {


    @Override
    protected void update() {
        setSelected(OWLRendererPreferences.getInstance().isDisplayThumbnailsInline());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        OWLRendererPreferences.getInstance().setDisplayThumbnailsInline(isSelected());
    }

    @Override
    public void initialise() throws Exception {
        update();
    }

    @Override
    public void dispose() throws Exception {

    }
}
