package org.protege.editor.owl.ui.frame;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public abstract class AbstractOWLFrame<R extends Object> implements OWLFrame<R> {

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


    protected void addSection(OWLFrameSection<? extends Object, ? extends Object, ? extends Object> section,
                              int index) {
        sections.add(index, section);
    }


    protected int getSectionCount() {
        return sections.size();
    }


    protected void addSection(OWLFrameSection<? extends Object, ? extends Object, ? extends Object> section) {
        sections.add(section);
    }


    protected void clearSections() {
        sections.clear();
        fireContentChanged();
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


    public void refill() {
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
