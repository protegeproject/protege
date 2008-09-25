package org.protege.editor.owl.model.io;

import org.protege.editor.core.Disposable;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owl.model.OWLOntology;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.util.*;
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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 23, 2008<br><br>
 *
 * Checks every t seconds to see if the ontology source files have changed since loading
 */
public class OntologySourcesManager extends IOListener implements Disposable {

    public static final String ID = OntologySourcesManager.class.getName();

    private Map<URI, Long> timestamps = new HashMap<URI, Long>();

    private Timer timer;

    private OWLModelManager mngr;

    private List<OntologySourcesListener> listeners = new ArrayList<OntologySourcesListener>();

    private ActionListener timerAction = new ActionListener(){
        public void actionPerformed(ActionEvent event) {
            checkSources();
        }
    };


    public OntologySourcesManager(OWLModelManager mngr) {
        this.mngr = mngr;
        mngr.addIOListener(this);
    }


    public void setTimer(int millisecs){
        if (timer != null){
            stopTimer();
        }
        if (millisecs > 0){
        timer = new Timer(millisecs, timerAction);
        startTimer();
        }
        else{
            timer = null;
        }
    }

    public void checkSources() {
        stopTimer();
        Set<OWLOntology> changedOntologies = getChangedOntologies();
        if (!changedOntologies.isEmpty()){
            OntologySourcesListener.OntologySourcesChangeEvent event = new OntologySourcesListener.OntologySourcesChangeEvent(getChangedOntologies());
            for (OntologySourcesListener l : listeners){
                l.ontologySourcesChanged(event);
            }
        }
        startTimer();
    }


    protected Set<OWLOntology> getChangedOntologies() {
        Set<OWLOntology> changedOntologies = new HashSet<OWLOntology>();
        for (OWLOntology ont : mngr.getOntologies()){
            URI uri = mngr.getOntologyPhysicalURI(ont);
            final long currentTimestamp = getTimestamp(uri);
            if (timestamps.get(uri) != null && timestamps.get(uri) < currentTimestamp){
                changedOntologies.add(ont);
            }
        }
        return changedOntologies;
    }


    private void update(URI uri) {
        stopTimer();
        long timestamp = getTimestamp(uri);
        if (timestamp >= 0){
            timestamps.put(uri, timestamp);
        }
        startTimer();
    }


    private long getTimestamp(URI uri){
        if (uri.getScheme().equals("file")){
            File file = new File(uri);
            if (file.exists()){
                return file.lastModified();
            }
        }
        return -1;
    }


    public void beforeSave(IOListenerEvent event) {
        // do nothing
    }


    public void afterSave(IOListenerEvent event) {
        final URI uri = event.getPhysicalURI();
        update(uri);
    }


    public void beforeLoad(IOListenerEvent event) {
        // do nothing
    }


    public void afterLoad(IOListenerEvent event) {
        final URI uri = event.getPhysicalURI();
        update(uri);
    }


    public void dispose() {
        stopTimer();
        timestamps.clear();
    }

    public void addListener(OntologySourcesListener l){
        listeners.add(l);
    }

    public void removeListener(OntologySourcesListener l){
        listeners.remove(l);
    }


    public void ignoreUpdates(Set<OWLOntology> onts) {
        stopTimer();
        for (OWLOntology ont : onts){
            URI uri = mngr.getOntologyPhysicalURI(ont);
            long timestamp = getTimestamp(uri);
            if (timestamp >= 0){
                timestamps.put(uri, timestamp);
            }
        }
        startTimer();
    }


    private void startTimer(){
        if (timer != null){
            timer.start();
        }
    }

    private void stopTimer(){
        if (timer != null){
            timer.stop();
        }
    }
}
