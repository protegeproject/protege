package org.protege.editor.owl.ui.frame;

import java.util.Iterator;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.swrl.SWRLRulePreferences;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.SWRLRule;

/*
 * 
 * 
 * @author wvw
 * 
 */

public class AnnotatedSWRLRuleFrameSectionRow extends SWRLRuleFrameSectionRow {

	public AnnotatedSWRLRuleFrameSectionRow(OWLEditorKit owlEditorKit,
			OWLFrameSection<OWLOntology, SWRLRule, SWRLRule> section, OWLOntology ontology, OWLOntology rootObject,
			SWRLRule axiom) {

		super(owlEditorKit, section, ontology, rootObject, axiom);
	}

	@Override
	public String getRendering() {
		SWRLRulePreferences prefs = SWRLRulePreferences.getInstance();
		if (!prefs.getShowAnnPrpWithRule())
			return super.getRendering();
		
		StringBuilder sb = new StringBuilder();

		OWLAnnotation ann = SWRLRuleUtils.findAnnotation(axiom.getAnnotations());
		if (ann != null) {
			String value = ann.getValue().asLiteral().get().getLiteral();

			sb.append("<demph>").append(value).append("</demph> = ");
		}

		for (Iterator<? extends OWLObject> it = getManipulatableObjects().iterator(); it.hasNext();) {
			OWLObject obj = it.next();
			sb.append(getObjectRendering(obj));
			if (it.hasNext()) {
				sb.append(getDelimeter());
			}
		}
		sb.append(getSuffix());

		return sb.toString();
	}

	// TODO
	@Override
	public String getTooltip() {
		return null;
	}
}
