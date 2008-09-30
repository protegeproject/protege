package org.protege.editor.owl.model.library;

import org.apache.log4j.Logger;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


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

    private static final String PREFERENCES_ID = "org.protege.editor.owl.ontologylibraries";

    private static final String PREFS_KEY = "data";

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
            byte [] buf = PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_ID).getByteArray(PREFS_KEY, null);
            if (buf == null) {
                buf = java.util.prefs.Preferences.userRoot().getByteArray(PREFERENCES_ID, null);
            }
            if (buf != null){
                ByteArrayInputStream is = new ByteArrayInputStream(buf);
                // We load the mementos from the input stream
                ObjectInputStream ois = new ObjectInputStream(is);
                List<OntologyLibraryMemento> mementos = (List<OntologyLibraryMemento>) ois.readObject();
                ois.close();
                loadOntologyLibraries(mementos);
            }
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

            Preferences prefs = PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_ID);
            prefs.putByteArray(PREFS_KEY, bos.toByteArray());
        }
        catch (IOException e) {
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
