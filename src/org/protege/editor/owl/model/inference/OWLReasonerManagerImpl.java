package org.protege.editor.owl.model.inference;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.inference.MonitorableOWLReasoner;
import org.semanticweb.owlapi.inference.OWLReasoner;
import org.semanticweb.owlapi.inference.OWLReasonerException;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLRuntimeException;
import org.semanticweb.owlapi.util.NullProgressMonitor;
import org.semanticweb.owlapi.util.ProgressMonitor;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLReasonerManagerImpl implements OWLReasonerManager, OWLModelManagerListener {
    private static Logger logger = Logger.getLogger(OWLReasonerManager.class);

    public static boolean AUTO_RELOAD_ON_ACTIVE_ONTOLOGY_CHANGE = false;

    public static boolean AUTO_CLASSIFY_ON_ACTIVE_ONTOLOGY_CHANGE = false;

    private OWLModelManager owlModelManager;

    private Set<ProtegeOWLReasonerFactory> reasonerFactories;

    private ProtegeOWLReasonerFactory currentReasonerFactory;

    private OWLReasoner currentReasoner;

    public static final String DEFAULT_REASONER_ID = "NoOpReasoner";

    private ProgressMonitor reasonerProgressMonitor;

    private boolean reload;

    private OWLReasonerExceptionHandler exceptionHandler;

    private Thread currentReasonerThread;

    private OWLReasoner runningReasoner;


    public OWLReasonerManagerImpl(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
        reasonerFactories = new HashSet<ProtegeOWLReasonerFactory>();
        reasonerProgressMonitor = new NullProgressMonitor();
        installFactories();
        owlModelManager.addListener(this);
        reload = true;
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
        try {
            owlModelManager.removeListener(this);
            currentReasoner.dispose();
            currentReasoner = null;
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public void handleChange(OWLModelManagerChangeEvent event) {
        // If the active ontology changes then we essentially need to clear
        // the reasoner.
        if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
            handleActiveOntologyChange();
        }
    }


    private void handleActiveOntologyChange() {
        reload = true;

        try {
            // The ontology closure is potentially wrong now. Clear
            // all ontologies.
            currentReasoner.clearOntologies();
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public String getCurrentReasonerName() {
        return currentReasonerFactory.getReasonerName();
    }


    public ProtegeOWLReasonerFactory getCurrentReasonerFactory() {
        return currentReasonerFactory;
    }


    /**
     * Loads the active ontologies into the current reasoner
     */
    private void loadOntologies(OWLReasoner reasoner) throws OWLReasonerException {
        reasoner.clearOntologies();
        Set<OWLOntology> ontologies = new HashSet<OWLOntology>(owlModelManager.getActiveOntologies());
        reasoner.loadOntologies(ontologies);
        reload = false;
    }


    public void setReasonerProgressMonitor(ProgressMonitor progressMonitor) {
        this.reasonerProgressMonitor = progressMonitor;
        if (currentReasoner instanceof MonitorableOWLReasoner) {
            ((MonitorableOWLReasoner) currentReasoner).setProgressMonitor(progressMonitor);
        }
    }


    public Set<ProtegeOWLReasonerFactory> getInstalledReasonerFactories() {
        return reasonerFactories;
    }


    private void installFactories() {

        ProtegeOWLReasonerFactoryPluginLoader loader = new ProtegeOWLReasonerFactoryPluginLoader(owlModelManager);
        for (ProtegeOWLReasonerFactoryPlugin plugin : loader.getPlugins()) {
            try {
                ProtegeOWLReasonerFactory factory = plugin.newInstance();
                factory.initialise();
                reasonerFactories.add(factory);
            }
            catch (ClassNotFoundException e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
            catch (IllegalAccessException e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
            catch (InstantiationException e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
            catch (Exception e) {
                ProtegeApplication.getErrorLog().logError(e);
            }
        }
        setCurrentReasonerFactoryId(DEFAULT_REASONER_ID);
    }


    public String getCurrentReasonerFactoryId() {
        return currentReasonerFactory.getReasonerId();
    }


    public void setCurrentReasonerFactoryId(String id) {
        for (ProtegeOWLReasonerFactory reasonerFactory : reasonerFactories) {
            if (reasonerFactory.getReasonerId().equals(id)) {
                currentReasonerFactory = reasonerFactory;
                createCurrentReasoner();
                owlModelManager.fireEvent(EventType.REASONER_CHANGED);
                classifyAsynchronously();
                return;
            }
        }
        throw new RuntimeException("Unknown reasoner ID");
    }


    private void createCurrentReasoner() {
        try {
            if (currentReasoner != null) {
                currentReasoner.dispose();
            }
            currentReasoner = currentReasonerFactory.createReasoner(owlModelManager.getOWLOntologyManager());
            if (currentReasoner instanceof MonitorableOWLReasoner) {
                MonitorableOWLReasoner monReasoner = (MonitorableOWLReasoner) currentReasoner;
                monReasoner.setProgressMonitor(reasonerProgressMonitor);
            }
            reload = true;
        }
        catch (OWLException e) {
            throw new OWLRuntimeException(e);
        }
    }


    public OWLReasoner getCurrentReasoner() {
        if (currentReasoner == null) {
            throw new OWLRuntimeException("Reasoner manager has been disposed of!");
        }
        return currentReasoner;
    }


    public OWLReasoner createReasoner(OWLOntologyManager owlOntologyManager) {
        try {
            return currentReasonerFactory.createReasoner(owlOntologyManager);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Classifies the current active ontologies.
     */
    public void classifyAsynchronously() {
        owlModelManager.fireEvent(EventType.ABOUT_TO_CLASSIFY);
        runningReasoner = currentReasoner;
        currentReasoner = new NoOpReasoner(owlModelManager.getOWLOntologyManager());
        currentReasonerThread = new Thread(new Runnable() {
            public void run() {
                try {
                    if (reload) {
                        loadOntologies(runningReasoner);
                    }
                    long start = System.currentTimeMillis();
                    runningReasoner.classify();
                    if (runningReasoner.isClassified()){
                        String s = currentReasonerFactory.getReasonerName() + " classified in " + (System.currentTimeMillis()-start) + "ms";
                        reasonerProgressMonitor.setProgress(s, 100, 100);
                        logger.info(s);
                        currentReasoner = runningReasoner;
                        fireReclassified();
                    }
                }
                catch (ReasonerException e) {
                    exceptionHandler.handle(e);
                }
                catch (Exception e) {
                    exceptionHandler.handle(new ReasonerException(e));
                }
                finally{
                    runningReasoner = null;
                }
            }
        }, "Classify Thread");
        currentReasonerThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            public void uncaughtException(Thread thread, Throwable throwable) {
                logger.info("Classification interrupted");
            }
        });
        currentReasonerThread.start();
    }


    public void killCurrentClassification() {
        currentReasonerThread.stop();
        currentReasonerThread = null;
        if (runningReasoner != null){
            try {
                runningReasoner.dispose();
                runningReasoner = null;
            }
            catch (OWLReasonerException e) {
                exceptionHandler.handle(new ReasonerException(e));
            }
        }
        createCurrentReasoner();
        reasonerProgressMonitor.setFinished();
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
}
