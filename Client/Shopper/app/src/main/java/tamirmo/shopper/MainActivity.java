package tamirmo.shopper;

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

import tamirmo.shopper.Database.Class.CartItem;
import tamirmo.shopper.Database.Class.Department;
import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.Database.Class.Sale;
import tamirmo.shopper.Database.Class.Shopper;
import tamirmo.shopper.Database.Class.UserLocation;
import tamirmo.shopper.Database.Class.UserSettings;
import tamirmo.shopper.Database.Database;
import tamirmo.shopper.Login.LoginFragment;
import tamirmo.shopper.Network.NetworkFragment;
import tamirmo.shopper.MainMenu.MainMenuFragment;
import tamirmo.shopper.Discounts.DiscountsFragment;
import tamirmo.shopper.Settings.ChangeSettingsFragment;

public class MainActivity extends AppCompatActivity {

    // Class Constants
    private static final long NORMAL_VIBRATE_TIME = 400; // time in milliseconds

    // Class Requests Utilities
    private static final String END_OF_MESSAGE = "\nover";
    private static final String MESSAGE_DELIMITER = " ";

    // User Command Type
    private static final String COMMAND_TYPE = "REQUEST";

    // Request Names
    private static final String LOGIN_REQUEST = "login";
    private static final String GET_REQUEST = "get";
    private static final String SET_REQUEST = "set";
    private static final String SEND_REQUEST = "send";
    private static final String LOGOUT_REQUEST = "logout";

    // Item Types for get Requests
    private static final String DEPARTMENT_ITEM_TYPE = "departments";
    private static final String PRODUCT_ITEM_TYPE = "products";
    private static final String DISCOUNTS_ITEM_TYPE = "discounts";

    // Request Parts
    private static final int REQUEST_PARTS_AMOUNT = 2;
    private static final int REQUEST_NAME = 0;
    private static final int REQUEST_ARGS = 1;

    // Response messages
    private static final String ERROR_RESPONSE = "error";

    // Event Names - Server Requests
    private static final String PICK_REQUEST = "pick";
    private static final String RETURN_REQUEST = "return";
    private static final String SALE_REQUEST = "sale";
    private static final String MOVE_REQUEST = "move";

    // Client Identity
    private static final String CLIENT_IDENTITY = "SHOPPER";
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
    private MainMenuFragment mainMenuFragment;
    private DiscountsFragment discountsFragment;
    private ChangeSettingsFragment changeSettingsFragment;

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
        getSupportFragmentManager().beginTransaction().add(R.id.container, loginFragment).commit();
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
            database.loadShopperFromString(response);
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
        database.loadDepartmentsFromString(getRequest(DEPARTMENT_ITEM_TYPE));
        database.loadProductsFromString(getRequest(PRODUCT_ITEM_TYPE));
        database.loadDiscountsFromString(getRequest(DISCOUNTS_ITEM_TYPE));

        // Loads local data from a shared repository
        SharedPreferences sharedCart = getSharedPreferences(getString(R.string.shared_cart_file), MODE_PRIVATE);
        SharedPreferences sharedSettings = getSharedPreferences(getString(R.string.shared_settings_file), MODE_PRIVATE);
        String defaultValue = null;
        String cartJson = sharedCart.getString(getString(R.string.shared_cart_key) + clientID, defaultValue);
        String settingsJson = sharedSettings.getString(getString(R.string.shared_settings_key)  + clientID, defaultValue);
        if (cartJson != null) {
            try {
                database.loadCartFromString(cartJson);
            } catch (Exception e) {
                // No need to do anything
            }
        }
        if (settingsJson != null) {
            try {
                database.loadUserSettingsFromString(settingsJson);
            } catch (Exception e) {
                // No need to do anything
            }
        }
    }

    // Starts S-Mart for work
    public void start() {
        // Bottom Navigation fragments are created
        mainMenuFragment = new MainMenuFragment();
        discountsFragment = new DiscountsFragment();
        changeSettingsFragment = new ChangeSettingsFragment();

        // Bottom Navigation buttons are activated
        bottomBar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        if (activeFragment != loginFragment) {
                            // Checks which fragment should be displayed
                            switch (item.getItemId()) {
                                case R.id.navigation_shopping_cart:
                                    if (activeFragment != mainMenuFragment) {
                                        reduceStackSize(1);
                                    }
                                    break;
                                case R.id.navigation_discount:
                                    if (!discountsFragment.isVisible()) {
                                        if (activeFragment == changeSettingsFragment){
                                            replaceFragment(discountsFragment, R.string.same_chain_transaction, true);
                                        } else if (activeFragment instanceof SettingsFragment){
                                            popBackStack();
                                            replaceFragment(discountsFragment, R.string.same_chain_transaction, true);
                                        } else{
                                            replaceFragment(discountsFragment, R.string.unique_chain_transaction, true);
                                        }
                                    }
                                    break;
                                case R.id.navigation_settings:
                                    if (!changeSettingsFragment.isVisible()) {
                                        if (activeFragment == discountsFragment) {
                                            replaceFragment(changeSettingsFragment, R.string.same_chain_transaction, true);
                                        } else if (activeFragment instanceof SettingsFragment) {
                                            popBackStack();
                                        } else {
                                            replaceFragment(changeSettingsFragment, R.string.unique_chain_transaction, true);
                                        }
                                    }
                                    break;
                            }
                            return true;
                        }
                        return false;
                    }
                });

        // Replaces Login Fragment with Cart Option Fragment
        replaceFragment(mainMenuFragment, R.string.unique_chain_transaction, true);
    }

    // Returns account data as a reference
    public Shopper getAccount() {
        return database.getAccount();
    }

    // Returns departments data as a reference
    public List<Department> getDepartments() {
        return database.getDepartments();
    }

    // Returns products data as a reference
    public List<Product> getProducts() {
        return database.getProducts();
    }

    // Returns discounts data as a reference
    public List<Discount> getDiscounts() {
        return database.getDiscounts();
    }

    // Returns user cart as a reference
    public List<CartItem> getCart() {
        return database.getCart();
    }

    // Returns user sales as a reference
    public List<Sale> getSales() {
        return database.getSales();
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
    // IF saveTransaction TRUE & transactionType = unique => Back button returns you to the replaced out fragment
    // IF saveTransaction TRUE & transactionType = same => Back button returns you to the first replaced out fragment in the chain
    public void replaceFragment(Fragment fragmentIn, int transactionType, boolean saveTransaction) {
        String transactionLevel;

        if (transactionType == R.string.unique_chain_transaction && saveTransaction) {
            fragmentStack.add(activeFragment);
        }

        transactionLevel = String.valueOf(fragmentStack.size());

        // Pops out all transactions with the same level until one with a different level
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

    // Pops back stack until the required size of fragmentStack has been reached
    public void reduceStackSize(int requiredSize) {
        int size = fragmentStack.size();
        while (size > requiredSize) {
            popBackStack();
            size--;
        }
    }

    // Pops back stack one time
    public void popBackStack() {
        int size = fragmentStack.size();

        getSupportFragmentManager().popBackStackImmediate();
        activeFragment = fragmentStack.pop();
        activeFragment.updateFragment();
    }

    @Override
    public void onBackPressed() {
        if(fragmentStack.size() > 0) {
            activeFragment = fragmentStack.pop();
        }

        super.onBackPressed();
        activeFragment.updateFragment();
    }

    // Sets user account information
    public boolean setRequest(String credentialType, String credentialValue) throws Exception {
        String request = SET_REQUEST + MESSAGE_DELIMITER + credentialType + MESSAGE_DELIMITER + credentialValue;
        String response;

        try {
            response = sendRequest(request);
        } catch (Exception e) {
            throw new Exception("" + R.string.connection_error);
        }

        if (response.equals(ERROR_RESPONSE)) {
            return false;
        }

        return true;
    }

    // Sends the receipt to server
    public boolean sendReceiptRequest(String receipt) throws Exception {
        String request = SEND_REQUEST + MESSAGE_DELIMITER + receipt;
        String response;

        try {
            response = sendRequest(request);
        } catch (Exception e) {
            throw new Exception("" + R.string.connection_error);
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
                case PICK_REQUEST:
                    pickRequest(eventArgs);
                    break;
                case RETURN_REQUEST:
                    returnRequest(eventArgs);
                    break;
                case SALE_REQUEST:
                    saleRequest(eventArgs);
                    break;
                case MOVE_REQUEST:
                    moveRequest(eventArgs);
                    break;
            }
        } catch (Exception e) {
            popUpMessageDialog(e.getMessage());
        }
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

    // Picks a product from its shelf
    public void pickRequest(String requestArgs) throws Exception {
        String[] args = requestArgs.split(MESSAGE_DELIMITER, 2);
        String productID, shopperID;
        productID = args[0];
        shopperID = args[1];
        if (shopperID.equals(clientID)) {
            boolean done = false;
            for (CartItem item : database.getCart()) {
                if (item.getProductID().equals(productID)) {
                    if (item.getPickedAmount() == item.getAmount()) {
                        item.setAmount(item.getAmount() + 1);
                    }
                    item.setPickedAmount(item.getPickedAmount() + 1);
                    done = true;
                    break;
                }
            }
            if (!done) {
                CartItem item = new CartItem(productID);
                item.setPickedAmount(1);
                database.getCart().add(item);
            }

            playMusic(R.raw.pick);
            updateFragment();
        }
    }

    // Returns a product to a shelf
    public void returnRequest(String requestArgs) throws Exception {
        String[] args = requestArgs.split(MESSAGE_DELIMITER, 2);
        String productID, shopperID;

        productID = args[0];
        shopperID = args[1];
        if (shopperID.equals(clientID)) {
            for (CartItem item : database.getCart()) {
                if (item.getProductID().equals(productID)) {
                    if(item.getPickedAmount() == 0){
                        database.getCart().remove(item);
                    }else{
                        item.setPickedAmount(item.getPickedAmount()-1);
                    }
                    updateFragment();
                    playMusic(R.raw.remove);
                    break;
                }
            }
        }
    }

    // Notifies a user for a temporarily sale
    public void saleRequest(String requestArgs) throws Exception {
        String[] args = requestArgs.split(MESSAGE_DELIMITER, 4);
        String productID, shopperID;
        int unitAmount, bonusAmount;
        String message = "";

        productID = args[0];
        shopperID = args[1];
        unitAmount = Integer.parseInt(args[2]);
        bonusAmount = Integer.parseInt(args[3]);
        if (shopperID.equals(clientID)) {
            database.getSales().add(new Sale(productID, unitAmount, bonusAmount));
            updateFragment();
            playMusic(R.raw.sale);
            for (Product product : getProducts()) {
                if (product.getProductId().equals(productID)) {
                    message = product.getName() + " " + product.getAmountPerUnit() + product.getUnitType().name() + " : " + unitAmount + " + " + bonusAmount;
                    break;
                }
            }
            sendNotification(getString(R.string.sale_notification_ticker), getString(R.string.sale_notification_title), message);
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

    @Override
    protected void onStop() {
        super.onStop();

        try {
            SharedPreferences sharedCart = getSharedPreferences(getString(R.string.shared_cart_file), MODE_PRIVATE);
            sharedCart.edit().putString(getString(R.string.shared_cart_key) + clientID, database.getCartJsonString()).apply();
            SharedPreferences sharedSettings = getSharedPreferences(getString(R.string.shared_settings_file), MODE_PRIVATE);
            sharedSettings.edit().putString(getString(R.string.shared_settings_key) + clientID, database.getUserSettingsJsonString()).apply();
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
