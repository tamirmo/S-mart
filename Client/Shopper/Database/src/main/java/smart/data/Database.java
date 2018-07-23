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
	private static final int EMPLOYEE_ARGS_AMOUNT = 3;
	private static final int PRODUCT_ARGS_AMOUNT = 8;
	private static final int SHOPPER_ARGS_AMOUNT = 4;
	private static final int DISCOUNT_ARGS_AMOUNT = 4;

	// Exception messages
	private static final String UNKNOWN_TYPE_EXCEPTION = "Database couldn't identify this data type";
	private static final String PARSING_EXCEPTION = "Database couldn't parse the args";

	// Sizes of the digital map as a matrix
	public static final int MAP_ROWS_COUNT = 14;
	public static final int MAP_COLS_COUNT = 10;

	// Private members to hold server data
	private List<Department> departments;
	private List<Product> products;
	private List<Shopper> shoppers;
	private List<Employee> employees;
	private List<Discount> discounts;

	// Loads a list of departments as JSONs from a String
	public void loadDepartmentsFromString(String fileContent) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(fileContent));
		Gson gson = new Gson();

		departments = gson.fromJson(jsonReader, DEPARTMENT_TYPE);

		jsonReader.close();
	}

	// Loads a list of products as JSONs from a String
	public void loadProductsFromString(String fileContent) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(fileContent));
		Gson gson = new Gson();

		products = gson.fromJson(jsonReader, PRODUCT_TYPE);

		jsonReader.close();
	}

	// Loads a list of shoppers as JSONs from a String
	public void loadShoppersFromString(String fileContent) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(fileContent));
		Gson gson = new Gson();

		shoppers = gson.fromJson(jsonReader, SHOPPER_TYPE);

		jsonReader.close();
	}

	// Loads a list of employees as JSONs from a String
	public void loadEmployeesFromString(String fileContent) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(fileContent));
		Gson gson = new Gson();

		employees = gson.fromJson(jsonReader, EMPLOYEE_TYPE);

		jsonReader.close();
	}

	// Loads a list of discounts as JSONs from a String
	public void loadDiscountsFromString(String fileContent) throws IOException {
		JsonReader jsonReader = new JsonReader(new StringReader(fileContent));
		Gson gson = new Gson();

		discounts = gson.fromJson(jsonReader, DISCOUNT_TYPE);

		jsonReader.close();
	}

	// Returns a string of all departments data
	public String getDepartmentsString() {
		StringBuilder string = new StringBuilder();
		int size = departments.size();

		for (int i = 0; i < size; i++)
			string.append(departments.get(i) + "\n");

		return string.toString();
	}

	// Returns a string of all products data
	public String getProductsString() {
		StringBuilder string = new StringBuilder();
		int size = products.size();

		for (int i = 0; i < size; i++)
			string.append(products.get(i) + "\n");

		return string.toString();
	}

	// Returns a string of all shoppers data
	public String getShoppersString() {
		StringBuilder string = new StringBuilder();
		int size = shoppers.size();

		for (int i = 0; i < size; i++)
			string.append(shoppers.get(i) + "\n");

		return string.toString();
	}

	// Returns a string of all employees data
	public String getEmployeesString() {
		StringBuilder string = new StringBuilder();
		int size = employees.size();

		for (int i = 0; i < size; i++)
			string.append(employees.get(i) + "\n");

		return string.toString();
	}

	// Returns a string of all discounts data
	public String getDiscountsString() {
		StringBuilder string = new StringBuilder();
		int size = discounts.size();

		for (int i = 0; i < size; i++)
			string.append(discounts.get(i) + "\n");

		return string.toString();
	}

	// Adds a department to the database
	public void addDepartment(String args) throws Exception {
		String argsParts[] = args.split(" ", DEPARTMENT_ARGS_AMOUNT);
		String name;
		int id;
		
		try {
			id = Integer.parseInt(argsParts[0]);
			name = argsParts[1];
		}catch (Exception e) {
			throw new Exception(PARSING_EXCEPTION + " : " + args);
		}
		
		departments.add(new Department(id, name));
	}

	// Adds a product to the database
	public void addProduct(String args) throws Exception {
		String argsParts[] = args.split(" ", PRODUCT_ARGS_AMOUNT);
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
			throw new Exception(PARSING_EXCEPTION + " : " + args);
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

		products.add(new Product(productId, name, departmentId, pricePerUnit, locationX, locationY, amountPerUnit,
				unitType));
	}

	// Adds a shopper to the database
	public void addShopper(String args) throws Exception {
		String argsParts[] = args.split(" ", SHOPPER_ARGS_AMOUNT+1); // +1 because we want to delete all other data that might after command args
		long id; 
		String email, password, creditCard;
		
		try {
			id = Long.parseLong(argsParts[0]);
			email = argsParts[1];
			password = argsParts[2];
			creditCard = argsParts[3];
		}catch (Exception e) {
			throw new Exception(PARSING_EXCEPTION + " : " + args);
		}
		
		shoppers.add(new Shopper(id, email, password, creditCard));	
	}

	// Adds an employee to the database
	public void addEmployee(String args) throws Exception {
		String argsParts[] = args.split(" ", EMPLOYEE_ARGS_AMOUNT+1); // +1 because we want to delete all other data that might after command args
		int id; 
		String email, password;
		
		try {
			id = Integer.parseInt(argsParts[0]);
			email = argsParts[1];
			password = argsParts[2];
		}catch (Exception e) {
			throw new Exception(PARSING_EXCEPTION + " : " + args);
		}

		employees.add(new Employee(id, email, password));
	}

	// Adds a discount to the database
	public void addDiscount(String args) throws Exception {
		String argsParts[] = args.split(" ", DISCOUNT_ARGS_AMOUNT);
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
			throw new Exception(PARSING_EXCEPTION + " : " + args);
		}
		
		discounts.add(new Discount(productId, normalPrice, discountedPrice, shopperId));
	}

	
	
	
	
	
	
	
	
	
	
	
	
	public List<Department> getDepartments() {
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
	 */
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
	 */
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
	 */
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

	/**
	 * Pulling the details of the shopper with the given id.
	 * 
	 * @param shopperId long, The id of the shopper to look for
	 * @return ShooperDetails, The details of the shopper
	 */
	public Shopper getShopperDetailsById(long shopperId) {
		Shopper foundDetails = null;

		for (Shopper shopperDetails : shoppers) {
			if (shopperDetails.getId() == shopperId) {
				foundDetails = shopperDetails;
			}
		}

		return foundDetails;
	}

	/**
	 * Pulling the details of the employee with the given id.
	 * 
	 * @param employeeId long, The id of the employee to look for
	 * @return EmployeeDetails, The details of the employee
	 */
	public Employee getEmployeeDetailsById(long employeeId) {
		Employee foundDetails = null;

		for (Employee employeeDetails : employees) {
			if (employeeDetails.getId() == employeeId) {
				foundDetails = employeeDetails;
			}
		}

		return foundDetails;
	}

	// Methods for reading lists of data from json file:

	public List<Department> readDepartmentsFromFile(String fileName) {
		List<Department> data = readListFromFile(fileName, DEPARTMENT_TYPE);
		departments = data;

		return data;
	}

	public List<Product> readProductsFromFile(String fileName) {
		List<Product> data = readListFromFile(fileName, PRODUCT_TYPE);
		products = data;

		return data;
	}

	public List<Shopper> readShoppersDetailsFromFile(String fileName) {
		List<Shopper> data = readListFromFile(fileName, SHOPPER_TYPE);
		shoppers = data;

		return data;
	}

	public List<Employee> readEmployeeDetailsFromFile(String fileName) {
		List<Employee> data = readListFromFile(fileName, EMPLOYEE_TYPE);
		employees = data;

		return data;
	}

	public List<Discount> readDiscountsFromFile(String fileName) {
		List<Discount> data = readListFromFile(fileName, DISCOUNT_TYPE);
		discounts = data;

		return data;
	}

	/**
	 * This method creates a Json String from the given discounts
	 * 
	 * @param discounts List<Discount>, The discounts to convert
	 * @return String, A Json string with the discounts
	 */
	public String getDiscountsAsJson(List<Discount> discounts) {
		Gson gson = new Gson();
		return gson.toJson(discounts, DISCOUNT_TYPE);
	}

	/**
	 * This method creates a Json String from the given cart
	 * 
	 * @param cart List<CartItem>, The items in the cart
	 * @return String, A Json string with the cart items
	 */
	public String getCartAsJson(List<CartItem> cart) {
		Gson gson = new Gson();
		return gson.toJson(cart, CART_TYPE);
	}

	// Methods for reading lists of data from json string (given in udp from the
	// server):

	public List<Shopper> readShoppersDetailsFromJsonString(String jsonContent) {
		Gson gson = new Gson();
		List<Shopper> shoppers = gson.fromJson(jsonContent, SHOPPER_TYPE);
		this.shoppers = shoppers;

		return shoppers;
	}

	public List<Employee> readEmployeesDetailsFromJsonString(String jsonContent) {
		Gson gson = new Gson();
		List<Employee> employees = gson.fromJson(jsonContent, EMPLOYEE_TYPE);
		this.employees = employees;

		return employees;
	}

	public List<Product> readProductsFromJsonString(String jsonContent) {

		Gson gson = new Gson();
		List<Product> products = gson.fromJson(jsonContent, PRODUCT_TYPE);
		this.products = products;
		return products;
	}

	public List<Department> readDepartmentsFromJsonString(String jsonContent) {

		Gson gson = new Gson();
		List<Department> departments = gson.fromJson(jsonContent, DEPARTMENT_TYPE);
		this.departments = departments;
		return departments;
	}

	/**
	 * Reading discounts from a json string and updating the products with the
	 * discounted price.
	 * 
	 * @param jsonContent String, The json string containing the discounts.
	 * @return List<Discount>, The discounts from the json.
	 */
	public List<Discount> readDiscountsFromJsonString(String jsonContent) {

		Gson gson = new Gson();
		List<Discount> discounts = gson.fromJson(jsonContent, DISCOUNT_TYPE);

		if (discounts != null) {
			// Setting Product objects for each discount
			for (Discount discount : discounts) {
				for (Product product : products) {
					if (discount.getProductId() == product.getProductId()) {
						discount.setProduct(product);
						// Updating the price of the product for this discount
						product.setPricePerUnit(discount.getDiscountedPrice());
					}
				}
			}
		} else {
			discounts = null;
		}

		this.discounts = discounts;
		return discounts;
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

	// Methods for saving lists of data to json file:

	public void saveShoppersDetailsToFile(String fileName, List<Shopper> shoppersDetails) {
		saveListToFile(fileName, shoppersDetails, SHOPPER_TYPE);
		this.shoppers = shoppersDetails;
	}

	public void saveEmployeesDetailsToFile(String fileName, List<Employee> employeesDetails) {
		saveListToFile(fileName, employeesDetails, EMPLOYEE_TYPE);
		this.employees = employeesDetails;
	}

	public void saveProductsToFile(String fileName, List<Product> products) {
		saveListToFile(fileName, products, PRODUCT_TYPE);
		this.products = products;
	}

	public void saveDepartmentsToFile(String fileName, List<Department> departments) {
		saveListToFile(fileName, departments, DEPARTMENT_TYPE);
		this.departments = departments;
	}

	public void saveDiscountsToFile(String fileName, List<Discount> discounts) {
		saveListToFile(fileName, discounts, DISCOUNT_TYPE);
		this.discounts = discounts;
	}

	public String getDepartmentsJsonString() {
		String departmentsJson = null;
		if (departments != null) {
			Gson gson = new Gson();
			departmentsJson = gson.toJson(departments, DEPARTMENT_TYPE);
		}

		return departmentsJson;
	}

	public String getProductsJsonString() {
		String productsJson = null;
		if (products != null) {
			Gson gson = new Gson();
			productsJson = gson.toJson(products, PRODUCT_TYPE);
		}

		return productsJson;
	}

	public String getDiscountsJsonString() {
		String discountsJson = null;
		if (discounts != null) {
			Gson gson = new Gson();
			discountsJson = gson.toJson(discounts, DISCOUNT_TYPE);
		}

		return discountsJson;
	}

	/**
	 * Generic method for reading a list from json file.
	 * 
	 * @param fileName  String, The file to read the list from
	 * @param itemsType Type, The type of object to read (List<T>)
	 * @return A list of type T read from the file in the given path
	 */
	private <T> List<T> readListFromFile(String fileName, Type itemsType) {

		Gson gson = new Gson();
		JsonReader reader;
		List<T> data = null;
		try {
			reader = new JsonReader(new FileReader(fileName));
			data = gson.fromJson(reader, itemsType);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return data;
	}

	/**
	 * Generic method for writing a list of objects to a json file
	 * 
	 * @param fileName  String, The file to write the list to
	 * @param list      List<T>, The list to read
	 * @param itemsType Type, The type of object to write (List<T>)
	 */
	private <T> void saveListToFile(String fileName, List<T> list, Type itemsType) {
		Gson gson = new Gson();
		String departmentsJson = gson.toJson(list, itemsType);

		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(departmentsJson);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
