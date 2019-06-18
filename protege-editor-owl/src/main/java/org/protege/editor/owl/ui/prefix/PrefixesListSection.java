package org.protege.editor.owl.ui.prefix;

import org.protege.editor.core.ui.list.MListSectionHeader;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-06-10
 */
public class PrefixesListSection implements MListSectionHeader, PrefixListElement {

    @Override
    public String getName() {
        return "Prefixes";
    }

    @Override
    public boolean canAdd() {
        return true;
    }
}
