package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.selection.ontologies.ActiveOntologySelectionStrategy;
import org.protege.editor.owl.model.selection.ontologies.OntologySelectionStrategy;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 6, 2008<br><br>
 */
public class ActiveOntologyStrategyAction extends AbstractOntologySelectionStrategyAction {

    private OntologySelectionStrategy strategy;

    protected OntologySelectionStrategy getStrategy() {
        if (strategy == null){
            strategy = new ActiveOntologySelectionStrategy(getOWLModelManager());
        }
        return strategy;
    }
}
