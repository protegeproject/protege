package org.protege.editor.owl.ui.usage;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.*;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 21-Feb-2007<br><br>
 */
public class UsageByEntityTreeModel extends DefaultTreeModel implements UsageTreeModel {

    /**
     * 
     */
    private static final long serialVersionUID = -2530774548488512609L;

    private OWLModelManager owlModelManager;

    private DefaultMutableTreeNode rootNode;

    private AxiomSorter axiomSorter;

    private OWLOntology currentOntology;

    private Map<OWLEntity, DefaultMutableTreeNode> nodeMap;

    private OWLEntity entity;

    private Map<OWLEntity, Set<OWLAxiom>> axiomsByEntityMap;

    // axioms that cannot be indexed by entity
    private Set<OWLAxiom> additionalAxioms = new HashSet<OWLAxiom>();

    private int usageCount;

    private Set<UsageFilter> filters = new HashSet<UsageFilter>();


    public UsageByEntityTreeModel(OWLEditorKit owlEditorKit) {
        super(new DefaultMutableTreeNode("No usage"));
        owlModelManager = owlEditorKit.getModelManager();
        axiomSorter = new AxiomSorter();
        nodeMap = new HashMap<OWLEntity, DefaultMutableTreeNode>();
        axiomsByEntityMap = new TreeMap<OWLEntity, Set<OWLAxiom>>(owlModelManager.getOWLObjectComparator());
    }

    public UsageByEntityTreeModel(OWLEditorKit owlEditorKit, OWLEntity entity) {
        this(owlEditorKit);
        setOWLEntity(entity);
    }

    private String getRootContent(OWLModelManager mngr, OWLEntity entity){
        return entity != null ? "Found " + usageCount + " uses of " + mngr.getRendering(entity) : "";
    }

    public void setOWLEntity(OWLEntity owlEntity) {
        this.entity = owlEntity;
        axiomsByEntityMap.clear();
        usageCount = 0;

        for (OWLOntology ont : owlModelManager.getActiveOntologies()) {
            currentOntology = ont;
            Set<OWLAxiom> axioms = ont.getReferencingAxioms(owlEntity);
            for (OWLAxiom ax : axioms) {
                axiomSorter.setAxiom(ax);
                ax.accept(axiomSorter);
            }
        }

        rootNode = new DefaultMutableTreeNode(getRootContent(owlModelManager, entity));
        setRoot(rootNode);

        for (OWLEntity ent : axiomsByEntityMap.keySet()) {
            for (OWLAxiom ax : axiomsByEntityMap.get(ent)) {
                getNode(ent).add(new UsageTreeNode(null, ax));
            }
        }
        if (!additionalAxioms.isEmpty()){
            DefaultMutableTreeNode otherNode = new DefaultMutableTreeNode("Other");
            rootNode.add(otherNode);
            for (OWLAxiom ax : additionalAxioms){
                otherNode.add(new DefaultMutableTreeNode(ax));
            }
        }
    }


    public void addFilter(UsageFilter filter) {
        filters.add(filter);
    }


    public void addFilters(Set<UsageFilter> filters) {
        this.filters.addAll(filters);
    }


    public void removeFilter(UsageFilter filter) {
        filters.remove(filter);
    }


    private boolean isFilterSet(UsageFilter filter){
        return filters.contains(filter);
    }


    private DefaultMutableTreeNode getNode(OWLEntity entity) {
        DefaultMutableTreeNode node = nodeMap.get(entity);
        if (node == null) {
            node = new DefaultMutableTreeNode(entity);
            nodeMap.put(entity, node);
            rootNode.add(node);
        }
        return node;
    }


    public void refresh(){
        setOWLEntity(entity);
    }


    private class AxiomSorter implements OWLAxiomVisitor, OWLEntityVisitor, OWLPropertyExpressionVisitor {

        private OWLAxiom currentAxiom;


        public void setAxiom(OWLAxiom axiom) {
            currentAxiom = axiom;
        }


        private void add(OWLEntity ent) {

            if (isFilterSet(UsageFilter.filterSelf) && entity.equals(ent)) {
                return;
            }
            usageCount++;
            Set<OWLAxiom> axioms = axiomsByEntityMap.get(ent);
            if (axioms == null) {
                axioms = new HashSet<OWLAxiom>();
                axiomsByEntityMap.put(ent, axioms);
            }
            axioms.add(currentAxiom);
        }


        public void visit(OWLClass cls) {
            add(cls);
        }


        public void visit(OWLDatatype dataType) {
            add(dataType);
        }


        public void visit(OWLNamedIndividual individual) {
            add(individual);
        }


        public void visit(OWLDataProperty property) {
            add(property);
        }


        public void visit(OWLObjectProperty property) {
            add(property);
        }


        public void visit(OWLAnnotationProperty property) {
            add(property);
        }


        public void visit(OWLObjectInverseOf property) {
            property.getInverse().accept(this);
        }


        public void visit(OWLAsymmetricObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLAnnotationAssertionAxiom axiom) {
            if (axiom.getSubject() instanceof IRI){
                IRI subjectIRI = (IRI)axiom.getSubject();
                for (OWLOntology ont : owlModelManager.getActiveOntologies()){
                    if (ont.containsClassInSignature(subjectIRI)){
                        add(owlModelManager.getOWLDataFactory().getOWLClass(subjectIRI));
                    }
                    if (ont.containsObjectPropertyInSignature(subjectIRI)){
                        add(owlModelManager.getOWLDataFactory().getOWLObjectProperty(subjectIRI));
                    }
                    if (ont.containsDataPropertyInSignature(subjectIRI)){
                        add(owlModelManager.getOWLDataFactory().getOWLDataProperty(subjectIRI));
                    }
                    if (ont.containsIndividualInSignature(subjectIRI)){
                        add(owlModelManager.getOWLDataFactory().getOWLNamedIndividual(subjectIRI));
                    }
                    if (ont.containsAnnotationPropertyInSignature(subjectIRI)){
                        add(owlModelManager.getOWLDataFactory().getOWLAnnotationProperty(subjectIRI));
                    }
                    if (ont.containsDatatypeInSignature(subjectIRI)){
                        add(owlModelManager.getOWLDataFactory().getOWLDatatype(subjectIRI));
                    }
                }
            }
        }


        public void visit(OWLSubAnnotationPropertyOfAxiom axiom) {
            axiom.getSubProperty().accept(this);
        }


        public void visit(OWLAnnotationPropertyDomainAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLAnnotationPropertyRangeAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLClassAssertionAxiom axiom) {
            if (!axiom.getIndividual().isAnonymous()){
                axiom.getIndividual().asOWLNamedIndividual().accept(this);
            }
        }


        public void visit(OWLDataPropertyAssertionAxiom axiom) {
            if (!axiom.getSubject().isAnonymous()){
                axiom.getSubject().asOWLNamedIndividual().accept(this);
            }
        }


        public void visit(OWLDataPropertyDomainAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLDataPropertyRangeAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLSubDataPropertyOfAxiom axiom) {
            axiom.getSubProperty().accept(this);
        }


        public void visit(OWLDeclarationAxiom axiom) {
            axiom.getEntity().accept(this);
        }


        public void visit(OWLDifferentIndividualsAxiom axiom) {
            for (OWLIndividual ind : axiom.getIndividuals()) {
                if (!ind.isAnonymous()){
                    ind.asOWLNamedIndividual().accept(this);
                }
            }
        }


        public void visit(OWLDisjointClassesAxiom axiom) {
            boolean hasBeenIndexed = false;
            if (!isFilterSet(UsageFilter.filterDisjoints)){
                for (OWLClassExpression desc : axiom.getClassExpressions()) {
                    if (!desc.isAnonymous()) {
                        desc.asOWLClass().accept(this);
                        hasBeenIndexed = true;
                    }
                }
            }
            if (!hasBeenIndexed){
                additionalAxioms.add(axiom);
                usageCount++;
            }            
        }


        public void visit(OWLDisjointDataPropertiesAxiom axiom) {
            if (!isFilterSet(UsageFilter.filterDisjoints)){
                for (OWLDataPropertyExpression prop : axiom.getProperties()) {
                    prop.accept(this);
                }
            }
        }


        public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
            if (!isFilterSet(UsageFilter.filterDisjoints)){
                for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                    prop.accept(this);
                }
            }
        }


        public void visit(OWLDisjointUnionAxiom axiom) {
            if (!isFilterSet(UsageFilter.filterDisjoints)){
                axiom.getOWLClass().accept(this);
            }
        }


        public void visit(OWLEquivalentClassesAxiom axiom) {
            boolean hasBeenIndexed = false;
            for (OWLClassExpression desc : axiom.getClassExpressions()) {
                if (!desc.isAnonymous()) {
                    desc.asOWLClass().accept(this);
                    hasBeenIndexed = true;
                }
            }
            if (!hasBeenIndexed){
                additionalAxioms.add(axiom);
                usageCount++;
            }
        }


        public void visit(OWLEquivalentDataPropertiesAxiom axiom) {
            for (OWLDataPropertyExpression prop : axiom.getProperties()) {
                prop.accept(this);
            }
        }


        public void visit(OWLEquivalentObjectPropertiesAxiom axiom) {
            for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                prop.accept(this);
            }
        }


        public void visit(OWLFunctionalDataPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLFunctionalObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLInverseObjectPropertiesAxiom axiom) {
            for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                prop.accept(this);
            }
        }


        public void visit(OWLHasKeyAxiom axiom) {
            //@@TODO implement
        }


        public void visit(OWLDatatypeDefinitionAxiom axiom) {
            axiom.getDatatype().accept(this);
        }


        public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
            if (!axiom.getSubject().isAnonymous()){
                axiom.getSubject().asOWLNamedIndividual().accept(this);
            }
        }


        public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
            if (!axiom.getSubject().isAnonymous()){
                axiom.getSubject().asOWLNamedIndividual().accept(this);
            }
        }


        public void visit(OWLObjectPropertyAssertionAxiom axiom) {
            if (!axiom.getSubject().isAnonymous()){
                axiom.getSubject().asOWLNamedIndividual().accept(this);
            }
        }


        public void visit(OWLSubPropertyChainOfAxiom axiom) {
            axiom.getSuperProperty().accept(this);
        }


        public void visit(OWLObjectPropertyDomainAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLObjectPropertyRangeAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLSubObjectPropertyOfAxiom axiom) {
            axiom.getSubProperty().accept(this);
        }


        public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLSameIndividualAxiom axiom) {
            for (OWLIndividual ind : axiom.getIndividuals()) {
                if (!ind.isAnonymous()){
                    ind.asOWLNamedIndividual().accept(this);
                }
            }
        }


        public void visit(OWLSubClassOfAxiom axiom) {
            if (!axiom.getSubClass().isAnonymous()) {
                if (!isFilterSet(UsageFilter.filterNamedSubsSupers) ||
                    (!axiom.getSubClass().equals(entity) && !axiom.getSuperClass().equals(entity))){
                    axiom.getSubClass().asOWLClass().accept(this);
                }
            }
            else{
                additionalAxioms.add(axiom);
                usageCount++;
            }
        }


        public void visit(OWLSymmetricObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLTransitiveObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(SWRLRule rule) {

        }
    }


    protected class UsageTreeNode extends DefaultMutableTreeNode {

        /**
         * 
         */
        private static final long serialVersionUID = -53617232488795863L;

        private OWLOntology ont;

        private OWLAxiom axiom;


        public UsageTreeNode(OWLOntology ont, OWLAxiom axiom) {
            super(axiom);
            this.ont = ont;
            this.axiom = axiom;
        }


        public OWLAxiom getAxiom() {
            return axiom;
        }


        public OWLOntology getOntology() {
            return ont;
        }
    }
}
