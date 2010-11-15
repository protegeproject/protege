package org.protege.editor.owl.ui.prefix;

import java.awt.event.ActionEvent;

import org.protege.editor.owl.ui.action.ProtegeOWLAction;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 22-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ShowPrefixMappingsAction extends ProtegeOWLAction {
	private static final long serialVersionUID = 3527061473023719657L;


	public void actionPerformed(ActionEvent e) {
        PrefixMappingPanel.showDialog(getOWLEditorKit());
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
