package org.protege.editor.owl.model.entity;

import org.apache.log4j.Logger;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

import java.net.URI;

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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 24, 2008<br><br>
 */
public class EntityCreationPreferences {

    private static Logger logger = Logger.getLogger(EntityCreationPreferences.class);

    private static final String PREFERENCES_SET_KEY = "org.protege.editor.owl.entity.creation";

    private static final String DEFAULT_BASE_URI = "DEFAULT_BASE_URI";
    private static final String USE_DEFAULT_BASE_URI = "USE_DEFAULT_BASE_URI";

    private static final String USE_AUTO_ID_FOR_FRAGMENT = "USE_AUTO_ID_FOR_FRAGMENT";

    private static final String NAME_LABEL_GENERATE = "NAME_LABEL_GENERATE";
    private static final String NAME_LABEL_URI = "NAME_LABEL_URI";
    private static final String NAME_LABEL_LANG = "NAME_LABEL_LANG";

    private static final String ID_LABEL_GENERATE = "ID_LABEL_GENERATE";
    private static final String ID_LABEL_URI = "ID_LABEL_URI";
    private static final String ID_LABEL_LANG = "ID_LABEL_LANG";

    private static final String AUTO_ID_GENERATOR = "AUTO_ID_GENERATOR_CLASS";
    private static final String DEFAULT_AUTO_ID_GENERATOR_CLASS_NAME = "org.protege.editor.owl.model.entity.PseudoRandomAutoIDGenerator";

    private static final String AUTO_ID_PREFIX = "AUTO_ID_PREFIX";
    private static final String AUTO_ID_SUFFIX = "AUTO_ID_SUFFIX";
    private static final String AUTO_ID_SIZE = "AUTO_ID_SIZE";
    private static final String AUTO_ID_START = "AUTO_ID_START";
    private static final String AUTO_ID_END = "AUTO_ID_END";

    private static final String LABEL_DESCRIPTOR = "LABEL_DESCRIPTOR";
    private static final String DEFAULT_LABEL_DESCRIPTOR_CLASS = "org.protege.editor.owl.model.entity.MatchRendererLabelDescriptor";



    private static Preferences getPrefs() {
        return PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_SET_KEY);
    }

    public static URI getDefaultBaseURI() {
        Preferences prefs = getPrefs();
        String baseURIStr = prefs.getString(DEFAULT_BASE_URI, "http://www.co-ode.org/ontologies/ont.owl#");
        return URI.create(baseURIStr);
    }


    public static boolean useDefaultBaseURI() {
        Preferences prefs = getPrefs();
        return prefs.getBoolean(USE_DEFAULT_BASE_URI, false);
    }


    public static void setUseDefaultBaseURI(boolean use) {
        Preferences prefs = getPrefs();
        prefs.putBoolean(USE_DEFAULT_BASE_URI, use);
    }


    public static void setDefaultBaseURI(URI defaultBase) {
        Preferences prefs = getPrefs();
        prefs.putString(DEFAULT_BASE_URI, defaultBase.toString());
    }


    public static Class<? extends AutoIDGenerator> getAutoIDGeneratorClass(){
        Preferences prefs = getPrefs();
        String className = prefs.getString(AUTO_ID_GENERATOR, DEFAULT_AUTO_ID_GENERATOR_CLASS_NAME);
        try {
            return (Class<AutoIDGenerator>)Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            logger.error("Cannot find an Auto ID generator.", e);
        }
        try {
            return (Class<AutoIDGenerator>)Class.forName(DEFAULT_AUTO_ID_GENERATOR_CLASS_NAME);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static void setAutoIDGeneratorClass(Class<? extends AutoIDGenerator> cls){
        Preferences prefs = getPrefs();
        prefs.putString(AUTO_ID_GENERATOR, cls.getName());
    }


    public static String getPrefix() {
        Preferences prefs = getPrefs();
        return prefs.getString(AUTO_ID_PREFIX, "[type]_");
    }


    public static void setPrefix(String prefix) {
        Preferences prefs = getPrefs();
        prefs.putString(AUTO_ID_PREFIX, prefix);
    }


    public static String getSuffix() {
        Preferences prefs = getPrefs();
        return prefs.getString(AUTO_ID_SUFFIX, "");
    }


    public static void setSuffix(String suffix) {
        Preferences prefs = getPrefs();
        prefs.putString(AUTO_ID_SUFFIX, suffix);
    }


    public static int getAutoIDDigitCount() {
        Preferences prefs = getPrefs();
        return prefs.getInt(AUTO_ID_SIZE, 20); // just about big enough for System.currentTimeMillis()
    }


    public static void setAutoIDDigitCount(int size) {
        Preferences prefs = getPrefs();
        prefs.putInt(AUTO_ID_SIZE, size);
    }


    public static int getAutoIDStart() {
        Preferences prefs = getPrefs();
        return prefs.getInt(AUTO_ID_START, 0);
    }


    public static void setAutoIDStart(int start) {
        Preferences prefs = getPrefs();
        prefs.putInt(AUTO_ID_START, start);
    }

    public static int getAutoIDEnd() {
        Preferences prefs = getPrefs();
        return prefs.getInt(AUTO_ID_END, -1);
    }


    public static void setAutoIDEnd(int end) {
        Preferences prefs = getPrefs();
        prefs.putInt(AUTO_ID_END, end);
    }


    public static boolean isFragmentAutoGenerated() {
        Preferences prefs = getPrefs();
        return prefs.getBoolean(USE_AUTO_ID_FOR_FRAGMENT, false);
    }


    public static void setFragmentAutoGenerated(boolean autoGenerateFragment) {
        Preferences prefs = getPrefs();
        prefs.putBoolean(USE_AUTO_ID_FOR_FRAGMENT, autoGenerateFragment);
    }


    public static boolean isGenerateNameLabel() {
        Preferences prefs = getPrefs();
        return prefs.getBoolean(NAME_LABEL_GENERATE, false);
    }


    public static void setGenerateNameLabel(boolean gen) {
        Preferences prefs = getPrefs();
        prefs.putBoolean(NAME_LABEL_GENERATE, gen);
    }


    public static URI getNameLabelURI() {
        Preferences prefs = getPrefs();
        String uriStr = prefs.getString(NAME_LABEL_URI, null);
        if (uriStr != null){
            return URI.create(uriStr);
        }
        return null;
    }


    public static void setNameLabelURI(URI labelURI) {
        Preferences prefs = getPrefs();
        prefs.putString(NAME_LABEL_URI, (labelURI == null) ? null : labelURI.toString());
    }

    public static String getNameLabelLang() {
        Preferences prefs = getPrefs();
        return prefs.getString(NAME_LABEL_LANG, null);
    }


    public static void setNameLabelLang(String lang) {
        Preferences prefs = getPrefs();
        prefs.putString(NAME_LABEL_LANG, lang);
    }


    public static boolean isGenerateIDLabel() {
        Preferences prefs = getPrefs();
        return prefs.getBoolean(ID_LABEL_GENERATE, false);
    }


    public static void setGenerateIDLabel(boolean gen) {
        Preferences prefs = getPrefs();
        prefs.putBoolean(ID_LABEL_GENERATE, gen);
    }

// for now just use the same as the name    
//    public static URI getIDLabelURI() {
//        Preferences prefs = getPrefs();
//        String uriStr = prefs.getString(ID_LABEL_URI, null);
//        if (uriStr != null){
//            return URI.create(uriStr);
//        }
//        return null;
//    }
//
//
//    public static void setIDLabelURI(URI labelURI) {
//        Preferences prefs = getPrefs();
//        prefs.putString(ID_LABEL_URI, (labelURI == null) ? null : labelURI.toString());
//    }
//
//
//    public static String getIDLabelLang() {
//        Preferences prefs = getPrefs();
//        return prefs.getString(ID_LABEL_LANG, null);
//    }
//
//
//    public static void setIDLabelLang(String lang) {
//        Preferences prefs = getPrefs();
//        prefs.putString(ID_LABEL_LANG, lang);
//    }


    public static Class<? extends LabelDescriptor> getLabelDescriptorClass() {
        Preferences prefs = getPrefs();
        String className = prefs.getString(LABEL_DESCRIPTOR, DEFAULT_LABEL_DESCRIPTOR_CLASS);
        try {
            return (Class<LabelDescriptor>)Class.forName(className);
        }
        catch (ClassNotFoundException e) {
            logger.error("Cannot find a label descriptor.", e);
        }
        try {
            return (Class<LabelDescriptor>)Class.forName(DEFAULT_LABEL_DESCRIPTOR_CLASS);
        }
        catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setLabelDescriptorClass(Class<? extends LabelDescriptor> cls) {
        Preferences prefs = getPrefs();
        prefs.putString(LABEL_DESCRIPTOR, cls.getName());
    }
}
