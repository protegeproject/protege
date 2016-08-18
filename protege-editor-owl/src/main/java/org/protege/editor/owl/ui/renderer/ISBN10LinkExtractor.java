package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.ui.renderer.layout.ISBN10Link;
import org.protege.editor.owl.ui.renderer.layout.Link;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class ISBN10LinkExtractor implements LinkExtractor {

    private static final Pattern pattern = Pattern.compile("ISBN:(\\d{10})");

    @Override
    public Optional<Link> extractLink(String s) {
        return extractISBN(s).map(ISBN10Link::new);
    }

    public Optional<String> extractISBN(String s) {
        Matcher matcher = pattern.matcher(s);
        if(matcher.matches()) {
            return Optional.of(matcher.group(1));
        }
        else {
            return Optional.empty();
        }
    }
}
