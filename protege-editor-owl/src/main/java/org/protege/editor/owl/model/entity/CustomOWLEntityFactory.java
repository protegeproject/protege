package org.protege.editor.owl.model.entity;

/*
* Copyright (C) 2007, University of Manchester
*
*
*/

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.annotation.*;
import org.protege.editor.owl.model.user.DefaultUserNameProvider;
import org.protege.editor.owl.model.user.UserNamePreferencesManager;
import org.protege.editor.owl.model.user.UserPreferences;
import org.protege.editor.owl.model.util.ISO8601Formatter;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.DublinCoreVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 28, 2008<br><br>
 */
public class CustomOWLEntityFactory implements OWLEntityFactory {

    private Logger logger = LoggerFactory.getLogger(CustomOWLEntityFactory.class);

    private OWLModelManager mngr;

    private static AutoIDGenerator autoIDGenerator; // only a single generator between instances

    private LabelDescriptor labelDescriptor;


    public CustomOWLEntityFactory(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public static <T extends OWLEntity> T getOWLEntity(OWLDataFactory factory, Class<T> type, IRI iri) {
	    if (OWLClass.class.isAssignableFrom(type)){
	        return type.cast(factory.getOWLClass(iri));
	    }
	    else if (OWLObjectProperty.class.isAssignableFrom(type)){
	        return type.cast(factory.getOWLObjectProperty(iri));
	    }
	    else if (OWLDataProperty.class.isAssignableFrom(type)){
	        return type.cast(factory.getOWLDataProperty(iri));
	    }
	    else if (OWLNamedIndividual.class.isAssignableFrom(type)){
	        return type.cast(factory.getOWLNamedIndividual(iri));
	    }
	    else if (OWLAnnotationProperty.class.isAssignableFrom(type)){
	        return type.cast(factory.getOWLAnnotationProperty(iri));
	    }
	    else if (OWLDatatype.class.isAssignableFrom(type)){
	        return type.cast(factory.getOWLDatatype(iri));
	    }
	    throw new RuntimeException("Missing branch for entity type: " + type.getSimpleName());
	}


	public OWLEntityCreationSet<OWLClass> createOWLClass(String shortName, IRI baseIRI) throws OWLEntityCreationException {
        return createOWLEntity(OWLClass.class, shortName, baseIRI);
    }


    public OWLEntityCreationSet<OWLObjectProperty> createOWLObjectProperty(String shortName, IRI baseURI) throws OWLEntityCreationException {
        return createOWLEntity(OWLObjectProperty.class, shortName, baseURI);
    }


    public OWLEntityCreationSet<OWLDataProperty> createOWLDataProperty(String shortName, IRI baseURI) throws OWLEntityCreationException {
        return createOWLEntity(OWLDataProperty.class, shortName, baseURI);
    }


    public OWLEntityCreationSet<OWLAnnotationProperty> createOWLAnnotationProperty(String shortName, IRI baseURI) throws OWLEntityCreationException {
        return createOWLEntity(OWLAnnotationProperty.class, shortName, baseURI);
    }


    public OWLEntityCreationSet<OWLNamedIndividual> createOWLIndividual(String shortName, IRI baseURI) throws OWLEntityCreationException {
        return createOWLEntity(OWLNamedIndividual.class, shortName, baseURI);
    }


    public OWLEntityCreationSet<OWLDatatype> createOWLDatatype(String shortName, IRI baseIRI) throws OWLEntityCreationException {
        return createOWLEntity(OWLDatatype.class, shortName, baseIRI);
    }


    public <T extends OWLEntity> OWLEntityCreationSet<T> createOWLEntity(Class<T> type, String shortName, IRI baseURI) throws OWLEntityCreationException {
        try {

        	EntityNameInfo name = generateName(type, shortName, baseURI);
            T entity = getOWLEntity(mngr.getOWLDataFactory(), type, name.getIri());
            List<OWLOntologyChange> changes = getChanges(entity, name);
            return new OWLEntityCreationSet<>(entity, changes);
        }
        catch (URISyntaxException | AutoIDException e) {
            throw new OWLEntityCreationException(e);
        }
    }


    public <T extends OWLEntity> OWLEntityCreationSet<T> preview(Class<T> type, String shortName, IRI base) throws OWLEntityCreationException {
        // There is probably a better way round this.
        if (getAutoIDGenerator() instanceof Revertable){
            ((Revertable)getAutoIDGenerator()).checkpoint();
        }
        try{
            return createOWLEntity(type, shortName, base);
        }
        catch(OWLEntityCreationException e){
            throw e;
        }
        finally{
            if (getAutoIDGenerator() instanceof Revertable){
                ((Revertable)getAutoIDGenerator()).revert();
            }
        }
    }
    
    protected <T extends OWLEntity> EntityNameInfo generateName(Class<T> type, String shortName, IRI baseURI) throws AutoIDException, URISyntaxException, OWLEntityCreationException {
        if (baseURI == null){
            if (useDefaultBaseIRI() || mngr.getActiveOntology().getOntologyID().isAnonymous()){
                baseURI = getDefaultBaseIRI();
            }
            else{
                baseURI = mngr.getActiveOntology().getOntologyID().getOntologyIRI().get();
            }
        }

        IRI iri;
        String id = null;
        if (isFragmentAutoGenerated()){
        	Set<IRI> tried = new HashSet<IRI>();
            do{
                id = getAutoIDGenerator().getNextID(type);
                iri = createIRI(id, baseURI);
                if (!tried.contains(iri)) {
                	tried.add(iri);
                }
                else {
                	throw new AutoIDException("Auto id generator ran out of new ids - fix this in the new entity preferences");
                }
            } while (isIRIAlreadyUsed(iri)); // don't pun unnecessarily
        }
        else {
            iri = createIRI(shortName, baseURI);

            if (isIRIAlreadyUsed(type, iri)){
                throw new OWLEntityCreationException("Entity already exists: " + iri);
            }

            if (isGenerateIDLabel()){
                id = getAutoIDGenerator().getNextID(type); // critical it is unique?
            }
        }
        return new EntityNameInfo(iri, id, shortName);
    }


    protected <T extends OWLEntity > List<OWLOntologyChange> getChanges(T entity, EntityNameInfo name) {
    	List<OWLOntologyChange> changes = new ArrayList<>();

        if (isGenerateIDLabel() && name.getId() != null) {
            changes.addAll(createLabel(entity, name.getId()));
        }

        if (isGenerateNameLabel() && name.getShortName() != null) {
            changes.addAll(createLabel(entity, name.getShortName()));
        }

        OWLDataFactory df = mngr.getOWLDataFactory();
        OWLAxiom ax = df.getOWLDeclarationAxiom(entity);
        changes.add(new AddAxiom(mngr.getActiveOntology(), ax));

        changes.addAll(getEntityCreationMetadataChanges(entity));
        return changes;
    }

    private <T extends OWLEntity> List<OWLOntologyChange> getEntityCreationMetadataChanges(T entity) {
        EntityCreationMetadataProvider metadataProvider = getEntityCreationMetadataProvider();
        return metadataProvider.getEntityCreationMetadataChanges(entity, mngr.getActiveOntology(), mngr.getOWLDataFactory());
    }

    private EntityCreationMetadataProvider getEntityCreationMetadataProvider() {
        Preferences metadataPreferences = EntityCreationMetadataPreferences.get();
        EntityCreationMetadataPreferencesManager metadataPreferencesManager = new EntityCreationMetadataPreferencesManager(metadataPreferences);
        if(metadataPreferencesManager.isCreationMetadataEnabled()) {
            Preferences preferences = UserPreferences.get();
            UserNamePreferencesManager userNamePreferencesManager = new UserNamePreferencesManager(preferences);
            return new SimpleEntityCreationMetadataProvider(
                    new DefaultUserNameAnnotationPropertyIriProvider(metadataPreferencesManager),
                    new DefaultUserNameProvider(userNamePreferencesManager),
                    new DefaultDateAnnotationPropertyIriProvider(metadataPreferencesManager),
                    metadataPreferencesManager.getDateFormatter()
            );
        }
        else {
            return new NullEntityCreationMetadataProvider();
        }
    }


    protected IRI createIRI(String fragment, IRI baseIRI) throws URISyntaxException {
        fragment = fragment.replace(" ", "_");
        if (baseIRI == null){
            if (useDefaultBaseIRI()){
                baseIRI = EntityCreationPreferences.getDefaultBaseIRI();
            }
            else{
                baseIRI = mngr.getActiveOntology().getOntologyID().getOntologyIRI().get();
            }
        }
        String base = baseIRI.toString().replace(" ", "_");
        if (!base.endsWith("#") && !base.endsWith("/")) {
            base += EntityCreationPreferences.getDefaultSeparator();
        }
        return IRI.create(new URI(base + fragment));
    }


    private List<? extends OWLOntologyChange> createLabel(OWLEntity owlEntity, String value) {
        LabelDescriptor descr = getLabelDescriptor();
        IRI iri = descr.getIRI();
        String lang = descr.getLanguage();

        OWLDataFactory df = mngr.getOWLDataFactory();
        OWLLiteral con = df.getOWLLiteral(value, lang);
        OWLAnnotationProperty prop = df.getOWLAnnotationProperty(iri);
        OWLAxiom ax = df.getOWLAnnotationAssertionAxiom(prop, owlEntity.getIRI(), con);
        return Collections.singletonList(new AddAxiom(mngr.getActiveOntology(), ax));
    }


    private LabelDescriptor getLabelDescriptor() {
        Class<? extends LabelDescriptor> cls = EntityCreationPreferences.getLabelDescriptorClass();
        if (labelDescriptor == null || !cls.equals(labelDescriptor.getClass())){
            try {
                labelDescriptor = cls.newInstance();
            }
            catch (InstantiationException | IllegalAccessException e) {
                logger.error("Cannot create label descriptor", e);
            }
        }
        return labelDescriptor;
    }


    private AutoIDGenerator getAutoIDGenerator() {
        final Class<? extends AutoIDGenerator> prefAutoIDClass = EntityCreationPreferences.getAutoIDGeneratorClass();
        if (autoIDGenerator == null || !prefAutoIDClass.equals(autoIDGenerator.getClass())){
            try {
                autoIDGenerator = prefAutoIDClass.newInstance();
            }
            catch (InstantiationException | IllegalAccessException e) {
                logger.error("Cannot create auto ID generator", e);
            }
        }
        return autoIDGenerator;
    }


    private <T extends OWLEntity> boolean isIRIAlreadyUsed(Class<T> type, IRI iri) {
        for (OWLOntology ont : mngr.getActiveOntologies()){
            if ((OWLClass.class.isAssignableFrom(type) && ont.containsClassInSignature(iri)) ||
                (OWLObjectProperty.class.isAssignableFrom(type) && ont.containsObjectPropertyInSignature(iri)) ||
                (OWLDataProperty.class.isAssignableFrom(type) && ont.containsDataPropertyInSignature(iri)) ||
                (OWLIndividual.class.isAssignableFrom(type) && ont.containsIndividualInSignature(iri)) ||
                (OWLAnnotationProperty.class.isAssignableFrom(type) && ont.containsAnnotationPropertyInSignature(iri)) ||
                (OWLDatatype.class.isAssignableFrom(type) && ont.containsDatatypeInSignature(iri))){
                return true;
            }
        }
        return false;
    }


    private boolean isIRIAlreadyUsed(IRI iri) {
        for (OWLOntology ont : mngr.getOntologies()){
            if (ont.containsEntityInSignature(iri)){
                return true;
            }
        }
        return false;
    }


    protected boolean useDefaultBaseIRI() {
        return EntityCreationPreferences.useDefaultBaseIRI();
    }


    protected boolean isFragmentAutoGenerated() {
        return EntityCreationPreferences.isFragmentAutoGenerated();
    }


    protected boolean isGenerateNameLabel() {
        return EntityCreationPreferences.isGenerateNameLabel();
    }


    protected boolean isGenerateIDLabel() {
        return EntityCreationPreferences.isGenerateIDLabel();
    }


    protected IRI getDefaultBaseIRI() {
        return EntityCreationPreferences.getDefaultBaseIRI();
    }
    
    public static class EntityNameInfo {
    	private IRI iri;
    	private String id;
    	private String shortName;
    	    	
		public EntityNameInfo(IRI iri, String id, String shortName) {
			this.iri = iri;
			this.id = id;
			this.shortName = shortName;
		}
		public IRI getIri() {
			return iri;
		}
		public String getId() {
			return id;
		}
		public String getShortName() {
			return shortName;
		}
    }
}
