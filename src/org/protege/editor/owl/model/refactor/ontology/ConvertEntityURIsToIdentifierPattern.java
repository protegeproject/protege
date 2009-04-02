package org.protege.editor.owl.model.refactor.ontology;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.OWLEntityRendererImpl;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.semanticweb.owl.model.*;
import org.semanticweb.owl.util.AnnotationValueShortFormProvider;
import org.semanticweb.owl.util.OWLEntityURIConverter;
import org.semanticweb.owl.util.OWLEntityURIConverterStrategy;
import org.semanticweb.owl.util.ShortFormProvider;
import org.semanticweb.owl.vocab.OWLRDFVocabulary;

import java.net.URI;
import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 30-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ConvertEntityURIsToIdentifierPattern {

    private Logger logger = Logger.getLogger(ConvertEntityURIsToIdentifierPattern.class);


    public Set<OWLOntology> ontologies;

    private OWLModelManager mngr;

    private Map<OWLEntity, URI> uriMap = new HashMap<OWLEntity, URI>();

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
        buildNewURIMap();

        if (!uriMap.isEmpty()){

            List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

            changes.addAll(createNewLabelAxioms());

            final OWLEntityURIConverterStrategy converterStrategy = new OWLEntityURIConverterStrategy() {
                public URI getConvertedURI(OWLEntity owlEntity) {
                    URI uri = uriMap.get(owlEntity);
                    return (uri != null) ? uri : owlEntity.getURI();
                }
            };

            OWLEntityURIConverter entityURIConverter = new OWLEntityURIConverter(mngr.getOWLOntologyManager(),
                                                                                 mngr.getOntologies(),
                                                                                 converterStrategy);

            changes.addAll(entityURIConverter.getChanges());

            mngr.applyChanges(changes);
        }

        logger.info("Converted " + uriMap.size() + " entities to use labels");
    }


    private void setupRenderers() {
    }


    private void buildNewURIMap() {
        uriMap.clear();

        // The label renderer drops through to a specified backup renderer if a label cannot be found
        // So, hook it up with one that returns null so we can check if the label rendering failed.
        AnnotationValueShortFormProvider sfp = new AnnotationValueShortFormProvider(OWLRendererPreferences.getInstance().getAnnotationURIs(),
                                                                                    OWLRendererPreferences.getInstance().getAnnotationLangs(),
                                                                                    mngr.getOWLOntologyManager(), new ShortFormProvider(){
            public String getShortForm(OWLEntity owlEntity) {
                return null;
            }

            public void dispose() {
                // do nothing
            }
        });

        OWLEntityURIRegenerator uriGen = new OWLEntityURIRegenerator(mngr);

        for(OWLEntity entity : getAllReferencedEntities()) {
            String labelRendering = sfp.getShortForm(entity);
            if (labelRendering == null){
                uriMap.put(entity, uriGen.generateNewURI(entity));
            }
        }

        try {
            uriGen.dispose();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private Collection<? extends OWLOntologyChange> createNewLabelAxioms() {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();

        OWLDataFactory df = mngr.getOWLDataFactory();

        OWLEntityRendererImpl fragmentRenderer = new OWLEntityRendererImpl(); // basic fragment renderer
        fragmentRenderer.setup(mngr);
        fragmentRenderer.initialise();

        for (OWLEntity entity : uriMap.keySet()){
            final Set<OWLOntology> onts = getOntologiesForEntityLabel(entity);
            if (!onts.isEmpty()){
                String uriRendering = fragmentRenderer.render(entity);

                OWLConstantAnnotation annotation = generateLabelAnnotation(uriRendering);

                final URI newURI = uriMap.get(entity);
                final OWLEntity newEntity = getEntityOfSameType(newURI, entity);
                final OWLEntityAnnotationAxiom ax = df.getOWLEntityAnnotationAxiom(newEntity, annotation);

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

    private OWLConstantAnnotation generateLabelAnnotation(String label) {
        OWLDataFactory df = mngr.getOWLDataFactory();
        URI annotationURI = getPreferredLabel();
        String lang = getPreferredLanguage(annotationURI);

        OWLConstant value;

        if (lang != null){
            value = df.getOWLUntypedConstant(label, lang);
        }
        else{
            value = df.getOWLUntypedConstant(label);
        }

        return df.getOWLConstantAnnotation(annotationURI, value);
    }


    private String getPreferredLanguage(URI uri) {
        final List<String> langs = OWLRendererPreferences.getInstance().getAnnotationLangs(uri);
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


    public URI getPreferredLabel() {
        final List<URI> uris = OWLRendererPreferences.getInstance().getAnnotationURIs();
        return uris.isEmpty() ? OWLRDFVocabulary.RDFS_LABEL.getURI() : uris.get(0);
    }


    private Set<OWLEntity> getAllReferencedEntities() {
        Set<OWLEntity> entities = new HashSet<OWLEntity>();
        for(OWLOntology ont : ontologies) {
            entities.addAll(ont.getReferencedClasses());
            entities.addAll(ont.getReferencedObjectProperties());
            entities.addAll(ont.getReferencedDataProperties());
            entities.addAll(ont.getReferencedIndividuals());

        }
        entities.remove(mngr.getOWLDataFactory().getOWLThing());
        return entities;
    }


    private OWLEntity getEntityOfSameType(URI uri, OWLEntity entity) {
        if (entity.isOWLClass()){
            return mngr.getOWLDataFactory().getOWLClass(uri);
        }
        else if (entity.isOWLObjectProperty()){
            return mngr.getOWLDataFactory().getOWLObjectProperty(uri);
        }
        else if (entity.isOWLDataProperty()){
            return mngr.getOWLDataFactory().getOWLDataProperty(uri);
        }
        else if (entity.isOWLIndividual()){
            return mngr.getOWLDataFactory().getOWLIndividual(uri);
        }
        return null;
    }


    public void dispose() {
        ontologyImportsWalker.dispose();

        uriMap.clear();
        ontologies.clear();

        mngr = null;
    }
}
