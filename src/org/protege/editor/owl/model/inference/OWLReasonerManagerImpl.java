package org.protege.editor.owl.model.inference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.NullReasonerProgressMonitor;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLReasonerManagerImpl implements OWLReasonerManager {
    private static Logger logger = Logger.getLogger(OWLReasonerManager.class);
    
    private OWLModelManager owlModelManager;
    
    private Set<ProtegeOWLReasonerFactory> reasonerFactories;

    private ProtegeOWLReasonerFactory currentReasonerFactory;

    private Map<OWLOntology, OWLReasoner> currentReasonerMap = new HashMap<OWLOntology, OWLReasoner>();
    private OWLReasoner runningReasoner;
    private boolean classificationInProgress = false;
    
    public static final String NULL_REASONER_ID = "org.protege.editor.owl.NoOpReasoner";

    private ReasonerProgressMonitor reasonerProgressMonitor;


    private OWLReasonerExceptionHandler exceptionHandler;

    public OWLReasonerManagerImpl(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
        reasonerFactories = new HashSet<ProtegeOWLReasonerFactory>();
        reasonerProgressMonitor = new NullReasonerProgressMonitor();
        installFactories();
        exceptionHandler = new DefaultOWLReasonerExceptionHandler();
    }


    public void setReasonerExceptionHandler(OWLReasonerExceptionHandler handler) {
        if(handler != null) {
            exceptionHandler = handler;
        }
        else {
            exceptionHandler = new DefaultOWLReasonerExceptionHandler();
        }
    }


    public void dispose() {
        clearAndDisposeReasoners();
    }

    private void clearAndDisposeReasoners() {
        for (OWLReasoner reasoner : currentReasonerMap.values()) {
            if (reasoner != null) {
                reasoner.dispose();
            }
        }
        currentReasonerMap.clear();
    }




    public String getCurrentReasonerName() {
        return currentReasonerFactory.getReasonerName();
    }


    public ProtegeOWLReasonerFactory getCurrentReasonerFactory() {
        return currentReasonerFactory;
    }


    public void setReasonerProgressMonitor(ReasonerProgressMonitor progressMonitor) {
        this.reasonerProgressMonitor = progressMonitor;
    }
    
    public ReasonerProgressMonitor getReasonerProgressMonitor() {
		return reasonerProgressMonitor;
	}


    public Set<ProtegeOWLReasonerFactory> getInstalledReasonerFactories() {
        return reasonerFactories;
    }


    private void installFactories() {
        
        ProtegeOWLReasonerFactoryPluginLoader loader = new ProtegeOWLReasonerFactoryPluginLoader(owlModelManager);
        addReasonerFactories(loader.getPlugins());
        setCurrentReasonerFactoryId(NULL_REASONER_ID);
    }

    public void addReasonerFactories(Set<ProtegeOWLReasonerFactoryPlugin> plugins) {
        for (ProtegeOWLReasonerFactoryPlugin plugin : plugins) {
            try {
                ProtegeOWLReasonerFactory factory = plugin.newInstance();
                factory.initialise();
                reasonerFactories.add(factory);
            }
            catch (Throwable t) {
                ProtegeApplication.getErrorLog().logError(t);
            }
        }
    }

    public String getCurrentReasonerFactoryId() {
        return currentReasonerFactory.getReasonerId();
    }


    public void setCurrentReasonerFactoryId(String id) {
        if (currentReasonerFactory != null && currentReasonerFactory.getReasonerId().equals(id)) {
            return;
        }
        for (ProtegeOWLReasonerFactory reasonerFactory : reasonerFactories) {
            if (reasonerFactory.getReasonerId().equals(id)) {
                currentReasonerFactory = reasonerFactory;
                clearAndDisposeReasoners();
                owlModelManager.fireEvent(EventType.REASONER_CHANGED);
                classifyAsynchronously();
                return;
            }
        }
        throw new RuntimeException("Unknown reasoner ID");
    }


    public OWLReasoner getCurrentReasoner() {
        OWLReasoner reasoner;
        synchronized (currentReasonerMap)  {
            reasoner = currentReasonerMap.get(owlModelManager.getActiveOntology());
        }
        if (reasoner == null) {
            reasoner = new NoOpReasoner(owlModelManager.getActiveOntology());
        }
        return reasoner;
    }
    
    public boolean isClassificationInProgress() {
        synchronized (currentReasonerMap) {
            return classificationInProgress;
        }
    }
    
    public boolean isClassified() {
        synchronized (currentReasonerMap) {
            OWLReasoner reasoner = getCurrentReasoner();
            return !(reasoner instanceof NoOpReasoner) && 
            			(reasoner.getPendingChanges() == null || reasoner.getPendingChanges().isEmpty());
        }
    }

    /**
     * Classifies the current active ontologies.
     */
    public boolean classifyAsynchronously() {
        if (currentReasonerFactory instanceof NoOpReasonerFactory) {
            return true;
        }
        final OWLOntology currentOntology = owlModelManager.getActiveOntology();
        synchronized (currentReasonerMap) {
            if (classificationInProgress) {
                return false;
            }
            runningReasoner = currentReasonerMap.get(currentOntology);
            currentReasonerMap.put(currentOntology, new NoOpReasoner(currentOntology));
            classificationInProgress = true;
        }
        owlModelManager.fireEvent(EventType.ABOUT_TO_CLASSIFY);
        Thread currentReasonerThread = new Thread(new ClassificationRunner(currentOntology), "Classification Thread");
        currentReasonerThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            public void uncaughtException(Thread thread, Throwable throwable) {
                exceptionHandler.handle(throwable);
                ProtegeApplication.getErrorLog().logError(throwable);
            }
        });
        currentReasonerThread.start();
        return true;
    }


    public void killCurrentClassification() {
        synchronized (currentReasonerMap) {
            if (runningReasoner != null){
                runningReasoner.interrupt();
            }
        }
    }


    /**
     * Fires a reclassify event, ensuring that the event
     * is fired in the event dispatch thread.
     */
    private void fireReclassified() {
        Runnable r = new Runnable() {
            public void run() {
                owlModelManager.fireEvent(EventType.ONTOLOGY_CLASSIFIED);
            }
        };
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        }
        else {
            SwingUtilities.invokeLater(r);
        }
    }
    
    private class ClassificationRunner implements Runnable {
        private OWLOntology ontology;
        
        public ClassificationRunner(OWLOntology ontology) {
            this.ontology = ontology;
        }
        
        public void run() {
            try {
                long start = System.currentTimeMillis();
                if (runningReasoner != null && 
                        (runningReasoner.getBufferingMode() == null 
                                || (runningReasoner.getBufferingMode() == BufferingMode.NON_BUFFERING && !runningReasoner.getPendingChanges().isEmpty()))) {
                    runningReasoner.dispose();
                    runningReasoner = null;
                }
                if (runningReasoner == null) {
                    runningReasoner = currentReasonerFactory.createReasoner(ontology, reasonerProgressMonitor);
                    runningReasoner.prepareReasoner();
                }
                else if (runningReasoner.getBufferingMode() == BufferingMode.BUFFERING) {
                    runningReasoner.flush();
                }

                String s = currentReasonerFactory.getReasonerName() + " classified in " + (System.currentTimeMillis()-start) + "ms";
                logger.info(s);

            }
            finally{
                synchronized (currentReasonerMap) {
                    currentReasonerMap.put(ontology, runningReasoner);
                    runningReasoner = null;
                    classificationInProgress = false;
                }
                fireReclassified();
            }
        }
    }
    
}
