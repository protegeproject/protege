package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.util.ShortFormProvider;
import uk.ac.manchester.cs.owl.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A renderer that renders objects using the Manchester OWL Syntax.
 * Axiom level OWLObjects are rendered in Manchester "style"
 */
public class OWLObjectRendererImpl implements OWLObjectRenderer {

    private final OWLModelManager mngr;

    private org.semanticweb.owl.io.OWLObjectRenderer delegate;


    public OWLObjectRendererImpl(OWLModelManager mngr) {
        this.mngr = mngr;
        delegate = new ManchesterOWLSyntaxOWLObjectRendererImpl();
        delegate.setShortFormProvider(new ShortFormProvider(){
            public String getShortForm(OWLEntity owlEntity) {
                return OWLObjectRendererImpl.this.mngr.getRendering(owlEntity);
            }

            public void dispose() {
                // do nothing
            }
        });
    }


    public String render(OWLObject object) {
        return delegate.render(object);
    }
}
