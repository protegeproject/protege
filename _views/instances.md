---
title: Instances
layout: view
blurb: Displays a list of individuals that are instances of the selected class
menuPath: Individual views > Instances
---
The instances view displays a list of individuals that are asserted to be instances of the selected class.  The individuals in the view may be clicked to change the global individual selection.

Above the individuals list is the displayed class section that causes the listed individuals to appear in the view.  The individuals in the list are asserted to be an instance of this class.

## Creating and Deleting Individuals

The view toolbar contains two buttons for adding new individuals and deleting existing individuals.

To add an individual as an instance of the selected class press the "Add Individual" toolbar button.  A dialog will be displayed for entering the name of the individual.  Enter the name and press the OK button.  A fresh individual will be added as an instance of the current selected class.

<figure>
  <img src="{{site.baseurl}}/assets/views/instances/instances-view-toolbar.png" style="max-width: 300px"/>
  <figcaption>The instances view toolbar.  From left to right the buttons are: "Add Individual" and "Delete individual(s)"</figcaption>
</figure>

To delete individuals select the individuals to be deleted from the list.  Press the "Delete individual(s)" toolbar button.  All axioms that reference the selected individuals (contain one or more of the selected individuals in their signature) will be removed from the set of active ontologies.
