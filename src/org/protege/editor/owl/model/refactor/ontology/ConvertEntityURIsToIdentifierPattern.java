package org.protege.editor.owl.model.refactor.ontology;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLOntology;

import java.net.URI;
import java.util.Set;


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

    public Set<OWLOntology> ontologies;

    private int count = 0;

    private OWLModelManager owlModelManager;


    public ConvertEntityURIsToIdentifierPattern(OWLModelManager owlModelManager, Set<OWLOntology> ontologies) {
        this.ontologies = ontologies;
        this.owlModelManager = owlModelManager;
    }


    public void performConversion() {
//        EntityRenamer entityRenamer = new EntityRenamer(ontologies);
//        Set<OWLEntity> entities = new HashSet<OWLEntity>();
//        for(OWLOntology ont : ontologies) {
//            entities.addAll(ont.getClasses());
//            entities.addAll(ont.getObjectProperties());
//            entities.addAll(ont.getDataProperties());
//            entities.addAll(ont.getAnnotationProperties());
//            entities.addAll(ont.getIndividuals());
//
//        }
//        entities.remove(owlModelManager.getOWLDataFactory().getOWLThing());
//            for(OWLEntity entity : new ArrayList<OWLEntity>(entities)) {
//                String rendering = owlModelManager.getOWLEntityRenderer().render(entity);
//                AddAnnotationInstance instance = new AddAnnotationInstance(
//                        owlModelManager.getActiveOntology(),
//                        entity, owlModelManager.getOWLDataFactory().getOWLAnnotationProperty(URI.create(OWLVocabularyAdapter.INSTANCE.getLabel())),
//                        owlModelManager.getOWLDataFactory().getOWLConcreteData(
//                                URI.create(XMLSchemaSimpleDatatypeVocabulary.INSTANCE.getString()),
//                                null,
//                                rendering
//                        ), null);
//
//                owlModelManager.applyChange(instance);
//                entityRenamer.setURI(getNextURI(entity));
//                entity.accept(entityRenamer);
//                owlModelManager.applyChanges(entityRenamer.getChanges());
//            }

    }


    private URI getNextURI(OWLEntity entity) {
        count++;
        return URI.create("http://www.co-ode.org/test#Entity" + owlModelManager.getRendering(entity).hashCode());
    }
}
