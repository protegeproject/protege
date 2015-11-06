package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.SWRLRuleEditor;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.SWRLRule;

import java.util.Comparator;


/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 06-Jul-2007<br><br>
 */
public class SWRLRulesFrameSection extends AbstractOWLFrameSection<OWLOntology, SWRLRule, SWRLRule> {


    public SWRLRulesFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLOntology> owlFrame) {
        super(editorKit, "Rules", "Rule", owlFrame);
    }


    protected SWRLRule createAxiom(SWRLRule object) {
        return object;
    }


    public OWLObjectEditor<SWRLRule> getObjectEditor() {
        return new SWRLRuleEditor(getOWLEditorKit());
    }


    public boolean canAdd() {
        return true;
    }


    protected void refill(OWLOntology ontology) {
        for (SWRLRule rule : ontology.getAxioms(AxiomType.SWRL_RULE)) {
            addRow(new SWRLRuleFrameSectionRow(getOWLEditorKit(), this, ontology, ontology, rule));
        }
    }


    protected void clear() {
    }


    public Comparator<OWLFrameSectionRow<OWLOntology, SWRLRule, SWRLRule>> getRowComparator() {
        return null;
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	if (!change.isAxiomChange()) {
    		return false;
    	}
    	return change.getAxiom() instanceof SWRLRule;
    }

}
