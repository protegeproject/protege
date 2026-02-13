package org.protege.editor.owl.ui.preferences;

import java.awt.BorderLayout;

import javax.swing.JCheckBox;

import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.owl.model.swrl.SWRLRulePreferences;
import org.protege.editor.owl.ui.view.SWRLRulesViewComponent;

/**
 * 
 * 
 * @author wvw
 *
 */

public class SWRLRulePreferencesPanel extends OWLPreferencesPanel {

	private static final long serialVersionUID = 1L;

	private JCheckBox sortRulesOnAnnPrpCheckBox;
	private JCheckBox showAnnPrpWithRuleCheckBox;

	@Override
	public void initialise() throws Exception {
		setLayout(new BorderLayout());

		PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
		add(panel, BorderLayout.NORTH);
		panel.addGroup("Display");

		SWRLRulePreferences prefs = SWRLRulePreferences.getInstance();

		sortRulesOnAnnPrpCheckBox = new JCheckBox("Sort rules based on rdfs:comment", prefs.getSortRulesOnAnnPrp());
		panel.addGroupComponent(sortRulesOnAnnPrpCheckBox);

		showAnnPrpWithRuleCheckBox = new JCheckBox("Show rdfs:comment with rule", prefs.getShowAnnPrpWithRule());
		panel.addGroupComponent(showAnnPrpWithRuleCheckBox);
	}

	@Override
	public void dispose() throws Exception {
	}

	@Override
	public void applyChanges() {
		SWRLRulePreferences prefs = SWRLRulePreferences.getInstance();

		prefs.setSortRulesOnAnnPrp(sortRulesOnAnnPrpCheckBox.isSelected());
		prefs.setShowAnnPrpWithRule(showAnnPrpWithRuleCheckBox.isSelected());

		// TODO has no effect on sorting of already shown rule list
		// getOWLEditorKit().getOWLWorkspace().refreshComponents();
		SWRLRulesViewComponent.preferencesUpdated();
	}
}
