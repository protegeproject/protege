package org.protege.editor.owl.model;

import org.semanticweb.owlapi.dlsyntax.parser.DLSyntaxOWLParserFactory;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxHTMLStorerFactory;
import org.semanticweb.owlapi.dlsyntax.renderer.DLSyntaxStorerFactory;
import org.semanticweb.owlapi.functional.parser.OWLFunctionalSyntaxOWLParserFactory;
import org.semanticweb.owlapi.functional.renderer.FunctionalSyntaxStorerFactory;
import org.semanticweb.owlapi.io.OWLParserFactory;
import org.semanticweb.owlapi.krss2.parser.KRSS2OWLParserFactory;
import org.semanticweb.owlapi.krss2.renderer.KRSS2OWLSyntaxStorerFactory;
import org.semanticweb.owlapi.latex.renderer.LatexStorerFactory;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxOntologyParserFactory;
import org.semanticweb.owlapi.manchestersyntax.renderer.ManchesterSyntaxStorerFactory;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLStorerFactory;
import org.semanticweb.owlapi.oboformat.OBOFormatOWLAPIParserFactory;
import org.semanticweb.owlapi.oboformat.OBOFormatStorerFactory;
import org.semanticweb.owlapi.owlxml.parser.OWLXMLParserFactory;
import org.semanticweb.owlapi.owlxml.renderer.OWLXMLStorerFactory;
import org.semanticweb.owlapi.rdf.rdfxml.parser.RDFXMLParserFactory;
import org.semanticweb.owlapi.rdf.rdfxml.renderer.RDFXMLStorerFactory;
import org.semanticweb.owlapi.rdf.turtle.parser.TurtleOntologyParserFactory;
import org.semanticweb.owlapi.rdf.turtle.renderer.TurtleStorerFactory;
import org.semanticweb.owlapi.rio.*;
import org.semanticweb.owlapi.util.PriorityCollection;
import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyFactoryImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLOntologyManagerImpl;
import uk.ac.manchester.cs.owl.owlapi.concurrent.ConcurrentOWLOntologyBuilder;
import uk.ac.manchester.cs.owl.owlapi.concurrent.NonConcurrentOWLOntologyBuilder;

import javax.annotation.Nonnull;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 4 Sep 2018
 */
public class OntologyManagerFactory {


    @Nonnull
    public static OWLOntologyManager createManager() {
        ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        OWLOntologyManager manager = new OWLOntologyManagerImpl(new OWLDataFactoryImpl(), readWriteLock);
        ConcurrentOWLOntologyBuilder ontologyBuilder = new ConcurrentOWLOntologyBuilder(new NonConcurrentOWLOntologyBuilder(), readWriteLock);
        manager.getOntologyFactories().add(new OWLOntologyFactoryImpl(ontologyBuilder));
        PriorityCollection<OWLParserFactory> parsers = manager.getOntologyParsers();

        // Added in reverse order of priority
        parsers.add(new OBOFormatOWLAPIParserFactory());
        parsers.add(new DLSyntaxOWLParserFactory());
        parsers.add(new KRSS2OWLParserFactory());
        parsers.add(new RioTrixParserFactory());
        parsers.add(new RioTrigParserFactory());
        parsers.add(new RioN3ParserFactory());
        parsers.add(new RioJsonLDParserFactory());
        parsers.add(new RioJsonParserFactory());
        parsers.add(new RioRDFXMLParserFactory());
        parsers.add(new RioBinaryRdfParserFactory());
        parsers.add(new OWLXMLParserFactory());
        parsers.add(new ManchesterOWLSyntaxOntologyParserFactory());
        parsers.add(new TurtleOntologyParserFactory());
        parsers.add(new OWLFunctionalSyntaxOWLParserFactory());
        parsers.add(new RioTurtleParserFactory());
        parsers.add(new RDFXMLParserFactory());
        
        PriorityCollection<OWLStorerFactory> ontologyStorers = manager.getOntologyStorers();
        ontologyStorers.add(new RDFXMLStorerFactory());
        ontologyStorers.add(new OBOFormatStorerFactory());
        ontologyStorers.add(new TurtleStorerFactory());
        ontologyStorers.add(new ManchesterSyntaxStorerFactory());
        ontologyStorers.add(new OWLXMLStorerFactory());
        ontologyStorers.add(new FunctionalSyntaxStorerFactory());
        ontologyStorers.add(new DLSyntaxStorerFactory());
        ontologyStorers.add(new DLSyntaxHTMLStorerFactory());
        ontologyStorers.add(new LatexStorerFactory());
        ontologyStorers.add(new KRSS2OWLSyntaxStorerFactory());
        ontologyStorers.add(new RioJsonLDStorerFactory());
        ontologyStorers.add(new RioJsonStorerFactory());
        ontologyStorers.add(new RioTrigStorerFactory());
        ontologyStorers.add(new RioTrixStorerFactory());
        return manager;
    }
}
