package org.protege.editor.owl.model.selection.ontologies;

import org.protege.editor.owl.model.OntologyVisibilityManager;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 6, 2008<br><br>
 */
public class VisibilityManagerSelectionStrategy implements OntologySelectionStrategy {

    OntologyVisibilityManager vm;

    public VisibilityManagerSelectionStrategy(OntologyVisibilityManager vm){
        this.vm = vm;
    }


    public Set<OWLOntology> getOntologies() {
        return vm.getVisibleOntologies();
    }


    public String getName() {
        return "Show user selected ontologies...";
    }
}
