package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;


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

    private org.semanticweb.owlapi.io.OWLObjectRenderer delegate;

    private OntologyIRIShortFormProvider ontURISFP;


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

        ontURISFP = new OntologyIRIShortFormProvider();
    }


    public String render(OWLObject object) {
        if (object instanceof OWLOntology){
            return renderOntology((OWLOntology) object);
        }
        return delegate.render(object);
    }


    private String renderOntology(OWLOntology ontology) {
        if (ontology.isAnonymous()){
            return ontology.getOntologyID().toString();
        }

        // shows the version uri or the ont uri if there is no version
        IRI iri = ontology.getOntologyID().getDefaultDocumentIRI();
        return ontURISFP.getShortForm(iri);
    }
}
