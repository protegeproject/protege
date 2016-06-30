---
title: Individual Description
layout: view
blurb: Displays type information along with SameIndividual and DifferentIndividuals information for the selected individuals
menuPath: Individual views > Description
---
The Individual Description view displays types (ClassAssertions) for the selected individual along with SameIndividual and DifferentIndividuals assertions.  The view contains three sections:

* **Types** - Displays a list of class expressions that the selected individual is a direct instance of.  If reasoning is turned on then direct inferred named types are displayed here with a pale yellow background. Note that inferred types will only be displayed if this option is enabled in the [Reasoner Preferences]({{site.baseurl}}/preferences/reasoner).
* **SameIndividual** - Displays a list of individuals that the selected individual is asserted or inferred to be the same as.  Inferred same individuals will only be displayed if this option is enabled in the [Reasoner Preferences]({{site.baseurl}}/preferences/reasoner).
* **DifferentIndividuals** - Displays a list of individual that the selected individual is *asserted* to be different from.  Note that *inferred* different individuals are *not* displayed here.
