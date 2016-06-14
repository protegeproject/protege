package org.protege.editor.owl.model.search;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

/**
 * @author Josef Hardi <johardi@stanford.edu> <br>
 * Stanford Center for Biomedical Informatics Research
 */
public class CompoundKeyword implements SearchInput, Iterable<SearchKeyword> {

    private ImmutableList.Builder<SearchKeyword> builder = ImmutableList.builder();

    public CompoundKeyword() {
        // NO-OP
    }

    public CompoundKeyword(Collection<? extends SearchKeyword> keywords) {
        builder.addAll(keywords);
    }

    public void add(SearchKeyword keyword) {
        builder.add(keyword);
    }

    public List<SearchKeyword> getAll() {
        return builder.build();
    }

    @Override
    public Iterator<SearchKeyword> iterator() {
        return getAll().iterator();
    }

    @Override
    public String asSearchString() {
        StringBuilder sb = new StringBuilder();
        
        boolean needSeparator = false;
        for (SearchKeyword keyword : this) {
            if (needSeparator) {
                sb.append(", ");
            }
            sb.append(keyword.toString());
            needSeparator = true;
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return asSearchString();
    }
}
