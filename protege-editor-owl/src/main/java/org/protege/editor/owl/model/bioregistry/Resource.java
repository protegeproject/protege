package org.protege.editor.owl.model.bioregistry;

import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonProperty;

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

    public Resource(@Nonnull @JsonProperty("pattern") String pattern,
                    @Nonnull @JsonProperty("uri_format") String uriFormat) {
        idPattern = Pattern.compile(pattern);
        this.uriFormat = uriFormat;
    }

    public String formatURI(@Nonnull String id) {
        if (idPattern.matcher(id).matches()) {
            return uriFormat.replace("$1", id);
        }
        return null;
    }
}
