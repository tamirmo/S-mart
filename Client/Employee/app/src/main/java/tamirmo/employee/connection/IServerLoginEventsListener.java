package tamirmo.employee.connection;

/**
 * Created by Tamir on 14/06/2018.
 * An interface with server messages (log in request result, data received, settings changed approvals)
 */

public interface IServerLoginEventsListener {
    void onLogInRequestResult(boolean isSuccessful);
    // No response from the server
    void onServerResultTimeout();

    // Indicating all departments and products were received from the server
    void onAllDataReceived();
}
