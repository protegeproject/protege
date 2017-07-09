package org.protege.editor.owl.model.search.impl;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.search.SearchCategory;
import org.protege.editor.owl.model.search.impl.importer.AxiomAnnotationSearchMetadataImporter;
import org.protege.editor.owl.model.search.impl.importer.DisplayNameSearchMetadataImporter;
import org.protege.editor.owl.model.search.impl.importer.EntityAnnotationValueSearchMetadataImporter;
import org.protege.editor.owl.model.search.impl.importer.EntityIRISearchMetadataImporter;
import org.protege.editor.owl.model.search.impl.importer.LogicalAxiomRenderingSearchMetadataImporter;
import org.protege.editor.owl.model.search.impl.importer.OntologyAnnotationSearchMetadataImporter;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.AxiomSubjectProvider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 18/09/2012
 */
public class DefaultSearchMetadataImporter implements SearchMetadataImporter {


    public SearchMetadataDB getSearchMetadata(final OWLEditorKit editorKit, Set<SearchCategory> categories) {
        SearchMetadataImportContext context = new SearchMetadataImportContext(editorKit);
        SearchMetadataDB db = new SearchMetadataDB();

        getEntityBasedSearchMetadata(categories, context, db);
        getAxiomBasedSearchMetadata(categories, context, db);
        getOntologyBasedSearchMetadata(categories, context, db);

        return db;
    }

    private void getEntityBasedSearchMetadata(Set<SearchCategory> categories, SearchMetadataImportContext context, SearchMetadataDB db) {

        List<EntityBasedSearchMDImporter> importers = getEntityBasedSearchMetadataImporters(categories);

        Set<OWLEntity> processed = new HashSet<OWLEntity>();
        for (OWLOntology ontology : context.getOntologies()) {
            for (OWLEntity entity : ontology.getSignature()) {
                if (processed.add(entity)) {
                    getSearchMetadataForEntity(entity, context, db, importers);
                }
            }
        }
    }

    private void getSearchMetadataForEntity(OWLEntity entity, SearchMetadataImportContext context, SearchMetadataDB db, List<EntityBasedSearchMDImporter> importers) {
        String entityRendering = context.getRendering(entity);
        for (EntityBasedSearchMDImporter importer : importers) {
            importer.generateSearchMetadataFor(entity, entityRendering, context, db);
        }
    }


    private void getAxiomBasedSearchMetadata(Set<SearchCategory> categories, SearchMetadataImportContext context, SearchMetadataDB db) {
        for (AxiomType<?> axiomType : AxiomType.AXIOM_TYPES) {
            getSearchMetadataForAxiomsOfType(axiomType, categories, context, db);
        }
    }

    private void getSearchMetadataForAxiomsOfType(AxiomType<?> axiomType, Set<SearchCategory> categories, SearchMetadataImportContext context, SearchMetadataDB db) {
        for (AxiomBasedSearchMetadataImporter importer : getAxiomBasedSearchMetadataImporters(categories, axiomType)) {
            for (OWLOntology ontology : context.getOntologies()) {
                for (OWLAxiom ax : ontology.getAxioms(axiomType)) {
                    OWLObject subject = new AxiomSubjectProvider().getSubject(ax);
                    if (subject instanceof OWLEntity) {
                        OWLEntity entSubject = (OWLEntity) subject;
                        String rendering = context.getRendering(entSubject);
                        importer.generateSearchMetadataFor(ax, entSubject, rendering, context, db);
                    }
                }
            }
        }
    }


    private void getOntologyBasedSearchMetadata(Set<SearchCategory> categories, SearchMetadataImportContext context, SearchMetadataDB db) {
        List<OntologyBasedSearchMDImporter> ontologyBasedSearchMDImporters = getOntologyBasedSearchMetadataImporters(categories);
        for (OWLOntology ontology : context.getOntologies()) {
            for (OntologyBasedSearchMDImporter importer : ontologyBasedSearchMDImporters) {
                importer.generateSearchMetadata(ontology, context, db);
            }
        }
    }


//    /**
//     * A convenience method which gets the styled string rendering for an {@link OWLObject}
//     * @param editorKit The editor kit which should be used to compute rendering for entities embedded within the
//     * object.
//     * @param object The object to be rendered
//     * @return The {@link StyledString} rendering of the object.
//     */
//    private StyledString getStyledStringRendering(final OWLEditorKit editorKit, OWLObject object) {
//        OWLEditorKitShortFormProvider sfp = new OWLEditorKitShortFormProvider(editorKit);
//        OWLEditorKitOntologyShortFormProvider ontologySfp = new OWLEditorKitOntologyShortFormProvider(editorKit);
//        OWLObjectRenderingContext renderingContext = new OWLObjectRenderingContext(sfp, ontologySfp);
//        OWLObjectStyledStringRenderer renderer = new OWLObjectStyledStringRenderer(renderingContext);
//        return renderer.getRendering(object);
//    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private List<EntityBasedSearchMDImporter> getEntityBasedSearchMetadataImporters(Set<SearchCategory> categories) {
        List<EntityBasedSearchMDImporter> entityBasedSearchMDImporters = new ArrayList<EntityBasedSearchMDImporter>();
        entityBasedSearchMDImporters.add(new DisplayNameSearchMetadataImporter());
        entityBasedSearchMDImporters.add(new EntityIRISearchMetadataImporter());
        entityBasedSearchMDImporters.add(new EntityAnnotationValueSearchMetadataImporter());

        List<EntityBasedSearchMDImporter> result = new ArrayList<EntityBasedSearchMDImporter>();
        for (EntityBasedSearchMDImporter importer : entityBasedSearchMDImporters) {
            if (importer.isImporterFor(categories)) {
                result.add(importer);
            }
        }
        return result;
    }

    private List<AxiomBasedSearchMetadataImporter> getAxiomBasedSearchMetadataImporters(Set<SearchCategory> categories, AxiomType<?> axiomType) {
        List<AxiomBasedSearchMetadataImporter> axiomBasedSearchMetadataImporters = new ArrayList<AxiomBasedSearchMetadataImporter>();
        axiomBasedSearchMetadataImporters.add(new AxiomAnnotationSearchMetadataImporter());
        axiomBasedSearchMetadataImporters.add(new LogicalAxiomRenderingSearchMetadataImporter());

        List<AxiomBasedSearchMetadataImporter> result = new ArrayList<AxiomBasedSearchMetadataImporter>();
        for (AxiomBasedSearchMetadataImporter importer : axiomBasedSearchMetadataImporters) {
            if (importer.isImporterFor(axiomType, categories)) {
                result.add(importer);
            }
        }
        return result;
    }

    private List<OntologyBasedSearchMDImporter> getOntologyBasedSearchMetadataImporters(Set<SearchCategory> categories) {
        List<OntologyBasedSearchMDImporter> ontologyBasedSearchMDImporters = new ArrayList<OntologyBasedSearchMDImporter>();
        ontologyBasedSearchMDImporters.add(new OntologyAnnotationSearchMetadataImporter());

        List<OntologyBasedSearchMDImporter> result = new ArrayList<OntologyBasedSearchMDImporter>();
        for (OntologyBasedSearchMDImporter importer : ontologyBasedSearchMDImporters) {
            if (importer.isImporterFor(categories)) {
                result.add(importer);
            }
        }
        return result;
    }
}
