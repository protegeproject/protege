package org.protege.editor.owl.ui.renderer;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLModelManagerEntityRenderer extends OWLEntityRenderer, Disposable {

    void setup(OWLModelManager owlModelManager);


    void initialise();


    String render(OWLEntity entity);
    
    
    String render(IRI iri);


    void addListener(OWLEntityRendererListener listener);


    void removeListener(OWLEntityRendererListener listener);
}
