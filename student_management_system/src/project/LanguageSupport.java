package project;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageSupport {

    private static ResourceBundle messages;

    static {
        setLanguage("en"); // Default language
    }

    public static void setLanguage(String lang) {
        Locale locale = new Locale(lang);
        messages = ResourceBundle.getBundle("project.messages", locale);
    }

    public static String getString(String key) {
        return messages.getString(key);
    }
}
