package org.protege.editor.owl.ui.clsdescriptioneditor;

import java.util.List;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.SWRLRule;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 23, 2008<br><br>
 */
public class OWLExpressionCheckerFactoryImpl implements OWLExpressionCheckerFactory {

    private OWLModelManager mngr;


    public OWLExpressionCheckerFactoryImpl(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public OWLExpressionChecker<OWLDescription> getOWLDescriptionChecker() {
        return new OWLDescriptionChecker(mngr);
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


    public OWLExpressionChecker<Set<OWLDescription>> getOWLDescriptionSetChecker() {
        return new OWLDescriptionSetChecker(mngr);
    }

}
