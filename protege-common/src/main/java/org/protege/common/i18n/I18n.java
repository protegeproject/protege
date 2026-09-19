package org.protege.common.i18n;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {
    private static final String BUNDLE_NAME = "i18n.Message";

    // 初始化加载资源束
    private static ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_NAME, Locale.CHINA);

    private static String get(String key, Object... args) {
        try {
            String value = bundle.getString(key);
            if (args.length > 0) {
                return java.text.MessageFormat.format(value, arge);
            }
            return value;
        } catch (Exception e) {
            return '!' + key + '!'; // 当找不到 key 时返回调试占位符
        }
    }
}
