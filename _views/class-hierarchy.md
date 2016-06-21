---
title: Class hierarchy
layout: view
blurb: Displays the asserted or inferred class hierarchy for the active ontologies.
---
Displays the asserted and inferred class hierarchies.  The asserted class hierarchy is visible by default.  To switch to the asserted hierarchy either select "Asserted" from the drop-down or press 'a' when the hierarchy has the focus.  To switch to the inferred hierarchy either select "Inferred" from the drop-down or press 'i' when the hierarchy has the focus.

![Class Hierarchy]({{site.baseurl}}/assets/views/class-hierarchy/class-hierarchy.png)

The asserted class hierarchy view is one of the primary navigation devices for named OWL classes. The tree shows the subclass hierarchy that is derived from asserted SubClassOf and EquivalentClasses axioms in the ontology. Any classes that are not asserted to be a subclass of some other class will show up directly under owl:Thing (the root). For convenience, the tree also performs some trivial inferences to allow defined classes to appear in the hierarchy. Classes that have been asserted to be equivalent show up together on the appropriate node in the tree. Cycles are detected and removed. Classes will show up multiple times in the tree if they asserted to be a subclass of more than one class.

Defined classes are classes that are asserted to be equivalent to some other class expressions and are shown with a three-line equivalence symbol in their icon.
Primitive classes are classes that are not defined classes and are shown with a simple plain filled circle icon.

Dragging and dropping classes in the hierarchy will cause subclass relations to change (the old subclass relation will be removed and a new one added).
