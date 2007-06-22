package org.protege.editor.owl.model.library;

import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
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
