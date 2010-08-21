package org.protege.editor.owl.ui.explanation;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.owl.model.inference.ReasonerPreferences;
import org.semanticweb.owlapi.model.OWLAxiom;

public class ExplanationDialog extends JDialog {
	private static final long serialVersionUID = -4476549896762790748L;
	public static final String PREFERENCES_SET_KEY = "EXPLANATION_PREFS_SET";
    public static final String DEFAULT_EXPLANATION_ID = "PREFERRED_PLUGIN_ID";
    
	private JPanel explanationContainer;
	private ExplanationResult explanation;
	private OWLAxiom axiom;
	
	public ExplanationDialog(Frame owner, ExplanationManager explanationManager, OWLAxiom axiom) {
		super(owner, "Explanation for " + explanationManager.getModelManager().getRendering(axiom));
		setModalityType(ModalityType.MODELESS);
		
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
		explanationContainer = new JPanel();
		explanationContainer.setLayout(new BoxLayout(explanationContainer, BoxLayout.Y_AXIS));
		explanationContainer.add(explanation);
		add(explanationContainer, BorderLayout.CENTER);

		add(createButtons(), BorderLayout.SOUTH);
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
				explanationContainer.removeAll();
				if (explanation != null) {
					explanation.dispose();
				}
				explanation = t.explain(axiom);
				explanationContainer.add(explanation);
			}
		});
		selector.setSelectedItem(selected);
		explanation = selected.explain(axiom);
		return selector;
	}
	
	
	private JComponent createButtons() {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout());
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExplanationDialog.this.setVisible(false);
			}
		});
		ok.setAlignmentX(CENTER_ALIGNMENT);
		panel.add(ok);
		return panel;
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
	
	@Override
	public void dispose() {
		if (explanation != null) {
			explanation.dispose();
		}
		super.dispose();
	}
}
