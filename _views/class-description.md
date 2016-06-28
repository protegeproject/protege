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
* **General Class Axioms**.  Each entry shows a General Class Axiom that contains the current selected class in its signature (i.e. mentions the current selected class).
* **Instances**.  Each entry specifies an individual that has the current selected class as its type in a class assertion axiom.
* **DisjointWith**. Each entry specifies a list of classes that this class is disjoint with. A DisjointClasses axiom can contain 2 or more classes (the current selected class is removed from the list for clarity)
* **Target for Key** Specifies a mixed list of object and data properties that act as a key for instances of the current selected class.  Keys are a new feature in OWL 2 and consist of a set of properties.  For a given individual the particular values of these properties taken together imply distinctness.  For example, a key consisting of hasSurname and hasDateOfBirth could be used (in a limited setting) to imply distinctness of the individuals in the class Person.
* **Disjoint Union Of** Specifies that this class is the main class in a DisjointUnion class axiom.
* **SubClassOf (Anonymous Ancestor)** Protege searches all ancestors of the selected class and accumulates all of their anonymous superclasses which are then displayed in this section.

## Inferred information

Inferred information is shown inline with a yellow background.

<figure>
  <img src="{{site.baseurl}}/assets/views/class-description/class-description-inferred-information.png" style="max-width: 600px;"/>
  <figcaption>The class description view showing a mix of asserted and inferred information.  The rows with the yellow background are inferred.  Here, the selected class is asserted to be a subclass of "Aircraft" and "hasManufacturer value Boeing" and is inferred to be a subclass of "BoeingAircraft".</figcaption>
</figure>


## Adding and Editing Class Expressions

Adding or editing Equivalent classes or Superclasses displays a dialog that contains multiple editors. The editors range from a simple tree from which a class can be picked, to restriction creators that help produce simple restrictions with a named filler, to a fully functioning expression editor. The editor set is pluggable so developers can add further tool support.

<figure>
  <img src="{{site.baseurl}}/assets/views/class-description/class-expression-editor.png" style="max-width: 657px;"/>
  <figcaption>The class expression editor showing the class expression editing tab.  There are also tabs for selecting classes from a tree view, or creating object and data restrictions by picking properties and classes from a tree view.</figcaption>
</figure>

The Disjoint classes editor gives the choice of a class hierarchy on which multi-select can be performed, or an expression editor where multiple values can be entered by comma separating them.

## Popup Menu Actions

Right clicking on a line in the Class Description view displays a popup menu offering various actions:

* **Switch to defining ontology** - Changes the active ontology that contains the underlying axiom for the selected row.
* **Pull into active ontology** - Moves the underlying axiom for the selected row into the active ontology.  This action is greyed out if the axiom is already in the active ontology.
* **Move axioms to ontology** - Moves the underlying axioms for the selected rows to a different ontology.  A dialog box will be displayed for selecting the target ontology.
* **Convert selected row to defined class** - Applicable only to rows in the SubClassOf section.  Converts the selected rows from a set of SubClassOf axioms to an EquivalentClasses axiom.  For example, given the selected class ```A```, and the selected rows ```B``` and ```C```, which represent ```A SubClassOf B``` and ```A SubClassOf C```, the two two axioms will be replaced by a single EquivalentClasses axiom, ```A EquivalentTo B and C```.
* **Create new defined class** -  Creates a new class and makes it equivalent to the class expression in the selected row.
* **Create closure axiom** - Applicable only to selected rows in the SubClassOf section containing class expressions of the form ```p some A```.  Accumulates all the fillers of ```some``` restrictions on the associated property and adds a new SubClassOf axiom with a class expression that is an AllValuesFrom restriction along the property with a filler that is a disjunction of the fillers of the selected SomeValuesFrom restrictions.  For example, if the selected class is ```A``` and the selected rows contain ```p some B``` and ```p some C```, then ```A``` will also be made a SubClassOf ```p only (B or C)```.

<figure>
  <img src="{{site.baseurl}}/assets/views/class-description/class-description-popup-menu.png" style="max-width: 600px;"/>
  <figcaption>The popup menu for the class description view.</figcaption>
</figure>
