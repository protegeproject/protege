package org.protege.editor.owl.model.library;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;

import org.protege.editor.owl.model.library.folder.FolderGroupManager;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.protege.xmlcatalog.entry.GroupEntry;

import junit.framework.TestCase;


public class Folder_IT extends TestCase {
	/*
	 * there is something funny about this.  You would think that something 
	 * much smaller would be enough but with 100 it doesn't work.
	 */
	public static final int SUFFICIENTLY_LONG_TIME = 1000;
	
    public static final File TEST_DIR=new File("target/folder.test");
    public static final File SOURCE_DIR=new File("src/test/resources/ontologies");
    
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
        	removeDirectory(TEST_DIR);
        }
        TEST_DIR.mkdirs();
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
    
    public void testShadowed() throws IOException {
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        String subDirectoryName = "folder";
        File subDirectory = new File(TEST_DIR, subDirectoryName);
        subDirectory.mkdir();
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(subDirectory, AMINO_ACID_FILE));
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.SHADOWED_SCHEME + AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, subDirectoryName + "/" + AMINO_ACID_FILE).toURI()));     
    }
    
    public void testUpdateNoChange() throws IOException, InterruptedException {
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        Thread.sleep(SUFFICIENTLY_LONG_TIME);
        
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        File catalogFile = new File(TEST_DIR, CATALOG_FILE);
        long changed = catalogFile.lastModified();
        Thread.sleep(SUFFICIENTLY_LONG_TIME);
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
        Thread.sleep(SUFFICIENTLY_LONG_TIME);
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
        
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog) == null);
        URI duplicateRedirect = CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.DUPLICATE_SCHEME + PIZZA_NS), catalog);
        assertTrue(duplicateRedirect.equals(new File(TEST_DIR, PIZZA_FILE).toURI()) || duplicateRedirect.equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));
    }
    
    public void testDontTouchExisting() throws IOException, InterruptedException {
        copy(new File(SOURCE_DIR, "catalog-existing-with-no-groups.xml"), new File(TEST_DIR, CATALOG_FILE));
        Thread.sleep(SUFFICIENTLY_LONG_TIME);
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
        Thread.sleep(SUFFICIENTLY_LONG_TIME);
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
    
    public void testUpdateWithRetainedShadows01() throws IOException, InterruptedException {
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        String subDirectoryName = "folder";
        File subDirectory = new File(TEST_DIR, subDirectoryName);
        subDirectory.mkdir();
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(subDirectory, AMINO_ACID_FILE));
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.SHADOWED_SCHEME + AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, subDirectoryName + "/" + AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog) == null);

        File catalogFile = new File(TEST_DIR, CATALOG_FILE);
        long changed = catalogFile.lastModified();
        long sleepInterval = SUFFICIENTLY_LONG_TIME;
        Thread.sleep(sleepInterval);
        
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.SHADOWED_SCHEME + AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, subDirectoryName + "/" + AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog).equals(new File(TEST_DIR, PIZZA_FILE).toURI()));
        assertTrue(catalogFile.lastModified() >= changed + sleepInterval);
    }
    
    public void testUpdateWithRetainedShadows02() throws IOException, InterruptedException {
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        String subDirectoryName = "folder";
        File subDirectory = new File(TEST_DIR, subDirectoryName);
        subDirectory.mkdir();
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(subDirectory, AMINO_ACID_FILE));
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.SHADOWED_SCHEME + AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, subDirectoryName + "/" + AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog).equals(new File(TEST_DIR, PIZZA_FILE).toURI()));

        File catalogFile = new File(TEST_DIR, CATALOG_FILE);
        long changed = catalogFile.lastModified();
        long sleepInterval = SUFFICIENTLY_LONG_TIME;
        Thread.sleep(sleepInterval);
        
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.SHADOWED_SCHEME + AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, subDirectoryName + "/" + AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog).equals(new File(TEST_DIR, PIZZA_FILE).toURI()));
        assertTrue(catalogFile.lastModified() >= changed + sleepInterval);
    }
    
    public void testDeletedShadow() throws IOException, InterruptedException {
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        String subDirectoryName = "folder";
        File subDirectory = new File(TEST_DIR, subDirectoryName);
        subDirectory.mkdir();
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(subDirectory, AMINO_ACID_FILE));
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.SHADOWED_SCHEME + AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, subDirectoryName + "/" + AMINO_ACID_FILE).toURI()));     
        
        new File(subDirectory, AMINO_ACID_FILE).delete();
        catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.SHADOWED_SCHEME + AMINO_ACID_NS), catalog) == null);     
    }
    
    public void testUpdateShadowed() throws IOException, InterruptedException {
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        String subDirectoryName = "folder";
        File subDirectory = new File(TEST_DIR, subDirectoryName);
        subDirectory.mkdir();
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(subDirectory, AMINO_ACID_FILE));
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.SHADOWED_SCHEME + AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, subDirectoryName + "/" + AMINO_ACID_FILE).toURI()));     
        
        Thread.sleep(SUFFICIENTLY_LONG_TIME);
        
        new File(subDirectory, AMINO_ACID_FILE).delete();
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(subDirectory, AMINO_ACID_FILE));
        catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.SHADOWED_SCHEME + AMINO_ACID_NS), catalog) == null);     
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog).equals(new File(TEST_DIR, subDirectoryName + "/" + AMINO_ACID_FILE).toURI()));
    }
    
    public void testUpdateShadower() throws IOException, InterruptedException {
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        String subDirectoryName = "folder";
        File subDirectory = new File(TEST_DIR, subDirectoryName);
        subDirectory.mkdir();
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(subDirectory, AMINO_ACID_FILE));
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.SHADOWED_SCHEME + AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, subDirectoryName + "/" + AMINO_ACID_FILE).toURI()));     
        
        Thread.sleep(SUFFICIENTLY_LONG_TIME);
        
        new File(TEST_DIR, AMINO_ACID_FILE).delete();
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, subDirectoryName + "/" + AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.SHADOWED_SCHEME + AMINO_ACID_NS), catalog) == null);     
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));
    }
    
    public void testDeletedShadower() throws IOException, InterruptedException {
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        String subDirectoryName = "folder";
        File subDirectory = new File(TEST_DIR, subDirectoryName);
        subDirectory.mkdir();
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(subDirectory, AMINO_ACID_FILE));
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.SHADOWED_SCHEME + AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, subDirectoryName + "/" + AMINO_ACID_FILE).toURI()));     
        
        new File(TEST_DIR, AMINO_ACID_FILE).delete();
        catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, subDirectoryName + "/" + AMINO_ACID_FILE).toURI()));     
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.SHADOWED_SCHEME + AMINO_ACID_NS), catalog) == null);     
    }
    
    public void testUpdateWithDuplicate() throws IOException {
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog) == null);
        URI duplicateRedirect = CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.DUPLICATE_SCHEME + PIZZA_NS), catalog);
        assertTrue(duplicateRedirect.equals(new File(TEST_DIR, PIZZA_FILE).toURI()) || duplicateRedirect.equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));
        assertTrue(CatalogUtilities.getRedirect(URI.create(PHOTOGRAPHY_NS), catalog) == null);
        
        copy(new File(SOURCE_DIR, PHOTOGRAPHY_FILE), new File(TEST_DIR, PHOTOGRAPHY_FILE));
        catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog) == null);
        duplicateRedirect = CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.DUPLICATE_SCHEME + PIZZA_NS), catalog);
        assertTrue(duplicateRedirect.equals(new File(TEST_DIR, PIZZA_FILE).toURI()) || duplicateRedirect.equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));
        assertTrue(CatalogUtilities.getRedirect(URI.create(PHOTOGRAPHY_NS), catalog).equals(new File(TEST_DIR, PHOTOGRAPHY_FILE).toURI()));
    }
    
    public void testDeleteDuplicate() throws IOException {
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog) == null);
        URI duplicateRedirect = CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.DUPLICATE_SCHEME + PIZZA_NS), catalog);
        assertTrue(duplicateRedirect.equals(new File(TEST_DIR, PIZZA_FILE).toURI()) || duplicateRedirect.equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));
        
        new File(TEST_DIR, AMINO_ACID_FILE).delete();
        catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog).equals(new File(TEST_DIR, PIZZA_FILE).toURI()));
        duplicateRedirect = CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.DUPLICATE_SCHEME + PIZZA_NS), catalog);
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.DUPLICATE_SCHEME + PIZZA_NS), catalog) == null);
    }
    
    public void testUpdateDuplicate() throws IOException, InterruptedException {
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        copy(new File(SOURCE_DIR, PIZZA_FILE), new File(TEST_DIR, PIZZA_FILE));
        OntologyCatalogManager catalogManager = new OntologyCatalogManager(Collections.singletonList(new FolderGroupManager()));
        XMLCatalog catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog) == null);
        URI duplicateRedirect = CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.DUPLICATE_SCHEME + PIZZA_NS), catalog);
        assertTrue(duplicateRedirect.equals(new File(TEST_DIR, PIZZA_FILE).toURI()) || duplicateRedirect.equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog) == null);
        Thread.sleep(SUFFICIENTLY_LONG_TIME);
        new File(TEST_DIR, AMINO_ACID_FILE).delete();
        copy(new File(SOURCE_DIR, AMINO_ACID_FILE), new File(TEST_DIR, AMINO_ACID_FILE));
        catalog = catalogManager.ensureCatalogExists(TEST_DIR);
        assertTrue(CatalogUtilities.getRedirect(URI.create(PIZZA_NS), catalog).equals(new File(TEST_DIR, PIZZA_FILE).toURI()));
        assertTrue(CatalogUtilities.getRedirect(URI.create(CatalogEntryManager.DUPLICATE_SCHEME + PIZZA_NS), catalog) == null);
        assertTrue(CatalogUtilities.getRedirect(URI.create(AMINO_ACID_NS), catalog).equals(new File(TEST_DIR, AMINO_ACID_FILE).toURI()));
    }
    
    private void removeDirectory(File dir) {
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				removeDirectory(f);
			}
			else {
				f.delete();
			}
		}
		dir.delete();
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
