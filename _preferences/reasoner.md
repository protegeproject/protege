---
title: Reasoner Preferences
layout: preferences
---

The reasoner preferences panel allows the way in which Protégé interacts with the selected reasoner to be configured.

## Reasoning Overview

The selected reasoner performs a number of reasoning tasks based on the kinds of inferences that Protégé is configured to display.  The computation time for a  task varies depending upon the task itself, the selected reasoner, the complexity (the kinds of class expressions and axiom types used in the ontology) of the ontology and to some extent the size of the ontology.  For example, the selected reasoner might be able to perform the task of computing the types for the selected individual quicker than it can perform the task of computing the  property assertions for the selected individual.  It is important to realise that different reasoners may perform totally differently on the same ontology as different reasoners are optimised for different styles of ontologies.

If reasoning is turned on (in the Reasoner menu) and Protégé appears to be sluggish, it may be the case that certain reasoning tasks, that are triggered to retrieve information about the selected entity, are taking a while to run.  If faster interaction with Protégé is desired (at the expense of not seeing more information) then slower reasoning tasks can be disabled using the "Displayed Inferences" panel, which is shown in the figure below.

## Configuring Displayed Inferences

The "Displayed Inferences" panel consists of a series of checkboxes that select the kinds of inferences that are displayed in the Protégé user interface.  If a box is checked then Protégé will query the reasoner for the information that is related to the checked box when the ontology is being browsed.  For example, if the "Equivalent Classes" checkbox is checked then Protégé will display inferred classes in the EquivalentTo section in the Class Description View.  Similarly, if the "Domains" checkbox is checked in the "Object Property Inferences" section then Protégé will query for the inferred object property domains and display them in the Object Property Description view when the object property selection changes.

For each checkbox in the "Displayed Inferences" panel, the name of the associated inference is displayed, and if the reasoner has been run previously (in the current Protégé session) timing information will be displayed in grey along side it.  The timing information shows the total time spent by the reasoner computing the given inferences and also displays the average time per inference.  This information can be used to determine slow running reasoner tasks.

<figure>
  <img src="{{site.baseurl}}/assets/preferences/reasoner/displayed-inferences.png" alt="Displayed Inferences"/>
  <figcaption>
    The Reasoner Preferences panel that allows displayed inferences to be specified.
  </figcaption>
<figure>
