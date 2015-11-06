package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.hierarchy.OWLObjectHierarchyProvider;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 30-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class MakePrimitiveSiblingsDisjoint extends SelectedOWLClassAction {

    protected void initialiseAction() throws Exception {

    }


    public void actionPerformed(ActionEvent e) {
        OWLClass selCls = getOWLClass();
        if (selCls == null) {
            return;
        }
        // TODO: Extract this and make less dependent on hierarchy provider
        OWLObjectHierarchyProvider<OWLClass> provider = getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider();
        Set<OWLClass> clses = new HashSet<OWLClass>();
        for (OWLClass par : provider.getParents(selCls)) {
            clses.addAll(provider.getChildren(par));
        }
        for(Iterator<OWLClass> it = clses.iterator(); it.hasNext(); ) {
            OWLClass cls = it.next();
            for(OWLOntology ont : getOWLModelManager().getActiveOntologies()) {
                if(EntitySearcher.isDefined(cls, ont)) {
                    it.remove();
                    break;
                }
            }
        }

        if (clses.size() > 1){
            OWLAxiom ax = getOWLDataFactory().getOWLDisjointClassesAxiom(clses);
            getOWLModelManager().applyChange(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
        }
        // 2) Get the named subs

//        try {
//            OWLOntology owlOntology = getOWLModelManager().getActiveOntology();
//            DisjointAxiomCreator creator = new DisjointAxiomCreator(getOWLModelManager().getOWLClassHierarchyProvider(),
//                                                                    owlOntology,
//                                                                    getOWLModelManager().getActiveOntologies());
//
//            getOWLModelManager().applyChanges(creator.getChanges());
//        } catch (OWLException ex) {
//            logger.error(ex);
//        }
    }
}
