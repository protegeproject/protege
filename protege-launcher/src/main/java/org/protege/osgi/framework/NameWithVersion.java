package org.protege.osgi.framework;

import static com.google.common.base.MoreObjects.toStringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.framework.Version;

import com.google.common.collect.Ordering;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 08/09/15
 */
public class NameWithVersion implements Comparable<NameWithVersion> {

    private final static Pattern VERSION_NUMBER_PATTERN = Pattern.compile("[0-9]+(\\.[0-9]+)*");

    private final String fileName;

    public NameWithVersion(String fileName) {
        this.fileName = fileName;
    }

    private List<Comparable> split() {
        Matcher matcher = VERSION_NUMBER_PATTERN.matcher(fileName);
        int prevEnd = 0;
        List<Comparable> components = new ArrayList<>();
        while (matcher.find()) {
            int start = matcher.start();
            if (prevEnd != start) {
                String prev = fileName.substring(prevEnd, start);
                components.add(new StringComponent(prev));
            }
            prevEnd = matcher.end();
            String versionComponents = fileName.substring(matcher.start(), matcher.end());
            components.add(new VersionComponent(new Version(versionComponents)));
        }
        if (prevEnd < fileName.length()) {
            components.add(new StringComponent(fileName.substring(prevEnd + 1)));
        }


        return components;

    }


    @Override
    public String toString() {
        return toStringHelper("PluginFileName")
                .addValue(fileName)
                .toString();
    }

    @Override
    public int compareTo(NameWithVersion o) {

        return Ordering.natural().lexicographical().compare(this.split(), o.split());
    }

    private abstract static class NameComponent implements Comparable<Object> {

    }

    private static class StringComponent extends NameComponent {
        private String component;

        public StringComponent(String component) {
            this.component = component;
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof StringComponent) {
                return this.component.compareTo(((StringComponent) o).component);
            }
            else {
                return -1;
            }
        }

        @Override
        public String toString() {
            return toStringHelper("StringComponent")
                    .addValue(component)
                    .toString();
        }


    }

    private static class VersionComponent extends NameComponent {
        private Version version;

        public VersionComponent(Version version) {
            this.version = version;
        }

        @Override
        public int compareTo(Object o) {
            if (o instanceof VersionComponent) {
                return this.version.compareTo(((VersionComponent) o).version);
            }
            else {
                return 1;
            }
        }

        @Override
        public String toString() {
            return toStringHelper("VersionComponent")
                    .addValue(version)
                    .toString();
        }
    }
}
