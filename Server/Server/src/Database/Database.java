package Database;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import Class.Department;
import Class.Discount;
import Class.Employee;
import Class.Product;
import Class.Product.UnitType;
import Class.Shopper;

// Responsible for all data changes
public class Database {
	
	// Class Messages
	private static final String DONE_LOOP = "done";
	private static final String MESSAGE_DELIMITER = " ";

	// Class Exceptions
	private static final String IO_EXCEPTION = "Couldn't close JSON reader";
	private static final String UNKNOWN_TYPE_EXCEPTION = "Couldn't identify this data type";
	private static final String LOCATION_EXCEPTION = "Couldn't find an item in the index";
	private static final String INVALID_ARGS_EXCEPTION = "Invalid args";
	private static final String UNKNOWN_USER_EXCEPTION = "Couldn't find the user";
	private static final String UNKNOWN_PRODUCT_EXCEPTION = "Couldn't find the product";
	private static final String EXCEPTION_DELIMITER = " : ";

	// Item types for all data loaded, or written to JSON
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

	// Args amount of each item type
	private static final int DEPARTMENT_ARGS_AMOUNT = 2;
	private static final int EMPLOYEE_ARGS_AMOUNT = 3 + 1; // +1 to delete all other data
	private static final int PRODUCT_ARGS_AMOUNT = 8;
	private static final int SHOPPER_ARGS_AMOUNT = 4 + 1; // +1 to delete all other data
	private static final int DISCOUNT_ARGS_AMOUNT = 4;

	// Class attributes
	private List<Department> departments;
	private List<Product> products;
	private List<Shopper> shoppers;
	private List<Employee> employees;
	private List<Discount> discounts;
	private String className;
	
	public Database() {
		// Gets class name for future exceptions messages
		className = new Object() {}.getClass().getEnclosingClass().getSimpleName();
	}

	// Loads a list of departments from a JSON string
	public void loadDepartmentsFromString(String content) throws Exception {
		JsonReader jsonReader = new JsonReader(new StringReader(content));
		Gson gson = new Gson();

		departments = gson.fromJson(jsonReader, DEPARTMENT_TYPE);

		try {
			jsonReader.close();
		} catch (IOException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + IO_EXCEPTION);
		}
	}

	// Loads a list of products from a JSON string
	public void loadProductsFromString(String content) throws Exception {
		JsonReader jsonReader = new JsonReader(new StringReader(content));
		Gson gson = new Gson();

		products = gson.fromJson(jsonReader, PRODUCT_TYPE);

		try {
			jsonReader.close();
		} catch (IOException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + IO_EXCEPTION);
		}
	}

	// Loads a list of shoppers from a JSON string
	public void loadShoppersFromString(String content) throws Exception {
		JsonReader jsonReader = new JsonReader(new StringReader(content));
		Gson gson = new Gson();

		shoppers = gson.fromJson(jsonReader, SHOPPER_TYPE);

		try {
			jsonReader.close();
		} catch (IOException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + IO_EXCEPTION);
		}
	}

	// Loads a list of employees from a JSON string
	public void loadEmployeesFromString(String content) throws Exception {
		JsonReader jsonReader = new JsonReader(new StringReader(content));
		Gson gson = new Gson();

		employees = gson.fromJson(jsonReader, EMPLOYEE_TYPE);

		try {
			jsonReader.close();
		} catch (IOException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + IO_EXCEPTION);
		}
	}

	// Loads a list of discounts from a JSON string
	public void loadDiscountsFromString(String content) throws Exception {
		JsonReader jsonReader = new JsonReader(new StringReader(content));
		Gson gson = new Gson();

		discounts = gson.fromJson(jsonReader, DISCOUNT_TYPE);

		try {
			jsonReader.close();
		} catch (IOException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + IO_EXCEPTION);
		}
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
		String argsParts[] = itemArgs.split(MESSAGE_DELIMITER, DEPARTMENT_ARGS_AMOUNT);
		String name, id;
		
		try {
			id = argsParts[0];
			name = argsParts[1];
		}catch(ArrayIndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_ARGS_EXCEPTION + EXCEPTION_DELIMITER + itemArgs);
		}
		
		return new Department(id, name);
	}
	
	// Creates a product from a string
	private Product processProductString(String itemArgs) throws Exception{
		String argsParts[] = itemArgs.split(MESSAGE_DELIMITER, PRODUCT_ARGS_AMOUNT);
		String name, productId, departmentId, locationX, locationY, pricePerUnit, amountPerUnit;
		UnitType unitType;

		try {
			productId = argsParts[0];
			name = argsParts[7];
			departmentId = argsParts[1];
			pricePerUnit = argsParts[2];
			locationX = argsParts[3];
			locationY = argsParts[4];
			amountPerUnit = argsParts[5];
		} catch(ArrayIndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_ARGS_EXCEPTION + EXCEPTION_DELIMITER + itemArgs);
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
			throw new Exception(className + EXCEPTION_DELIMITER + UNKNOWN_TYPE_EXCEPTION + EXCEPTION_DELIMITER + argsParts[6]);
		}
		
		return new Product(productId, name, departmentId, pricePerUnit, locationX, locationY, amountPerUnit, unitType);
	}
	
	// Creates a shopper from a string
	private Shopper processShopperString(String itemArgs) throws Exception{
		String argsParts[] = itemArgs.split(MESSAGE_DELIMITER, SHOPPER_ARGS_AMOUNT);
		String id, email, password, creditCard;
		
		try {
			id = argsParts[0];
			email = argsParts[1];
			password = argsParts[2];
			creditCard = argsParts[3];
		}catch (ArrayIndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_ARGS_EXCEPTION + EXCEPTION_DELIMITER + itemArgs);
		}
		
		return new Shopper(id, email, password, creditCard);
	}
	
	// Creates an employee from a string
	private Employee processEmployeeString(String itemArgs) throws Exception{
		String argsParts[] = itemArgs.split(MESSAGE_DELIMITER, EMPLOYEE_ARGS_AMOUNT);
		String id, email, password;
		
		try {
			id = argsParts[0];
			email = argsParts[1];
			password = argsParts[2];
		}catch (ArrayIndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_ARGS_EXCEPTION + EXCEPTION_DELIMITER + itemArgs);
		}
		
		return new Employee(id, email, password);
	}
	
	// Creates a discount from a string
	private Discount processDiscountString(String itemArgs) throws Exception{
		String argsParts[] = itemArgs.split(MESSAGE_DELIMITER, DISCOUNT_ARGS_AMOUNT);
		String productId, normalPrice, discountedPrice, shopperId;
		
		try {
			productId = argsParts[0];
			normalPrice = argsParts[1];
			discountedPrice = argsParts[2];
			if(argsParts[3].toLowerCase().equals(Discount.GENERAL_DISCOUNT_SHOPPER_ID.toLowerCase()))
				shopperId = Discount.GENERAL_DISCOUNT_SHOPPER_ID;
			else
				shopperId = argsParts[3];
		}catch (ArrayIndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_ARGS_EXCEPTION + EXCEPTION_DELIMITER + itemArgs);
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
	
	// Edits a department from the database
	public void editDepartment(int itemNumber, String itemArgs) throws Exception {
		Department item = processDepartmentString(itemArgs);
	
		try {
			// Replaces an item
			departments.set(itemNumber, item);
		}catch(IndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + LOCATION_EXCEPTION + EXCEPTION_DELIMITER + itemNumber);
		}
	}
	
	// Edits a product from the database
	public void editProduct(int itemNumber, String itemArgs) throws Exception {
		Product item = processProductString(itemArgs);

		try {
			// Replaces an item
			products.set(itemNumber, item);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + LOCATION_EXCEPTION + EXCEPTION_DELIMITER + itemNumber);
		}
	}

	// Edits a shopper from the database
	public void editShopper(int itemNumber, String itemArgs) throws Exception {
		Shopper item = processShopperString(itemArgs);

		try {
			// Replaces an item
			shoppers.set(itemNumber, item);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + LOCATION_EXCEPTION + EXCEPTION_DELIMITER + itemNumber);
		}
	}

	// Edits an employee from the database
	public void editEmployee(int itemNumber, String itemArgs) throws Exception {
		Employee item = processEmployeeString(itemArgs);

		try {
			// Replaces an item
			employees.set(itemNumber, item);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + LOCATION_EXCEPTION + EXCEPTION_DELIMITER + itemNumber);
		}
	}

	// Edits discount from the database
	public void editDiscount(int itemNumber, String itemArgs) throws Exception {
		Discount item = processDiscountString(itemArgs);

		try {
			// Replaces an item
			discounts.set(itemNumber, item);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + LOCATION_EXCEPTION + EXCEPTION_DELIMITER + itemNumber);
		}
	}
	
	// Removes a department from the database
	public void removeDepartment(int itemNumber) throws Exception {	
		try {
			departments.remove(itemNumber);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + LOCATION_EXCEPTION + EXCEPTION_DELIMITER + itemNumber);
		}
	}
	
	// Removes a product from the database
	public void removeProduct(int itemNumber) throws Exception {
		try {
			products.remove(itemNumber);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + LOCATION_EXCEPTION + EXCEPTION_DELIMITER + itemNumber);
		}
	}
	
	// Removes a shopper from the database
	public void removeShopper(int itemNumber) throws Exception {
		try {
			shoppers.remove(itemNumber);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + LOCATION_EXCEPTION + EXCEPTION_DELIMITER + itemNumber);
		}
	}
	
	// Removes an employee
	public void removeEmployee(int itemNumber) throws Exception {		
		try {
			employees.remove(itemNumber);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + LOCATION_EXCEPTION + EXCEPTION_DELIMITER + itemNumber);
		}
	}
	
	// Removes a discount
	public void removeDiscount(int itemNumber) throws Exception {		
		try {
			discounts.remove(itemNumber);
		} catch (IndexOutOfBoundsException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + LOCATION_EXCEPTION + EXCEPTION_DELIMITER + itemNumber);
		}
	}
	
	// Logins a shopper
	public String loginShopper(String email, String password) throws Exception {
		String response = "";
		Gson gson = new Gson();
		
		// Checks through all shoppers who is our shopper
		for(Shopper shopper : shoppers) {
			if(shopper.getEmail().equals(email) && shopper.getPassword().equals(password)){
				response = shopper.getId() + MESSAGE_DELIMITER + gson.toJson(shopper);
				break;
			}
		}
		
		if(response.isEmpty()){
			// Couldn't find the user by the email, and password
			throw new Exception(className + EXCEPTION_DELIMITER + UNKNOWN_USER_EXCEPTION);
		}
		
		return response;
	}
	
	// Logins an employee
	public String loginEmployee(String email, String password) throws Exception {
		String response = "";
		Gson gson = new Gson();
		
		// Checks through all employees who is our employee
		for(Employee employee : employees) {
			if(employee.getEmail().equals(email) && employee.getPassword().equals(password)){
				response = employee.getId() + MESSAGE_DELIMITER + gson.toJson(employee);
				break;
			}
		}
		
		if(response.isEmpty()){
			// Couldn't find the user by the email, and password
			throw new Exception(className + EXCEPTION_DELIMITER + UNKNOWN_USER_EXCEPTION);
		}
		
		return response;
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
	
	// Returns a JSON string of all discounts data of a certain shopper
	public String getPersonalDiscounts(String shopperID) {
		Gson gson = new Gson();
		List<Discount> shopperDiscounts = new ArrayList<Discount>();

		for (Discount discount : discounts) {
			if (discount.getShopperId().toLowerCase().equals(shopperID.toLowerCase())) {
				shopperDiscounts.add(discount);
			}
		}

		return gson.toJson(shopperDiscounts, DISCOUNT_TYPE);
	}
	
	// Edits a specific attribute in a shopper data
	public void setShopper(String userID, String credentialType, String credentialValue) throws Exception {
		String conclusion = "";

		for (Shopper shopper : shoppers) {
			if (shopper.getId().equals(userID)) {
				switch (credentialType.toLowerCase()) {
				case "email":
					shopper.setEmail(credentialValue);
					break;
				case "password":
					shopper.setPassword(credentialValue);
					break;
				case "card":
					shopper.setCreditCard(credentialValue);
					break;
				default:
					throw new Exception(
							className + EXCEPTION_DELIMITER + UNKNOWN_TYPE_EXCEPTION + EXCEPTION_DELIMITER + credentialValue);
				}
				conclusion = DONE_LOOP;
			}
		}

		if (conclusion.isEmpty())
			throw new Exception(
					className + EXCEPTION_DELIMITER + UNKNOWN_USER_EXCEPTION + EXCEPTION_DELIMITER + userID);
	}

	// Edits a specific attribute in an employee data
	public void setEmployee(String userID, String credentialType, String credentialVaule) throws Exception {
		String conclusion = "";

		for (Employee employee : employees) {
			if (employee.getId().equals(userID)) {
				switch (credentialType.toLowerCase()) {
				case "email":
					employee.setEmail(credentialVaule);
					break;
				case "password":
					employee.setPassword(credentialVaule);
					break;
				default:
					throw new Exception(
							className + EXCEPTION_DELIMITER + UNKNOWN_TYPE_EXCEPTION + EXCEPTION_DELIMITER + credentialType);
				}
				conclusion = DONE_LOOP;
			}
		}

		if (conclusion.isEmpty())
			throw new Exception(
					className + EXCEPTION_DELIMITER + UNKNOWN_USER_EXCEPTION + EXCEPTION_DELIMITER + userID);
	}

	// Returns a shopper E-Mail information
	public String getShopperMail(String userID) throws Exception {
		String response = "";

		for (Shopper shopper : shoppers) {
			if (shopper.getId().equals(userID)) {
				response = shopper.getEmail();
				break;
			}
		}

		if (response.isEmpty()) {
			throw new Exception(
					className + EXCEPTION_DELIMITER + UNKNOWN_USER_EXCEPTION + EXCEPTION_DELIMITER + userID);
		}

		return response;
	}
	
	// Checks if a product exists
	public void checkProductID(String productID) throws Exception {
		String conclustion = "";
		
		for(Product product : products) {
			if(product.getProductId().equals(productID)) {
				conclustion = DONE_LOOP;
				break;
			}		
		}
		
		if(conclustion.isEmpty())
			throw new Exception (className + EXCEPTION_DELIMITER + UNKNOWN_PRODUCT_EXCEPTION + EXCEPTION_DELIMITER + productID);
	}
}
