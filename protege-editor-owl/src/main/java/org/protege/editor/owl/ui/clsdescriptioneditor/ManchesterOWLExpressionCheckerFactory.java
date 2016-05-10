package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.*;

import java.util.List;
import java.util.Set;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 23, 2008<br><br>
 */
public class ManchesterOWLExpressionCheckerFactory implements OWLExpressionCheckerFactory {

    private OWLModelManager mngr;


    public ManchesterOWLExpressionCheckerFactory(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public OWLExpressionChecker<OWLClassExpression> getOWLClassExpressionChecker() {
        return new OWLClassExpressionChecker(mngr);
    }


    public OWLExpressionChecker<Set<OWLClassExpression>> getOWLClassExpressionSetChecker() {
        return new OWLClassExpressionSetChecker(mngr);
    }


    public OWLExpressionChecker<OWLClassAxiom> getClassAxiomChecker() {
        return new OWLClassAxiomChecker(mngr);
    }


    public OWLExpressionChecker<List<OWLObjectPropertyExpression>> getPropertyChainChecker() {
        return new OWLPropertyChainChecker(mngr);
    }


    public OWLExpressionChecker<SWRLRule> getSWRLChecker() {
        return new SWRLRuleChecker(mngr);
    }


    public OWLExpressionChecker<Set<OWLPropertyExpression>> getPropertySetChecker() {
        return new OWLPropertySetChecker(mngr);
    }
    
    public OWLExpressionChecker<Set<OWLObjectPropertyExpression>> getObjectPropertySetChecker() {
    	return new OWLObjectPropertySetChecker(mngr);
    }
}
