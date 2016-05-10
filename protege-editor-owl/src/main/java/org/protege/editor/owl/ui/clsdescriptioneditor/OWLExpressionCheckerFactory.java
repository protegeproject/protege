package org.protege.editor.owl.ui.clsdescriptioneditor;

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
public interface OWLExpressionCheckerFactory {

    OWLExpressionChecker<OWLClassExpression> getOWLClassExpressionChecker();

    OWLExpressionChecker<Set<OWLClassExpression>> getOWLClassExpressionSetChecker();

    OWLExpressionChecker<OWLClassAxiom> getClassAxiomChecker();

    OWLExpressionChecker<List<OWLObjectPropertyExpression>> getPropertyChainChecker();

    OWLExpressionChecker<SWRLRule> getSWRLChecker();

    OWLExpressionChecker<Set<OWLPropertyExpression>> getPropertySetChecker();
    
    OWLExpressionChecker<Set<OWLObjectPropertyExpression>> getObjectPropertySetChecker();
}
