---
title: Standard View Look and Feel
layout: default
---

# Common View Look and Feel

Many of the views in the Protégé distribution, such as the [Class Description]({{site.baseurl}}/views/class-description) view shown below, have a common look and feel.  A typical view consist of a list of *Sections*, and each Section contains a list of *Rows*.  

An example of such a view is the [Class Description]({{site.baseurl}}/views/class-description) view, which contains sections such as "Equivalent To", "SubClass Of" and "Disjoint With" (amongst others).  These sections are pertinent to the current selected class, and a given section contains rows that are pertinent to that section.  For example, the "SubClass Of" section contains rows that contain class expressions that the current selected class is a subclass of.  Similarly, the "Equivalent To" section contains rows that contain class expressions that the current selected class is equivalent to.

The purpose of this page is to describe these sections and rows based views, along with their generic functionality are described in more detail.

<figure>
<img src="{{site.baseurl}}/assets/frame/frame.svg" style="max-width: 100%;"/>
</figure>

## Sections

As mentioned above, most views are broken up into sections.  Each section has a *header* that specifies the meaning of the rows that it contains.  For example, in the [Class Description]({{site.baseurl}}/views/class-description) view, the section, "SubClassOf", contains rows that the current selected class is a subclass of.    

Sections that can have rows added to them have a + button (Add button) to the right of the header label. Pressing this button displays an editor for adding a new row to the section.

<figure>
<img src="{{site.baseurl}}/assets/frame/subclass-of-section-header.png" width="180px"/>
<figcaption>An example of a Section Header.  This section header is taken from the Class Description view.  The + button (Add button) allows extra rows to be added to the associated section.</figcaption>
</figure>

## Rows

Each row in a section typically represents a single axiom (or statement) in one of the active ontologies. For example, in the class description view shown above, the "SubClass Of" section contains the row "hasTopping some TomatoTopping".  This particular row represents the axiom "AmericanHot SubClassOf hasTopping some TomatoTopping" (since the view is displaying information for the class "AmericanHot").  We say that this is the underlying axiom for this row.

## Asserted and Inferred Rows

A row that has a white background represents an **asserted** row.  In other words, the axiom that is represented by the row has been asserted into one of the active ontologies.  The afore mentioned row containing "hasTopping some TomatoTopping" in an example of an asserted row.  Hovering over an asserted row will cause a tooltip to be displayed that shows the ontology that the axiom is asserted in. Finally, rows whose underlying axioms are asserted in the active ontology are highlighted in bold.

<figure>
<img src="{{site.baseurl}}/assets/frame/asserted-row.png" style="max-width: 100%;"/>
<figcaption>An example of an asserted row.  Note the white background that indicates the underlying axiom is asserted.  In this particular case, the row is shown in bold, which indicates that the underlying axiom is asserted in the active ontology.</figcaption>
</figure>

A row that has a yellow background represents an **inferred** row.  In other words, the axiom that is represented by the row has been inferred and is not contained in one of the active ontologies.  In the class description view shown above, the row containing "SpicyPizza" is an example of such a row.  This row means that the axiom "AmericanHot SubClassOf SpicyPizza" is inferred.

<figure>
<img src="{{site.baseurl}}/assets/frame/inferred-row.png" style="max-width: 100%;"/>
<figcaption>An example of an inferred row.  Note the pale yellow background that indicates that the row is inferred and the underlying axiom is entailed by the active ontologies but is not asserted in any of the active ontologies.</figcaption>
</figure>

## Row Buttons
Each row has a series of *row buttons* that are located on the right hand side of it.  The standard buttons for an assert row are "Explain inference", "Annotations", "Remove" (Delete) and "Edit".  The standard buttons for an inferred row are "Explain inference" and "Annotations".  

<figure>
<img src="{{site.baseurl}}/assets/frame/row-buttons.png" style="width: 150px; max-width: 100%;"/>
<figcaption>
The standard row buttons for an asserted row.  From left to right: "Explain inference", "Annotations", "Delete" and "Edit".
</figcaption>
</figure>

These buttons offer the following functionality:

* **Explain inference** <img src="{{site.baseurl}}/assets/frame/button-explain-inference.png" width="22px"/> - Explains why the axiom underlying the row in question follows from the set of active ontologies.  This button is available for both asserted and inferred rows.  This is because of the fact that there can be multiple explanations for a given axiom and there may be other reasons as to why the axiom can be inferred other than the fact that it is asserted.

* **Annotations** <img src="{{site.baseurl}}/assets/frame/button-annotations.png" width="22px"/> - Displays the annotations on the underlying axiom for the row in question.  If the axiom has annotations on it then this button will be highlighted with a yellow background.  Pressing this button allows the axiom annotations to be viewed, edited, deleted or added to.

* **Delete** <img src="{{site.baseurl}}/assets/frame/button-delete.png" width="22px"/> - Removes the row and hence removes the underlying axiom from its containing ontology.  

* **Edit** <img src="{{site.baseurl}}/assets/frame/button-edit.png" width="22px"/> - Displays a popup editor that allows the row content (and hence underlying axiom) to be edited.

## Section Header Keyboard Shortcuts

* **Enter/Return** - If a section header is selected, pressing Enter/Return is equivalent to pressing the + button for that section header.  For example, pressing enter if the "SubClass Of" header is selected in the Class Description View will display the dialog to enter a class expression that will be added as a row in this section.


## Row Keyboard Shortcuts

* **Enter/Return** - If a row is selected, pressing Enter/Return is a shortcut for pressing the edit button for that row.  

* **Ctrl+Delete (Cmd+Delete)** - If a row is selected, then pressing Ctrl+Delete (Cmd+Delete on a Mac) is a shortcut for pressing the Delete button.
