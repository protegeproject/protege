package org.protege.editor.owl.model.search.impl.importer;

import org.protege.editor.owl.model.search.*;
import org.protege.editor.owl.model.search.impl.AxiomBasedSearchMetadataImporter;
import org.protege.editor.owl.model.search.impl.SearchMetadataDB;
import org.protege.editor.owl.model.search.impl.SearchMetadataImportContext;
import org.protege.editor.owl.ui.renderer.styledstring.StyledString;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/10/2012
 */
public class AxiomAnnotationSearchMetadataImporter extends AxiomBasedSearchMetadataImporter {

    @Override
    public boolean isImporterFor(AxiomType<?> axiomType, Set<SearchCategory> categories) {
        return categories.contains(SearchCategory.ANNOTATION_VALUE);
    }

    @Override
    public void generateSearchMetadataFor(OWLAxiom axiom, OWLEntity axiomSubject, String axiomSubjectRendering, SearchMetadataImportContext context, SearchMetadataDB db) {
        for (OWLAnnotation annotation : axiom.getAnnotations()) {
            generateSearchMetadataForAnnotation(annotation, axiomSubject, axiomSubjectRendering, context, db);
        }
    }

    private void generateSearchMetadataForAnnotation(final OWLAnnotation annotation, OWLEntity axiomSubject, String axiomSubjectRendering, final SearchMetadataImportContext context, SearchMetadataDB db) {
        String group = context.getRendering(annotation.getProperty());
        StyledString ren = context.getStyledStringRendering(annotation);
        SearchMetadata md = new SearchMetadata(SearchCategory.ANNOTATION_VALUE, group, axiomSubject, axiomSubjectRendering, ren.getString()) {
            @Override
            public StyledString getStyledSearchSearchString() {
                return context.getStyledStringRendering(annotation);
            }
        };
        db.addResult(md);
        for (OWLAnnotation anno : annotation.getAnnotations()) {
            generateSearchMetadataForAnnotation(anno, axiomSubject, axiomSubjectRendering, context, db);
        }
    }
}
