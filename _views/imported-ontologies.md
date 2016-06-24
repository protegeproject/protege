---
title: Imported Ontologies
blurb: Displays the direct and indirect imports for the active ontology
menuPath: Ontology Views > Imported Ontologies
layout: view
---

The Imported Ontology view displays the direct and indirect imports for the
active ontology.  Each entry corresponds to an ```owl:imports``` statement
for the active ontology, and shows:

* The IRI that is the subject of the ```owl:imports``` statement surrounded by
triangular brackets
* The short name for the ontology, which is derived from the imported Ontology IRI or labelling annotation (such as ```rdfs:label``` or ```dc:title```) if present.
* The Ontology IRI of the imported ontology, which should be the same, but may differ from the IRI that is the subject of the owl:imports statement.
* The location where the ontology was loaded from.  This is a hyperlink if the ontology was loaded from the Web or local file system.

In cases where an import could not be loaded for whatever reason, only the owl:imports IRI will be visible.
