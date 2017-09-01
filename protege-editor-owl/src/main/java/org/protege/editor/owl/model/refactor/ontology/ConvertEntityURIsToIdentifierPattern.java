package org.protege.editor.owl.model.refactor.ontology;

import com.google.common.collect.ListMultimap;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.AnnotationValueShortFormProvider;
import org.protege.editor.owl.ui.renderer.OWLEntityRendererImpl;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityURIConverter;
import org.semanticweb.owlapi.util.OWLEntityURIConverterStrategy;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 30-Aug-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ConvertEntityURIsToIdentifierPattern {

    private final Logger logger = LoggerFactory.getLogger(ConvertEntityURIsToIdentifierPattern.class);


    public Set<OWLOntology> ontologies;

    private OWLModelManager mngr;

    private Map<OWLEntity, IRI> iriMap = new HashMap<>();

    private OntologyImportsWalker ontologyImportsWalker;

    private OntologyTargetResolver resolver;

    public ConvertEntityURIsToIdentifierPattern(OWLModelManager mngr, Set<OWLOntology> ontologies) {
        this.ontologies = ontologies;
        this.mngr = mngr;
        this.ontologyImportsWalker = new OntologyImportsWalker(mngr, ontologies);

        setupRenderers();
    }


    public void setOntologyResolver(OntologyTargetResolver resolver){
        this.resolver = resolver;
    }


    public void performConversion() {
        buildNewIRIMap();

        if (!iriMap.isEmpty()){

            List<OWLOntologyChange> changes = new ArrayList<>();

            changes.addAll(createNewLabelAxioms());

            final OWLEntityURIConverterStrategy converterStrategy = owlEntity -> {
                IRI uri = iriMap.get(owlEntity);
                return (uri != null) ? uri : owlEntity.getIRI();
            };

            OWLEntityURIConverter entityURIConverter = new OWLEntityURIConverter(mngr.getOWLOntologyManager(),
                                                                                 mngr.getOntologies(),
                                                                                 converterStrategy);

            changes.addAll(entityURIConverter.getChanges());

            mngr.applyChanges(changes);
        }

        logger.info("Converted " + iriMap.size() + " entities to use labels");
    }


    private void setupRenderers() {
    }


    private void buildNewIRIMap() {
        iriMap.clear();

        // The label renderer drops through to a specified backup renderer if a label cannot be found
        // So, hook it up with one that returns null so we can check if the label rendering failed.
        ShortFormProvider nullSFP =  new ShortFormProvider(){
            public String getShortForm(OWLEntity owlEntity) {
                return "";
            }

            public void dispose() {
                // do nothing
            }
        };

        // convert the preferences wrt IRIs into maps using annotation properties
        List<OWLAnnotationProperty> annotationProperties = new ArrayList<>();
        Map<OWLAnnotationProperty, List<String>> langMap = new HashMap<>();

        ListMultimap<IRI, String> annotMap = OWLRendererPreferences.getInstance().getAnnotationLangMap();
        for (IRI iri : annotMap.keySet()){
            OWLAnnotationProperty p  = mngr.getOWLDataFactory().getOWLAnnotationProperty(iri);
            annotationProperties.add(p);
            langMap.put(p, annotMap.get(iri));
        }

        AnnotationValueShortFormProvider sfp = new AnnotationValueShortFormProvider(annotationProperties,
                                                                                    langMap,
                                                                                    mngr.getOWLOntologyManager(),
                                                                                    nullSFP);

        OWLEntityIRIRegenerator IRIGen = new OWLEntityIRIRegenerator(mngr);

        for(OWLEntity entity : getAllReferencedEntities()) {
            String labelRendering = sfp.getShortForm(entity);
            if (refactorWhenLabelPresent(entity, labelRendering)){
                iriMap.put(entity, IRIGen.generateNewIRI(entity));
            }
        }

        try {
            IRIGen.dispose();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean refactorWhenLabelPresent(OWLEntity entity, String labelRendering) {
        String iri = entity.getIRI().toString();
        if (!iri.endsWith(labelRendering)) {
            return false;
        }
        char c = iri.charAt(iri.length() - 1 - labelRendering.length());
        return c == '#' || c == '/';
    }

    private Collection<? extends OWLOntologyChange> createNewLabelAxioms() {
        List<OWLOntologyChange> changes = new ArrayList<>();

        OWLDataFactory df = mngr.getOWLDataFactory();
        EntityOfSameTypeGenerator gen = new EntityOfSameTypeGenerator(df);

        OWLEntityRendererImpl fragmentRenderer = new OWLEntityRendererImpl(); // basic fragment renderer
        fragmentRenderer.setup(mngr);
        fragmentRenderer.initialise();

        for (OWLEntity entity : iriMap.keySet()){
            final Set<OWLOntology> onts = getOntologiesForEntityLabel(entity);
            if (!onts.isEmpty()){
                String uriRendering = fragmentRenderer.render(entity);

                OWLAnnotation annotation = generateLabelAnnotation(uriRendering);

                final IRI newIRI = iriMap.get(entity);
                final OWLEntity newEntity = gen.getEntityOfSameType(newIRI, entity);
                final OWLAnnotationAssertionAxiom ax = df.getOWLAnnotationAssertionAxiom(newEntity.getIRI(), annotation);

                for (OWLOntology ont : onts){
                    changes.add(new AddAxiom(ont, ax));
                }
            }
            else{
                logger.warn("Ignored ID conversion for entity (" + mngr.getRendering(entity) + "): cannot determine suitable ontology target for axiom");
            }
        }

        fragmentRenderer.dispose();

        return changes;
    }

    private OWLAnnotation generateLabelAnnotation(String label) {
        OWLDataFactory df = mngr.getOWLDataFactory();
        OWLAnnotationProperty aProp = getPreferredLabel();
        String lang = getPreferredLanguage();

        OWLLiteral value = df.getOWLLiteral(label, lang);

        return df.getOWLAnnotation(aProp, value);
    }


    private String getPreferredLanguage() {
        final List<String> langs = OWLRendererPreferences.getInstance().getAnnotationLangs();
        return langs.isEmpty() ? null : langs.get(0);
    }


    private Set<OWLOntology> getOntologiesForEntityLabel(OWLEntity entity) {
        Set<OWLOntology> onts = ontologyImportsWalker.getLowestOntologiesToContainReference(entity);
        if (onts.size() == 1){
            return onts;
        }
        if (resolver != null){
            return resolver.resolve(entity, onts);
        }
        return Collections.emptySet();
    }


    public OWLAnnotationProperty getPreferredLabel() {
        final List<IRI> iris = OWLRendererPreferences.getInstance().getAnnotationIRIs();
        IRI iri = iris.isEmpty() ? OWLRDFVocabulary.RDFS_LABEL.getIRI() : iris.get(0);
        return mngr.getOWLDataFactory().getOWLAnnotationProperty(iri);
    }


    private Set<OWLEntity> getAllReferencedEntities() {
        Set<OWLEntity> entities = new HashSet<>();
        for(OWLOntology ont : ontologies) {
            entities.addAll(ont.getSignature());
        }
        entities.remove(mngr.getOWLDataFactory().getOWLThing());
        return entities;
    }


    public void dispose() {
        ontologyImportsWalker.dispose();

        iriMap.clear();
        ontologies.clear();

        mngr = null;
    }


    class EntityOfSameTypeGenerator implements OWLEntityVisitor{

        private OWLDataFactory df;

        private IRI iri;

        private OWLEntity entity;

        public EntityOfSameTypeGenerator(OWLDataFactory df) {
            this.df = df;
        }


        public OWLEntity getEntityOfSameType(IRI iri, OWLEntity entity) {
            this.iri = iri;
            entity.accept(this);
            return this.entity;
        }


        public void visit(OWLClass owlClass) {
            entity = df.getOWLClass(iri);
        }


        public void visit(OWLObjectProperty owlObjectProperty) {
            entity = df.getOWLObjectProperty(iri);
        }


        public void visit(OWLDataProperty owlDataProperty) {
            entity = df.getOWLDataProperty(iri);
        }


        public void visit(OWLNamedIndividual owlNamedIndividual) {
            entity = df.getOWLNamedIndividual(iri);
        }


        public void visit(OWLDatatype owlDatatype) {
            entity = df.getOWLDatatype(iri);
        }


        public void visit(OWLAnnotationProperty owlAnnotationProperty) {
            entity = df.getOWLAnnotationProperty(iri);
        }
    }
}
