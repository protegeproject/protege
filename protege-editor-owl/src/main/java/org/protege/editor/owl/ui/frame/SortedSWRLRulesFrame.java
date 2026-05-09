package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;

/**
 * 
 * 
 * @author wvw
 *
 */

public class SortedSWRLRulesFrame extends SWRLRulesFrame {

	// when passing editorKit as a whole to super constructor,
	// sorting doesn't work
	public SortedSWRLRulesFrame(OWLEditorKit editorKit) {
		super(editorKit.getModelManager().getOWLOntologyManager());
		addSection(new SortedSWRLRulesFrameSection(editorKit, this));
	}
}
