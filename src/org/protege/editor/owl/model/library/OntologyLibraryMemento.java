package org.protege.editor.owl.model.library;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 20-Jul-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public final class OntologyLibraryMemento implements Serializable {

    private String typeId;

    private Map<String, String> map;


    public String getLibraryTypeId() {
        return typeId;
    }


    public OntologyLibraryMemento(String typeId) {
        this.typeId = typeId;
        map = new HashMap<String, String>();
    }


    public Map<String, String> getMap() {
        return new HashMap<String, String>(map);
    }


    public void setMap(Map<String, String> map) {
        this.map.clear();
        this.map.putAll(map);
    }


    public void putString(String key, String value) {
        map.put(key, value);
    }


    public String getString(String key) {
        return map.get(key);
    }


    public void putFile(String key, File value) {
        map.put(key, value.toURI().toString());
    }


    public File getFile(String key) {
        return new File(URI.create(map.get(key)));
    }


    public void putURI(String key, URI value) {
        map.put(key, value.toString());
    }


    public URI getURI(String key) {
        return URI.create(map.get(key));
    }


    public void putURL(String key, URL value) {
        map.put(key, value.toString());
    }


    public URL getURL(String key) {
        try {
            return new URL(map.get(key));
        }
        catch (MalformedURLException e) {
            return null;
        }
    }


    public void putBoolean(String key, boolean value) {
        map.put(key, Boolean.toString(value));
    }


    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(map.get(key));
    }
}
