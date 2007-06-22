package org.protege.editor.owl.model.inference;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owl.inference.MonitorableOWLReasoner;
import org.semanticweb.owl.inference.OWLReasoner;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyManager;
import org.semanticweb.owl.model.OWLRuntimeException;
import org.semanticweb.owl.util.NullProgressMonitor;
import org.semanticweb.owl.util.ProgressMonitor;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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

    public static boolean AUTO_RELOAD_ON_ACTIVE_ONTOLOGY_CHANGE = false;

    public static boolean AUTO_CLASSIFY_ON_ACTIVE_ONTOLOGY_CHANGE = false;

    private static Logger logger = Logger.getLogger(OWLReasonerManager.class.getName());

    private OWLModelManager owlModelManager;

    private Set<ProtegeOWLReasonerFactory> reasonerFactories;

    private ProtegeOWLReasonerFactory currentReasonerFactory;

    private OWLReasoner currentReasoner;

    public static final String DEFAULT_REASONER_ID = "NoOpReasoner";

    private ProgressMonitor reasonerProgressMonitor;

    private boolean reload;


    public OWLReasonerManagerImpl(OWLModelManager owlModelManager) {
        this.owlModelManager = owlModelManager;
        reasonerFactories = new HashSet<ProtegeOWLReasonerFactory>();
        reasonerProgressMonitor = new NullProgressMonitor();
        installFactories();
        owlModelManager.addListener(this);
        reload = true;
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
        try {
            // The ontology closure is potentially wrong now. Clear
            // all ontologies.
            currentReasoner.clearOntologies();
            reload = true;
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
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
        try {
            for (ProtegeOWLReasonerFactory reasonerFactory : reasonerFactories) {
                if (reasonerFactory.getReasonerId().equals(id)) {
                    currentReasonerFactory = reasonerFactory;
                    if (currentReasoner != null) {
                        currentReasoner.dispose();
                    }
                    currentReasoner = currentReasonerFactory.createReasoner(owlModelManager.getOWLOntologyManager());
                    if (currentReasoner instanceof MonitorableOWLReasoner) {
                        MonitorableOWLReasoner monReasoner = (MonitorableOWLReasoner) currentReasoner;
                        monReasoner.setProgressMonitor(reasonerProgressMonitor);
                    }
                    reload = true;
                    owlModelManager.fireEvent(EventType.REASONER_CHANGED);
                    classifyAsynchronously();
                    return;
                }
            }
            throw new RuntimeException("Unknown reasoner ID");
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
        final OWLReasoner r = currentReasoner;
        currentReasoner = new NoOpReasoner(owlModelManager.getOWLOntologyManager());
        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    if (reload) {
                        loadOntologies(r);
                    }
                    r.classify();
                    currentReasoner = r;
                    fireReclassified();
                }
                catch (OWLReasonerException e) {
                    throw new OWLRuntimeException(e);
                }
            }
        });
        t.start();
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
