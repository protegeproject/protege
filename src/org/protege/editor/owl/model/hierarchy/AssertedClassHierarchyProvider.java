package org.protege.editor.owl.model.hierarchy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomChange;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLObjectIntersectionOf;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLOntologyChangeListener;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.util.OWLAxiomVisitorAdapter;
import org.semanticweb.owl.util.OWLDescriptionVisitorAdapter;
import org.semanticweb.owl.util.SimpleRootClassChecker;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 17-Jan-2007<br><br>
 */
public class AssertedClassHierarchyProvider extends AbstractOWLObjectHierarchyProvider<OWLClass> {

    private static final Logger logger = Logger.getLogger(AssertedClassHierarchyProvider.class);

    private OWLOntologyManager owlOntologyManager;

    private Set<OWLOntology> ontologies;

    private OWLClass root;

    private ParentClassExtractor parentClassExtractor;

    private ChildClassExtractor childClassExtractor;

    private OWLOntologyChangeListener listener;

    private Set<OWLClass> implicitRoots;

    private boolean rebuild;


    public AssertedClassHierarchyProvider(OWLOntologyManager owlOntologyManager) {
        super(owlOntologyManager);
        this.owlOntologyManager = owlOntologyManager;
        ontologies = new HashSet<OWLOntology>();
        implicitRoots = new HashSet<OWLClass>();

        parentClassExtractor = new ParentClassExtractor();
        childClassExtractor = new ChildClassExtractor();
        listener = new OWLOntologyChangeListener() {
            public void ontologiesChanged(List<? extends OWLOntologyChange> changes) {
                handleChanges(changes);
            }
        };
        getManager().addOntologyChangeListener(listener);
    }


    /**
     * Sets the ontologies that this hierarchy provider should use
     * in order to determine the hierarchy.
     */
    public void setOntologies(Set<OWLOntology> ontologies) {
        this.ontologies = new HashSet<OWLOntology>(ontologies);
        if (root == null) {
            root = owlOntologyManager.getOWLDataFactory().getOWLThing();
        }
        rebuildImplicitRoots();
        fireHierarchyChanged();
    }


    private void rebuildImplicitRoots() {
        checker = new SimpleRootClassChecker(ontologies);
        implicitRoots.clear();
        Set<OWLClass> checkedClasses = new HashSet<OWLClass>();
        for (OWLOntology ont : ontologies) {
            int counter = 0;
            Set<OWLClass> ref = ont.getReferencedClasses();
            int total = ref.size();
            for (OWLClass cls : ref) {
                counter++;
                if (counter % 100 == 0) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Done " + counter + " (" + (counter * 100 / total) + "%)");
                    }
                }
                if (checkedClasses.contains(cls)) {
                    continue;
                }
                if (isImplicitSubClassOfThing(cls)) {
                    implicitRoots.add(cls);
                }
                checkedClasses.add(cls);
            }
        }
    }


    private SimpleRootClassChecker checker;


    private boolean isImplicitSubClassOfThing(OWLClass cls) {
        boolean isRoot = checker.isRootClass(cls);
        if (!isRoot) {
            Set<OWLClass> ancestors = getAncestors(cls);
            if (ancestors.contains(cls)) {
                for (OWLClass anc : ancestors) {
                    if (getAncestors(anc).contains(cls)) {
                        implicitRoots.add(anc);
                    }
                }
                return true;
            }
        }
        return isRoot;
    }


    public void dispose() {
        getManager().removeOntologyChangeListener(listener);
    }


    private void handleChanges(List<? extends OWLOntologyChange> changes) {
        Set<OWLClass> changedClasses = new HashSet<OWLClass>();
        for (OWLOntologyChange change : changes) {
            if (change.isAxiomChange()) {
                if (change.getAxiom() instanceof OWLSubClassAxiom || change.getAxiom() instanceof OWLEquivalentClassesAxiom || change.getAxiom() instanceof OWLDeclarationAxiom)
                {
                    for (OWLEntity entity : ((OWLAxiomChange) change).getEntities()) {
                        if (entity instanceof OWLClass) {
                            changedClasses.add((OWLClass) entity);
                            if (updateImplicitRoots((OWLClass) entity)) {
                                changedClasses.add(root);
                            }
                        }
                    }
                }
            }
        }
        for (OWLClass cls : changedClasses) {
            fireNodeChanged(cls);
            Set<OWLClass> anc = getAncestors(cls);
            if (anc.contains(cls)) {
                for (OWLClass an : anc) {
                    fireNodeChanged(an);
                }
            }
        }
        if (!changedClasses.contains(root)) {
            for (OWLClass cls : changedClasses) {
                if (implicitRoots.contains(cls)) {
                    fireNodeChanged(root);
                }
            }
        }
    }


    private boolean updateImplicitRoots(OWLClass cls) {
        if (!containsReference(cls)) {
            return implicitRoots.remove(cls);
        }
        if (isImplicitSubClassOfThing(cls)) {
            return implicitRoots.add(cls);
        }
        else {
            return implicitRoots.remove(cls);
        }
    }


    public Set<OWLClass> getRoots() {
        if (root == null) {
            root = owlOntologyManager.getOWLDataFactory().getOWLThing();
        }
        return Collections.singleton(root);
    }


    public Set<OWLClass> getChildren(OWLClass object) {
        Set<OWLClass> result;
        if (object.equals(root)) {
            result = new HashSet<OWLClass>();
            result.addAll(implicitRoots);
            result.addAll(extractChildren(object));
            result.remove(object);
        }
        else {
            result = extractChildren(object);
            for (Iterator<OWLClass> it = result.iterator(); it.hasNext();) {
                OWLClass curChild = it.next();
                if (getAncestors(curChild).contains(curChild)) {
                    it.remove();
                }
            }
        }

        return result;
    }


    private Set<OWLClass> extractChildren(OWLClass parent) {
        childClassExtractor.setCurrentParentClass(parent);
        Set<OWLClass> result = new HashSet<OWLClass>(childClassExtractor.getResult());
        for (OWLOntology ont : ontologies) {
            for (OWLAxiom ax : ont.getReferencingAxioms(parent)) {
                childClassExtractor.reset();
                if (ax.isLogicalAxiom()) {
                    ax.accept(childClassExtractor);
                    result.addAll(childClassExtractor.getResult());
                }
            }
        }
        return result;
    }


    public boolean containsReference(OWLClass object) {
        for (OWLOntology ont : ontologies) {
            if (ont.containsClassReference(object.getURI())) {
                return true;
            }
        }
        return false;
    }


    public Set<OWLClass> getParents(OWLClass object) {
        // If the object is thing then there are no
        // parents
        if (object.equals(root)) {
            return Collections.emptySet();
        }
        Set<OWLClass> result = new HashSet<OWLClass>();
        // Thing if the object is a root class
        if (implicitRoots.contains(object)) {
            result.add(root);
        }
        // Not a root, so must have another parent
        parentClassExtractor.reset();
        parentClassExtractor.setCurrentClass(object);
        for (OWLOntology ont : ontologies) {
            for (OWLAxiom ax : ont.getAxioms(object)) {
                ax.accept(parentClassExtractor);
            }
        }
        result.addAll(parentClassExtractor.getResult());
        return result;
    }


    public Set<OWLClass> getEquivalents(OWLClass object) {
        Set<OWLClass> result = new HashSet<OWLClass>();
        for (OWLOntology ont : ontologies) {
            for (OWLDescription equiv : object.getEquivalentClasses(ont)) {
                if (!equiv.isAnonymous()) {
                    result.add((OWLClass) equiv);
                }
            }
        }
        Set<OWLClass> ancestors = getAncestors(object);
        if (ancestors.contains(object)) {
            for (OWLClass cls : ancestors) {
                if (getAncestors(cls).contains(object)) {
                    result.add(cls);
                }
            }
            result.remove(object);
            result.remove(root);
        }
        return result;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////


    private Set<OWLClass> extractParents(OWLClass cls) {
        parentClassExtractor.reset();
        for (OWLOntology ont : ontologies) {
            for (OWLAxiom ax : ont.getAxioms(cls)) {
                ax.accept(parentClassExtractor);
            }
        }
        return new HashSet<OWLClass>(parentClassExtractor.getResult());
    }


    private class ParentClassExtractor extends OWLAxiomVisitorAdapter {

        private NamedClassExtractor extractor = new NamedClassExtractor();

        private OWLClass current;


        public void setCurrentClass(OWLClass current) {
            this.current = current;
        }


        public void reset() {
            extractor.reset();
        }


        public Set<OWLClass> getResult() {
            return extractor.getResult();
        }


        public void visit(OWLSubClassAxiom axiom) {
            axiom.getSuperClass().accept(extractor);
        }


        public void visit(OWLEquivalentClassesAxiom axiom) {
            for (OWLDescription desc : axiom.getDescriptions()) {
                if (desc.equals(current)) {
                    continue;
                }
                desc.accept(extractor);
            }
        }
    }


    private class NamedClassExtractor extends OWLDescriptionVisitorAdapter {

        Set<OWLClass> result = new HashSet<OWLClass>();


        public void reset() {
            result.clear();
        }


        public Set<OWLClass> getResult() {
            return result;
        }


        public void visit(OWLClass desc) {
            result.add(desc);
        }


        public void visit(OWLObjectIntersectionOf desc) {
            for (OWLDescription op : desc.getOperands()) {
                op.accept(this);
            }
        }
    }


    /**
     * Checks whether a class description contains a specified named conjunct.
     */
    private class NamedConjunctChecker extends OWLDescriptionVisitorAdapter {

        private boolean found;

        private OWLClass searchClass;


        public boolean containsConjunct(OWLClass conjunct, OWLDescription description) {
            found = false;
            searchClass = conjunct;
            description.accept(this);
            return found;
        }

        //////////////////////////////////////////////////////////////////////////////////////////


        public void visit(OWLClass desc) {
            if (desc.equals(searchClass)) {
                found = true;
            }
        }


        public void visit(OWLObjectIntersectionOf desc) {
            for (OWLDescription op : desc.getOperands()) {
                op.accept(this);
                if (found) {
                    break;
                }
            }
        }

        //////////////////////////////////////////////////////////////////////////////////////////
    }


    private class ChildClassExtractor extends OWLAxiomVisitorAdapter {


        private NamedConjunctChecker checker = new NamedConjunctChecker();

        private NamedClassExtractor namedClassExtractor = new NamedClassExtractor();

        private OWLClass currentParentClass;

        private Set<OWLClass> results = new HashSet<OWLClass>();


        public void reset() {
            results.clear();
            namedClassExtractor.reset();
        }


        public void setCurrentParentClass(OWLClass currentParentClass) {
            this.currentParentClass = currentParentClass;
//            checker.setNamedConjunct(currentParentClass);
            reset();
        }


        public Set<OWLClass> getResult() {
            return results;
        }


        public void visit(OWLSubClassAxiom axiom) {
            // Example:
            // If searching for subs of B, candidates are:
            // SubClassOf(A B)
            // SubClassOf(A And(B ...))
            if (checker.containsConjunct(currentParentClass, axiom.getSuperClass())) {
                // We only want named classes
                if (!axiom.getSubClass().isAnonymous()) {
                    results.add((OWLClass) axiom.getSubClass());
                }
            }
        }


        public void visit(OWLEquivalentClassesAxiom axiom) {
            // EquivalentClasses(A  And(B...))
            Set<OWLDescription> equivalentClasses = axiom.getDescriptions();
            Set<OWLDescription> candidateDescriptions = new HashSet<OWLDescription>();
            boolean found = false;
            for (OWLDescription equivalentClass : equivalentClasses) {
                if (!checker.containsConjunct(currentParentClass, equivalentClass)) {
                    // Potential operand
                    candidateDescriptions.add(equivalentClass);
                }
                else {
                    // This axiom is relevant
                    if (equivalentClass.isAnonymous()) {
                        found = true;
                    }
                }
            }
            if (!found) {
                return;
            }
            namedClassExtractor.reset();
            for (OWLDescription desc : candidateDescriptions) {
                desc.accept(namedClassExtractor);
            }
            results.addAll(namedClassExtractor.getResult());
        }
    }
}
