package smart.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.lang.model.type.UnknownTypeException;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import smart.data.Product.UnitType;

public class Database {

	// Server data types names
	private static final String DEPARTMENT_NAME = "department";
	private static final String PRODUCT_NAME = "product";
	private static final String SHOPPER_NAME = "shopper";
	private static final String EMPLOYEE_NAME = "employee";
	private static final String DISCOUNT_NAME = "discount";

	// Types for all data loaded and written to JSON:
	private static final Type DEPARTMENT_TYPE = new TypeToken<List<Department>>() {
	}.getType();
	private static final Type EMPLOYEE_TYPE = new TypeToken<List<Employee>>() {
	}.getType();
	private static final Type PRODUCT_TYPE = new TypeToken<List<Product>>() {
	}.getType();
	private static final Type SHOPPER_TYPE = new TypeToken<List<Shopper>>() {
	}.getType();
	private static final Type DISCOUNT_TYPE = new TypeToken<List<Discount>>() {
	}.getType();
	private static final Type CART_TYPE = new TypeToken<List<CartItem>>() {
	}.getType();

	// Args amount of each Type
	private static final int DEPARTMENT_ARGS_AMOUNT = 2;
	private static final int EMPLOYEE_ARGS_AMOUNT = 4; // +1 because we want to delete all other data that might be after command args
	private static final int PRODUCT_ARGS_AMOUNT = 8;
	private static final int SHOPPER_ARGS_AMOUNT = 5; // +1 because we want to delete all other data that might be after command args
	private static final int DISCOUNT_ARGS_AMOUNT = 4;

	// Exception messages
	private static final String UNKNOWN_TYPE_EXCEPTION = "Database couldn't identify this data type";
	private static final String PARSING_EXCEPTION = "Database couldn't parse the args";
	private static final String LOCATION_EXCEPTION = "Database couldn't find an item in the index";

	// Sizes of the digital map as a matrix
	public static final int MAP_ROWS_COUNT = 14;
	public static final int MAP_COLS_COUNT = 10;

	// Private members to hold server data
	private List<Department> departments;
	private List<Product> products;
	private List<Shopper> shoppers;
	private List<Employee> employees;
	private List<Discount> discounts;
	private String className;
	
	public Database(){
		// Gets class name for future exceptions messages
		className = new Object(){}.getClass().getEnclosingClass().getSimpleName();
	}

	// Loads a list of departments from a JSON string
	public void loadDepartmentsFromString(String content) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(content));
		Gson gson = new Gson();

		departments = gson.fromJson(jsonReader, DEPARTMENT_TYPE);

		jsonReader.close();
	}

	// Loads a list of products from a JSON string
	public void loadProductsFromString(String content) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(content));
		Gson gson = new Gson();

		products = gson.fromJson(jsonReader, PRODUCT_TYPE);

		jsonReader.close();
	}

	// Loads a list of shoppers from a JSON string
	public void loadShoppersFromString(String content) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(content));
		Gson gson = new Gson();

		shoppers = gson.fromJson(jsonReader, SHOPPER_TYPE);

		jsonReader.close();
	}

	// Loads a list of employees from a JSON string
	public void loadEmployeesFromString(String content) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(content));
		Gson gson = new Gson();

		employees = gson.fromJson(jsonReader, EMPLOYEE_TYPE);

		jsonReader.close();
	}

	// Loads a list of discounts from a JSON string
	public void loadDiscountsFromString(String content) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(content));
		Gson gson = new Gson();

		discounts = gson.fromJson(jsonReader, DISCOUNT_TYPE);

		jsonReader.close();
	}
	
	// Returns a string of all departments data
	public String getDepartmentsString() {
		StringBuilder string = new StringBuilder();
		int size = departments.size();
		int counter = 0;

		for (int i = 0; i < size; i++) {
			string.append(counter + " : " + departments.get(i) + "\n");
			counter++;
		}

		return string.toString();
	}
	
	// Returns a string of all products data
	public String getProductsString() {
		StringBuilder string = new StringBuilder();
		int size = products.size();
		int counter = 0;

		for (int i = 0; i < size; i++) {
			string.append(counter + " : " + products.get(i) + "\n");
			counter++;
		}

		return string.toString();
	}
	
	// Returns a string of all shoppers data
	public String getShoppersString() {
		StringBuilder string = new StringBuilder();
		int size = shoppers.size();
		int counter = 0;
		
		for (int i = 0; i < size; i++) {
			string.append(counter + " : " + shoppers.get(i) + "\n");
			counter++;
		}

		return string.toString();
	}
	
	// Returns a string of all employees data
	public String getEmployeesString() {
		StringBuilder string = new StringBuilder();
		int size = employees.size();
		int counter = 0;
		
		for (int i = 0; i < size; i++) {
			string.append(counter + " : " + employees.get(i) + "\n");
			counter++;
		}

		return string.toString();
	}
	
	// Returns a string of all discounts data
	public String getDiscountsString() {
		StringBuilder string = new StringBuilder();
		int size = discounts.size();
		int counter = 0;

		for (int i = 0; i < size; i++) {
			string.append(counter + " : "+discounts.get(i) + "\n");
			counter++;
		}

		return string.toString();
	}
	
	// Creates a department from a string
	private Department processDepartmentString(String itemArgs) throws Exception{
		String argsParts[] = itemArgs.split(" ", DEPARTMENT_ARGS_AMOUNT);
		String name;
		int id;
		
		try {
			id = Integer.parseInt(argsParts[0]);
			name = argsParts[1];
		}catch (Exception e) {
			throw new Exception(PARSING_EXCEPTION + " : " + itemArgs);
		}
		
		return new Department(id, name);
	}
	
	// Creates a product from a string
	private Product processProductString(String itemArgs) throws Exception{
		String argsParts[] = itemArgs.split(" ", PRODUCT_ARGS_AMOUNT);
		String name;
		long productId;
		int departmentId, locationX, locationY;
		double pricePerUnit, amountPerUnit;
		UnitType unitType;

		try {
			productId = Long.parseLong(argsParts[0]);
			name = argsParts[7];
			departmentId = Integer.parseInt(argsParts[1]);
			pricePerUnit = Double.parseDouble(argsParts[2]);
			locationX = Integer.parseInt(argsParts[3]);
			locationY = Integer.parseInt(argsParts[4]);
			amountPerUnit = Double.parseDouble(argsParts[5]);
		} catch (Exception e) {
			throw new Exception(PARSING_EXCEPTION + " : " + itemArgs);
		}

		switch (argsParts[6].toLowerCase()) {
		case "default":
			unitType = UnitType.DEFAULT;
			break;
		case "kg":
			unitType = UnitType.KG;
			break;
		case "l":
			unitType = UnitType.L;
			break;
		case "g":
			unitType = UnitType.G;
			break;
		default:
			throw new Exception(UNKNOWN_TYPE_EXCEPTION + " : " + argsParts[6]);
		}
		
		return new Product(productId, name, departmentId, pricePerUnit, locationX, locationY, amountPerUnit, unitType);
	}
	
	// Creates a shopper from a string
	private Shopper processShopperString(String itemArgs) throws Exception{
		String argsParts[] = itemArgs.split(" ", SHOPPER_ARGS_AMOUNT);
		long id; 
		String email, password, creditCard;
		
		try {
			id = Long.parseLong(argsParts[0]);
			email = argsParts[1];
			password = argsParts[2];
			creditCard = argsParts[3];
		}catch (Exception e) {
			throw new Exception(PARSING_EXCEPTION + " : " + itemArgs);
		}
		
		return new Shopper(id, email, password, creditCard);
	}
	
	// Creates an employee from a string
	private Employee processEmployeeString(String itemArgs) throws Exception{
		String argsParts[] = itemArgs.split(" ", EMPLOYEE_ARGS_AMOUNT);
		int id; 
		String email, password;
		
		try {
			id = Integer.parseInt(argsParts[0]);
			email = argsParts[1];
			password = argsParts[2];
		}catch (Exception e) {
			throw new Exception(PARSING_EXCEPTION + " : " + itemArgs);
		}
		
		return new Employee(id, email, password);
	}
	
	// Creates a discount from a string
	private Discount processDiscountString(String itemArgs) throws Exception{
		String argsParts[] = itemArgs.split(" ", DISCOUNT_ARGS_AMOUNT);
		long productId;
		double normalPrice, discountedPrice;
		int shopperId;
		
		try {
			productId = Long.parseLong(argsParts[0]);
			normalPrice = Double.parseDouble(argsParts[1]);
			discountedPrice = Double.parseDouble(argsParts[2]);
			if(argsParts[3].toLowerCase().equals("all"))
				shopperId = -1;
			else
				shopperId = Integer.parseInt(argsParts[3]);
		}catch (Exception e) {
			throw new Exception(PARSING_EXCEPTION + " : " + itemArgs);
		}
		
		return new Discount(productId, normalPrice, discountedPrice, shopperId);
	}
	
	// Adds a department to the database
	public void addDepartment(String itemArgs) throws Exception {
		Department item = processDepartmentString(itemArgs);
		
		departments.add(item);
	}
	
	// Adds a product to the database
	public void addProduct(String itemArgs) throws Exception {
		Product item = processProductString(itemArgs);
		
		products.add(item);
	}
	
	// Adds a shopper to the database
	public void addShopper(String itemArgs) throws Exception {
		Shopper item = processShopperString(itemArgs);
		
		shoppers.add(item);	
	}
	
	// Adds an employee to the database
	public void addEmployee(String itemArgs) throws Exception {
		Employee item = processEmployeeString(itemArgs);

		employees.add(item);
	}

	// Adds a discount to the database
	public void addDiscount(String itemArgs) throws Exception {
		Discount item = processDiscountString(itemArgs);
		
		discounts.add(item);
	}

	// Parse item number from a string
	private int parseItemNumber(String itemNumber) throws Exception {
		try {
			return Integer.parseInt(itemNumber);
		} catch (Exception e) {
			throw new Exception(PARSING_EXCEPTION + " : " + itemNumber);
		}
	}
	
	// Edits a department from the database
	public void editDepartment(String itemNumber, String itemArgs) throws Exception {
		Department item = processDepartmentString(itemArgs);
		int index = parseItemNumber(itemNumber);
	
		try {
			// Replaces an item
			departments.set(index, item);
		}catch(IndexOutOfBoundsException e) {
			throw new Exception(LOCATION_EXCEPTION + " : " + index);
		}
	}
	
	// Edits a product from the database
	public void editProduct(String itemNumber, String itemArgs) throws Exception {
		Product item = processProductString(itemArgs);
		int index = parseItemNumber(itemNumber);

		try {
			// Replaces an item
			products.set(index, item);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(LOCATION_EXCEPTION + " : " + index);
		}
	}

	// Edits a shopper from the database
	public void editShopper(String itemNumber, String itemArgs) throws Exception {
		Shopper item = processShopperString(itemArgs);
		int index = parseItemNumber(itemNumber);

		try {
			// Replaces an item
			shoppers.set(index, item);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(LOCATION_EXCEPTION + " : " + index);
		}
	}

	// Edits an employee from the database
	public void editEmployee(String itemNumber, String itemArgs) throws Exception {
		Employee item = processEmployeeString(itemArgs);
		int index = parseItemNumber(itemNumber);

		try {
			// Replaces an item
			employees.set(index, item);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(LOCATION_EXCEPTION + " : " + index);
		}
	}

	// Edits discount from the database
	public void editDiscount(String itemNumber, String itemArgs) throws Exception {
		Discount item = processDiscountString(itemArgs);
		int index = parseItemNumber(itemNumber);

		try {
			// Replaces an item
			discounts.set(index, item);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(LOCATION_EXCEPTION + " : " + index);
		}
	}
	
	// Removes a department from the database
	public void removeDepartment(String itemNumber) throws Exception {
		int index = parseItemNumber(itemNumber);
		
		try {
			departments.remove(index);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(LOCATION_EXCEPTION + " : " + itemNumber);
		}
	}
	
	// Removes a product from the database
	public void removeProduct(String itemNumber) throws Exception {
		int index = parseItemNumber(itemNumber);
		
		try {
			products.remove(index);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(LOCATION_EXCEPTION + " : " + itemNumber);
		}
	}
	
	// Removes a shopper from the database
	public void removeShopper(String itemNumber) throws Exception {
		int index = parseItemNumber(itemNumber);
		
		try {
			shoppers.remove(index);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(LOCATION_EXCEPTION + " : " + itemNumber);
		}
	}
	
	// Removes an employee
	public void removeEmployee(String itemNumber) throws Exception {
		int index = parseItemNumber(itemNumber);
		
		try {
			employees.remove(index);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(LOCATION_EXCEPTION + " : " + itemNumber);
		}
	}
	
	// Removes a discount
	public void removeDiscount(String itemNumber) throws Exception {
		int index = parseItemNumber(itemNumber);
		
		try {
			discounts.remove(index);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(LOCATION_EXCEPTION + " : " + itemNumber);
		}
	}
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// Returns a JSON string of all departments data
	public String getDepartmentsJsonString() {
		Gson gson = new Gson();

		return gson.toJson(departments, DEPARTMENT_TYPE);
	}

	// Returns a JSON string of all products data
	public String getProductsJsonString() {
			Gson gson = new Gson();
			
		return gson.toJson(products, PRODUCT_TYPE);
	}
	
	// Returns a JSON string of all discounts data
	public String getDiscountsJsonString() {
		Gson gson = new Gson();
		
		return gson.toJson(discounts, DISCOUNT_TYPE);
	}
	
	// Returns a JSON string of all shoppers data
	public String getShoppersJsonString() {
		Gson gson = new Gson();
		
		return gson.toJson(shoppers, SHOPPER_TYPE);
	}
	
	// Returns a JSON string of all employees data
	public String getEmployeesJsonString() {
		Gson gson = new Gson();
		
		return gson.toJson(employees, EMPLOYEE_TYPE);
	}
	
	// Exits Database
	public void exit() {
		// Just in case we will want something special like print className
	}
	
	
	
	
	
	
	
	
	
	/*public List<Department> getDepartments() {
		return departments;
	}

	public List<Product> getProducts() {
		return products;
	}

	public List<Shopper> getShopperDetails() {
		return shoppers;
	}

	public List<Employee> getEmployees() {
		return employees;
	}

	public List<Discount> getDiscounts() {
		return discounts;
	}

	/**
	 * Pulling only the discounts for the given shopper as Json string (personalized
	 * with it's shopper id and general).
	 * 
	 * @param shopperId long, The id of the shopper to apply only the discounts with
	 *                  this id or general id.
	 * @return String, The discounts for the user with the given id as Json string.
	 
	public String getAllShopperDiscounts(long shopperId) {
		List<Discount> userDiscounts = new ArrayList<>();

		if (discounts != null) {
			// Setting Product objects for each discount
			for (Discount discount : discounts) {
				// Checking if this discount belongs to the current use or it is a general one
				// (general belongs to all users)
				if (discount.getShopperId() == Discount.GENERAL_DISCOUNT_SHOPPER_ID
						|| discount.getShopperId() == shopperId) {

					// Adding the current discount to the user's discounts list
					userDiscounts.add(discount);
				}
			}
		}

		// Returning the Json representation of this list
		return new Gson().toJson(userDiscounts, DISCOUNT_TYPE);
	}

	/**
	 * Reading cart Json file
	 * 
	 * @param fileName String, The name of the file to read
	 * @return String, The content of the json file
	 
	public String readCartFromFile(String fileName) {
		String line = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			line = br.readLine();
			br.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return line;
	}

	/**
	 * Saving the given cart (represented as Json) to the given path
	 * 
	 * @param fileName String, The path to save
	 * @param cartJson String, The cart as Json
	 
	public void saveCartToFile(String fileName, String cartJson) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(cartJson);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public List<CartItem> readCartFromJsonString(String jsonContent) {

		Gson gson = new Gson();
		List<CartItem> cart = gson.fromJson(jsonContent, CART_TYPE);

		if (cart != null) {
			// Setting Product objects for each cart item
			for (CartItem cartItem : cart) {
				for (Product product : products) {
					if (cartItem.getProductId() == product.getProductId()) {
						cartItem.setProduct(product);
					}
				}
			}
		} else {
			cart = new ArrayList<>();
		}

		return cart;
	}
	*/
}
