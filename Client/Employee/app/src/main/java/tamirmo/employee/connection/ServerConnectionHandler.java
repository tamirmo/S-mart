package tamirmo.employee.connection;

import android.util.Log;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import communicationUtilities.Message;
import smart.data.Product;
import smart.data.SmartDataManager;
import tamirmo.employee.tasks.TasksHandler;
import udp.IUdpMessageReceived;
import udp.UdpSocketHandler;

/**
 * Created by Tamir on 14/06/2018.
 * Handling the connection to the server.
 * Accepts messages from and sending messages to the server.
 */

public class ServerConnectionHandler implements IUdpMessageReceived {

    private enum CurrentServerRequestType{LOGIN, SETTINGS_CHANGE }

    private static final int SERVER_PORT = 5001;
    // The time (in ms) to wait for a server response until raising no response event.
    private static final int SERVER_REQUEST_TIMEOUT = 3000;

    // The requests codes that are sent between the server and the client app
    private static final int EMPLOYEE_LOGIN_REQUEST = 1;
    private static final int GET_DEPARTMENTS_REQUEST = 2;
    private static final int GET_PRODUCTS_REQUEST = 3;
    private static final int CHANGE_SETTINGS = 7;
    private static final int ITEM_STOCK_UPDATED = 8;

    // After a change settings request one of these are sent:
    private static final int CHANGE_EMAIL = 0;
    private static final int CHANGE_PASSWORD = 1;
    private static final int CHANGE_CREDIT = 2;

    // Response codes sent back to the user
    private static final int CHANGE_SETTINGS_WRONG_RESPONSE = 0;
    private static final int CHANGE_SETTINGS_OK_RESPONSE = 1;
    private static final int LOGIN_WRONG_RESPONSE = 0;
    private static final int LOGIN_OK_RESPONSE = 1;

    // A character separating parameters in a message sent to clients
    // (used in the ITEM_STOCK_UPDATED message sent to employees, login parameters)
    private static final String MESSAGE_PARAMS_DELIMITER = ",";

    // Singleton:
    private static ServerConnectionHandler instance;
    public static ServerConnectionHandler getInstance(){
        if(instance == null) {
            instance = new ServerConnectionHandler();
        }
        return instance;
    }

    private String serverIp;
    private UdpSocketHandler udpSocketHandler;
    // A listener for server events (new data received, connection timeout...)
    private IServerLoginEventsListener iServerLoginEventsListener;
    private Timer serverResponseTimer;
    private IServerChangeSettingsEventsListener iServerChangeSettingsListener;
    // The type of the last request sent to the server
    // (used to figure which event of no response to raise)
    private CurrentServerRequestType currentServerRequestType;

    /**
     * Opening a listener to listen to messages from the server.
     * @param serverIp String, The ip of the server
     *                 (received from UI, log ing screen's email long click dialog)
     */
    public void start(String serverIp){
        if(udpSocketHandler == null){
            udpSocketHandler = new UdpSocketHandler(SERVER_PORT);
            udpSocketHandler.addNewMessageRecievedListener(this);
            udpSocketHandler.startListening();
        }
        this.serverIp = serverIp;
    }

    public void setServerMessagesListener(IServerLoginEventsListener listener){
        this.iServerLoginEventsListener = listener;
    }

    public void setServerChangeSettingsEvents(IServerChangeSettingsEventsListener listener){
        this.iServerChangeSettingsListener = listener;
    }

    public void sendLogInRequest(String email, String password){
        // Assembling the request
        String request = String.format("%d,email=%d,%s,pass=%d,%s",
                EMPLOYEE_LOGIN_REQUEST, email.length(), email, password.length(), password);

        // Indicating log in message was sent
        // (to figure which timeout event to raise)
        currentServerRequestType = CurrentServerRequestType.LOGIN;

        // Sending the request to the server
        udp.UdpSender.getInstance().sendMessage(request,
                serverIp,
                SERVER_PORT);

        startResponseTimer();
    }

    /**
     * Sending a request to the server to get products, departments.
     * Called after a successful login.
     */
    private void dataRequest(){
        // At first getting the departments:
        String departmentsRequest = String.valueOf(GET_DEPARTMENTS_REQUEST);
        sendServerRequest(departmentsRequest);
    }

    private void productsRequest(){
        String departmentsRequest = String.valueOf(GET_PRODUCTS_REQUEST);
        sendServerRequest(departmentsRequest);
    }

    public void sendChangeEmail(String newEmail){
        // Assembling the request at the format the server accepts
        String changeEmailRequest = String.format("%d%d,email=%d,%s",
                CHANGE_SETTINGS, CHANGE_EMAIL, newEmail.length(), newEmail);
        // Indicating settings change message was sent
        // (to figure which timeout event to raise)
        currentServerRequestType = CurrentServerRequestType.SETTINGS_CHANGE;
        sendServerRequest(changeEmailRequest);
    }

    public void sendChangeCredit(String newCredit){
        // Assembling the request at the format the server accepts
        String changeEmailRequest = String.format("%d%d,credit=%d,%s",
                CHANGE_SETTINGS, CHANGE_CREDIT, newCredit.length(),newCredit);
        // Indicating settings change message was sent
        // (to figure which timeout event to raise)
        currentServerRequestType = CurrentServerRequestType.SETTINGS_CHANGE;
        sendServerRequest(changeEmailRequest);
    }

    public void sendChangePassword(String oldPass, String newPass){
        // Assembling the request at the format the server accepts
        String changeEmailRequest = String.format("%d%d,old_pass=%d,%s,new_pass=%d,%s",
                CHANGE_SETTINGS, CHANGE_PASSWORD, oldPass.length(), oldPass, newPass.length(), newPass);
        // Indicating settings change message was sent
        // (to figure which timeout event to raise)
        currentServerRequestType = CurrentServerRequestType.SETTINGS_CHANGE;
        sendServerRequest(changeEmailRequest);
    }

    /**
     * Sending a given request to the server and starts waiting for response
     * @param request String, The request to send
     */
    private void sendServerRequest(String request){
        // Sending the request to the server
        udp.UdpSender.getInstance().sendMessage(request,
                serverIp,
                SERVER_PORT);

        startResponseTimer();
    }

    private void startResponseTimer(){
        // Canceling old timer
        if(serverResponseTimer != null){
            serverResponseTimer.cancel();
        }
        serverResponseTimer = new Timer();
        serverResponseTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Resetting timer for next time
                serverResponseTimer.cancel();
                serverResponseTimer = null;

                // Alerting of a timeout according to the last sent type:

                if(currentServerRequestType == CurrentServerRequestType.LOGIN){
                    if(iServerLoginEventsListener != null){
                        iServerLoginEventsListener.onServerResultTimeout();
                    }
                }
                else if(currentServerRequestType == CurrentServerRequestType.SETTINGS_CHANGE){
                    if(iServerChangeSettingsListener != null){
                        iServerChangeSettingsListener.onServerResultTimeout();
                    }
                }

            }
        }, SERVER_REQUEST_TIMEOUT);
    }

    private void stopResponseTimer(){
        // Canceling old timer
        if(serverResponseTimer != null){
            serverResponseTimer.cancel();
            serverResponseTimer = null;
        }
    }

    public void stop(){
        if(udpSocketHandler != null){
            udpSocketHandler.stopListening();
            udpSocketHandler = null;
        }
    }

    @Override
    public void messageReceived(Message messageReceived) {
        // Checking what type of response we got from the server and handling it:

        if(messageReceived.getMessageContent().startsWith(String.valueOf(EMPLOYEE_LOGIN_REQUEST))) {

            boolean isLoginSuccessful = false;

            // Checking the log in result
            if(messageReceived.getMessageContent().substring(1).equals(String.valueOf(LOGIN_OK_RESPONSE))){
                isLoginSuccessful = true;
                dataRequest();
            }else if (messageReceived.getMessageContent().substring(1).equals(String.valueOf(LOGIN_WRONG_RESPONSE))){
                isLoginSuccessful = false;
            }

            if (iServerLoginEventsListener != null) {
                iServerLoginEventsListener.onLogInRequestResult(isLoginSuccessful);
            }

            stopResponseTimer();
        }
        else if(messageReceived.getMessageContent().startsWith(String.valueOf(GET_DEPARTMENTS_REQUEST))) {
            // After getting the departments, requesting the products
            productsRequest();
            // Getting only the json sent (Skipping the response code)
            String departmentsJson = messageReceived.getMessageContent().substring(1);
            // Saving it to the data manager
            SmartDataManager.getInstance().readDepartmentsFromJsonString(departmentsJson);
        }
        else if(messageReceived.getMessageContent().startsWith(String.valueOf(GET_PRODUCTS_REQUEST))) {
            // We got the response, no pint in keep on waiting
            stopResponseTimer();

            // Getting only the json sent (Skipping the response code)
            String productsJson = messageReceived.getMessageContent().substring(1);
            // Saving it to the data manager
            List<Product> products = SmartDataManager.getInstance().readProductsFromJsonString(productsJson);

            TasksHandler.getInstance().onProductsListReceived(products);

            // Finished getting all data from the server, alerting the listener (LoginActivity):
            if(iServerLoginEventsListener != null){
                iServerLoginEventsListener.onAllDataReceived();
            }
        }
        else if(messageReceived.getMessageContent().startsWith(String.valueOf(ITEM_STOCK_UPDATED))) {
            try {
                // Getting the details of the item (the id and updated stock count):
                // An item update stock message looks like(8 = message code, 1 = itemId, 2 = updated stock): "8,1,2"
                String productDetails = messageReceived.getMessageContent().substring(2);

                // The product's id should be the number in the details until the delimiter
                String productIdString = productDetails.substring(0, productDetails.indexOf(MESSAGE_PARAMS_DELIMITER));

                // The stock should be after the delimiter up until the end
                String updatedStockString = productDetails.substring(productDetails.indexOf(MESSAGE_PARAMS_DELIMITER) + 1);

                long productId = Long.parseLong(productIdString);
                int updatedStock = Integer.parseInt(updatedStockString);

                // Updating the task manager
                TasksHandler.getInstance().onProductUpdated(productId, updatedStock);
            }catch (Exception ex){
                Log.e("", "ServerConnectionHandler ITEM_PICKED processing error", ex);
            }
        }
        else if(messageReceived.getMessageContent().startsWith(String.valueOf(CHANGE_SETTINGS))){
            stopResponseTimer();

            // Getting the response result (Skipping the response code)
            String response = messageReceived.getMessageContent().substring(1);

            // Checking the result code and raising a suitable event:

            if(response.equals(String.valueOf(CHANGE_SETTINGS_WRONG_RESPONSE))){
                if(iServerChangeSettingsListener != null){
                    iServerChangeSettingsListener.onSettingsWrongRes();
                }
            }else if(response.equals(String.valueOf(CHANGE_SETTINGS_OK_RESPONSE))){
                if(iServerChangeSettingsListener != null){
                    iServerChangeSettingsListener.onSettingsChange();
                }
            }
        }
    }
}
