package org.protege.editor.owl.model.obofoundry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-22
 */

@AutoValue
public abstract class OboFoundryLicenseDetails {

    private static final OboFoundryLicenseDetails EMPTY = get("", "", "");

    @JsonCreator
    @Nonnull
    public static OboFoundryLicenseDetails get(@JsonProperty("label") String label,
                                               @JsonProperty("logo") String logo,
                                               @JsonProperty("url") String url) {
        return new AutoValue_OboFoundryLicenseDetails(orEmpty(label), orEmpty(logo), orEmpty(url));
    }

    private static String orEmpty(String s) {
        return Objects.toString(s, "");
    }

    public boolean isEmpty() {
        return getUrl().isEmpty();
    }

    @Nonnull
    public static OboFoundryLicenseDetails empty() {
        return EMPTY;
    }

    @JsonProperty("label")
    public abstract String getLabel();

    @JsonProperty("logo")
    public abstract String getLogo();

    @JsonProperty("url")
    public abstract String getUrl();
}
