package org.protege.editor.owl.ui.renderer;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 19-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLRendererPreferences {


    public static final String RENDER_HYPERLINKS = "RENDER_HYPERLINKS";

    public static final String HIGHLIGHT_ACTIVE_ONTOLOGY_STATEMENTS = "HIGHLIGHT_ACTIVE_ONTOLOGY_STATEMENTS";

    public static final String HIGHLIGHT_CHANGED_ENTITIES = "HIGHLIGHT_CHANGED_ENTITIES";

    public static final String HIGHLIGHT_KEY_WORDS = "HIGHLIGHT_KEY_WORDS";

    public static final String RENDERER_CLASS = "RENDERER_CLASS";


    private static OWLRendererPreferences instance;

    private boolean renderHyperlinks;

    private boolean highlightActiveOntologyStatements;

    private boolean highlightChangedEntities;

    private boolean highlightKeyWords;

    private String rendererClass;


    private OWLRendererPreferences() {
        rendererClass = OWLEntityRendererImpl.class.getName();
        load();
    }


    public static synchronized OWLRendererPreferences getInstance() {
        if (instance == null) {
            instance = new OWLRendererPreferences();
        }
        return instance;
    }


    private Preferences getPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences(getClass());
    }


    private void load() {
        Preferences p = getPreferences();
        renderHyperlinks = p.getBoolean(RENDER_HYPERLINKS, true);
        highlightActiveOntologyStatements = p.getBoolean(HIGHLIGHT_ACTIVE_ONTOLOGY_STATEMENTS, true);
        highlightChangedEntities = p.getBoolean(HIGHLIGHT_CHANGED_ENTITIES, false);
        highlightKeyWords = p.getBoolean(HIGHLIGHT_KEY_WORDS, true);
        rendererClass = p.getString(RENDERER_CLASS, OWLEntityRendererImpl.class.getName());
    }


    public void reset() {
        renderHyperlinks = true;
        highlightActiveOntologyStatements = true;
        highlightChangedEntities = false;
        highlightKeyWords = true;
    }


    public boolean isRenderHyperlinks() {
        return renderHyperlinks;
    }


    public String getRendererClass() {
        return rendererClass;
    }


    public void setRendererClass(String rendererClass) {
        this.rendererClass = rendererClass;
        getPreferences().putString(RENDERER_CLASS, rendererClass);
    }


    public void setRenderHyperlinks(boolean renderHyperlinks) {
        this.renderHyperlinks = renderHyperlinks;
        getPreferences().putBoolean(RENDER_HYPERLINKS, renderHyperlinks);
    }


    public boolean isHighlightActiveOntologyStatements() {
        return highlightActiveOntologyStatements;
    }


    public void setHighlightActiveOntologyStatements(boolean highlightActiveOntologyStatements) {
        this.highlightActiveOntologyStatements = highlightActiveOntologyStatements;
        getPreferences().putBoolean(HIGHLIGHT_ACTIVE_ONTOLOGY_STATEMENTS, highlightActiveOntologyStatements);
    }


    public boolean isHighlightChangedEntities() {
        return highlightChangedEntities;
    }


    public void setHighlightChangedEntities(boolean highlightChangedEntities) {
        this.highlightChangedEntities = highlightChangedEntities;
        getPreferences().putBoolean(HIGHLIGHT_CHANGED_ENTITIES, highlightChangedEntities);
    }


    public boolean isHighlightKeyWords() {
        return highlightKeyWords;
    }


    public void setHighlightKeyWords(boolean highlightKeyWords) {
        this.highlightKeyWords = highlightKeyWords;
        getPreferences().putBoolean(HIGHLIGHT_KEY_WORDS, highlightKeyWords);
    }
}
