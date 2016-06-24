---
title: Ontology header
blurb: Displays the active ontology IRI, ontology version IRI and ontology annotations.
menuPath: Ontology views > Ontology Header
layout: view
---

The Ontology Header view shows the Ontology IRI, the Ontology Version IRI and Ontology annotations for the current active ontology.

## Ontology IRI and Ontology Version IRI

 The two fields at the top of the view allow the Ontology IRI and Ontology Version IRI to be viewed and edited.  Both of these fields will be empty in the case of an anonymous ontology.  For named ontology (the most common case), the Ontology IRI should be an absolute IRI (Internationalized Resource Identifier).  It is recommended that the Ontology IRI is in fact a URL (http://...) that points to the Web location from where the ontology can be downloaded.  

 If an ontology has an Ontology IRI then it may also have an Ontology Version IRI.  The version IRI should describe the version of the ontology and it is also recommended that the version IRI is a URL that points to the Web location where this version of the ontology is published.  The latest version of an ontology should be published at its Ontology IRI.

For more information about Ontology IRIs and Ontology Version IRIs please see the W3C [OWL specification](https://www.w3.org/TR/owl2-syntax/#Ontology_IRI_and_Version_IRI).

## Ontology Annotations

An ontology may be annotated (in a similar manner to the way that entities are annotated).  We recommend ontology authors provide a human readble label for the title of the ontology (using ```dc:title``` for example) and license information (using ```dc:rights``` for example).

For more information on annotations and editing annotations please see the [Annotations View]({{site.baseurl}}/views/annotations/) documentation.
