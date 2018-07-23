package Database;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import smart.data.Database;


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

public class DataManager {
	
	// Server data files 
	private static final String DEPARTMENTS_FILE = "departments.json";
	private static final String PRODUCTS_FILE = "products.json";
	private static final String SHOPPERS_FILE = "shoppers.json";
	private static final String EMPLOYEES_FILE = "employees.json";
	private static final String DISCOUNTS_FILE = "discounts.json";
	
	// Server data types
	private static final String DEPARTMENT = "department";
	private static final String PRODUCT = "product";
	private static final String SHOPPER = "shopper";
	private static final String EMPLOYEE = "employee";
	private static final String DISCOUNT = "discount";
	
	// A database to manage
	private Database database;
	
	public DataManager() throws FileNotFoundException, Exception {
		database = new Database();
		
		loadFromFile(DEPARTMENTS_FILE,DEPARTMENT);
		loadFromFile(EMPLOYEES_FILE,EMPLOYEE);
		loadFromFile(PRODUCTS_FILE,PRODUCT);
		loadFromFile(SHOPPERS_FILE,SHOPPER);
		loadFromFile(DISCOUNTS_FILE,DISCOUNT);
	}
	
	// Loads all data from a file
	public void loadFromFile(String fileName, String dataType) throws FileNotFoundException, Exception {
		Scanner input = new Scanner(new File(fileName));
		String fileContent = input.useDelimiter("\\Z").next();
		
		database.readListFromString(fileContent, dataType);
		input.close();
	}
	
	
	/*
	// Saves all data of a certain type to a file
	public void saveToFile() {
		
	}
	
	public boolean validateUser() {
		
		return false;
	}
	
	public boolean addItem() {
		
		
		return false;
	}
	
	public boolean editItem() {
		
		return false;
	}
	
	public boolean removeItem() {
		
		return false;
	}
	
	public String getItem() {
		
		return "empty";
	}
	
	public String getItems() {
		
		return "empty";
	}
	
	*/
}

/*public static void loadDepartments(){
File depFile = new File(Database.DEPARTMENTS_FILE);

if(depFile.exists()){
	Database.getInstance().readDepartmentsFromFile(Database.DEPARTMENTS_FILE);			
}
else{
	// Creating the default data
	List<Department> departments = new ArrayList<>();
	
	departments.add(new Department(0, "Drinks"));
	departments.add(new Department(1, "Dairy"));
	departments.add(new Department(2, "Vegetables"));
	departments.add(new Department(3, "Fruits"));
	departments.add(new Department(4, "Sweets"));
	
	Database.getInstance().saveDepartmentsToFile(Database.DEPARTMENTS_FILE, departments);
}
}

public static void loadProducts(){
File prodFile = new File(Database.PRODUCTS_FILE);

if(prodFile.exists()){
	Database.getInstance().readProductsFromFile(Database.PRODUCTS_FILE);
}
else{
	// Creating the default data
	List<Product> products = new ArrayList<>();
	
	products.add(new Product(1, "Coca Cola", 0, 0.25, 
			//5, 13,
			5,9,
			1.5, Product.UnitType.L));
	products.add(new Product(2, "Banana", 3, 2.99, 
			1, 0, 
			1, Product.UnitType.KG));
	products.add(new Product(3, "Sprite", 0, 0.15, 
			//11, 13, 
			10, 9,
			1.5, Product.UnitType.L));
	products.add(new Product(4, "Avokado", 2, 4.99, 
			3, 3, 
			1, Product.UnitType.KG));
	products.add(new Product(5, "Apple", 3, 3.99, 
			0, 1, 
			1, Product.UnitType.KG));
	products.add(new Product(6, "Cucumber", 2, 1.99, 
			4, 3, 
			1, Product.UnitType.KG));
	products.add(new Product(7, "Milk", 1, 0.15, 
			//3, 13,
			9, 9,
			7, Product.UnitType.L));
	products.add(new Product(8, "Soy chocolate", 1, 0.15, 
			12, 9, 
			125, Product.UnitType.G));
	products.add(new Product(9, "Lindt chocolate", 4, 0.15, 
			3, 6, 
			100, Product.UnitType.G));
	products.add(new Product(10, "Loacker chocolate bites", 4, 0.15, 
			//12, 8,
			6, 6,
			250, Product.UnitType.G));
	products.add(new Product(11, "Twix minis", 4, 0.15, 
			9, 6, 
			187, Product.UnitType.G));
	
	Database.getInstance().saveProductsToFile(Database.PRODUCTS_FILE, products);
}
}

public static void loadShoppers(){
File shopFile = new File(Database.SHOPPERS_FILE);

if(shopFile.exists()){
	Database.getInstance().readShoppersDetailsFromFile(Database.SHOPPERS_FILE);
}else{
	// Creating the default data
	List<ShopperDetails> shoppers = new ArrayList<>();
	
	shoppers.add(new ShopperDetails(0, "tamirmoa123@gmail.com", "1234", "19838394"));
	shoppers.add(new ShopperDetails(1, "tamir@gmail.com", "1234", "19838395"));
	shoppers.add(new ShopperDetails(2, "yunikr@gmail.com", "1234", "19838396"));
	shoppers.add(new ShopperDetails(3, "shiran0azulay@gmail.com", "1234", "19838397"));
	
	Database.getInstance().saveShoppersDetailsToFile(Database.SHOPPERS_FILE, shoppers);
}		
}

public static void loadEmployees(){
File empFile = new File(Database.EMPLOYEES_FILE);

if(empFile.exists()){
	Database.getInstance().readEmployeeDetailsFromFile(Database.EMPLOYEES_FILE);
}else{
	// Creating the default data
	List<EmployeeDetails> employees = new ArrayList<>();
	
	employees.add(new EmployeeDetails(0, "moshe@gmail.com", "1234"));
	employees.add(new EmployeeDetails(1, "moti@gmail.com", "1234"));
	
	Database.getInstance().saveEmployeesDetailsToFile(Database.EMPLOYEES_FILE, employees);
}
}

public static void loadDiscounts(){
File discountsFile = new File(Database.DISCOUNTS_FILE);

if(discountsFile.exists()){
	Database.getInstance().readDiscountsFromFile(Database.DISCOUNTS_FILE);
}else{
	// Creating the default data
	List<Discount> discounts = new ArrayList<>();
	
	discounts.add(new Discount(1, 0.25, 0.15, Discount.GENERAL_DISCOUNT_SHOPPER_ID));
	discounts.add(new Discount(2, 2.99, 1.99, Discount.GENERAL_DISCOUNT_SHOPPER_ID));
	// Personal discount for user 0
	discounts.add(new Discount(4, 4.99, 3.99, 0));
	// Personal discount for user 0
	discounts.add(new Discount(6, 1.99, 0.99, 0));
	
	Database.getInstance().saveDiscountsToFile(Database.DISCOUNTS_FILE, discounts);
}
}*/
