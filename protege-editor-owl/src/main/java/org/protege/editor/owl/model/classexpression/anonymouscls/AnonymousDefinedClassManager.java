package org.protege.editor.owl.model.classexpression.anonymouscls;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.entity.AbstractIDGenerator;
import org.protege.editor.owl.model.entity.AutoIDException;
import org.protege.editor.owl.model.entity.OWLEntityCreationSet;
import org.protege.editor.owl.model.entity.Revertable;
import org.protege.editor.owl.model.io.IOListener;
import org.protege.editor.owl.model.io.IOListenerEvent;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 24, 2008<br><br>
 */
public class AnonymousDefinedClassManager implements Disposable {

    public static final String ID = AnonymousDefinedClassManager.class.getName();

    private static final String DEFAULT_ANON_CLASS_URI_PREFIX = "http://www.co-ode.org/ontologies/owlx/anon#";

    private static final URI DEFAULT_ANON_CLASS_ANNOTATION_URI = URI.create(DEFAULT_ANON_CLASS_URI_PREFIX + "anonClass");

    private OWLModelManager mngr;

    private PseudoRandomAutoIDGenerator idGen;

    private ADCRewriter adcRewriter;

    private ADCFactory adcFactory;

    private IOListener ioListener = new IOListener(){

        public void beforeSave(IOListenerEvent event) {
            //To change body of implemented methods use File | Settings | File Templates.
        }


        public void afterSave(IOListenerEvent event) {
            // do nothing
        }


        public void beforeLoad(IOListenerEvent event) {
            // do nothing
        }


        public void afterLoad(IOListenerEvent event) {
            OWLOntologyID ontologyID = event.getOntologyID();
            OWLOntology ont = mngr.getOWLOntologyManager().getOntology(ontologyID);
            adcFactory.getADCsForOntology(ont);
        }
    };



    public AnonymousDefinedClassManager(OWLModelManager mngr) {
        this.mngr = mngr;

        idGen = new PseudoRandomAutoIDGenerator();

        adcRewriter = new ADCRewriter(this, mngr.getOWLOntologyManager());

        adcFactory = new ADCFactory(this);

        mngr.addIOListener(ioListener);
    }


    public boolean isAnonymous(OWLClass cls){
        if(cls.getIRI().toString().startsWith(DEFAULT_ANON_CLASS_URI_PREFIX)){
            for (OWLOntology ont : mngr.getActiveOntologies()){
                if (ont.containsClassInSignature(cls.getIRI())){
                    return true;
                }
            }
        }
        return false;
    }


    public OWLEntityCreationSet<OWLClass> createAnonymousClass(OWLOntology ont, OWLClassExpression descr){

        OWLClass anonCls = mngr.getOWLDataFactory().getOWLClass(getNextID());

        List<OWLOntologyChange> changes = new ArrayList<>();
        changes.add(new AddAxiom(ont, mngr.getOWLDataFactory().getOWLEquivalentClassesAxiom(anonCls, descr)));

        return new OWLEntityCreationSet<>(anonCls, changes);
    }


    private IRI getNextID() {
        IRI iri;
        do{
            try {
                iri = IRI.create(DEFAULT_ANON_CLASS_URI_PREFIX + idGen.getNextID(OWLClass.class));
            }
            catch (AutoIDException e) {
                throw new RuntimeException(e);
            }
        }
        while (entityExists(iri));
        return iri;
    }


    private boolean entityExists(IRI iri) {
        for (OWLOntology ont : mngr.getActiveOntologies()){
            if (ont.containsClassInSignature(iri)){
                return true;
            }
        }
        return false;
    }


    public OWLClassExpression getExpression(OWLClass cls) {
        for (OWLClassExpression descr : EntitySearcher.getEquivalentClasses(cls, mngr.getActiveOntologies().stream()).toArray(OWLClassExpression[]::new)){
            if (!descr.equals(cls)){
                return descr;
            }
        }
        LoggerFactory.getLogger(this.getClass()).error("Malformed Anonymous Defined Class: " + cls);
        return null;
    }


    public ADCRewriter getChangeRewriter(){
        return adcRewriter;
    }

    public void dispose() throws Exception {
        mngr = null;
    }


    public URI getURI() {
        return DEFAULT_ANON_CLASS_ANNOTATION_URI;
    }
    
    public class PseudoRandomAutoIDGenerator extends AbstractIDGenerator implements Revertable {

        private long nextId = System.nanoTime();

        private Stack<Long> checkpoints = new Stack<>();

        protected long getRawID(Class<? extends OWLEntity> type) throws AutoIDException {
            long id = nextId;
            nextId = System.nanoTime();
            return id;
        }


        public void checkpoint() {
            checkpoints.push(nextId);
        }


        public void revert() {
            nextId = checkpoints.pop();
        }

    }
}
