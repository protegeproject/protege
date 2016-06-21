---
title: Ontology Metrics
layout: view
blurb: Displays entity and axiom counts for the active ontology and its imports closure
menuPath: Ontology views > Ontology metrics
---
The ontology metrics view displays entity and axiom counts for the axioms in the active ontology and its imports closure.  Descriptions (and where appropriate links to the W3C documentation) are listed below.

For the different types of axiom metrics in this view, for example, ```SubClassOf```, right clicking (or pressing Cmd and clicking on a Mac) will display a popup menu which can be used to show the axioms in question.

* **Axioms** - the combined logical and non-logical axiom count
* **Logical axioms** - the number of logical axioms
* **Declaration axioms count** - the number of declaration axioms.  Declaration
axioms are added under the hood in Protégé and cannot be seen in the user interface.
* **Class|Data Property|Data Property|Individual count** - the number of classes,
object propeeties, data properties and individuals in the signature of the
imports closure of the active ontology.  In other words, the number of distinct
classes, object properties, data properties and individuals that are mentioned
in the ontology.  The numbers here include built in entities, such as ```owl:Thing```
if they are explicitly mentioned in the ontology.  For example, an ontology
that contains the single axiom ```SubClassOf(A owl:Thing)``` will have a class count of 2.
* **DL Expressivity** - the [Description Logic](https://en.wikipedia.org/wiki/Description_logic) expressivity of the ontology.  This is the name of the Description Logic that the ontology uses. It
depends upon the kinds of axioms in the ontology and the kinds of class expressions
used in the ontology.  The maximum expressivity of an OWL 2 (DL) ontology is SROIQ(D).  The maximum expressivity of an OWL 1 ontology is SHOIN(D).  

## Class Axioms

* **SubClassOf** - the number of [SubClassOf](http://www.w3.org/TR/owl2-syntax/#Subclass_Axioms) axioms in the ontology and its imports closure.
* **EquivalentClasses** - the number of [EquivalentClasses](http://www.w3.org/TR/owl2-syntax/#Equivalent_Classes) axioms in the ontology and its imports closure.
* **DisjointClasses** - the number of of [DisjointClasses](http://www.w3.org/TR/owl2-syntax/#Disjoint_Classes)
axiom in the ontology and its imports closure.
* **GCI Count** - the number of GCIs, or General Concept Inclusion axioms in
the ontology an its imports closure.  Protégé regards a GCI as being a SubClassOf
axiom whose subclass (left hand side) is a complex class expression.
* **Hidden GCI Count** - A hidden GCI is a pair of axioms, one an EquivalentClasses
axiom and the other a SubClassOf axiom of the form EquivalentClass(A C) and
SubClassOf(A D), where C and D are complex class expressions.

## Object Property axioms

* **SubObjectPropertyOf** - the number of [SubObjectPropertyOf](http://www.w3.org/TR/owl2-syntax/#Object_Subproperties) axioms in
the ontology and its imports closure.
* **EquivalentObjectProperties** - the number of [EquivalentObjectProperties](http://www.w3.org/TR/owl2-syntax/#Equivalent_Object_Properties) axioms
in the ontology at its imports closure.
* **InverseObjectProperties** - the number of [InverseObjectProperties](http://www.w3.org/TR/owl2-syntax/#Subclass_Axioms) axioms in the ontology and its imports closure.
* **DisjointObjectProperties** - the number of [DisjointObjectProperties](http://www.w3.org/TR/owl2-syntax/#Disjoint_Object_Properties) axioms in the ontology and its imports closure.
* **FunctionalObjectProperty** - the number of [FunctionalObjectProperty](http://www.w3.org/TR/owl2-syntax/#Functional_Object_Properties) axioms in the ontology and its imports closure.
* **InverseFunctionalObjectProperty** - the number of [InverseFunctionalObjectProperty](http://www.w3.org/TR/owl2-syntax/#Inverse-Functional_Object_Properties) axioms in the ontology and its imports closure.
* **TransitiveObjectProperty** - the number of [TransitiveObjectProperty](http://www.w3.org/TR/owl2-syntax/#Transitive_Object_Properties) axioms in the ontology and its imports closure.
* **SymmetricsObjectProperty** - the number of [SymmetricsObjectProperty](http://www.w3.org/TR/owl2-syntax/#Symmetric_Object_Properties) axioms in the ontology and its imports closure.
* **AsymmetricObjectProperty** - the number of [AsymmetricObjectProperty](http://www.w3.org/TR/owl2-syntax/#Asymmetric_Object_Properties) axioms in the ontology and its imports closure.
* **ReflexiveObjectProperty** - the number of [ReflexiveObjectProperty](http://www.w3.org/TR/owl2-syntax/#Reflexive_Object_Properties) axioms in the ontology and its imports closure.
* **IrreflexiveObjectProperty** - the number of [IrreflexiveObjectProperty](http://www.w3.org/TR/owl2-syntax/#Irreflexive_Object_Properties) axioms in the ontology and its imports closure.
* **ObjectPropertyDomain** - the number of [ObjectPropertyDomain](http://www.w3.org/TR/owl2-syntax/#Object_Property_Domain) axioms in the ontology and its imports closure.
* **ObjectPropertyRange** - the number of [ObjectPropertyRange](http://www.w3.org/TR/owl2-syntax/#Object_Property_Range) axioms in the ontology and its imports closure.
* **SubPropertyChainOf** - the number of [SubPropertyChainOf](http://www.w3.org/TR/owl2-syntax/#Subclass_Axioms) axioms in the ontology and its imports closure.

## Data Property axioms
* **SubObjectPropertyOf** - the number of [SubDataPropertyOf](http://www.w3.org/TR/owl2-syntax/#Data_Subproperties) axioms in
the ontology and its imports closure.
* **EquivalentDataProperties** - the number of [EquivalentDataProperties](http://www.w3.org/TR/owl2-syntax/#Equivalent_Data_Properties) axioms
in the ontology at its imports closure.
* **DisjointDataProperties** - the number of [DisjointDataProperties](http://www.w3.org/TR/owl2-syntax/#Disjoint_Data_Properties) axioms in the ontology and its imports closure.
* **FunctionalDataProperty** - the number of [FunctionalDataProperty](http://www.w3.org/TR/owl2-syntax/#Functional_Data_Properties) axioms in the ontology and its imports closure.
* **DataPropertyDomain** - the number of [DataPropertyDomain](http://www.w3.org/TR/owl2-syntax/#Data_Property_Domain) axioms in the ontology and its imports closure.
* **DataPropertyRange** - the number of [DataPropertyRange](http://www.w3.org/TR/owl2-syntax/#Data_Property_Range) axioms in the ontology and its imports closure.

## Individual axioms
* **ClassAssertion** - the number of [ClassAssertion](http://www.w3.org/TR/owl2-syntax/#Class_Assertions) axioms in
the ontology and its imports closure.
* **ObjectPropertyAssertion** - the number of [ObjectPropertyAssertion](http://www.w3.org/TR/owl2-syntax/#Positive_Object_Property_Assertions) axioms in
the ontology and its imports closure.
* **DataPropertyAssertion** - the number of [DataPropertyAssertion](http://www.w3.org/TR/owl2-syntax/#Positive_Data_Property_Assertions) axioms in
the ontology and its imports closure.
* **NegativeObjectPropertyAssertion** - the number of [NegativeObjectPropertyAssertion](http://www.w3.org/TR/owl2-syntax/#Negative_Object_Property_Assertions) axioms in
the ontology and its imports closure.
* **NegativeDataPropertyAssertion** - the number of [NegativeDataPropertyAssertion](http://www.w3.org/TR/owl2-syntax/#Negative_Data_Property_Assertions) axioms in
the ontology and its imports closure.
* **SameIndividual** - the number of [SameIndividual](http://www.w3.org/TR/owl2-syntax/#Individual_Equality) axioms in
the ontology and its imports closure.
* **DifferentIndividuals** - the number of [DifferentIndividuals](http://www.w3.org/TR/owl2-syntax/#Individual_Inequality) axioms in
the ontology and its imports closure.

## Annotation axioms

* **AnnotationAssertion** - the number of [AnnotationAssertion](http://www.w3.org/TR/owl2-syntax/#Annotation_Assertion) axioms in
the ontology and its imports closure.
* **SubAnnotationPropertyOf** - the number of [SubAnnotationPropertyOf](http://www.w3.org/TR/owl2-syntax/#Annotation_Subproperties) axioms in
the ontology and its imports closure.
* **AnnotationPropertyDomain** - the number of [AnnotationPropertyDomain](http://www.w3.org/TR/owl2-syntax/#Annotation_Property_Domain) axioms in
the ontology and its imports closure.
* **AnnotationPropertyRange** - the number of [AnnotationPropertyRange](http://www.w3.org/TR/owl2-syntax/#Annotation_Property_Range) axioms in
the ontology and its imports closure.
