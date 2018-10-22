package org.protege.editor.core.log;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;

/**
 * Preference settings for Protege log window
 * 
 * @author Yevgeny Kazakov
 */
public class LogPreferences {

	private static final String PREFERENCES_SET_KEY_ = "org.protege.editor.core.log",
			LOG_LEVEL_KEY_ = "LOG_LEVEL",
			LOG_WRAP_LINES_KEY_ = "LOG_WRAP_LINES",
			LOG_LIMIT_OUTPUT_KEY_ = "LOG_LIMIT_OUTPUT",
			LOG_CHARACTER_LIMIT_KEY_ = "LOG_CHARACTER_LIMIT",
			LOG_PATTERN_KEY_ = "LOG_PATTERN",
			LOG_STYLES_NAMES_KEY_ = "LOG_STYLES_NAME",
			LOG_STYLES_FOREGTROUD_KEY_ = "LOG_STYLES_FORGROUND";

	private final static String DEFAULT_LOG_LEVEL_ = "INFO";
	private final static Boolean DEFAULT_WRAP_LINES_ = false,
			DEFAULT_LOG_LIMIT_OUTPUT_ = true;
	private final static int DEFAULT_LOG_CHARACTER_LIMIT_ = 80000;
	private final static String DEFAULT_LOG_PATTERN_ = "%7level  %date{HH:mm:ss}  %message%n";
	private final static List<String> DEFAULT_LOG_SYTLE_LEVELS_ = Collections
			.emptyList();
	private final static List<byte[]> DEFAULT_LOG_SYTLE_FOREGROUNDS_ = Collections
			.emptyList();

	public String logLevel;
	public Boolean wrapLines, limitLogOutput;
	public int logCharacterLimit;
	public String logPattern;
	public Map<String, Color> logStyleForegrounds;

	private LogPreferences() {
		// use create()
	}

	/**
	 * @return the preferences initialized with default values
	 */
	public static LogPreferences create() {
		return new LogPreferences().reset();
	}

	private static Preferences getPrefs() {
		PreferencesManager prefMan = PreferencesManager.getInstance();
		return prefMan.getPreferencesForSet(PREFERENCES_SET_KEY_,
				LogPreferences.class);
	}

	public LogPreferences load() {
		Preferences prefs = getPrefs();
		logLevel = prefs.getString(LOG_LEVEL_KEY_, DEFAULT_LOG_LEVEL_);
		wrapLines = prefs.getBoolean(LOG_WRAP_LINES_KEY_, DEFAULT_WRAP_LINES_);
		limitLogOutput = prefs.getBoolean(LOG_LIMIT_OUTPUT_KEY_,
				DEFAULT_LOG_LIMIT_OUTPUT_);
		logCharacterLimit = prefs.getInt(LOG_CHARACTER_LIMIT_KEY_,
				DEFAULT_LOG_CHARACTER_LIMIT_);
		logPattern = prefs.getString(LOG_PATTERN_KEY_, DEFAULT_LOG_PATTERN_);
		List<String> names = prefs.getStringList(LOG_STYLES_NAMES_KEY_,
				DEFAULT_LOG_SYTLE_LEVELS_);
		List<byte[]> foregrounds = prefs.getByteArrayList(
				LOG_STYLES_FOREGTROUD_KEY_, DEFAULT_LOG_SYTLE_FOREGROUNDS_);
		logStyleForegrounds = new HashMap<String, Color>(foregrounds.size());
		try {
			for (int i = 0; i < foregrounds.size(); i++) {
				logStyleForegrounds.put(names.get(i),
						(Color) deserialize(foregrounds.get(i)));
			}
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public LogPreferences save() {
		Preferences prefs = getPrefs();
		prefs.putString(LOG_LEVEL_KEY_, logLevel);
		prefs.putBoolean(LOG_WRAP_LINES_KEY_, wrapLines);
		prefs.putBoolean(LOG_LIMIT_OUTPUT_KEY_, limitLogOutput);
		prefs.putInt(LOG_CHARACTER_LIMIT_KEY_, logCharacterLimit);
		prefs.putString(LOG_PATTERN_KEY_, logPattern);		
		try {
			List<String> names = new ArrayList<String>(
					logStyleForegrounds.keySet());
			List<byte[]> foregroundColors = new ArrayList<byte[]>(names.size());			
			for (int i = 0; i < names.size(); i++) {
				foregroundColors
						.add(serialize(logStyleForegrounds.get(names.get(i))));
			}
			prefs.putStringList(LOG_STYLES_NAMES_KEY_, names);
			prefs.putByteArrayList(LOG_STYLES_FOREGTROUD_KEY_,
					foregroundColors);			
		} catch (IOException e) {
			e.printStackTrace();			
		}
		ProtegeApplication.applyLogPreferences();
		return this;
	}

	public LogPreferences reset() {
		logLevel = DEFAULT_LOG_LEVEL_;
		wrapLines = DEFAULT_WRAP_LINES_;
		limitLogOutput = DEFAULT_LOG_LIMIT_OUTPUT_;
		logCharacterLimit = DEFAULT_LOG_CHARACTER_LIMIT_;
		logPattern = DEFAULT_LOG_PATTERN_;
		logStyleForegrounds = Collections.emptyMap();
		return this;
	}

	public static byte[] serialize(Object obj) throws IOException {
		try (ByteArrayOutputStream b = new ByteArrayOutputStream()) {
			try (ObjectOutputStream o = new ObjectOutputStream(b)) {
				o.writeObject(obj);
			}
			return b.toByteArray();
		}
	}

	public static Object deserialize(byte[] bytes)
			throws IOException, ClassNotFoundException {
		try (ByteArrayInputStream b = new ByteArrayInputStream(bytes)) {
			try (ObjectInputStream o = new ObjectInputStream(b)) {
				return o.readObject();
			}
		}
	}

}
