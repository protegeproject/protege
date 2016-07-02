---
title: Individual Description
layout: view
blurb: Displays type information along with SameIndividual and DifferentIndividuals information for the selected individuals
menuPath: Individual views > Description
---
The Individual Description view displays types (ClassAssertions) for the selected individual along with SameIndividual and DifferentIndividuals assertions.  The view follows the [common view look and feel]({{site.baseurl}}/view-look-and-feel) and contains three sections:

* **Types** - Displays a list of class expressions that the selected individual is a direct instance of.  

  If reasoning is turned on then direct inferred named types are displayed here using a pale yellow background. Note that inferred types will only be displayed if this option is enabled in the [Reasoner Preferences]({{site.baseurl}}/preferences/reasoner).  

  Each row in this section corresponds to a [ClassAssertion](http://www.w3.org/TR/owl2-syntax/#Class_Assertions) axiom where the individual is the current selected individual.
* **Same Individual As** - Displays a list of individuals that the selected individual is asserted or inferred to be the same as.  Inferred same individuals will only be displayed if this option is enabled in the [Reasoner Preferences]({{site.baseurl}}/preferences/reasoner).

  Each row in this section corresponds to a [SameIndividual](http://www.w3.org/TR/owl2-syntax/#Individual_Equality) axiom that contains the current selected individual.
* **Different Individuals** - Displays a list of individual that the selected individual is *asserted* to be different from.  Note that *inferred* different individuals are *not* displayed here.

  Each row in this section corresponds to a [DifferentIndividuals](http://www.w3.org/TR/owl2-syntax/#Individual_Inequality) axiom that contains the current selected individual.
