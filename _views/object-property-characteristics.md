---
title: Object Property Characteristics
layout: view
blurb: Displays the asserted property characteristics for the selected Object Property.
menuPath: Object property views > Characteristics
---

The Object Property characteristics view displays the asserted characteristics for the selected object property.  Characteristics are shown as a list of checkboxes.  If the box is checked for a given characteristic that means that some ontology in the imports closure of the active ontology asserts that characteristic.  If the box in unchecked for a given characteristic that means that the characteristic is not asserted at all in any ontology in the imports closure of the active ontology.

## Available characteristics

* **Functional** - asserts that the selected property is Functional.  Intuitively, this means that for any given individual, the property can have at most one value.  In other words, there can be at most one out going relationship along the property for that individual.  Note that, if multiple individuals are specified as values for the property then these values will be inferred to denote the same object.
* **Inverse Functional** - asserts that the selected property is InverseFunctional.  Intuitively, this means the inverse property of the selected property (whether it explicitly declared or not) is Functional. In other words, there can be at most one incoming relationship along the property for that individual.  Note that, if multiple individuals are specified as incoming values for the property then these values will be inferred to denote the same object.
* **Transitive** - asserts that the selected property is Transitive.  Intuitively, this means that if individual ```x``` is related to individual ```y```, and individual ```y``` is related to individual ```z```, then individual ```x``` will be related to individual ```z```.  In other words, a single "hop" is implied over a chain of two along a given property if that property is transitive.
* **Symmetric** - asserts that the selected property is Symmetric.  Intuitively, this means that the property has itself as an inverse, so if individual ```x``` is related to individual ```y``` then individual ```y``` must also be related to individual ```x``` along the same property.
* **Asymmetric** - asserts that the selected property is Asymmetric.  Intuitively, this means that if individual ```x``` is related to individual ```y``` then individual ```y``` is not related to individual ```x``` along the same property.
* **Reflexive** - asserts that the selected property is Reflexive.  Asserting that a property is reflexive causes every single individual to be related to itself via that property.  
* **Irreflexive** - asserts that the selected property is Irreflexive. Asserting that a property is irreflexive means that an individual cannot be related to itself via that property.  

## Adding and Deleting a Property Characteristic

To add a given characteristic for a property, the property should be selected and the associated characteristic checkbox should be checked.  The relevant axiom for the characteristic will be added to the active ontology.

To remove/delete a characteristic for a given property, the property should be selected and the associated characteristic checkbox should be unchecked.  Any axioms that specify the characteristic in the imports closure of the active ontology will be removed.
