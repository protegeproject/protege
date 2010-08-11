package org.protege.editor.owl.ui.renderer;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.ShortFormProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
/*
 * TODO why is this interface here and different from OWLModelManagerEntityRenderer?
 */
public interface OWLEntityRenderer extends ShortFormProvider {

	String render(OWLEntity entity);
    
    void ontologiesChanged();

}
