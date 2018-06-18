package tamirmo.shopper.connection;

/**
 * Created by Tamir on 18/06/2018.
 * Events regarding settings changes (successful, wrong, timeout)
 */

public interface IServerChangeSettingsEventsListener {
    // All went well, setting changed
    void onSettingsChange();

    // The server has sent a message indicating the setting was not changed
    void onSettingsWrongRes();

    // No response from the server
    void onServerResultTimeout();

}
