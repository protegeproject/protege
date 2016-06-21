package org.protege.editor.owl.model.search.impl.importer;

import org.protege.editor.owl.model.search.*;
import org.protege.editor.owl.model.search.impl.AxiomBasedSearchMetadataImporter;
import org.protege.editor.owl.model.search.impl.SearchMetadataDB;
import org.protege.editor.owl.model.search.impl.SearchMetadataImportContext;
import org.protege.editor.owl.ui.renderer.styledstring.StyledString;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;

import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/10/2012
 */
public class LogicalAxiomRenderingSearchMetadataImporter extends AxiomBasedSearchMetadataImporter {

    @Override
    public boolean isImporterFor(AxiomType<?> axiomType, Set<SearchCategory> categories) {
        return axiomType.isLogical() && categories.contains(SearchCategory.LOGICAL_AXIOM);
    }

    @Override
    public void generateSearchMetadataFor(final OWLAxiom axiom, OWLEntity axiomSubject, String axiomSubjectRendering, final SearchMetadataImportContext context, SearchMetadataDB db) {
        StyledString rendering = context.getStyledStringRendering(axiom);
        String groupDescription = axiom.getAxiomType().getName();
        SearchMetadata md = new SearchMetadata(SearchCategory.LOGICAL_AXIOM, groupDescription, axiomSubject, axiomSubjectRendering, rendering.getString()) {
            @Override
            public StyledString getStyledSearchSearchString() {
                return context.getStyledStringRendering(axiom);
            }
        };
        db.addResult(md);
    }
}
