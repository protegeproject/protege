package org.protege.editor.owl.ui.frame;

import org.apache.log4j.Logger;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntologyManager;

import java.util.ArrayList;
import java.util.List;
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
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public abstract class AbstractOWLFrame<R extends OWLObject> implements OWLFrame<R> {

    private static final Logger logger = Logger.getLogger(AbstractOWLFrame.class);

    private R rootObject;

    private OWLOntologyManager owlOntologyManager;

    private List<OWLFrameListener> listeners;

    private List<OWLFrameSection> sections;


    public AbstractOWLFrame(OWLOntologyManager owlOntologyManager) {
        this.owlOntologyManager = owlOntologyManager;
        listeners = new ArrayList<OWLFrameListener>();
        sections = new ArrayList<OWLFrameSection>();
    }


    protected void addSection(OWLFrameSection<? extends Object, ? extends Object, ? extends Object> section) {
        sections.add(section);
    }


    public void dispose() {
        for (OWLFrameSection section : sections) {
            section.dispose();
        }
    }


    /**
     * Gets the sections within this frame.
     */
    public List<OWLFrameSection> getFrameSections() {
        return sections;
    }


    public R getRootObject() {
        return rootObject;
    }


    protected OWLOntologyManager getManager() {
        return owlOntologyManager;
    }


    public void setRootObject(R rootObject) {
        this.rootObject = rootObject;
        refill();
        fireContentChanged();
    }


    protected void refill() {
        for (OWLFrameSection<R, ? extends Object, ? extends Object> section : getFrameSections()) {
            try {
                section.setRootObject(rootObject);
            }
            catch (Exception ex) {
                logger.error(ex);
            }
        }
    }


    public void addFrameListener(OWLFrameListener listener) {
        listeners.add(listener);
    }


    public void removeFrameListener(OWLFrameListener listener) {
        listeners.remove(listener);
    }


    public void fireContentChanged() {
        for (OWLFrameListener listener : listeners) {
            try {
                listener.frameContentChanged();
            }
            catch (Exception e) {
                logger.error(e);
            }
        }
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getRootObject());
        sb.append("\n\n");

        for (OWLFrameSection sec : getFrameSections()) {
            sb.append(sec.toString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
