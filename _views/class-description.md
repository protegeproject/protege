---
title: Class Description
blurb: Displays the logical description for the selected class
layout: view
---

The class description view is the core of the class editor.  It allows the logical description of the selected class to be edited using Manchester OWL Syntax.

![Class Description View]({{site.baseurl}}/assets/views/class-description/class-description-view.png)

The sections are

* **EquivalentTo**.  Each entry specifies a class expression that is equivalent to the current selected class.
* **SubClassOf**.  Each entry specifies a class expression that the current selected class is a subclass of.
* **SubClassOf (Anonymous Ancestor)** Protege searches all ancestors of the selected class and accumulates all of their anonymous superclasses which are then displayed in this section.
* **Instances**.  Each entry specifies an individual that has this class as its type in a class assertion axiom
* **DisjointWith**. Each entry specifies a single disjoint statement. A disjoint statement can contain 2 or more classes (the current selected class is removed from the list for clarity)


Adding or editing Equivalent classes or Superclasses displays a dialog that contains multiple editors. The editors range from a simple tree from which a class can be picked, to restriction creators that help produce simple restrictions with a named filler, to a fully functioning expression editor. The editor set is pluggable so developers can add further tool support.

The Disjoint classes editor gives the choice of a class hierarchy on which multi-select can be performed, or an expression editor where multiple values can be entered by comma separating them.

Additional entries in the context menu include:


* Convert selected rows to defined class build an intersection using the selected superclasses and make this class equivalent to the intersection (removing the superclass assertions)
* Create new defined class make a new defined class that is equivalent to the selected class
* Create closure axiom (if the assertion is subclassOf(p some A)) accumulate all fillers of superclass some restrictions on p (say A, B and C). Add a new superclass - p only (A or B or C) to close the property p.
