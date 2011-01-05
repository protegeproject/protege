package org.protege.editor.owl.ui.renderer;

import org.protege.editor.core.Disposable;
import org.protege.editor.core.plugin.ProtegePluginInstance;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.util.ShortFormProvider;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public interface OWLModelManagerEntityRenderer extends ShortFormProvider, ProtegePluginInstance, Disposable {

    void setup(OWLModelManager owlModelManager);

    void initialise();

    String render(OWLEntity entity);
    
    String render(IRI iri);
    
    void ontologiesChanged();
    
    boolean isConfigurable();
    
    boolean configure(OWLEditorKit eKit);

    void addListener(OWLEntityRendererListener listener);

    void removeListener(OWLEntityRendererListener listener);
}
