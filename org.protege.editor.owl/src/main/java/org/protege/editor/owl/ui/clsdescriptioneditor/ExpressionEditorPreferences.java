package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 21, 2008<br><br>
 */
public class ExpressionEditorPreferences {

        public static final String PREFERENCES_KEY = "org.protege.editor.owl.expressioneditor";

        public static final String USE_EXPRESSION_COMPLETION_KEY = "USE_REGULAR_EXPRESSIONS";

        public static final String CHECK_DELAY_KEY = "CHECK_DELAY_KEY";

        private static ExpressionEditorPreferences instance;

        private boolean useExpressionCompletion;

        private int checkDelay;


        private ExpressionEditorPreferences() {
            load();
        }


        private static Preferences getPreferences() {
            return PreferencesManager.getInstance().getApplicationPreferences(PREFERENCES_KEY);
        }


        private void load() {
            Preferences prefs = getPreferences();
            useExpressionCompletion = prefs.getBoolean(USE_EXPRESSION_COMPLETION_KEY, true);
            checkDelay = prefs.getInt(CHECK_DELAY_KEY, 120);
        }


        public boolean isUseExpressionCompletion() {
            return useExpressionCompletion;
        }


        public void setUseExpressionCompletion(boolean useExpressionCompletion) {
            this.useExpressionCompletion = useExpressionCompletion;
            getPreferences().putBoolean(USE_EXPRESSION_COMPLETION_KEY, useExpressionCompletion);
        }


        public int getCheckDelay() {
            return checkDelay;
        }


        public void setCheckDelay(int checkDelay) {
            this.checkDelay = checkDelay;
            getPreferences().putInt(CHECK_DELAY_KEY, checkDelay);
        }


        public static synchronized ExpressionEditorPreferences getInstance() {
            if (instance == null) {
                instance = new ExpressionEditorPreferences();
            }
            return instance;
        }
    }
