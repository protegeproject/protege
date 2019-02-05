package org.protege.editor.owl.model.cache;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.*;

import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 21-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLEntityRenderingCache extends Disposable {

    void setOWLModelManager(OWLModelManager owlModelManager);


    void rebuild();


    void addRendering(OWLEntity owlEntity);


    void removeRendering(OWLEntity owlEntity);


    void updateRendering(OWLEntity owlEntity);


    OWLEntity getOWLEntity(String rendering);


    OWLClass getOWLClass(String rendering);

    /**
     * Gets the entities that have the specified rendering
     * @param rendering The rendering
     * @return A set of entities that have the specified rendering
     */
    Set<OWLEntity> getOWLEntities(String rendering);

    OWLObjectProperty getOWLObjectProperty(String rendering);


    OWLDataProperty getOWLDataProperty(String rendering);


    OWLAnnotationProperty getOWLAnnotationProperty(String rendering);


    OWLNamedIndividual getOWLIndividual(String rendering);


    OWLDatatype getOWLDatatype(String rendering);


    Set<String> getOWLClassRenderings();


    Set<String> getOWLObjectPropertyRenderings();


    Set<String> getOWLDataPropertyRenderings();


    Set<String> getOWLAnnotationPropertyRenderings();


    Set<String> getOWLIndividualRenderings();


    Set<String> getOWLDatatypeRenderings();


    Set<String> getOWLEntityRenderings();


    String getRendering(OWLEntity owlEntity);
}
