package org.protege.editor.owl.ui.explanation;

import org.semanticweb.owlapi.model.OWLAxiom;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class ExplanationDialog extends JPanel {

    private JPanel explanationContainer;

    private ExplanationResult explanation;

    private OWLAxiom axiom;

    public ExplanationDialog(ExplanationManager explanationManager, OWLAxiom axiom) {
        this.axiom = axiom;

        setLayout(new BorderLayout());
        Collection<ExplanationService> teachers = explanationManager.getTeachers(axiom);
        if (teachers.size() == 1) {
            explanation = teachers.iterator().next().explain(axiom);
        }
        else {
            JComboBox<ExplanationService> selector = createComboBox(teachers);
            add(selector, BorderLayout.NORTH);
        }
        explanationContainer = new JPanel();
        explanationContainer.setLayout(new BoxLayout(explanationContainer, BoxLayout.Y_AXIS));
        if (explanation != null) {
            explanationContainer.add(explanation);
        }
        add(explanationContainer, BorderLayout.CENTER);
    }

    private JComboBox<ExplanationService> createComboBox(Collection<ExplanationService> explanationServices) {
        ExplanationService[] teacherArray = explanationServices.toArray(new ExplanationService[explanationServices.size()]);
        final JComboBox<ExplanationService> selector = new JComboBox<>(teacherArray);
		final ExplanationPreferences prefs = ExplanationPreferences.create().load();
        if (teacherArray.length > 0) {
            ExplanationService selected = teacherArray[0];
			if (prefs.useLastExplanationService) {
				String id = prefs.defaultExplanationService;
				if (id != null) {
					for (ExplanationService t : explanationServices) {
						if (id.equals(t.getPluginId())) {
							selected = t;
						}
					}
				}
			}
            selector.setSelectedItem(selected);
            explanation = selected.explain(axiom);
        }
        selector.addActionListener(e -> {
            ExplanationService t = (ExplanationService) selector.getSelectedItem();
			prefs.load();
			prefs.defaultExplanationService = t.getPluginId();
			prefs.save();
            explanationContainer.removeAll();
            if (explanation != null) {
                explanation.dispose();
            }
            explanation = t.explain(axiom);
            explanationContainer.add(explanation);
            revalidate();
        });
        return selector;
    }

    public void dispose() {
        if (explanation != null) {
            explanation.dispose();
        }
    }
}
