package org.protege.editor.owl.model.search;

/**
 * Author: Josef Hardi <josef.hardi@stanford.edu><br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 04/11/2015
 */
public abstract class SearchInputHandlerBase<E> implements SearchInputHandler {

    @Override
    public void handle(SearchKeyword searchKeyword) {
        // NO-OP
    }

    @Override
    public void handle(CompoundKeyword compoundKeyword) {
        // NO-OP
    }

    public abstract E getQueryObject();
}
