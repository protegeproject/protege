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

    public static final String USE_THAT_KEYWORD = "USE_THAT_KEYWORD";

    public static final String RENDER_DOMAIN_AXIOMS_AS_GCIS = "RENDER_DOMAIN_AXIOMS_AS_GCIS";


    private static OWLRendererPreferences instance;

    private boolean renderHyperlinks;

    private boolean highlightActiveOntologyStatements;

    private boolean highlightChangedEntities;

    private boolean highlightKeyWords;

    private boolean useThatKeyword;

    private String rendererClass;

    private boolean renderDomainAxiomsAsGCIs;


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
        useThatKeyword = p.getBoolean(USE_THAT_KEYWORD, false);
        rendererClass = p.getString(RENDERER_CLASS, OWLEntityRendererImpl.class.getName());
        renderDomainAxiomsAsGCIs = p.getBoolean(RENDER_DOMAIN_AXIOMS_AS_GCIS, false);
    }


    public void reset() {
        renderHyperlinks = true;
        highlightActiveOntologyStatements = true;
        highlightChangedEntities = false;
        highlightKeyWords = true;
        useThatKeyword = false;
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


    public boolean isUseThatKeyword() {
        return useThatKeyword;
    }


    public void setUseThatKeyword(boolean useThatKeyword) {
        this.useThatKeyword = useThatKeyword;
        getPreferences().putBoolean(USE_THAT_KEYWORD, useThatKeyword);
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

    public void setRenderDomainAxiomsAsGCIs(boolean b) {
        this.renderDomainAxiomsAsGCIs = b;
        getPreferences().putBoolean(RENDER_DOMAIN_AXIOMS_AS_GCIS, b);
    }

    public boolean isRenderDomainAxiomsAsGCIs() {
        return renderDomainAxiomsAsGCIs;
    }
}
