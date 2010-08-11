package org.protege.editor.owl.ui.prefix;

import java.util.Map;

public interface PrefixMapperManager {

    void reload();

    void save();

    PrefixMapper getMapper();

    void setPrefixes(Map<String, String> prefixValueMap);

    Map<String, String> getPrefixes();

}
