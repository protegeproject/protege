package org.protege.editor.owl.model.search;

/**
 * @author Josef Hardi <johardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 22/06/2016
 */
public interface SearchInputVisitor {

    void visit(SearchInput searchQuery);
}
