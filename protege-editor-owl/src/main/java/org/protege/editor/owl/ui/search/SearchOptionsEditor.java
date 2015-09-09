package org.protege.editor.owl.ui.search;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 29/10/2012
 */
public interface SearchOptionsEditor {

    void setSearchOptions(SearchOptions searchOptions);

    SearchOptions getSearchOptions();

    void setSearchOptionsChangedListener(SearchOptionsChangedListener listener);
}
