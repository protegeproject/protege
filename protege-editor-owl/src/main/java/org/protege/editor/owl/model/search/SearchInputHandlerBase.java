package org.protege.editor.owl.model.search;

/**
 * @author Josef Hardi <johardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
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
