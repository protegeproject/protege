---
title: Usage
blurb: Shows the Usage of the current selected entity
layout: view
---
The Usage view displays axioms that reference (or mention) the current selected entity.  More specifically, it displays axioms that contain the current selected entity in their signature.

Usage is sorted by the subject entity (where possible) and all expressions are hyperlinked for easy navigation.

The Usage view can be useful for finding out how much a particular entity is used in an ontology, or when you want to know where a class has been used as the filler of a restriction for example.

There are additional filters that are available as a series of checkboxes at the top of the view, which reduce the number of usages shown:

* **this** - axioms that describe the currently selected entity and are likely to be easily visible in other views. For example, if a class is selected these axioms are displayed in the Class Description view.
* **disjoints** -  In some case, the number of disjoint classes axioms that reference a give class can be large (particularly if they are pairwise) and can clutter this view, so disjoint classes axioms are hidden unless this box is checked.
* **named sub/superclasses** - (for classes only) this information is easily read off the class hierarchy and a large number of subclasses clutters the usage view so they are hidden unless this box is checked.
