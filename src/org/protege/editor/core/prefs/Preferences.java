package org.protege.editor.core.prefs;

import java.io.Serializable;
import java.util.List;
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
public interface Preferences extends Serializable {

    // String


    String getString(String key, String def);


    void putString(String key, String val);


    List<String> getStringList(String key, List<String> def);


    void putStringList(String key, List<String> val);

    // Int


    int getInt(String key, int def);


    void putInt(String key, int val);

    // Float


    float getFloat(String key, float def);


    void putFloat(String key, float val);

    // long


    long getLong(String key, long def);


    void putLong(String key, long val);

    // double


    double getDouble(String key, double def);


    void putDouble(String key, double val);

    // boolean


    boolean getBoolean(String key, boolean def);


    void putBoolean(String key, boolean val);

    // byte array


    byte [] getByteArray(String key, byte [] def);


    void putByteArray(String key, byte [] val);


    List<byte []> getByteArrayList(String key, List<byte []> def);


    void putByteArrayList(String key, List<byte []> val);
}
