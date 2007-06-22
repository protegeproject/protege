package org.protege.editor.owl.model.library;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
