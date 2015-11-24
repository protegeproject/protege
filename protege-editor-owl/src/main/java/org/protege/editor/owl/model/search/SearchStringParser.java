package org.protege.editor.owl.model.search;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public interface SearchStringParser {

    void setSearchInputHandler(SearchInputHandler handler);

    void parse(String searchString);
}
