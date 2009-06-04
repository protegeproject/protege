package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.IRI;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.util.OntologyURIShortFormProvider;
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

    private OntologyURIShortFormProvider ontURISFP;


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

        ontURISFP = new OntologyURIShortFormProvider();
    }


    public String render(OWLObject object) {
        if (object instanceof OWLOntology){
            return renderOntology((OWLOntology) object);
        }
        return delegate.render(object);
    }


    private String renderOntology(OWLOntology ontology) {
        if (ontology.isAnonymous()){
            return "<ANONYMOUS ONTOLOGY>";
        }

        // shows the version uri or the ont uri if there is no version
        IRI iri = ontology.getOntologyID().getDefaultDocumentIRI();
        return ontURISFP.getShortForm(iri.toURI());
    }
}
