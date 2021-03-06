package tamirmo.shopper.Database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import tamirmo.shopper.Database.Class.CartItem;
import tamirmo.shopper.Database.Class.Department;
import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.Database.Class.Sale;
import tamirmo.shopper.Database.Class.Shopper;
import tamirmo.shopper.Database.Class.UserLocation;
import tamirmo.shopper.Database.Class.UserSettings;

public class Database {

    // Item types for all data loaded, or written to JSON
    private static final Type DEPARTMENT_TYPE = new TypeToken<List<Department>>() {
    }.getType();
    private static final Type PRODUCT_TYPE = new TypeToken<List<Product>>() {
    }.getType();
    private static final Type DISCOUNT_TYPE = new TypeToken<List<Discount>>() {
    }.getType();
    private static final Type SHOPPER_TYPE = new TypeToken<Shopper>() {
    }.getType();
    private static final Type CART_TYPE = new TypeToken<List<CartItem>>() {
    }.getType();
    private static final Type SETTINGS_TYPE = new TypeToken<UserSettings>() {
    }.getType();

    // Class attributes
    private List<Department> departments;
    private List<Product> products;
    private List<Discount> discounts;
    private Shopper account;
    private List<CartItem> cart;
    private List<Sale> sales;
    private UserLocation userLocation;
    private UserSettings userSettings;

    // Class Builder
    public Database(){
        cart = new ArrayList<CartItem>();
        sales = new ArrayList<Sale>();
        userLocation = new UserLocation();
        userSettings = new UserSettings();
    }

    // Loads a list of departments from a JSON string
    public void loadDepartmentsFromString(String content) throws Exception {
        JsonReader jsonReader = new JsonReader(new StringReader(content));
        Gson gson = new Gson();
        departments = gson.fromJson(jsonReader, DEPARTMENT_TYPE);
        jsonReader.close();
    }

    // Loads a list of products from a JSON string
    public void loadProductsFromString(String content) throws Exception {
        JsonReader jsonReader = new JsonReader(new StringReader(content));
        Gson gson = new Gson();
        products = gson.fromJson(jsonReader, PRODUCT_TYPE);
        jsonReader.close();
    }

    // Loads a list of discounts from a JSON string
    public void loadDiscountsFromString(String content) throws Exception {
        JsonReader jsonReader = new JsonReader(new StringReader(content));
        Gson gson = new Gson();
        discounts = gson.fromJson(jsonReader, DISCOUNT_TYPE);
        jsonReader.close();
    }

    // Loads a shopper from a JSON string
    public void loadShopperFromString(String content) throws Exception {
        JsonReader jsonReader = new JsonReader(new StringReader(content));
        Gson gson = new Gson();
        account = gson.fromJson(jsonReader, SHOPPER_TYPE);
        jsonReader.close();
    }

    // Loads a list of cart items from a JSON string
    public void loadCartFromString(String content) throws Exception{
        JsonReader jsonReader = new JsonReader(new StringReader(content));
        Gson gson = new Gson();
        cart = gson.fromJson(jsonReader, CART_TYPE);
        jsonReader.close();
    }

    // Loads a list of cart items from a JSON string
    public void loadUserSettingsFromString(String content) throws Exception{
        JsonReader jsonReader = new JsonReader(new StringReader(content));
        Gson gson = new Gson();
        userSettings = gson.fromJson(jsonReader, SETTINGS_TYPE);
        jsonReader.close();
    }

    // Returns a JSON string of all cart data
    public String getCartJsonString()throws Exception{
        Gson gson = new Gson();

        return gson.toJson(cart, CART_TYPE);
    }

    // Returns a JSON string of user settings data
    public String getUserSettingsJsonString() throws Exception{
        Gson gson = new Gson();

        return gson.toJson(userSettings, SETTINGS_TYPE);
    }

    // Returns user account
    public Shopper getAccount(){
        return account;
    }

    // Returns departments data
    public List<Department> getDepartments(){
        return departments;
    }

    // Returns products data
    public List<Product> getProducts(){
        return products;
    }

    // Returns discounts data
    public List<Discount> getDiscounts(){
        return discounts;
    }

    // Returns user cart
    public List<CartItem> getCart(){ return cart;}

    // Returns user sales
    public List<Sale> getSales(){ return sales;}

    // Returns user location
    public UserLocation getUserLocation(){return userLocation;}

    // Returns user settings
    public UserSettings getUserSettings() {
        return userSettings;
    }
}
