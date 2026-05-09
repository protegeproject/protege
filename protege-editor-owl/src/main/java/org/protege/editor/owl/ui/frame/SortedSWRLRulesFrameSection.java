package org.protege.editor.owl.ui.frame;

import java.util.Comparator;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.swrl.SWRLRulePreferences;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.SWRLRule;

/**
 * 
 * 
 * @author wvw
 *
 */

public class SortedSWRLRulesFrameSection extends SWRLRulesFrameSection {

	public SortedSWRLRulesFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLOntology> owlFrame) {
		super(editorKit, owlFrame);
	}

	protected SortedSWRLRulesFrameSection(OWLEditorKit editorKit, String label, String rowLabel,
			OWLFrame<? extends OWLOntology> frame) {

		super(editorKit, label, rowLabel, frame);
	}

	@Override
	public Comparator<OWLFrameSectionRow<OWLOntology, SWRLRule, SWRLRule>> getRowComparator() {
		SWRLRulePreferences prefs = SWRLRulePreferences.getInstance();

		if (!prefs.getSortRulesOnAnnPrp())
			return null;

		return new Comparator<OWLFrameSectionRow<OWLOntology, SWRLRule, SWRLRule>>() {

			@Override
			public int compare(OWLFrameSectionRow<OWLOntology, SWRLRule, SWRLRule> o1,
					OWLFrameSectionRow<OWLOntology, SWRLRule, SWRLRule> o2) {

				return cmp(o1.getAxiom(), o2.getAxiom());
			}
		};
	}

	private int cmp(SWRLRule rule1, SWRLRule rule2) {
		OWLAnnotation ann1 = SWRLRuleUtils.findAnnotation(rule1.getAnnotations());
		OWLAnnotation ann2 = SWRLRuleUtils.findAnnotation(rule2.getAnnotations());

		// if both comments are null, rule1 == rule2
		// if comment1 is null, rule1 > rule2 (i.e., put rule1 at the back)
		// if comment2 is null, rule2 > rule1 (i.e., put rule2 at the back)
		if (ann1 == null) {
			if (ann2 == null)
				return 0;
			else
				return 1;

		} else {
			if (ann2 == null)
				return -1;
		}

		String value1 = ann1.getValue().asLiteral().get().getLiteral();
		String value2 = ann2.getValue().asLiteral().get().getLiteral();

		return doCmp(value1, value2);
	}

	// hook for subclasses
	protected int doCmp(String value1, String value2) {
		return value1.compareTo(value2);
	}
}
