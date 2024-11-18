package hfr.flaie;

import java.util.prefs.Preferences;

public class ConfigManager {
    private static final Preferences PREFERENCES = Preferences.userRoot().node("hfr.flaie.SendToRehost");

    public static void saveConfig(boolean connected, String email, String password, String format) {
        PREFERENCES.putBoolean("connected", connected);
        PREFERENCES.put("email", email);
        PREFERENCES.put("password", password);
        PREFERENCES.put("format", format);
    }

    public static boolean isConnected() {
        return PREFERENCES.getBoolean("connected", false);
    }

    public static String getEmail() {
        return PREFERENCES.get("email", "");
    }

    public static String getPassword() {
        return PREFERENCES.get("password", "");
    }

    public static String getFormat() {
        return PREFERENCES.get("format", "BBCode de l'image réduite avec lien");
    }
}
