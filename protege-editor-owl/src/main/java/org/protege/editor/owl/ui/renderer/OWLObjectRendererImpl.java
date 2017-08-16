package org.protege.editor.owl.ui.renderer;

import com.google.common.base.Optional;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OntologyIRIShortFormProvider;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;
import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxObjectRenderer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Apr 2, 2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>

 * A renderer that renders objects using the Manchester OWL Syntax.
 * Axiom level OWLObjects are rendered in Manchester "style"
 */
public class OWLObjectRendererImpl implements OWLObjectRenderer {

    private final OWLModelManager mngr;
    private WriterDelegate writerDelegate;
    private ManchesterOWLSyntaxObjectRenderer delegate;

    private OntologyIRIShortFormProvider ontURISFP;


    public OWLObjectRendererImpl(OWLModelManager mngr) {
        this.mngr = mngr;
        writerDelegate = new WriterDelegate();
        delegate = new PatchedManchesterOWLSyntaxObjectRenderer(writerDelegate, new ShortFormProvider(){
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
        writerDelegate.reset();
        object.accept(delegate);
        return writerDelegate.toString();
    }


    private String renderOntology(OWLOntology ontology) {
        if (ontology.isAnonymous()){
            return ontology.getOntologyID().toString();
        }

        // shows the version uri or the ont uri if there is no version
        Optional<IRI> iri = ontology.getOntologyID().getDefaultDocumentIRI();
        if (iri.isPresent()) {
            return ontURISFP.getShortForm(iri.get());
        }
        else {
            return "Anonymous Ontology";
        }
    }
    
    private class PatchedManchesterOWLSyntaxObjectRenderer extends ManchesterOWLSyntaxObjectRenderer {
        public PatchedManchesterOWLSyntaxObjectRenderer(Writer writer, ShortFormProvider entityShortFormProvider) {
            super(writer, entityShortFormProvider);
        }
        
        @Override
        public void visit(IRI iri) {
            write(mngr.getOWLEntityRenderer().render(iri));
        }
        
        
        /*
         * Workaround for owlapi feature request 2896097.  Remove this fix when 
         * the simple rule renderer and parser is implemented.
         */
        @Override
        public void visit(SWRLVariable node) {
            write("?");
            write(mngr.getOWLEntityRenderer().render(node.getIRI()));
        }
        
        /*
         * Workaround for owlapi feature request 2896097.  Remove this fix when 
         * the simple rule renderer and parser is implemented.
         */
        @Override
        public void visit(SWRLBuiltInAtom node) {
            SWRLBuiltInsVocabulary vocabulary = SWRLBuiltInsVocabulary.getBuiltIn(node.getPredicate());
            // Workaround for https://github.com/protegeproject/protege/issues/660
            // Render using short name
            if(vocabulary != null) {
                write(vocabulary.getShortForm());
            }
            else {
                write(mngr.getOWLEntityRenderer().render(node.getPredicate()));
            }
            write("(");
            for (Iterator<SWRLDArgument> it = node.getArguments().iterator(); it.hasNext();) {
                it.next().accept(this);
                if (it.hasNext()) {
                    write(", ");
                }
            }
            write(")");
        }
    }
    
    private class WriterDelegate extends Writer {

        private StringWriter delegate;

        private void reset() {
            delegate = new StringWriter();
        }


        public String toString() {
            return delegate.getBuffer().toString();
        }


        public void close() throws IOException {
            delegate.close();
        }


        public void flush() throws IOException {
            delegate.flush();
        }


        public void write(char cbuf[], int off, int len) throws IOException {
            delegate.write(cbuf, off, len);
        }
    }
    
}
