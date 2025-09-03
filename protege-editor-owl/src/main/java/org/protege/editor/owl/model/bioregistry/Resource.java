package org.protege.editor.owl.model.bioregistry;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Author: Damien Goutte-Gattat<br>
 * University of Cambridge<br>
 * FlyBase Group<br>
 * Date: 03/04/2025
 * <p>
 * A resource within the Bioregistry. We only support the minimal set of fields required
 * to be able to convert CURIEs into resolvable links.
 * </p>
 */
public class Resource {

    private String uriFormat;
    private Pattern idPattern;
    private List<String> prefixes = new ArrayList<>();

    public Resource(@Nonnull @JsonProperty("prefix") String prefix,
                   @Nonnull @JsonProperty("uri_format") String uriFormat,
                   @JsonProperty("pattern") String pattern,
                   @JsonProperty("synonyms") List<String> synonyms) {
        if (prefix == null) {
            throw new IllegalArgumentException("Missing prefix");
        }
        if (uriFormat == null) {
            throw new IllegalArgumentException("Missing URI format");
        }

        prefixes.add(prefix);
        this.uriFormat = uriFormat;
        if (pattern != null) {
            idPattern = Pattern.compile(pattern);
        }
        if (synonyms != null) {
            prefixes.addAll(synonyms);
        }
    }

    public String formatURI(@Nonnull String id) {
        if (idPattern == null || idPattern.matcher(id).matches()) {
            return uriFormat.replace("$1", id);
        }
        return null;
    }

    public List<String> getPrefixes() {
        return prefixes;
    }
}
