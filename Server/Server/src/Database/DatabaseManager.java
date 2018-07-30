package Database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;


/*
 * Data types examples
 * a. new Department(0, "Drinks");
 * b. new Product(1, "Coca Cola", 0,0.25, 5,9, 1.5, Product.UnitType.L);
 * c. new ShopperDetails(0, "tamirmoa123@gmail.com", "1234", "19838394");
 * d. new EmployeeDetails(0, "moshe@gmail.com", "1234");
 * e. new Discount(1, 0.25,0.15,Discount.GENERAL_DISCOUNT_SHOPPER_ID);
 */

/*
 * A data type as a JSON example :
 * {"productId":1,"name":"Coca Cola","departmentId":0,"pricePerUnit":0.25,"locationX":5,"locationY":9,"amountPerUnit":1.5,"unitType":"L"}
 */

/*
 * A list of JSON example :
 * [{"productId":1,"shopperId":-1,"originalPrice":0.25,"discountedPrice":0.15},{"productId":2,"shopperId":-1,"originalPrice":2.99,"discountedPrice":1.99}]
 */

// Responsible to process the data types from requests/commands
public class DatabaseManager {
	
	// Class files 
	private static final String DEPARTMENTS_FILE = "departments.json";
	private static final String PRODUCTS_FILE = "products.json";
	private static final String SHOPPERS_FILE = "shoppers.json";
	private static final String EMPLOYEES_FILE = "employees.json";
	private static final String DISCOUNTS_FILE = "discounts.json";
	
	// Class Messages
	private static final String END_OF_FILE = "\\Z";

	// Class Exceptions
	private static final String FILE_NOT_FOUND_EXCEPTION = "File not found";
	private static final String UNKNOWN_TYPE_EXCEPTION = "Unknown type";
	private static final String PARSING_EXCEPTION = "Couldn't parse item number";
	private static final String EXCEPTION_DELIMITER = " : ";

	// Item types
	private static final String DEPARTMENT_TYPE = "department";
	private static final String PRODUCT_TYPE = "product";
	private static final String SHOPPER_TYPE = "shopper";
	private static final String EMPLOYEE_TYPE = "employee";
	private static final String DISCOUNT_TYPE = "discount";
	
	// Class attributes
	private Database database;
	private String className;
	
	public DatabaseManager() throws Exception {
		// Gets class name for future exceptions messages
		className = new Object(){}.getClass().getEnclosingClass().getSimpleName();
		
		// Continues to build the server from the Database layer
		database = new Database();
		
		// Loads all server data
		loadFromFile(DEPARTMENTS_FILE,DEPARTMENT_TYPE);
		loadFromFile(EMPLOYEES_FILE,EMPLOYEE_TYPE);
		loadFromFile(PRODUCTS_FILE,PRODUCT_TYPE);
		loadFromFile(SHOPPERS_FILE,SHOPPER_TYPE);
		loadFromFile(DISCOUNTS_FILE,DISCOUNT_TYPE);
	}
	
	// Loads all data from a file
	private void loadFromFile(String fileName, String dataType) throws Exception {
		String fileContent;
		
		try(Scanner input = new Scanner(new File(fileName))){
			fileContent = input.useDelimiter(END_OF_FILE).next();
			
			switch(dataType.toLowerCase()) {
			case DEPARTMENT_TYPE:
				database.loadDepartmentsFromString(fileContent);
				break;
			case PRODUCT_TYPE:
				database.loadProductsFromString(fileContent);
				break;
			case SHOPPER_TYPE:
				database.loadShoppersFromString(fileContent);
				break;
			case EMPLOYEE_TYPE:
				database.loadEmployeesFromString(fileContent);
				break;
			case DISCOUNT_TYPE:
				database.loadDiscountsFromString(fileContent);
				break;
			default :
				throw new Exception(className + EXCEPTION_DELIMITER + UNKNOWN_TYPE_EXCEPTION + EXCEPTION_DELIMITER + dataType);
			}
		}catch(FileNotFoundException ex) {
			throw new Exception(className + EXCEPTION_DELIMITER +FILE_NOT_FOUND_EXCEPTION + EXCEPTION_DELIMITER + fileName);
		}
	}
	
	// Returns a string of all data of a certain type 
	public String viewItems(String itemType) throws Exception {
		String response;

		switch (itemType.toLowerCase()) {
		case DEPARTMENT_TYPE:
			response = database.getDepartmentsString();
			break;
		case PRODUCT_TYPE:
			response = database.getProductsString();
			break;
		case SHOPPER_TYPE:
			response = database.getShoppersString();
			break;
		case EMPLOYEE_TYPE:
			response = database.getEmployeesString();
			break;
		case DISCOUNT_TYPE:
			response = database.getDiscountsString();
			break;
		default:
			throw new Exception(className + EXCEPTION_DELIMITER+ UNKNOWN_TYPE_EXCEPTION + EXCEPTION_DELIMITER + itemType);
		}

		return response;
	}
	
	// Adds an item to the database
	public void addItem(String itemType, String itemArgs) throws Exception {		
		switch (itemType.toLowerCase()) {
		case DEPARTMENT_TYPE:
			database.addDepartment(itemArgs);
			break;
		case PRODUCT_TYPE:
			database.addProduct(itemArgs);
			break;
		case SHOPPER_TYPE:
			database.addShopper(itemArgs);
			break;
		case EMPLOYEE_TYPE:
			database.addEmployee(itemArgs);
			break;
		case DISCOUNT_TYPE:
			database.addDiscount(itemArgs);
			break;
		default:
			throw new Exception(className + EXCEPTION_DELIMITER+ UNKNOWN_TYPE_EXCEPTION + EXCEPTION_DELIMITER + itemType);
		}
	}
	
	// Parse item number from a string
	private int parseItemNumber(String itemNumber) throws Exception {
		try {
			return Integer.parseInt(itemNumber);
		} catch (Exception e) {
			throw new Exception(className + EXCEPTION_DELIMITER + PARSING_EXCEPTION + EXCEPTION_DELIMITER + itemNumber);
		}
	}

	// Edits an item from the database
	public void editItem(String itemType, String itemNumber, String itemArgs) throws Exception {
		int number = parseItemNumber(itemNumber);
		switch (itemType.toLowerCase()) {
		case DEPARTMENT_TYPE:
			database.editDepartment(number, itemArgs);
			break;
		case PRODUCT_TYPE:
			database.editProduct(number, itemArgs);
			break;
		case SHOPPER_TYPE:
			database.editShopper(number, itemArgs);
			break;
		case EMPLOYEE_TYPE:
			database.editEmployee(number, itemArgs);
			break;
		case DISCOUNT_TYPE:
			database.editDiscount(number, itemArgs);
			break;
		default:
			throw new Exception(
					className + EXCEPTION_DELIMITER + UNKNOWN_TYPE_EXCEPTION + EXCEPTION_DELIMITER + itemType);
		}
	}
	
	// Removes an item from the database
	public void removeItem(String itemType, String itemNumber) throws Exception {
		int number = parseItemNumber(itemNumber);
		
		switch (itemType.toLowerCase()) {
		case DEPARTMENT_TYPE:
			database.removeDepartment(number);
			break;
		case PRODUCT_TYPE:
			database.removeProduct(number);
			break;
		case SHOPPER_TYPE:
			database.removeShopper(number);
			break;
		case EMPLOYEE_TYPE:
			database.removeEmployee(number);
			break;
		case DISCOUNT_TYPE:
			database.removeDiscount(number);
			break;
		default:
			throw new Exception(className + EXCEPTION_DELIMITER + UNKNOWN_TYPE_EXCEPTION + EXCEPTION_DELIMITER + itemType);
		}
	}
	
	// Saves all data to a file
	private void saveToFile(String fileName, String dataType) throws Exception {
		String fileContent;

		try (PrintWriter output = new PrintWriter(fileName)) {
			switch (dataType.toLowerCase()) {
			case DEPARTMENT_TYPE:
				fileContent = database.getDepartmentsJsonString();
				break;
			case PRODUCT_TYPE:
				fileContent = database.getProductsJsonString();
				break;
			case SHOPPER_TYPE:
				fileContent = database.getShoppersJsonString();
				break;
			case EMPLOYEE_TYPE:
				fileContent = database.getEmployeesJsonString();
				break;
			case DISCOUNT_TYPE:
				fileContent = database.getDiscountsJsonString();
				break;
			default:
				throw new Exception(
						className + EXCEPTION_DELIMITER + UNKNOWN_TYPE_EXCEPTION + EXCEPTION_DELIMITER + dataType);
			}
			output.println(fileContent);
		} catch (FileNotFoundException ex) {
			throw new Exception(
					className + EXCEPTION_DELIMITER + FILE_NOT_FOUND_EXCEPTION + EXCEPTION_DELIMITER + fileName);
		}
	}
	
	public void exit() throws Exception {		
		saveToFile(DEPARTMENTS_FILE,DEPARTMENT_TYPE);
		saveToFile(EMPLOYEES_FILE,EMPLOYEE_TYPE);
		saveToFile(PRODUCTS_FILE,PRODUCT_TYPE);
		saveToFile(SHOPPERS_FILE,SHOPPER_TYPE);
		saveToFile(DISCOUNTS_FILE,DISCOUNT_TYPE);
	}
	
	// Logins a user
	public String login(String userType, String email, String password) throws Exception {
		String response;
		
		switch(userType.toLowerCase()) {
		case SHOPPER_TYPE :
			response = database.loginShopper(email, password);
			break;
		case EMPLOYEE_TYPE :
			response = database.loginEmployee(email, password);
			break;
		default :
			throw new Exception(className + EXCEPTION_DELIMITER + UNKNOWN_TYPE_EXCEPTION + EXCEPTION_DELIMITER + userType);
		}
		
		return response;
	}
	
	// Gets all requested items of a certain type 
	public String getItems(String userID, String itemType) throws Exception {
		String response;

		switch (itemType.toLowerCase()) {
		case DEPARTMENT_TYPE:
			response = database.getDepartmentsJsonString();
			break;
		case PRODUCT_TYPE:
			response = database.getProductsJsonString();
			break;
		case DISCOUNT_TYPE:
			response = database.getPersonalDiscounts(userID);
			break;
		default:
			throw new Exception(
					className + EXCEPTION_DELIMITER + UNKNOWN_TYPE_EXCEPTION + EXCEPTION_DELIMITER + itemType);
		}

		return response;
	}
	
	// Edits a specific attribute in a user data
	public void setCredentials(String userType, String userID, String itemType, String itemArgs) throws Exception {
		
		switch(userType.toLowerCase()) {
		case SHOPPER_TYPE :
			database.setShopper(userID, itemType, itemArgs); 
			break;
		case EMPLOYEE_TYPE :
			database.setEmployee(userID, itemType, itemArgs); 
			break;
		default: throw new Exception(className + EXCEPTION_DELIMITER + UNKNOWN_TYPE_EXCEPTION + EXCEPTION_DELIMITER + userType);
		}
	}
	
	// Returns a shopper E-MAIL information 
	public String getShopperMail(String userID) throws Exception {
		String response = database.getShopperMail(userID);
		
		return response;
	}
}
