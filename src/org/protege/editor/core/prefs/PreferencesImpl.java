package org.protege.editor.core.prefs;

import java.util.HashMap;
import java.util.List;
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
 * Date: 20-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PreferencesImpl implements Preferences {

    private Map<String, Object> map;


    public PreferencesImpl() {
        map = new HashMap<String, Object>();
    }


    private Object getObject(String key, Object def) {
        Object obj = map.get(key);
        if (obj == null) {
            obj = def;
            map.put(key, obj);
        }
        return obj;
    }


    public String getString(String key, String def) {
        return (String) getObject(key, def);
    }


    public void putString(String key, String val) {
        map.put(key, val);
    }


    public List<String> getStringList(String key, List<String> def) {
        return (List<String>) getObject(key, def);
    }


    public void putStringList(String key, List<String> val) {
        map.put(key, val);
    }


    public int getInt(String key, int def) {
        return (Integer) getObject(key, def);
    }


    public void putInt(String key, int val) {
        map.put(key, val);
    }


    public float getFloat(String key, float def) {
        return (Float) getObject(key, def);
    }


    public void putFloat(String key, float val) {
        map.put(key, val);
    }


    public long getLong(String key, long def) {
        return (Long) getObject(key, def);
    }


    public void putLong(String key, long val) {
        map.put(key, val);
    }


    public double getDouble(String key, double def) {
        return (Double) getObject(key, def);
    }


    public void putDouble(String key, double val) {
        map.put(key, val);
    }


    public boolean getBoolean(String key, boolean def) {
        return (Boolean) getObject(key, def);
    }


    public void putBoolean(String key, boolean val) {
        map.put(key, val);
    }


    public byte [] getByteArray(String key, byte [] def) {
        return (byte []) getObject(key, def);
    }


    public void putByteArray(String key, byte [] val) {
        map.put(key, val);
    }


    public List<byte []> getByteArrayList(String key, List<byte []> def) {
        return (List<byte []>) getObject(key, def);
    }


    public void putByteArrayList(String key, List<byte []> val) {
        map.put(key, val);
    }
}
