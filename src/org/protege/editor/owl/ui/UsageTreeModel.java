package org.protege.editor.owl.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLAntiSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLAxiomAnnotationAxiom;
import org.semanticweb.owl.model.OWLAxiomVisitor;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLDataProperty;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLDataPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLDataPropertyExpression;
import org.semanticweb.owl.model.OWLDataPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLDataSubPropertyAxiom;
import org.semanticweb.owl.model.OWLDataType;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLDescription;
import org.semanticweb.owl.model.OWLDifferentIndividualsAxiom;
import org.semanticweb.owl.model.OWLDisjointClassesAxiom;
import org.semanticweb.owl.model.OWLDisjointDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLDisjointUnionAxiom;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLEntityVisitor;
import org.semanticweb.owl.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owl.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owl.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLFunctionalDataPropertyAxiom;
import org.semanticweb.owl.model.OWLFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLImportsDeclaration;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLInverseFunctionalObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owl.model.OWLIrreflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyChainSubPropertyAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;
import org.semanticweb.owl.model.OWLObjectPropertyInverse;
import org.semanticweb.owl.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owl.model.OWLObjectSubPropertyAxiom;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;
import org.semanticweb.owl.model.OWLPropertyExpressionVisitor;
import org.semanticweb.owl.model.OWLReflexiveObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLSameIndividualsAxiom;
import org.semanticweb.owl.model.OWLSubClassAxiom;
import org.semanticweb.owl.model.OWLSymmetricObjectPropertyAxiom;
import org.semanticweb.owl.model.OWLTransitiveObjectPropertyAxiom;
import org.semanticweb.owl.model.SWRLRule;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 21-Feb-2007<br><br>
 */
public class UsageTreeModel extends DefaultTreeModel {

    private OWLModelManager owlModelManager;

    private DefaultMutableTreeNode rootNode;

    private AxiomSorter axiomSorter;

    private OWLOntology currentOntology;

    private Map<OWLEntity, DefaultMutableTreeNode> nodeMap;

    private OWLEntity entity;

    private Map<OWLEntity, Set<OWLAxiom>> axiomsByEntityMap;

    private int usageCount;


    public UsageTreeModel(OWLEditorKit owlEditorKit, OWLEntity entity) {
        super(new DefaultMutableTreeNode("Usage for: " + entity));
        this.owlModelManager = owlEditorKit.getOWLModelManager();
        this.entity = entity;
        rootNode = (DefaultMutableTreeNode) getRoot();
        axiomSorter = new AxiomSorter();
        nodeMap = new HashMap<OWLEntity, DefaultMutableTreeNode>();
        axiomsByEntityMap = new TreeMap<OWLEntity, Set<OWLAxiom>>(new OWLObjectComparator<OWLEntity>(owlModelManager));
        setOWLEntity(entity);
    }


    private void setOWLEntity(OWLEntity owlEntity) {
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
        for (OWLEntity ent : axiomsByEntityMap.keySet()) {
            for (OWLAxiom ax : axiomsByEntityMap.get(ent)) {
                getNode(ent).add(new UsageTreeNode(null, ax));
//                insertNodeInto(, getNode(ent), 0);
            }
        }
        ((DefaultMutableTreeNode) getRoot()).setUserObject("Usage for: " + entity + " (" + usageCount + " usages)");
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


    private class AxiomSorter implements OWLAxiomVisitor, OWLEntityVisitor, OWLPropertyExpressionVisitor {

        private OWLAxiom currentAxiom;


        public void setAxiom(OWLAxiom axiom) {
            currentAxiom = axiom;
        }


        private void add(OWLEntity ent) {

            if (entity.equals(ent)) {
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


        public void visit(OWLDataType dataType) {
        }


        public void visit(OWLIndividual individual) {
            add(individual);
        }


        public void visit(OWLDataProperty property) {
            add(property);
        }


        public void visit(OWLObjectProperty property) {
            add(property);
        }


        public void visit(OWLObjectPropertyInverse property) {
            property.getInverse().accept(this);
        }


        public void visit(OWLAntiSymmetricObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLAxiomAnnotationAxiom axiom) {
            axiom.getSubject().accept(this);
        }


        public void visit(OWLClassAssertionAxiom axiom) {
            axiom.getIndividual().accept(this);
        }


        public void visit(OWLDataPropertyAssertionAxiom axiom) {
            axiom.getSubject().accept(this);
        }


        public void visit(OWLDataPropertyDomainAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLDataPropertyRangeAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLDataSubPropertyAxiom axiom) {
            axiom.getSubProperty().accept(this);
        }


        public void visit(OWLDeclarationAxiom axiom) {
            axiom.getEntity().accept(this);
        }


        public void visit(OWLDifferentIndividualsAxiom axiom) {
            for (OWLIndividual ind : axiom.getIndividuals()) {
                ind.accept(this);
            }
        }


        public void visit(OWLDisjointClassesAxiom axiom) {
            for (OWLDescription desc : axiom.getDescriptions()) {
                if (!desc.isAnonymous()) {
                    ((OWLClass) desc).accept(this);
                }
            }
        }


        public void visit(OWLDisjointDataPropertiesAxiom axiom) {
            for (OWLDataPropertyExpression prop : axiom.getProperties()) {
                prop.accept(this);
            }
        }


        public void visit(OWLDisjointObjectPropertiesAxiom axiom) {
            for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                prop.accept(this);
            }
        }


        public void visit(OWLDisjointUnionAxiom axiom) {
            axiom.getOWLClass().accept(this);
        }


        public void visit(OWLEntityAnnotationAxiom axiom) {
            axiom.getSubject().accept(this);
        }


        public void visit(OWLEquivalentClassesAxiom axiom) {
            for (OWLDescription desc : axiom.getDescriptions()) {
                if (!desc.isAnonymous()) {
                    ((OWLClass) desc).accept(this);
                }
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


        public void visit(OWLImportsDeclaration axiom) {
        }


        public void visit(OWLInverseFunctionalObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLInverseObjectPropertiesAxiom axiom) {
            for (OWLObjectPropertyExpression prop : axiom.getProperties()) {
                prop.accept(this);
            }
        }


        public void visit(OWLIrreflexiveObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
            axiom.getSubject().accept(this);
        }


        public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
            axiom.getSubject().accept(this);
        }


        public void visit(OWLObjectPropertyAssertionAxiom axiom) {
            axiom.getSubject().accept(this);
        }


        public void visit(OWLObjectPropertyChainSubPropertyAxiom axiom) {
            axiom.getSuperProperty().accept(this);
        }


        public void visit(OWLObjectPropertyDomainAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLObjectPropertyRangeAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLObjectSubPropertyAxiom axiom) {
            axiom.getSubProperty().accept(this);
        }


        public void visit(OWLOntologyAnnotationAxiom axiom) {
        }


        public void visit(OWLReflexiveObjectPropertyAxiom axiom) {
            axiom.getProperty().accept(this);
        }


        public void visit(OWLSameIndividualsAxiom axiom) {
            for (OWLIndividual ind : axiom.getIndividuals()) {
                ind.accept(this);
            }
        }


        public void visit(OWLSubClassAxiom axiom) {
            if (!axiom.getSubClass().isAnonymous()) {
                ((OWLClass) axiom.getSubClass()).accept(this);
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
