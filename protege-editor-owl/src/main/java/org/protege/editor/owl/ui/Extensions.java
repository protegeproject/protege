package org.protege.editor.owl.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.semanticweb.owlapi.formats.*;
import org.semanticweb.owlapi.model.OWLDocumentFormat;


/**
 * Author: Damien Goutte-Gattat<br>
 * University of Cambridge<br>
 * FlyBase Group<br>
 * Date: Jul 24, 2021<br><br>

 * dpg44@cam.ac.uk<br><br>
 */
public class Extensions {
    private final static HashMap<OWLDocumentFormat, List<String>> format_extensions;

    static {
        format_extensions = new HashMap<OWLDocumentFormat, List<String>>();

        format_extensions.put(new RDFXMLDocumentFormat(),
                            Arrays.asList(".rdf", ".xml"));
        format_extensions.put(new TurtleDocumentFormat(),
                            Arrays.asList(".ttl", ".turtle"));
        format_extensions.put(new OWLXMLDocumentFormat(),
                            Arrays.asList(".owx", ".xml", ".owl"));
        format_extensions.put(new FunctionalSyntaxDocumentFormat(),
                            Arrays.asList(".ofn"));
        format_extensions.put(new ManchesterSyntaxDocumentFormat(),
                            Arrays.asList(".omn"));
        format_extensions.put(new OBODocumentFormat(),
                            Arrays.asList(".obo"));
        format_extensions.put(new LatexDocumentFormat(),
                            Arrays.asList(".tex"));
        format_extensions.put(new RDFJsonLDDocumentFormat(),
                            Arrays.asList(".jsonld"));
        format_extensions.put(new NTriplesDocumentFormat(),
                            Arrays.asList(".nt", ".n3" /** .n3 is not an official N-Triples extension, but easy to confuse **/));
    }

    public static List<String> getExtensions(OWLDocumentFormat fmt) {
        if (format_extensions.containsKey(fmt)) {
            return format_extensions.get(fmt);
        }
        else {
            return Arrays.asList(".owl");
        }
    }

    public static List<String> getExtensions() {
        List<String> extensions = new ArrayList<>();

        for (List<String> exts : format_extensions.values()) {
            for (String ext : exts) {
                if (! extensions.contains(ext)) {
                    extensions.add(ext);
                }
            }
        }

        return extensions;
    }
}
