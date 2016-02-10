package org.protege.editor.owl.model.search;

import com.google.common.collect.ImmutableList;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.owl.OWLEditorKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 09/02/16
 */
public class SearchManagerSelector {

    private static final Logger logger = LoggerFactory.getLogger(SearchManagerSelector.class);

    public static final String DEFAULT_PLUGIN_ID = "org.protege.editor.owl.DefaultSearchManager";

    private final ImmutableList<SearchManagerPlugin> plugins;

    private final OWLEditorKit editorKit;

    private SearchManager currentSearchManager;



    public SearchManagerSelector(OWLEditorKit editorKit) {
        this.editorKit = checkNotNull(editorKit);
        SearchManagerPluginLoader loader = new SearchManagerPluginLoader(editorKit);
        Set<SearchManagerPlugin> plugins = loader.getPlugins();
        this.plugins = ImmutableList.copyOf(plugins);
    }

    /**
     * Gets the current search manager plugin id.
     * @return The plugin Id.
     */
    public String getCurrentPluginId() {
        String id = getSearchManagerPreferences().getString("search.manager", DEFAULT_PLUGIN_ID);
        if(id.equals(DEFAULT_PLUGIN_ID)) {
            return id;
        }
        for(SearchManagerPlugin plugin : plugins) {
            if(id.equals(plugin.getId())) {
                return id;
            }
        }
        return DEFAULT_PLUGIN_ID;
    }

    /**
     * Sets the current search manager plugin by its id.
     * @param id The id.  Not {@code null}.
     */
    public void setCurrentPluginId(String id) {
        getSearchManagerPreferences().putString("search.manager", checkNotNull(id));
        updateCurrentSearchManager();
    }

    private Preferences getSearchManagerPreferences() {
        return PreferencesManager.getInstance().getApplicationPreferences("search.manager.preferences");
    }

    public synchronized SearchManager getCurrentSearchManager() {
        if(currentSearchManager == null) {
            updateCurrentSearchManager();
        }
        return currentSearchManager;
    }

    public Collection<SearchManagerPlugin> getPlugins() {
        return plugins;
    }


    private void updateCurrentSearchManager() {
        try {
            if (currentSearchManager != null) {
                currentSearchManager.dispose();
                currentSearchManager = null;
            }
            String currentPluginId = getCurrentPluginId();
            for(SearchManagerPlugin plugin : plugins) {
                String pluginId = plugin.getId();
                if(pluginId.equals(currentPluginId)) {
                    installSearchManager(plugin);
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Error initialising selected search manager: {}", e.getMessage(), e);
        }
        finally {
            if(currentSearchManager == null) {
                installDefaultSearchManager();
            }
        }
    }

    private void installSearchManager(SearchManagerPlugin plugin) throws Exception {
        SearchManager searchManager = plugin.newInstance();
        searchManager.initialise();
        currentSearchManager = searchManager;
    }

    private void installDefaultSearchManager() {
        DefaultSearchManager defaultSearchManager = new DefaultSearchManager();
        defaultSearchManager.setup(editorKit);
        defaultSearchManager.initialise();
        currentSearchManager = defaultSearchManager;
    }

}
