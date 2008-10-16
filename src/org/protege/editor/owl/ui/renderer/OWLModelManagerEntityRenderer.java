package org.protege.editor.owl.ui.renderer;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLEntity;


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

    public void setup(OWLModelManager owlModelManager);


    public void initialise();


    public String render(OWLEntity entity);


    public void addListener(OWLEntityRendererListener listener);


    public void removeListener(OWLEntityRendererListener listener);
}
