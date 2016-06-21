---
title: Class Description
blurb: Displays the logical description for the selected class
layout: view
menuPath: Class views > Class Description
---
The class description view is the core of the class editor.  It allows the logical description of the selected class to be edited using Manchester OWL Syntax.  The view comprises a number of sections, which are described below.  Each line in a section corresponds to an axiom in one of the loaded ontologies.

The sections are

* **EquivalentTo**.  Each entry specifies a class expression that is equivalent to the current selected class.
* **SubClassOf**.  Each entry specifies a class expression that the current selected class is a subclass of.  In other words, each entry is a superclass of the current selected class.
* **SubClassOf (Anonymous Ancestor)** Protege searches all ancestors of the selected class and accumulates all of their anonymous superclasses which are then displayed in this section.
* **Instances**.  Each entry specifies an individual that has the current selected class as its type in a class assertion axiom.
* **DisjointWith**. Each entry specifies a list of classes that this class is disjoint with. A DisjointClasses axiom can contain 2 or more classes (the current selected class is removed from the list for clarity)
* **Target for Key** Specifies a mixed list of object and data properties that act as a key for instances of the current selected class.  Keys are a new feature in OWL 2 and consist of a set of properties.  For a given individual the particular values of these properties taken together imply distinctness.  For example, a key consisting of hasSurname and hasDateOfBirth could be used (in a limited setting) to imply distinctness of the individuals in the class Person.
* **Disjoint Union Of** Specifies that this class is the main class in a DisjointUnion class axiom.


Adding or editing Equivalent classes or Superclasses displays a dialog that contains multiple editors. The editors range from a simple tree from which a class can be picked, to restriction creators that help produce simple restrictions with a named filler, to a fully functioning expression editor. The editor set is pluggable so developers can add further tool support.

The Disjoint classes editor gives the choice of a class hierarchy on which multi-select can be performed, or an expression editor where multiple values can be entered by comma separating them.

Additional entries in the context menu include:


* Convert selected rows to defined class build an intersection using the selected superclasses and make this class equivalent to the intersection (removing the superclass assertions)
* Create new defined class make a new defined class that is equivalent to the selected class
* Create closure axiom (if the assertion is subclassOf(p some A)) accumulate all fillers of superclass some restrictions on p (say A, B and C). Add a new superclass - p only (A or B or C) to close the property p.
