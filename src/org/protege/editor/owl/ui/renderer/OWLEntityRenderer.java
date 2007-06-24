package org.protege.editor.owl.ui.renderer;

import org.semanticweb.owl.model.OWLEntity;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLEntityRenderer {

//    public void setup(OWLModelManager owlModelManager);

//    public void initialise();


    public String render(OWLEntity entity);

//    public void addListener(OWLEntityRendererListener listener);
//
//    public void removeListener(OWLEntityRendererListener listener);

//    public void dispose();
}
