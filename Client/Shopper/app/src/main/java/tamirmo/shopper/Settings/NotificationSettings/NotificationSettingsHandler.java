package tamirmo.shopper.Settings.NotificationSettings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class NotificationSettingsHandler {
    // Singleton:
    private static NotificationSettingsHandler instance;

    public static NotificationSettingsHandler getInstance() {
        if (instance == null) {
            instance = new NotificationSettingsHandler();
        }

        return instance;
    }

    private final static String IS_VIBRATION_ON_KEY = "IS_VIBRATION_ON";
    private final static String IS_SOUND_ON_KEY = "IS_SOUND_ON";
    private final static String IS_OFFLINE_NOTIFICATION_ON_KEY = "IS_OFFLINE_NOTIFICATION_ON";
    private final static String IS_ACTIVE_NOTIFICATION_ON_KEY = "IS_ACTIVE_NOTIFICATION_ON";

    private boolean isVibrationOn;
    private boolean isSoundOn;
    private boolean isOfflineNotificationOn;
    private boolean isActiveNotificationOn;

    /**
     * Loading the notification settings with the application's activity
     * from the shared preferences.
     * @param activity Activity, The activity to load shared preferences with.
     */
    public void loadSettings(Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        this.isVibrationOn = sharedPref.getBoolean(IS_VIBRATION_ON_KEY, true);
        this.isSoundOn = sharedPref.getBoolean(IS_SOUND_ON_KEY, true);
        this.isOfflineNotificationOn = sharedPref.getBoolean(IS_OFFLINE_NOTIFICATION_ON_KEY, true);
        this.isActiveNotificationOn = sharedPref.getBoolean(IS_ACTIVE_NOTIFICATION_ON_KEY, true);
    }

    public boolean isVibrationOn() {
        return isVibrationOn;
    }

    public boolean isSoundOn() {
        return isSoundOn;
    }

    public boolean isOfflineNotificationOn(){
        return isOfflineNotificationOn;
    }

    public boolean isActiveNotificationOn(){
        return isActiveNotificationOn;
    }

    public void setVibrationOn(boolean isVibrationOn, Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_VIBRATION_ON_KEY, isVibrationOn);
        editor.apply();

        this.isVibrationOn = isVibrationOn;
    }

    public void setSoundOn(boolean isSoundOn, Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_SOUND_ON_KEY, isSoundOn);
        editor.apply();

        this.isSoundOn = isSoundOn;
    }

    public void setOfflineNotificationOn(boolean isOfflineNotificationOn, Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_OFFLINE_NOTIFICATION_ON_KEY, isOfflineNotificationOn);
        editor.apply();

        this.isOfflineNotificationOn = isOfflineNotificationOn;
    }

    public void setActiveNotificationOn(boolean isActiveNotificationOn, Activity activity){
        SharedPreferences sharedPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_ACTIVE_NOTIFICATION_ON_KEY, isActiveNotificationOn);
        editor.apply();

        this.isActiveNotificationOn = isActiveNotificationOn;
    }
}
