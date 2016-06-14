package org.protege.editor.owl.model.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Josef Hardi <johardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class SearchSettings {

    private final Set<SearchCategory> categories = new HashSet<SearchCategory>();

    private final List<SearchSettingsListener> listeners = new ArrayList<SearchSettingsListener>();

    public SearchSettings() {
        setupDefaultSettings();
    }

    private void setupDefaultSettings() {
        categories.add(SearchCategory.DISPLAY_NAME);
        categories.add(SearchCategory.IRI);
        categories.add(SearchCategory.ANNOTATION_VALUE);
        categories.add(SearchCategory.LOGICAL_AXIOM);
    }

    public Set<SearchCategory> getSearchCategories() {
        return categories;
    }

    public boolean isSearchType(SearchCategory category) {
        return categories.contains(category);
    }

    public void setCategories(Collection<SearchCategory> categories) {
        this.categories.clear();
        this.categories.addAll(categories);
        fireSearchCategoriesChanged();
    }

    public void addSearchSettingsListeners(SearchSettingsListener listener) {
        listeners.add(listener);
    }

    private void fireSearchCategoriesChanged() {
        for (SearchSettingsListener listener : listeners) {
            listener.handleSearchSettingsChanged();
        }
    }
}
