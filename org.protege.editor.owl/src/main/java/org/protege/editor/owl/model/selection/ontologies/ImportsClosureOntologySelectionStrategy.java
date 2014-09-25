package org.protege.editor.owl.model.selection.ontologies;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 6, 2008<br><br>
 */
public class ImportsClosureOntologySelectionStrategy implements OntologySelectionStrategy {

    private OWLModelManager mngr;


    public ImportsClosureOntologySelectionStrategy(OWLModelManager mngr){
        this.mngr = mngr;
    }


    public Set<OWLOntology> getOntologies() {
        return mngr.getOWLOntologyManager().getImportsClosure(mngr.getActiveOntology());
    }


    public String getName() {
        return "Show the imports closure of the active ontology";
    }
}
