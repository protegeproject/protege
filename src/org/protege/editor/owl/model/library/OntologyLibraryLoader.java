package org.protege.editor.owl.model.library;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.log4j.Logger;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 23-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyLibraryLoader {

    private static final Logger logger = Logger.getLogger(OntologyLibraryLoader.class);

    public static final String PREFERENCES_KEY = "org.protege.editor.owl.ontologylibraries";

    private List<OntologyLibraryFactory> factories;

    private OntologyLibraryManager manager;


    public OntologyLibraryLoader(OntologyLibraryManager manager) {
        this.manager = manager;
        loadFactories();
    }


    /**
     * Loads the ontology library factories using the plugin mechanism.
     */
    private void loadFactories() {
        OntologyLibraryPluginLoader loader = new OntologyLibraryPluginLoader();
        factories = new ArrayList<OntologyLibraryFactory>();
        for (OntologyLibraryFactoryPlugin plugin : loader.getPlugins()) {
            try {
                factories.add(plugin.newInstance());
            }
            catch (Exception e) {
                logger.error(e);
            }
        }
    }


    public void loadOntologyLibraries() {
        try {
            byte [] buf = Preferences.userRoot().getByteArray(PREFERENCES_KEY, null);
            if (buf == null) {
                return;
            }
            ByteArrayInputStream is = new ByteArrayInputStream(buf);
            // We load the mementos from the input stream
            ObjectInputStream ois = new ObjectInputStream(is);
            List<OntologyLibraryMemento> mementos = (List<OntologyLibraryMemento>) ois.readObject();
            ois.close();
            loadOntologyLibraries(mementos);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void saveLibraries() {
        // Save the mementos to the output stream
        List<OntologyLibraryMemento> mementos = new ArrayList<OntologyLibraryMemento>();
        for (OntologyLibrary library : manager.getLibraries()) {
            mementos.add(library.getMemento());
        }
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(mementos);
            oos.flush();
            oos.close();
            // Store in Java prefs
            Preferences prefs = Preferences.userRoot();
            prefs.putByteArray(PREFERENCES_KEY, bos.toByteArray());
            prefs.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }
    }


    public void loadOntologyLibraries(List<OntologyLibraryMemento> mementos) {
        for (OntologyLibraryMemento memento : mementos) {
            for (OntologyLibraryFactory factory : factories) {
                if (factory.isSuitable(memento)) {
                    OntologyLibrary library = factory.createLibrary(memento);
                    if (library != null) {
                        manager.addLibrary(library);
                    }
                }
            }
        }
    }
}
