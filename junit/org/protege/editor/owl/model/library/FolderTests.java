package org.protege.editor.owl.model.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import junit.framework.TestCase;

import org.protege.editor.owl.model.library.folder.FolderGroupManager;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.GroupEntry;


public class FolderTests extends TestCase {
    public static final File TEST_DIR=new File("build/folder.test");
    public static final File SOURCE_DIR=new File("junit/ontologies");
    
    public static final String AMINO_ACID_FILE  = "amino-acid.owl";
    public static final String PIZZA_FILE       = "pizza.owl";
    public static final String PHOTOGRAPHY_FILE = "photography.owl";
        
    public static final String AMINO_ACID_NS  = "http://www.co-ode.org/ontologies/amino-acid/2009/02/16/amino-acid.owl";
    public static final String PIZZA_NS       = "http://www.co-ode.org/ontologies/pizza/2005/10/18/pizza.owl";
    public static final String PHOTOGRAPHY_NS = "http://www.co-ode.org/ontologies/photography/photography.owl";

    public static final String CATALOG_FILE = "catalog-v001.xml";

    @Override
    public void setUp() {
        if (TEST_DIR.exists()) {
            for (File f : TEST_DIR.listFiles()) {
                f.delete();
            }
        }
        else {
            TEST_DIR.mkdir();
        }
    }
    
    public void testBaseline() throws IOException {
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        copy(new File(SOURCE_DIR, PHOTOGRAPHY_FILE), new File(TEST_DIR, PHOTOGRAPHY_FILE));

        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(new File(TEST_DIR, CATALOG_FILE).exists());
        assertTrue(catalog.getEntries().size() == 1);
        GroupEntry ge = (GroupEntry) catalog.getEntries().get(0);
        assertTrue(ge.getEntries().size() == 3);
        
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog).equals(new File(TEST_DIR, PIZZA_FILE).toURI()));
        assertTrue(CatalogUtilities.getRedirect(URI.create(PHOTOGRAPHY_NS), catalog).equals(new File(TEST_DIR, PHOTOGRAPHY_FILE).toURI()));
    }
    
    public void testUpdateNoChange() throws IOException, InterruptedException {
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        File catalogFile = new File(TEST_DIR, CATALOG_FILE);
        long changed = catalogFile.lastModified();
        Thread.sleep(1000);
        catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(catalogFile.lastModified() == changed);
    }
    
    public void testUpdateChange() throws IOException, InterruptedException {
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        catalogManager.ensureCatalogExists(TEST_DIR);
        File catalogFile = new File(TEST_DIR, CATALOG_FILE);
        long changed = catalogFile.lastModified();
        Thread.sleep(1000);
        copy(new File(SOURCE_DIR, PHOTOGRAPHY_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(catalogFile.lastModified() > changed);
        assertTrue(CatalogUtilities.getRedirect(URI.create(PHOTOGRAPHY_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog).equals(new File(TEST_DIR, PIZZA_FILE).toURI()));
    }
    
    public void testDuplicate() throws IOException {
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog) != null);
    }
    
    public void testDontTouchExisting() throws IOException, InterruptedException {
        copy(new File(SOURCE_DIR, "catalog-existing-with-no-groups.xml"), new File(TEST_DIR, CATALOG_FILE));
        Thread.sleep(1000);
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        
        File catalogFile = new File(TEST_DIR, CATALOG_FILE);
        long changed = catalogFile.lastModified();
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(catalogFile.lastModified() == changed);
    }
    
    public void testAutoUpdateFalse() throws IOException, InterruptedException {
        copy(new File(SOURCE_DIR, "catalog-auto-update-false.xml"), new File(TEST_DIR, CATALOG_FILE));
        Thread.sleep(1000);
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        
        File catalogFile = new File(TEST_DIR, CATALOG_FILE);
        long changed = catalogFile.lastModified();
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(catalogFile.lastModified() == changed);
    }
    
    public void testFileDeleted() throws IOException {
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        copy(new File(SOURCE_DIR, PHOTOGRAPHY_FILE), new File(TEST_DIR, PHOTOGRAPHY_FILE));

        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        catalogManager.ensureCatalogExists(TEST_DIR);
        new File(TEST_DIR, PIZZA_FILE).delete();
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        
        assertTrue(catalog.getEntries().size() == 1);
        GroupEntry ge = (GroupEntry) catalog.getEntries().get(0);
        assertTrue(ge.getEntries().size() == 2);

        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog) == null);
        assertTrue(CatalogUtilities.getRedirect(URI.create(PHOTOGRAPHY_NS), catalog).equals(new File(TEST_DIR, PHOTOGRAPHY_FILE).toURI()));
    }
    
    private void copy(File source, File target) throws IOException {
        FileInputStream in = new FileInputStream(source);
        FileOutputStream out = new FileOutputStream(target);
        int c;
        while ((c = in.read()) != -1) {
            out.write(c);
        }
        out.flush();
        out.close();
        in.close();
    }
    
    
}
