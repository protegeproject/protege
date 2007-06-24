package org.protege.editor.owl.model.description.manchester;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: May 4, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class EntityMapperImpl implements EntityMapper {

    private OWLModelManager owlModelManager;


    public EntityMapperImpl(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
    }


    public OWLEntity getOWLEntity(String identifier) {
        return owlModelManager.getOWLEntity(identifier);
    }
}
