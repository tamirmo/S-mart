package tamirmo.employee;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.os.Vibrator;

import java.util.List;
import java.util.Stack;

import tamirmo.employee.Database.Class.EmptyTask;
import tamirmo.employee.Database.Class.ExpiredTask;
import tamirmo.employee.Database.Class.MisplacedTask;
import tamirmo.employee.Database.Class.Product;
import tamirmo.employee.Database.Class.Employee;
import tamirmo.employee.Database.Class.UserLocation;
import tamirmo.employee.Database.Class.UserSettings;
import tamirmo.employee.Database.Database;
import tamirmo.employee.Login.LoginFragment;
import tamirmo.employee.Map.MapFragment;
import tamirmo.employee.Network.NetworkFragment;
import tamirmo.employee.Settings.SettingsFragment;

public class MainActivity extends AppCompatActivity {

    // Class Constants
    private static final long NORMAL_VIBRATE_TIME = 400; // time in milliseconds
    private static final int INFINITY = 1000000000;

    // Class Requests Utilities
    private static final String END_OF_MESSAGE = "\nover";
    private static final String MESSAGE_DELIMITER = " ";

    // User Command Type
    private static final String COMMAND_TYPE = "REQUEST";

    // Request Names
    private static final String LOGIN_REQUEST = "login";
    private static final String GET_REQUEST = "get";
    private static final String SET_REQUEST = "set";
    private static final String LOGOUT_REQUEST = "logout";

    // Item Types for get Requests
    private static final String PRODUCT_ITEM_TYPE = "products";

    // Request Parts
    private static final int REQUEST_PARTS_AMOUNT = 2;
    private static final int REQUEST_NAME = 0;
    private static final int REQUEST_ARGS = 1;

    // Response messages
    private static final String ERROR_RESPONSE = "error";

    // Event Names - Server Requests
    private static final String PLACE_REQUEST = "place";
    private static final String EXPIRE_REQUEST = "expire";
    private static final String MISPLACE_REQUEST = "misplace";
    private static final String EMPTY_REQUEST = "empty";
    private static final String MOVE_REQUEST = "move";

    // Client Identity
    private static final String CLIENT_IDENTITY = "EMPLOYEE";
    private static final String OFFLINE_CLIENT_ID = "111";
    private static final int CLIENT_EVENTS_PORT = 5002;
    private static final int SERVER_PORT = 5001;

    // Class Widgets
    BottomNavigationView bottomBar;

    // Network Fragment - A non-UI Fragment
    private NetworkFragment networkFragment;

    // Login Fragment
    private LoginFragment loginFragment;

    // Bottom Navigation Fragments
    private MapFragment mapFragment;
    private SettingsFragment settingsFragment;

    // Fragment stack to allow better movement with the backStack
    private Stack<FragmentWithUpdates> fragmentStack;

    // Current fragment on screen, to be updated when needed
    private FragmentWithUpdates activeFragment;

    // Class attributes
    private String clientIP;
    private String clientID;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentStack = new Stack<FragmentWithUpdates>();

        // Starts a new database to hold all information from the server locally
        database = new Database();

        // Loads local data from a shared repository
        SharedPreferences sharedSettings = getSharedPreferences(getString(R.string.shared_settings_file), MODE_PRIVATE);
        String defaultValue = null;
        String settingsJson = sharedSettings.getString(getString(R.string.shared_settings_key), defaultValue);
        if (settingsJson != null) {
            try {
                database.loadUserSettingsFromString(settingsJson);
            } catch (Exception e) {
                // No need to do anything
            }
        }

        // Gets client IP-Address to send messages from the server
        clientIP = getClientIP();

        // A basic Client ID before getting the real one from the server
        clientID = OFFLINE_CLIENT_ID;

        // Bottom Navigation buttons aren't activated at this point
        bottomBar = findViewById(R.id.navigationView);

        // Adds a Network Fragment to work at the end of the Fragment stack
        networkFragment = new NetworkFragment();
        getSupportFragmentManager().beginTransaction().add(networkFragment, getString(R.string.network_frag_tag)).commit();
        try {
            networkFragment.startReceivingEvents(CLIENT_EVENTS_PORT);
        } catch (Exception e) {
            popUpMessageDialog(getString(R.string.connection_error));
            // Exits Application
            System.exit(0);
        }

        // Adds a login fragment on the top of the Fragment stack
        loginFragment = new LoginFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container, loginFragment, getString(R.string.login_frag_tag)).commit();
        activeFragment = loginFragment;
    }

    // Gets IP-Address of current client
    public String getClientIP() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        // Converts the IPv4 address to a string
        String ipString = String.format(
                "%d.%d.%d.%d",
                (ip & 0xff),
                (ip >> 8 & 0xff),
                (ip >> 16 & 0xff),
                (ip >> 24 & 0xff));

        return ipString;
    }

    // Sends a request to S-Mart server
    private String sendRequest(String request) throws Exception {
        String requestMessage, response;

        // Builds a complete request message
        requestMessage = CLIENT_IDENTITY + MESSAGE_DELIMITER + clientIP + MESSAGE_DELIMITER + clientID + MESSAGE_DELIMITER + COMMAND_TYPE + MESSAGE_DELIMITER + request + END_OF_MESSAGE;

        // Sends a request message to S-Mart server
        response = networkFragment.sendRequest(requestMessage);

        return response;
    }

    // Sends a login request to S-Mart server
    public boolean loginRequest(String serverIP, String email, String password) throws Exception {
        String request, response;

        // Builds the request main message
        request = LOGIN_REQUEST + MESSAGE_DELIMITER + email + MESSAGE_DELIMITER + password;

        try {
            // Connects to S-Mart Server
            networkFragment.startServerConnection(serverIP, SERVER_PORT);

            // Sends request to server
            response = sendRequest(request);
        } catch (Exception e) {
            // Connection issue
            throw new Exception(getString(R.string.connection_error));
        }

        // Checks if login was successful
        if (response.equals(ERROR_RESPONSE)) {
            // Login issue
            return false;
        }

        try {
            database.loadEmployeeFromString(response);
            clientID = database.getAccount().getId();
        } catch (Exception e) {
            // Parsing shopper account issue
            throw new Exception(getString(R.string.data_retrieval_error));
        }

        return true;
    }

    // Gets all items of a certain type
    private String getRequest(String itemType) throws Exception {
        // Builds the request main message
        String request = GET_REQUEST + MESSAGE_DELIMITER + itemType;

        // Sends request to server
        String response = sendRequest(request);

        if (response.equals(ERROR_RESPONSE)) {
            // Notifies that the server couldn't process request
            throw new Exception(getString(R.string.data_retrieval_error));
        }

        // A JSON string is returned
        return response;
    }

    // Loads all data from the server
    public void downloadAllData() throws Exception {
        database.loadProductsFromString(getRequest(PRODUCT_ITEM_TYPE));
    }

    // Starts S-Mart for work
    public void start() {
        // Bottom Navigation fragments are created
        mapFragment = new MapFragment();
        settingsFragment = new SettingsFragment();

        // Bottom Navigation buttons are activated
        bottomBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if(activeFragment != loginFragment) {
                            // Checks which fragment should be displayed
                            switch (item.getItemId()) {
                                case R.id.navigation_map:
                                    if (activeFragment != mapFragment) {
                                        replaceFragment(mapFragment, R.string.same_chain_transaction, true);
                                    }
                                    break;
                                case R.id.navigation_settings:
                                    if (activeFragment != settingsFragment) {
                                        if(activeFragment == mapFragment ) {
                                            replaceFragment(settingsFragment, R.string.different_chain_transaction, true);
                                        }else{
                                            replaceFragment(settingsFragment, R.string.same_chain_transaction, true);
                                        }
                                    }
                                    break;
                            }
                            return true;
                        }
                        return false;
                    }
                });

        // Replaces Login Fragment with Map Fragment
        replaceFragment(mapFragment, R.string.different_chain_transaction,true);
    }

    // Returns account data as a reference
    public Employee getAccount() {
        return database.getAccount();
    }

    // Returns products data as a reference
    public List<Product> getProducts() {
        return database.getProducts();
    }

    // Returns empty products data
    public List<EmptyTask> getEmptyProducts() {
        return database.getEmptyTasks();
    }

    // Returns expired products data
    public List<ExpiredTask> getExpiredProducts() {
        return database.getExpiredTasks();
    }

    // Returns misplaced products data
    public List<MisplacedTask> getMisplacedProducts() {
        return database.getMisplacedTasks();
    }

    // Returns user location
    public UserLocation getUserLocation() {
        return database.getUserLocation();
    }

    // Returns user Settings
    public UserSettings getUserSettings() {
        return database.getUserSettings();
    }

    // Replaces fragments on fragments container
    // IF saveTransaction FALSE => Back button returns you to the first saved transaction fragment that was replaced out
    // IF saveTransaction TRUE & lastTransactionName != newTransactionName => Back button returns you to the replaced out fragment
    // IF saveTransaction TRUE & lastTransactionName == newTransactionName => Back button returns you to the first replaced out fragment in the chain
    public void replaceFragment(Fragment fragmentIn, int transactionType, boolean saveTransaction) {
        String transactionLevel;

        if(transactionType == R.string.different_chain_transaction){
            fragmentStack.add(activeFragment);
        }

        transactionLevel = "" + fragmentStack.size();

        // Pops out all transactions with the same name until one with a different name
        getSupportFragmentManager().popBackStack(transactionLevel, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Adds a new transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragmentIn);

        if (saveTransaction) {
            // Saves transaction
            transaction.addToBackStack(transactionLevel);
        }

        // Changes active fragment
        activeFragment = (FragmentWithUpdates) fragmentIn;

        transaction.commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    @Override
    public void onBackPressed() {
        FragmentWithUpdates fragment;
        int size = fragmentStack.size();
        int limit = INFINITY;

        if(size > 0) {
            if(activeFragment == mapFragment){
                limit = 1;
            }else if(activeFragment == settingsFragment){
                limit = 2;
            }

            while(size > limit){
                getSupportFragmentManager().popBackStack(String.valueOf(size), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                fragmentStack.pop();
                size = fragmentStack.size();
            }

            activeFragment = fragmentStack.pop();
        }
        super.onBackPressed();
    }

    // Sets user account information
    public boolean setRequest(String credentialType, String credentialValue) throws Exception {
        String request = SET_REQUEST + MESSAGE_DELIMITER + credentialType + MESSAGE_DELIMITER + credentialValue;
        String response;

        try {
            response = sendRequest(request);
        } catch (Exception e) {
            throw new Exception(getString(R.string.connection_error));
        }

        if (response.equals(ERROR_RESPONSE)) {
            return false;
        }

        return true;
    }

    // Logout of the server
    public void logoutRequest() throws Exception {
        String request = LOGOUT_REQUEST;

        sendRequest(request);
    }

    // Process events from the server
    public void processEvent(String event) {
        String[] eventParts = event.split(MESSAGE_DELIMITER, REQUEST_PARTS_AMOUNT);
        String eventName, eventArgs;

        try {
            eventName = eventParts[REQUEST_NAME];
            eventArgs = eventParts[REQUEST_ARGS];

            switch (eventName) {
                case PLACE_REQUEST:
                    placeRequest(eventArgs);
                    break;
                case EXPIRE_REQUEST:
                    expireRequest(eventArgs);
                    break;
                case MISPLACE_REQUEST:
                    misplaceRequest(eventArgs);
                    break;
                case EMPTY_REQUEST:
                    emptyRequest(eventArgs);
                    break;
                case MOVE_REQUEST:
                    moveRequest(eventArgs);
                    break;
            }
        } catch (Exception e) {
            popUpMessageDialog(e.getMessage());
        }
    }

    // Places a product correctly on a shelf
    public void placeRequest(String requestArgs) throws Exception {
        String[] args = requestArgs.split(MESSAGE_DELIMITER, 2);
        String productID, employeeID;
        productID = args[0];
        employeeID = args[1];
        if (employeeID.equals(clientID)) {
            boolean done = false;
            List<ExpiredTask> expiredTasks = database.getExpiredTasks();
            for (ExpiredTask task : expiredTasks) {
                if (task.getProductID().equals(productID)) {
                    expiredTasks.remove(task);
                    done = true;
                    break;
                }
            }
            if (!done) {
                List<MisplacedTask> misplacedTasks = database.getMisplacedTasks();
                for (MisplacedTask task : misplacedTasks) {
                    if (task.getProductID().equals(productID)) {
                        misplacedTasks.remove(task);
                        done = true;
                        break;
                    }
                }
            }
            if (!done) {
                List<EmptyTask> emptyTasks = database.getEmptyTasks();
                for (EmptyTask task : emptyTasks) {
                    if (task.getProductID().equals(productID)) {
                        emptyTasks.remove(task);
                        done = true;
                        break;
                    }
                }
            }

            if (done) {
                updateFragment();
                playMusic(R.raw.place);
            }
        }
    }

    // Expires a product
    public void expireRequest(String requestArgs) throws Exception {
        String[] args = requestArgs.split(MESSAGE_DELIMITER, 2);
        String productID, employeeID;

        productID = args[0];
        employeeID = args[1];
        if (employeeID.equals(clientID)) {
            for (Product product : database.getProducts()) {
                if (product.getProductId().equals(productID)) {
                    database.getExpiredTasks().add(new ExpiredTask(productID));
                    updateFragment();
                    playMusic(R.raw.expired);
                    String message = product.getName() + " " + product.getAmountPerUnit() + product.getUnitType() + " " + getString(R.string.expire_notification_message);
                    sendNotification(getString(R.string.expire_notification_ticker), getString(R.string.expire_notification_title), message);
                    break;
                }
            }
        }
    }

    // Misplaces a product
    public void misplaceRequest(String requestArgs) throws Exception {
        String[] args = requestArgs.split(MESSAGE_DELIMITER, 4);
        String productID, employeeID;
        int locationX, locationY;

        productID = args[0];
        employeeID = args[1];
        locationX = Integer.parseInt(args[2]);
        locationY = Integer.parseInt(args[3]);
        if (employeeID.equals(clientID)) {
            for (Product product : database.getProducts()) {
                if (product.getProductId().equals(productID)) {
                    database.getMisplacedTasks().add(new MisplacedTask(productID, locationX, locationY));
                    updateFragment();
                    playMusic(R.raw.misplaced);
                    String message = product.getName() + " " + product.getAmountPerUnit() + product.getUnitType() + " " + getString(R.string.misplace_notification_message) + " (" + locationX + ", " + locationY + ")";
                    sendNotification(getString(R.string.misplace_notification_ticker), getString(R.string.misplace_notification_title), message);
                    break;
                }
            }
        }
    }

    // Empties a product
    public void emptyRequest(String requestArgs) throws Exception {
        String[] args = requestArgs.split(MESSAGE_DELIMITER, 2);
        String productID, employeeID;

        productID = args[0];
        employeeID = args[1];
        if (employeeID.equals(clientID)) {
            for (Product product : database.getProducts()) {
                if (product.getProductId().equals(productID)) {
                    database.getEmptyTasks().add(new EmptyTask(productID));
                    updateFragment();
                    playMusic(R.raw.empty);
                    String message = product.getName() + " " + product.getAmountPerUnit() + product.getUnitType() + " " + getString(R.string.empty_notification_message);
                    sendNotification(getString(R.string.empty_notification_ticker), getString(R.string.empty_notification_title), message);
                    break;
                }
            }
        }
    }

    // Moves shopper's digital location
    public void moveRequest(String requestArgs) throws Exception {
        String[] args = requestArgs.split(MESSAGE_DELIMITER, 4);
        String userType, shopperID;
        int locationX, locationY;

        userType = args[0];
        shopperID = args[1];
        locationX = Integer.parseInt(args[2]);
        locationY = Integer.parseInt(args[3]);
        if (shopperID.equals(clientID)) {
            UserLocation location = database.getUserLocation();
            location.setLocationX(locationX);
            location.setLocationY(locationY);
            updateFragment();
        }
    }

    // Shows a message
    public void popUpMessageDialog(String message) {
        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(MainActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(MainActivity.this);
        }

        builder.setTitle(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // Starts a sound effect
    public void playMusic(int musicId) {
        if (database.getUserSettings().getToSound()) {
            final MediaPlayer mp = MediaPlayer.create(this, musicId);
            mp.start();
        }
    }

    // Starts a vibration
    public void vibrate(long amount) {
        if (database.getUserSettings().getToVibrate()) {
            // Get instance of Vibrator from current Context
            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);

            // Vibrate for 400 milliseconds
            v.vibrate(amount);
        }
    }

    // Presents a notification
    public void sendNotification(String ticker, String title, String message) {
        if (database.getUserSettings().getToNotify()) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder builder = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                NotificationChannel notificationChannel = new NotificationChannel("ID", "Name", importance);
                notificationManager.createNotificationChannel(notificationChannel);
                builder = new NotificationCompat.Builder(getApplicationContext(), notificationChannel.getId());
            } else {
                builder = new NotificationCompat.Builder(getApplicationContext());
            }

            builder = builder
                    .setSmallIcon(R.mipmap.s_mart_ic_launcher_round)
                    .setContentTitle(title)
                    .setTicker(ticker)
                    .setContentText(message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true);
            notificationManager.notify(0, builder.build());
        }
    }

    // Updates current active fragment
    private void updateFragment() {
        if (activeFragment != null) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activeFragment.updateFragment();
                    vibrate(NORMAL_VIBRATE_TIME);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            SharedPreferences sharedSettings = getSharedPreferences(getString(R.string.shared_settings_file), MODE_PRIVATE);
            sharedSettings.edit().putString(getString(R.string.shared_settings_key), database.getUserSettingsJsonString()).apply();
        } catch (Exception e) {
            // Nothing to do with it
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            logoutRequest();
            networkFragment.exit();
        } catch (Exception e) {
            // Nothing to do with it
        }

    }
}
