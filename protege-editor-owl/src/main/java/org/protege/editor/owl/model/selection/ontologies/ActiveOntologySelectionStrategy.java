package org.protege.editor.owl.model.selection.ontologies;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Collections;
import java.util.Set;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 6, 2008<br><br>
 */
public class ActiveOntologySelectionStrategy implements OntologySelectionStrategy {

    private OWLModelManager mngr;


    public ActiveOntologySelectionStrategy(OWLModelManager mngr){
        this.mngr = mngr;
    }


    public Set<OWLOntology> getOntologies() {
        return Collections.singleton(mngr.getActiveOntology());
    }


    public String getName() {
        return "Show only the active ontology";
    }
}
