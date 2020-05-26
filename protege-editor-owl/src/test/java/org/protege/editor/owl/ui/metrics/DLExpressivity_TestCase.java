package org.protege.editor.owl.ui.metrics;

import org.junit.Before;

import org.junit.Test;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;


import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

public class DLExpressivity_TestCase {
	
	private OWLOntology owlModel1;
	private OWLOntology owlModel2;
	public static final OWLOntologyManager owlmanage1 = OWLManager.createOWLOntologyManager();
	public static final OWLOntologyManager owlmanage2 = OWLManager.createOWLOntologyManager();
	public static final File SOURCE_DIR=new File("src/test/resources/ontologies");
	public static final String PIZZA_File = "pizza.owl";
	public static final String SPORT_File = "sports.owl";
//	public static final String NETFLIX_File = "Netflix.owl";
//	public static final String PHOTO_File = "photography.owl";
	public static final String A1 = "SHIQ";
	public static final String A2 = "SHOIND";
	
	@Before
	public void setUp() throws OWLOntologyCreationException {
        owlModel1 = owlmanage1.loadOntologyFromOntologyDocument(new File(SOURCE_DIR,SPORT_File));
        owlModel2 = owlmanage2.loadOntologyFromOntologyDocument(new File(SOURCE_DIR,PIZZA_File));
    }
	
	@Test
	public void shouldPassTest1() {
		String dl1 = new DLExpressivityMetric(owlModel1).getValue();
		assertThat(dl1, is(A1));
	}
	
//	@Test
//	public void shouldPassTest2() {
//		String dl1 = new DLExpressivityMetric(owlModel2).getValue();
//		assertThat(dl1, is(A2));
//	}
//	
//	@Test
//	public void shouldPassTest3() {
//		String dl1 = new DLExpressivityMetric(owlModel3).getValue();
//		assertThat(dl1, is(A3));
//	}
//	
//	@Test
//	public void shouldPassTest4() {
//		String dl1 = new DLExpressivityMetric(owlModel4).getValue();
//		assertThat(dl1, is(A4));
//	}
//	
//	@Test
//	public void shouldPassTest5() {
//		String dl1 = new DLExpressivityMetric(owlModel5).getValue();
//		assertThat(dl1, is(A5));
//	}
}