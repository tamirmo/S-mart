package tamirmo.employee.Database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import tamirmo.employee.Database.Class.EmptyTask;
import tamirmo.employee.Database.Class.ExpiredTask;
import tamirmo.employee.Database.Class.MisplacedTask;
import tamirmo.employee.Database.Class.Product;
import tamirmo.employee.Database.Class.Employee;
import tamirmo.employee.Database.Class.UserLocation;
import tamirmo.employee.Database.Class.UserSettings;

public class Database {

    // Item types for all data loaded, or written to JSON
    private static final Type PRODUCT_TYPE = new TypeToken<List<Product>>() {
    }.getType();
    private static final Type EMPLOYEE_TYPE = new TypeToken<Employee>() {
    }.getType();
    private static final Type SETTINGS_TYPE = new TypeToken<UserSettings>() {
    }.getType();

    // Class attributes
    private List<Product> products;
    private Employee account;
    private List<EmptyTask> emptyTasks;
    private List<ExpiredTask> expiredTasks;
    private List<MisplacedTask> misplacedTasks;
    private UserLocation userLocation;
    private UserSettings userSettings;

    // Class Builder
    public Database() {
        emptyTasks = new ArrayList<EmptyTask>();
        expiredTasks = new ArrayList<ExpiredTask>();
        misplacedTasks = new ArrayList<MisplacedTask>();
        userLocation = new UserLocation();
        userSettings = new UserSettings();
    }

    // Loads a list of products from a JSON string
    public void loadProductsFromString(String content) throws Exception {
        JsonReader jsonReader = new JsonReader(new StringReader(content));
        Gson gson = new Gson();
        products = gson.fromJson(jsonReader, PRODUCT_TYPE);
        jsonReader.close();
    }

    // Loads a shopper from a JSON string
    public void loadEmployeeFromString(String content) throws Exception {
        JsonReader jsonReader = new JsonReader(new StringReader(content));
        Gson gson = new Gson();
        account = gson.fromJson(jsonReader, EMPLOYEE_TYPE);
        jsonReader.close();
    }

    // Loads a list of cart items from a JSON string
    public void loadUserSettingsFromString(String content) throws Exception {
        JsonReader jsonReader = new JsonReader(new StringReader(content));
        Gson gson = new Gson();
        userSettings = gson.fromJson(jsonReader, SETTINGS_TYPE);
        jsonReader.close();
    }

    // Returns a JSON string of user settings data
    public String getUserSettingsJsonString() throws Exception {
        Gson gson = new Gson();

        return gson.toJson(userSettings, SETTINGS_TYPE);
    }

    // Returns user account
    public Employee getAccount() {
        return account;
    }

    // Returns products data
    public List<Product> getProducts() {
        return products;
    }

    // Returns empty tasks data
    public List<EmptyTask> getEmptyTasks() {
        return emptyTasks;
    }

    // Returns expired tasks data
    public List<ExpiredTask> getExpiredTasks() {
        return expiredTasks;
    }

    // Returns misplaced tasks data
    public List<MisplacedTask> getMisplacedTasks() {
        return misplacedTasks;
    }

    // Returns user location
    public UserLocation getUserLocation() {
        return userLocation;
    }

    // Returns user settings
    public UserSettings getUserSettings() {
        return userSettings;
    }
}
