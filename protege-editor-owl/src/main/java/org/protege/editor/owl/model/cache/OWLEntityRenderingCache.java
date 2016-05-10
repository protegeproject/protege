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

    public void setOWLModelManager(OWLModelManager owlModelManager);


    public void rebuild();


    public void addRendering(OWLEntity owlEntity);


    public void removeRendering(OWLEntity owlEntity);


    public void updateRendering(OWLEntity owlEntity);


    public OWLEntity getOWLEntity(String rendering);


    public OWLClass getOWLClass(String rendering);


    public OWLObjectProperty getOWLObjectProperty(String rendering);


    public OWLDataProperty getOWLDataProperty(String rendering);


    public OWLAnnotationProperty getOWLAnnotationProperty(String rendering);


    public OWLNamedIndividual getOWLIndividual(String rendering);


    public OWLDatatype getOWLDatatype(String rendering);


    public Set<String> getOWLClassRenderings();


    public Set<String> getOWLObjectPropertyRenderings();


    public Set<String> getOWLDataPropertyRenderings();


    public Set<String> getOWLAnnotationPropertyRenderings();


    public Set<String> getOWLIndividualRenderings();


    public Set<String> getOWLDatatypeRenderings();


    public Set<String> getOWLEntityRenderings();


    String getRendering(OWLEntity owlEntity);
}
