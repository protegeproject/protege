package org.protege.editor.owl.model.obofoundry;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import java.util.Comparator;
import java.util.Objects;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-22
 */
@AutoValue
public abstract class OboFoundryEntry implements Comparable<OboFoundryEntry> {

    private static final Comparator<OboFoundryEntry> comparingById = Comparator.comparing(OboFoundryEntry::getId);

    @JsonCreator
    public static OboFoundryEntry get(@JsonProperty("id") String id,
                                      @JsonProperty("title") String title,
                                      @JsonProperty("ontology_purl") String ontologyPurl,
                                      @JsonProperty("depicted_by") String depictedBy,
                                      @JsonProperty("contact") OboFoundryOntologyContact contact,
                                      @JsonProperty("license") OboFoundryLicenseDetails license,
                                      @JsonProperty("is_obsolete") boolean isObsolete,
                                      @JsonProperty("tracker") String tracker,
                                      @JsonProperty("domain") String domain,
                                      @JsonProperty("activity_status") String activity_status,
                                      @JsonProperty("description") String description,
                                      @JsonProperty("homepage") String homepage) {
        return new AutoValue_OboFoundryEntry(orEmpty(id), orEmpty(title), orEmpty(ontologyPurl),
                                             orEmpty(depictedBy), orEmpty(contact), orEmpty(license), isObsolete, orEmpty(tracker), orEmpty(activity_status), orEmpty(domain), orEmpty(description), orEmpty(homepage));
    }

    @Override
    public int compareTo(OboFoundryEntry o) {
        return comparingById.compare(this, o);
    }

    private static String orEmpty(String s) {
        return Objects.toString(s, "");
    }

    private static OboFoundryLicenseDetails orEmpty(OboFoundryLicenseDetails details) {
        if(details == null) {
            return OboFoundryLicenseDetails.empty();
        }
        else {
            return details;
        }
    }

    private static OboFoundryOntologyContact orEmpty(OboFoundryOntologyContact contact) {
        if(contact == null) {
            return OboFoundryOntologyContact.empty();
        }
        else {
            return contact;
        }
    }

    @JsonProperty("id")
    public abstract String getId();

    @JsonProperty("title")
    public abstract String getTitle();

    @JsonProperty("ontology_purl")
    public abstract String getOntologyPurl();

    @JsonProperty("depicted_by")
    public abstract String getDepictedBy();

    @JsonProperty("contact")
    public abstract OboFoundryOntologyContact getContact();

    @JsonProperty("license")
    public abstract OboFoundryLicenseDetails getLicense();

    @JsonProperty("is_obsolete")
    public abstract boolean isObsolete();

    @JsonProperty("tracker")
    public abstract String getTracker();

    @JsonProperty("activity_status")
    public abstract String getActivityStatus();

    @JsonProperty("domain")
    public abstract String getDomain();

    @JsonProperty("description")
    public abstract String getDescription();

    @JsonProperty("homepage")
    public abstract String getHomepage();
}
