package org.protege.editor.owl.ui.renderer;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.owl.ui.renderer.plugin.RendererPlugin;
import org.protege.editor.owl.ui.renderer.plugin.RendererPluginLoader;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

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
	public static final String DEFAULT_RENDERER_CLASS_NAME = OWLEntityAnnotationValueRenderer.class.getName();
	
	public static final String ALLOW_PROTEGE_TO_OVERRIDE_RENDERER = "allow.protege.renderer.override";

    public static final String RENDER_HYPERLINKS = "RENDER_HYPERLINKS";

    public static final String HIGHLIGHT_ACTIVE_ONTOLOGY_STATEMENTS = "HIGHLIGHT_ACTIVE_ONTOLOGY_STATEMENTS";

    public static final String HIGHLIGHT_CHANGED_ENTITIES = "HIGHLIGHT_CHANGED_ENTITIES";

    public static final String HIGHLIGHT_KEY_WORDS = "HIGHLIGHT_KEY_WORDS";

    public static final String RENDERER_CLASS = "RENDERER_CLASS";

    public static final String USE_THAT_KEYWORD = "USE_THAT_KEYWORD";

    public static final String RENDER_DOMAIN_AXIOMS_AS_GCIS = "RENDER_DOMAIN_AXIOMS_AS_GCIS";

    public static final String FONT_SIZE = "FONT_SIZE";

    public static final String FONT_NAME = "FONT_NAME";

    public static final String ANNOTATIONS = "ANNOTATIONS";
    
    public static final int DEFAULT_FONT_SIZE = 14;

    public static final String DEFAULT_FONT_NAME = getDefaultFontName();


    public static final String NO_LANGUAGE_SET_USER_TOKEN = "!";
    
    public static final String NO_LANGUAGE_SET = "";

    private static OWLRendererPreferences instance;

    private boolean renderHyperlinks;

    private boolean highlightActiveOntologyStatements;

    private boolean highlightChangedEntities;

    private boolean highlightKeyWords;

    private boolean useThatKeyword;

    private boolean renderDomainAxiomsAsGCIs;

    private int fontSize;

    private String fontName = DEFAULT_FONT_NAME;

    private Font font;

    private List<IRI> annotationIRIS;

    private List<String> annotationLanguages;

    private List<RendererPlugin> rendererPlugins;
    
    private RendererPlugin currentRendererPlugin;
    
    private boolean allowProtegeToOverrideRenderer;

    /*
     * HELP!
     * 
     * I don't really understand this stuff at all.  But it appears that there are two schemes of naming
     * the dialog font.  The first one, which I believe is used by Windows and Linux is to have a series of
     * fonts called things like Dialog.plain, Dialog.bold, Dialog.bolditalic and Dialog.italic.  Each of these 
     * fonts has a family of Dialog and a style of plain.
     * 
     * OS/X uses a different scheme.  There is one dialog font with a name of Dialog (no .plain).  He can
     * be detected by looking for the font with the family Dialog and the style plain.  I am not sure what 
     * this should be doing exactly. There is also a DialogInput font.
     * 
     * This idea of setting the font is a bad idea and I agree with others here that we should get rid of it.
     */
    private static String getDefaultFontName() {
    	String qualifiedDefaultFontName = "Dialog.plain";
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font [] fonts = ge.getAllFonts();
        // essentially the following is what we had before and it works on linux and windows.
        for (Font f : fonts) {
        	if (f.getFontName().equals(qualifiedDefaultFontName)) {
        		return qualifiedDefaultFontName;
        	}
        }
        // if you do the following on windows and linux you might end up with something like Dialog.bold.
        for (Font f : fonts) {
        	if (f.getFamily().equals("Dialog") && f.getStyle() == Font.PLAIN) {
        		return f.getFontName();
        	}
        }
        return null; // well this is bad...
    }
    
    public Font getFont() {
        return font;
    }


    public String getFontName() {
        return fontName;
    }


    public void setFontName(String fontName) {
        this.fontName = fontName;
        getPreferences().putString(FONT_NAME, fontName);
        resetFont();

    }

    private void resetFont() {
        font = new Font(this.fontName, Font.PLAIN, fontSize);
    }



    public int getFontSize() {
        return fontSize;
    }


    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
        getPreferences().putInt(FONT_SIZE, fontSize);
        resetFont();
    }


    public void setAnnotations(List<IRI> iris){
        annotationIRIS = iris;
        writeAnnotations();
    }
    
    public void setAnnotationLanguages(List<String> annotationLanguages) {
    	this.annotationLanguages = new ArrayList<String>(annotationLanguages);
    	writeAnnotations();
    }


    public List<IRI> getAnnotationIRIs(){
        return new ArrayList<IRI>(annotationIRIS);
    }


    public List<String> getAnnotationLangs(){
    	return Collections.unmodifiableList(annotationLanguages);
    }

    public Map<IRI, List<String>> getAnnotationLangMap(){
    	Map<IRI, List<String>> langMap = new HashMap<IRI, List<String>>();
    	for (IRI iri : annotationIRIS) {
    		langMap.put(iri, Collections.unmodifiableList(annotationLanguages));
    	}
    	return langMap;
    }

    private OWLRendererPreferences() {
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
        allowProtegeToOverrideRenderer = p.getBoolean(ALLOW_PROTEGE_TO_OVERRIDE_RENDERER, true);
        setRendererPlugin(p.getString(RENDERER_CLASS, DEFAULT_RENDERER_CLASS_NAME));
        renderDomainAxiomsAsGCIs = false; p.putBoolean(RENDER_DOMAIN_AXIOMS_AS_GCIS, false);
        fontSize = p.getInt(FONT_SIZE, DEFAULT_FONT_SIZE);
        fontName = p.getString(FONT_NAME, DEFAULT_FONT_NAME);
        loadAnnotations();
        resetFont();
    }

    /*
     * Using a backwards compatible encoding of annotations and their languages.
     */
    private void loadAnnotations() {
        annotationIRIS = new ArrayList<IRI>();
        annotationLanguages = new ArrayList<String>();
        final List<String> defaultValues = Collections.emptyList();
        List<String> values = getPreferences().getStringList(ANNOTATIONS, defaultValues);

        if (values.equals(defaultValues)){
            annotationIRIS.add(OWLRDFVocabulary.RDFS_LABEL.getIRI());
            annotationIRIS.add(IRI.create("http://www.w3.org/2004/02/skos/core#prefLabel"));
            annotationLanguages = getDefaultLanguages();
        }
        else{
            for (String value : values){
                String[] tokens = value.split(",");
                try {
                    IRI iri = IRI.create(new URI(tokens[0].trim()));
                    for (int i=1; i<tokens.length; i++){
                        String token = tokens[i].trim();
                        if (token.equals(NO_LANGUAGE_SET_USER_TOKEN)){
                            token = NO_LANGUAGE_SET;
                        }
                        if (!annotationLanguages.contains(token)) {
                        	annotationLanguages.add(token);
                        }
                    }
                    annotationIRIS.add(iri);
                }
                catch (URISyntaxException e) {
                    ErrorLogPanel.showErrorDialog(e);
                }
            }
        }
        if (!annotationLanguages.contains(NO_LANGUAGE_SET)) {
        	annotationLanguages.add(NO_LANGUAGE_SET);
        }
    }
    
    /*
     * Using a backwards compatible encoding of annotations and their languages.
     */
    private void writeAnnotations() {
        List<String> values = new ArrayList<String>();
        StringBuilder langStringBuilder = new StringBuilder();
        for (String lang : annotationLanguages) {
            if (lang == null || lang.equals(NO_LANGUAGE_SET)){
                lang = NO_LANGUAGE_SET_USER_TOKEN;
            }
            langStringBuilder.append(", ").append(lang);
        }

        for (IRI iri : annotationIRIS){
            StringBuilder str = new StringBuilder(iri.toString());
            str.append(langStringBuilder.toString());
            values.add(str.toString());
        }
        getPreferences().putStringList(ANNOTATIONS, values);
    }
    
    private List<String> getDefaultLanguages() {
    	List<String> langs = new ArrayList<String>();
    	Locale locale = Locale.getDefault();
    	if (locale != null && locale.getLanguage() != null && !locale.getLanguage().equals("")) {
    		langs.add(locale.getLanguage());
    		if (locale.getCountry() != null && !locale.getCountry().equals("")) {
    			langs.add(locale.getLanguage() + "-" + locale.getCountry());
    		}
    	}
    	langs.add(NO_LANGUAGE_SET);
    	String en = Locale.ENGLISH.getLanguage();
    	if (!langs.contains(en)) {
    		langs.add(en);
    	}
    	return langs;
    }


    public void reset() {
        renderHyperlinks = true;
        highlightActiveOntologyStatements = true;
        highlightChangedEntities = false;
        highlightKeyWords = true;
        useThatKeyword = false;
        fontSize = DEFAULT_FONT_SIZE;
    }


    public boolean isRenderHyperlinks() {
        return renderHyperlinks;
    }

    public List<RendererPlugin> getRendererPlugins() {
    	if (rendererPlugins == null) {
    		RendererPluginLoader loader = new RendererPluginLoader();
    		rendererPlugins = new ArrayList<RendererPlugin>(loader.getPlugins());
    		Collections.sort(rendererPlugins);
    	}
    	return rendererPlugins;
    }
    


    public RendererPlugin getRendererPlugin() {
    	return currentRendererPlugin;
    }
    
    public RendererPlugin getRendererPluginByClassName(String javaClassName) {
    	for (RendererPlugin plugin : getRendererPlugins()) {
			if (plugin.getRendererClassName().equals(javaClassName)) {
				return plugin;
			}
		}
    	return null;
    }

    public void setRendererPlugin(RendererPlugin plugin) {
	    String rendererClass = plugin.getRendererClassName();
	    getPreferences().putString(RENDERER_CLASS, rendererClass);
	    currentRendererPlugin = plugin;
	}


	private void setRendererPlugin(String rendererClass) {
    	currentRendererPlugin = null;
    	for (RendererPlugin plugin : getRendererPlugins()) {
    		String pluginClassName = plugin.getRendererClassName();
    		if (pluginClassName.equals(rendererClass)) {
    			currentRendererPlugin = plugin;
    			break;
    		}
    		else if (currentRendererPlugin == null && pluginClassName.equals(DEFAULT_RENDERER_CLASS_NAME)) {
    			currentRendererPlugin = plugin;
    		}
    	}
    }
	
	public boolean isProtegeAllowedToOverrideRenderer() {
		return allowProtegeToOverrideRenderer;
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
