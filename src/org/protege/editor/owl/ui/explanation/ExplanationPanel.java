package org.protege.editor.owl.ui.explanation;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.semanticweb.owlapi.model.OWLAxiom;

public class ExplanationPanel extends JDialog {
	private static final long serialVersionUID = -4476549896762790748L;
	public static final String PREFERENCES_SET_KEY = "EXPLANATION_PREFS_SET";
    public static final String DEFAULT_EXPLANATION_ID = "PREFERRED_PLUGIN_ID";
    
	private JComponent explanation;
	private OWLAxiom axiom;
	
	public ExplanationPanel(Frame owner, ExplanationManager explanationManager, OWLAxiom axiom) {
		super(owner, "Explanation for " + explanationManager.getModelManager().getRendering(axiom));
		
		this.axiom = axiom;
		
		setLayout(new BorderLayout());
		Collection<ExplanationService> teachers = explanationManager.getTeachers(axiom);
		if (teachers.size() == 1) {
			explanation = teachers.iterator().next().explain(axiom);
		}
		else {
			JComboBox selector = createComboBox(teachers);
			add(selector, BorderLayout.NORTH);
		}
		add(explanation, BorderLayout.CENTER);
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExplanationPanel.this.setVisible(false);
			}
		});
		add(ok, BorderLayout.SOUTH);
		pack();
	}
	
	private JComboBox createComboBox(Collection<ExplanationService> teachers) {
		ExplanationService[] teacherArray = teachers.toArray(new ExplanationService[0]);
		final JComboBox selector = new JComboBox(teacherArray);
		ExplanationService selected = teacherArray[0];
		String id = getDefaultPluginId();
		if (id != null) {
			for (ExplanationService t : teachers) {
				if (id.equals(t.getPluginId())) {
					selected = t;
				}
			}
		}
		selector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExplanationService t = (ExplanationService) selector.getSelectedItem();
				setDefaultPluginId(t.getPluginId());
				explanation.removeAll();
				explanation.add(t.explain(axiom));
			}
		});
		selector.setSelectedItem(selected);
		explanation.add(selected.explain(axiom));
		return selector;
	}
	
	public String getDefaultPluginId() {
        PreferencesManager prefMan = PreferencesManager.getInstance();
        Preferences prefs = prefMan.getPreferencesForSet(PREFERENCES_SET_KEY, ReasonerPreferences.class);
        return prefs.getString(DEFAULT_EXPLANATION_ID, null);
	}
	
	public void setDefaultPluginId(String id) {
        PreferencesManager prefMan = PreferencesManager.getInstance();
        Preferences prefs = prefMan.getPreferencesForSet(PREFERENCES_SET_KEY, ReasonerPreferences.class);
        prefs.putString(DEFAULT_EXPLANATION_ID, id);
	}
}
