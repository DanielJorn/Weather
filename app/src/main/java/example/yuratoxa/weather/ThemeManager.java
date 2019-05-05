package example.yuratoxa.weather;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.reflect.Method;

import static example.yuratoxa.weather.MainActivity.TAG;

/**
 * Class for fast setting a right theme, which was selected by user.
 */
class ThemeManager {
    private Context context;
    private Resources resources;
    private final String PREFS_THEME = "prefs_theme";
    private String themeFromPrefs;
    private SharedPreferences sharedPreferences;

    ThemeManager(Context context) {
        this.context = context;
        resources = context.getResources();
        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(context);
    }

    /**
     * Sets a theme, which was selected by user in settings of
     * theme. It should be used in {@link Activity#onCreate(Bundle)} method.
     *
     * @param activity An activity where need to set a right theme.
     */
    void setRightTheme(Activity activity) {
        themeFromPrefs = sharedPreferences.getString(PREFS_THEME,
                resources.getString(R.string.default_theme));
        if (!isRightTheme()) {
            switch (themeFromPrefs) {
                case "LightMode":
                    activity.setTheme(R.style.LightMode);
                    Log.d(TAG, "setRightTheme: theme changed to light");
                    break;
                case "GlamorMode":
                    activity.setTheme(R.style.GlamorMode);
                    Log.d(TAG, "setRightTheme: theme changed to glamor");
                    break;
                case "DarkMode":
                    activity.setTheme(R.style.DarkMode);
                    Log.d(TAG, "setRightTheme: theme changed to dark");
                    break;
                case "SunnyMode":
                    activity.setTheme(R.style.SunnyMode);
                    Log.d(TAG, "setRightTheme: theme changed to sunny");
                    break;
                case "SkyBlue":
                    activity.setTheme(R.style.SkyBlue);
                    Log.d(TAG, "setRightTheme: theme changed to sku blue");
                    break;
            }
        }
    }

    private int getThemeId() {
        try {
            Class<?> wrapper = Context.class;
            Method method = wrapper.getMethod("getThemeResId");
            method.setAccessible(true);
            return (Integer) method.invoke(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private String getCurrentThemeName() {

        int themeResId = getThemeId();
        return resources.getResourceEntryName(themeResId);
    }

    /**
     * This method checks that theme from shared preferences and current
     * activity theme are equal.
     *
     * @return Returns an equality of theme which was selected by user and current theme.
     */
    boolean isRightTheme() {
        themeFromPrefs = sharedPreferences.getString(PREFS_THEME,
                resources.getString(R.string.default_theme));
        Log.d(TAG, "current theme " + getCurrentThemeName() + " theme from " +
                "prefs " + themeFromPrefs);
        return getCurrentThemeName().equals(themeFromPrefs);
    }
}
