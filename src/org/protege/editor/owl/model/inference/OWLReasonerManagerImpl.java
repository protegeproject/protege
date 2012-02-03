package org.protege.editor.owl.model.inference;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.ui.explanation.io.InconsistentOntologyManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.BufferingMode;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.reasoner.InferenceType;
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
    
    private ReasonerPreferences preferences;
    
    private Set<ProtegeOWLReasonerInfo> reasonerFactories;

    private ProtegeOWLReasonerInfo currentReasonerFactory;

    private Map<OWLOntology, OWLReasoner> reasonerMap = new HashMap<OWLOntology, OWLReasoner>();
    
    private OWLReasoner runningReasoner;
    private boolean classificationInProgress = false;
    
    private ReasonerProgressMonitor reasonerProgressMonitor;
    private OWLReasonerExceptionHandler exceptionHandler;
    
    private List<ReasonerFilter> reasonerFilters = new ArrayList<ReasonerFilter>();
    

    public OWLReasonerManagerImpl(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
        preferences = new ReasonerPreferences();
        preferences.load();
        reasonerFactories = new HashSet<ProtegeOWLReasonerInfo>();
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
        if (preferences != null) {
            preferences.save();
        }
        clearAndDisposeReasoners();
    }
    
    private void clearAndDisposeReasoners() {
        for (OWLReasoner reasoner : reasonerMap.values()) {
            if (reasoner != null) {
                try {
                    reasoner.dispose();
                }
                catch (Throwable t) {
                    ProtegeApplication.getErrorLog().logError(t);
                }
            }
        }
        reasonerMap.clear();
    }


    public String getCurrentReasonerName() {
        return getCurrentReasoner().getReasonerName();
    }


    public ProtegeOWLReasonerInfo getCurrentReasonerFactory() {
        if (currentReasonerFactory == null) {
            currentReasonerFactory = new NoOpReasonerInfo();
        }
        return currentReasonerFactory;
    }


    public void setReasonerProgressMonitor(ReasonerProgressMonitor progressMonitor) {
        this.reasonerProgressMonitor = progressMonitor;
    }
    
    public ReasonerProgressMonitor getReasonerProgressMonitor() {
		return reasonerProgressMonitor;
	}


    public Set<ProtegeOWLReasonerInfo> getInstalledReasonerFactories() {
        return reasonerFactories;
    }


    private void installFactories() {        
        ProtegeOWLReasonerPluginLoader loader = new ProtegeOWLReasonerPluginLoader(owlModelManager);
        addReasonerFactories(loader.getPlugins());
        setCurrentReasonerFactoryId(preferences.getDefaultReasonerId());
    }

    public void addReasonerFactories(Set<ProtegeOWLReasonerPlugin> plugins) {
        for (ProtegeOWLReasonerPlugin plugin : plugins) {
            try {
                ProtegeOWLReasonerInfo factory = plugin.newInstance();
                factory.initialise();
                reasonerFactories.add(factory);
            }
            catch (Throwable t) {
                ProtegeApplication.getErrorLog().logError(t);
            }
        }
    }

    public String getCurrentReasonerFactoryId() {
        return getCurrentReasonerFactory().getReasonerId();
    }


    public void setCurrentReasonerFactoryId(String id) {
        if (getCurrentReasonerFactory().getReasonerId().equals(id)) {
            return;
        }
        for (ProtegeOWLReasonerInfo reasonerFactory : reasonerFactories) {
            if (reasonerFactory.getReasonerId().equals(id)) {
                preferences.setDefaultReasonerId(id);
                preferences.save();
                clearAndDisposeReasoners();
                currentReasonerFactory = reasonerFactory;
                owlModelManager.fireEvent(EventType.REASONER_CHANGED);
                return;
            }
        }
        logger.warn("Reasoner with id " + id + " not found");
        preferences.setDefaultReasonerId(NoOpReasonerInfo.NULL_REASONER_ID);
        preferences.save();
    }


    public OWLReasoner getCurrentReasoner() {
        OWLReasoner reasoner;
        OWLOntology activeOntology = owlModelManager.getActiveOntology();
        synchronized (reasonerMap)  {
            reasoner = reasonerMap.get(activeOntology);
        }
        if (reasoner == null) {
            reasoner = new NoOpReasoner(activeOntology);
            synchronized (reasonerMap)  {
                reasonerMap.put(activeOntology, reasoner);
            }
        }
        return reasoner;
    }
    
    public void killCurrentReasoner() {
    	OWLReasoner reasoner = getCurrentReasoner();
    	if (!(reasoner instanceof NoOpReasoner)) {
        	try {
        		reasoner.dispose();
        	}
        	catch (Exception ex) {
        		ProtegeApplication.getErrorLog().logError(ex);
        	}
            synchronized (reasonerMap)  {
            	reasonerMap.put(owlModelManager.getActiveOntology(), null);
            }
    	}
    }
    
    public boolean isClassificationInProgress() {
        synchronized (reasonerMap) {
            return classificationInProgress;
        }
    }
    
    public boolean isClassified() {
        synchronized (reasonerMap) {
            OWLReasoner reasoner = getCurrentReasoner();
            return !(reasoner instanceof NoOpReasoner) && 
            			(reasoner.getPendingChanges() == null || reasoner.getPendingChanges().isEmpty());
        }
    }
    
    public ReasonerStatus getReasonerStatus() {
    	synchronized (reasonerMap) {
    		if (classificationInProgress) {
    			return ReasonerStatus.INITIALIZATION_IN_PROGRESS;
    		}
    		if (currentReasonerFactory.getReasonerFactory() instanceof NoOpReasonerFactory) {
    			return ReasonerStatus.NO_REASONER_FACTORY_CHOSEN;
    		}
    		else {
                OWLReasoner reasoner = getCurrentReasoner();
                try {
                	if (reasoner instanceof NoOpReasoner) {
                		return ReasonerStatus.REASONER_NOT_INITIALIZED;
                	}
                	else if (!reasoner.isConsistent()) {
                		return ReasonerStatus.INCONSISTENT;                	
                	}
                	else if (reasoner.getPendingChanges().isEmpty()) {
                		return ReasonerStatus.INITIALIZED;
                	}
                	else {
                		return ReasonerStatus.OUT_OF_SYNC;
                	}
                }
                catch (Exception e) {
                	killCurrentReasoner();
                	logger.warn("Reasoner died.");
                	throw new ReasonerDiedException(e);
                }
    		}
    	}
    }

    /**
     * Classifies the current active ontologies.
     */
    public boolean classifyAsynchronously(Set<InferenceType> precompute) {
        if (getCurrentReasonerFactory() instanceof NoOpReasonerInfo) {
            return true;
        }
        final OWLOntology currentOntology = owlModelManager.getActiveOntology();
        synchronized (reasonerMap) {
            if (classificationInProgress) {
                return false;
            }
            runningReasoner = reasonerMap.get(currentOntology);
            reasonerMap.put(currentOntology, new NoOpReasoner(currentOntology));
            classificationInProgress = true;
        }
        owlModelManager.fireEvent(EventType.ABOUT_TO_CLASSIFY);
        Thread currentReasonerThread = new Thread(new ClassificationRunner(currentOntology, precompute), "Classification Thread");
        currentReasonerThread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            public void uncaughtException(Thread thread, Throwable throwable) {
                ProtegeApplication.getErrorLog().logError(throwable);
            	try {
            		if (getReasonerStatus() != ReasonerStatus.REASONER_NOT_INITIALIZED) {
            			exceptionHandler.handle(throwable);
            		}
            	}
            	catch (ReasonerDiedException died) {
            		ReasonerUtilities.warnThatReasonerDied(null, died);
            	}
            }
        });
        currentReasonerThread.start();
        return true;
    }


    public void killCurrentClassification() {
        synchronized (reasonerMap) {
            if (runningReasoner != null){
                runningReasoner.interrupt();
            }
        }
    }
    
    public ReasonerPreferences getReasonerPreferences() {
        return preferences;
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
    
    public void addReasonerFilter(ReasonerFilter filter) {
        synchronized (reasonerMap) {
            reasonerFilters.add(filter);
        }
    }
    
    private OWLOntology applyReasonerFilters(OWLOntology ontology) {
        synchronized (reasonerMap) {
            for (ReasonerFilter filter : reasonerFilters) {
                ontology = filter.getFilteredOntology(ontology);
            }
        }
        return ontology;
    }
    
    private class ClassificationRunner implements Runnable {
        private OWLOntology ontology;
        private Set<InferenceType> precompute;
        private ProtegeOWLReasonerInfo currentReasonerFactory;
        
        public ClassificationRunner(OWLOntology ontology, Set<InferenceType> precompute) {
            this.ontology = ontology;
            this.precompute = EnumSet.noneOf(InferenceType.class);
            this.precompute.addAll(precompute);
            currentReasonerFactory = getCurrentReasonerFactory();
        }
        
        public void run() {
        	boolean inconsistencyFound = false;
        	boolean reasonerChanged = false;
            try {
            	long start = System.currentTimeMillis();
                reasonerChanged = ensureRunningReasonerInitialized();
                if (runningReasoner != null) {
                	precompute();
                	logger.info(currentReasonerFactory.getReasonerName() + " classified in " + (System.currentTimeMillis()-start) + "ms");
                }
            }
            catch (InconsistentOntologyException ioe) {
            	inconsistencyFound = true;
            }
            finally{
            	if (runningReasoner != null) {
            		synchronized (runningReasoner) {
            			reasonerFilters.clear();
            		}
            	}
            	installRunningReasoner(inconsistencyFound, reasonerChanged);
            }
        }
        
        public boolean ensureRunningReasonerInitialized() {
        	boolean reasonerChanged = false;
            if (runningReasoner instanceof NoOpReasoner) {
                runningReasoner = null;
            }
            if (runningReasoner != null && !runningReasoner.getPendingChanges().isEmpty()) {
                if (runningReasoner.getBufferingMode() == null 
                        || runningReasoner.getBufferingMode() == BufferingMode.NON_BUFFERING) {
                    runningReasoner.dispose();
                    runningReasoner = null;
                }
                else {
                    runningReasoner.flush();
                }
            }
            if (runningReasoner == null) {
            	runningReasoner = ReasonerUtilities.createReasoner(applyReasonerFilters(ontology), currentReasonerFactory, reasonerProgressMonitor);
            	reasonerChanged = true;
            }
            if (runningReasoner == null) {
            	classificationInProgress = false;
            	ProtegeApplication.getErrorLog().logError(new Exception("Reasoner Initialization failed (ontology is probably inconsistent)"));
            }
            return reasonerChanged;
        }
        
        public void precompute() {
            Set<InferenceType> precomputeThisRun  = EnumSet.noneOf(InferenceType.class);
            precomputeThisRun.addAll(precompute);
            precomputeThisRun.retainAll(runningReasoner.getPrecomputableInferenceTypes());
            if (!precomputeThisRun.isEmpty()) {
            	logger.info("Initializing the reasoner by performing the following steps:");
            	for (InferenceType type : precompute) {
            		logger.info("\t" + type);
            	}
                runningReasoner.precomputeInferences(precomputeThisRun.toArray(new InferenceType[precomputeThisRun.size()]));
            }
        }
        
        public void installRunningReasoner(boolean inconsistencyFound, boolean reasonerChanged) {
            synchronized (reasonerMap) {
                reasonerMap.put(ontology, runningReasoner);
                runningReasoner = null;
                classificationInProgress = false;
            }
            if (reasonerChanged) {
                SwingUtilities.invokeLater(new Runnable() {
                	public void run() {
                        owlModelManager.fireEvent(EventType.REASONER_CHANGED);
                	}
                });
            }
            fireReclassified();
            if (inconsistencyFound) {
            	InconsistentOntologyManager.get(owlModelManager).explain();
            }
        }
    }
    
}
