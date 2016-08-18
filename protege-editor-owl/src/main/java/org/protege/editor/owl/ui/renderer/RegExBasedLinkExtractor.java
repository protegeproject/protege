package org.protege.editor.owl.ui.renderer;

import com.google.common.base.Objects;
import org.protege.editor.owl.ui.renderer.layout.HTTPLink;
import org.protege.editor.owl.ui.renderer.layout.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class RegExBasedLinkExtractor implements LinkExtractor {

    private static final Logger logger = LoggerFactory.getLogger(RegExBasedLinkExtractor.class);

    private final String name;

    private final Pattern linkPattern;

    private final String replacementString;

    public RegExBasedLinkExtractor(@Nonnull String name, @Nonnull Pattern linkPattern, @Nonnull String replacementString) {
        this.name = checkNotNull(name);
        this.linkPattern = checkNotNull(linkPattern);
        this.replacementString = checkNotNull(replacementString);
    }

    public Optional<String> extractLinkLiteral(String s) {
        try {
            Matcher matcher = linkPattern.matcher(s);
            if (matcher.matches()) {
                String linkLexicalValue = matcher.replaceAll(replacementString);
                return Optional.of(linkLexicalValue);
            }
            return Optional.empty();
        } catch (IndexOutOfBoundsException e) {
            logger.warn("Link extractor ({}) threw an IndexOutOfBoundsException: {}", name, e.getMessage());
            return Optional.empty();
        }
    }

    @Nonnull
    @Override
    public Optional<Link> extractLink(String s) {
        return extractLinkLiteral(s).flatMap(l -> {
            try {
                URI uri = new URI(l);
                return Optional.of(new HTTPLink(uri));
            } catch (URISyntaxException e) {
                logger.warn("Link extractor ({}) returned invalid URI: {}", name, e.getMessage());
                return Optional.empty();
            }
        });
    }


    @Override
    public String toString() {
        return toStringHelper("RegExBasedLinkExtractor")
                .add("name", name)
                .add("pattern", linkPattern.pattern())
                .add("replacement", replacementString)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, linkPattern, replacementString);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RegExBasedLinkExtractor)) {
            return false;
        }
        RegExBasedLinkExtractor other = (RegExBasedLinkExtractor) obj;
        return this.name.equals(other.name)
                && this.linkPattern.equals(other.linkPattern)
                && this.replacementString.equals(other.replacementString);
    }

}
