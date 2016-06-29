---
title: Object Property Hierarchy
layout: view
blurb: Displays the asserted or inferred object property hierarchy for the active ontologies.
menuPath: Object property views > Object property hierarchy
---
The object property hierarchy view displays the asserted and inferred object property hierarchies.  The asserted object property hierarchy is visible by default.

The asserted object property hierarchy view is one of the primary navigation devices in {{site.protege}}.  It is presented as a tree where tree nodes correspond to object properties.  A child node represents an object property that is a subproperty of the property represented by the parent node.

An object property will be shown under another object property if it is asserted to be a SubPropertyOf that other property.  For example, if the ontology contains ```P SubPropertyOf Q```, then ```P``` will appear under ```Q``` in the tree. Any object properties that are not asserted to be a subproperty of some other object property will show up directly under ```owl:topObjectProperty``` (the root).

Note that names in the object property hierarchy are quoted using single quotes if the name contains spaces.  The quotes, however, are not part of the name.

## Switching between Asserted and Inferred modes

The asserted object property hierarchy is shown by default.  To switch to the inferred hierarchy either select "Inferred" from the drop-down or press 'i' when the hierarchy has the focus.  If the inferred hierarchy is shown, the asserted hierarchy can be displayed by selecting "Asserted" from the drop-down or press 'a' when the hierarchy has the focus.

## Inserting New object properties

In order to insert new object properties into the object property hierarchy, the **Asserted** object property hierarchy must be selected.

New object properties can be inserted into the object property hierarchy using the property creation buttons at the top of the hierarchy, by using the popup menu that is displayed by right clicking (CMD+Click on a Mac) on the object property hierarchy, or by using keyboard shortcuts.  These mechanisms are described in more detail below.

* **Add SubProperty Button** - Make sure that the Asserted object property hierarchy is selected. Select an object property and press the "Add SubProperty" button.  A dialog will be displayed for specifying the name of the new property.  The new property will be inserted as a subproperty of the selected property.
* **Add Sibling Property Button** -  Make sure that the Asserted object property hierarchy is selected. Select an object property that the new property will be a sibling of and press the "Add Sibling" button.  A dialog will be displayed for specifying the name of the new property.  The new property will be inserted as a sibling property of the selected property.

<figure>
  <img src="{{site.baseurl}}/assets/views/object-property-hierarchy/object-property-hierarchy-toolbar.png" style="max-width: 300px"/>
  <figcaption>The object property hierarchy toolbar.  From left to right the buttons are: "Add SubProperty", "Add sibling property", "Delete selected property"</figcaption>
</figure>

* **Add SubProperty Keyboard Shortcut** - Make sure that the Asserted object property hierarchy is selected. Select an object property and press ```Ctrl+E``` on Windows/Linux or ```Cmd+E``` on a Mac.  A dialog will be displayed for specifying the name of the new property.  The new property will be inserted as a subproperty of the selected property.
* **Add Sibling Class Keyboard Shortcut** - Make sure that the Asserted object property hierarchy is selected. Select an object property that the new property will be a sibling of and press  ```Ctrl+Shift+E``` on Windows/Linux or ```Cmd+Shift+E``` on a Mac.  A dialog will be displayed for specifying the name of the new property.  The new property will be inserted as a sibling property of the selected property.

## Deleting object properties

The object property hierarchy can be used to delete object properties (axioms that mention the selected object properties will be removed from the ontology).  Select the property (or object properties) to be deleted and then activate delete:

* **Delete selected property** - Make sure that the Asserted object property hierarchy is selected. Select the property (or object properties to be deleted). Press the "Delete selected property" button on the object property hierarchy toolbar.
* **Delete Keyboard Shortcut** - Make sure that the Asserted object property hierarchy is selected. Select the property (or object properties to be deleted).  Press the ```Delete``` button.

After activating delete in one of the ways described above, the "Delete object property" dialog will be displayed (shown below).  Select the option to either delete the selected property, or delete the selected property and all of the descendants of the selected property in the asserted object property hierarchy.  Protégé will compute the object properties to be deleted based on the selected option and will then remove all axioms that mention these object properties from the set of active ontologies.

<figure>
  <img src="{{site.baseurl}}/assets/views/object-property-hierarchy/delete-object-property-dialog.png" style="max-width: 600px;"/>
  <figcaption>The delete property dialog.  Here, the property hasPart is being deleted.</figcaption>
</figure>


## Bolding

The object property hierarchy view may show some names in a bold font and others in a regular font.  Roughly speaking, object properties whose names are shown in a bold font are described using axioms in the active ontology.  This means that the property appears on the left hand side of a SubPropertyOf axiom, in an Equivalentobject properties axiom, in a property characteristics axiom (Transitive, Symmetrics etc.).  Object properties whose names are shown in a regular weight font are merely referenced in the imports closure of the active ontology.

<figure>
<img src="{{site.baseurl}}/assets/views/object-property-hierarchy/object-property-bolding.png" style="max-width: 300px;"/>
<figcaption>An example the bolding used in the object property hierarchy.  The property hasPartDirect is shown in bold because the active ontology contains axioms that describe it.  The property hasPart is not shown in bold because it is merely referenced by the active ontology (in the description of hasPartDirect in this case).</figcaption>
</figure>

## Drag and Drop

It is possible to edit some of the SubPropertyOf axioms in the ontology by dragging and dropping tree nodes in the asserted object property hierarchy. Dropping an object property on top of an other property will make it a SubPropertyOf that other property.

## Cycles

Object properties that appear in a cycle of SubPropertyOf axioms, for example ```P SubPropertyOf Q```, ```Q SubPropertyOf R``` and ```R SubPropertyOf P``` will be collapsed and shown together, with one tree node for each property in the cycle so that each property may be selected.  An example is shown below.  

The reason for this notation is that a cycle in the object property hierarchy between two or more object properties states that the object properties in the cycle are equivalent.  This is because ```P EquivalentTo Q``` is an abbreviation for the two axioms ```P SubPropertyOf Q``` and ```Q SubPropertyOf P```.
