package org.protege.editor.owl.model.search.importer;

import org.protege.editor.owl.model.search.*;
import org.protege.editor.owl.ui.renderer.styledstring.StyledString;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/10/2012
 */
public class EntityAnnotationValueSearchMetadataImporter extends EntityBasedSearchMDImporter {

    @Override
    public boolean isImporterFor(Set<SearchCategory> categories) {
        return categories.contains(SearchCategory.ANNOTATION_VALUE);
    }

    @Override
    public void generateSearchMetadataFor(OWLEntity entity, String entityRendering, final SearchMetadataImportContext context, SearchMetadataDB db) {
        for (OWLOntology ontology : context.getOntologies()) {
            for (final OWLAnnotation annotation : EntitySearcher.getAnnotations(entity, ontology).toArray(OWLAnnotation[]::new)) {
                String groupDescription = context.getRendering(annotation.getProperty());
                StyledString ren = context.getStyledStringRendering(annotation);
                SearchMetadata md = new SearchMetadata(SearchCategory.ANNOTATION_VALUE, groupDescription, entity, entityRendering, ren.getString()) {
                    @Override
                    public StyledString getStyledSearchSearchString() {
                        return context.getStyledStringRendering(annotation);
                    }
                };
                db.addResult(md);
            }
        }
    }
}
