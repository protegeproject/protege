package org.protege.editor.owl.model.search.impl.importer;

import org.protege.editor.owl.model.search.*;
import org.protege.editor.owl.model.search.impl.OntologyBasedSearchMDImporter;
import org.protege.editor.owl.model.search.impl.SearchMetadata;
import org.protege.editor.owl.model.search.impl.SearchMetadataDB;
import org.protege.editor.owl.ui.renderer.styledstring.StyledString;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/10/2012
 */
public class OntologyAnnotationSearchMetadataImporter extends OntologyBasedSearchMDImporter {

    @Override
    public boolean isImporterFor(Set<SearchCategory> categories) {
        return categories.contains(SearchCategory.ANNOTATION_VALUE);
    }

    @Override
    public void generateSearchMetadata(OWLOntology ontology, SearchContext context, SearchMetadataDB db) {
        for (OWLAnnotation annotation : ontology.getAnnotations()) {
            generateSearchMetadataForAnnotation(annotation, ontology, context, db);
        }
    }

    private void generateSearchMetadataForAnnotation(final OWLAnnotation annotation, OWLOntology ontology, final SearchContext context, SearchMetadataDB db) {
        String groupDescription = context.getRendering(annotation.getProperty());
        StyledString rendering = context.getStyledStringRendering(annotation);
        SearchMetadata md = new SearchMetadata(SearchCategory.ANNOTATION_VALUE, groupDescription, ontology, context.getRendering(ontology), rendering.getString()) {
            @Override
            public StyledString getStyledSearchSearchString() {
                return context.getStyledStringRendering(annotation);
            }
        };
        db.addResult(md);
        for (OWLAnnotation anno : annotation.getAnnotations()) {
            generateSearchMetadataForAnnotation(anno, ontology, context, db);
        }
    }
}
