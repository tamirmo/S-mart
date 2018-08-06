package Database;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import smart.data.Department;
import smart.data.Discount;
import smart.data.EmployeeDetails;
import smart.data.Product;
import smart.data.ShopperDetails;
import smart.data.SmartDataManager;

public final class SmartDataLoader {
	
	public static void loadDepartments(){
		File depFile = new File(SmartDataManager.DEPARTMENTS_FILE);
		
		if(depFile.exists()){
			SmartDataManager.getInstance().readDepartmentsFromFile(SmartDataManager.DEPARTMENTS_FILE);			
		}
		else{
			// Creating the default data
			List<Department> departments = new ArrayList<>();
			
			departments.add(new Department(0, "Drinks"));
			departments.add(new Department(1, "Dairy"));
			departments.add(new Department(2, "Vegetables"));
			departments.add(new Department(3, "Fruits"));
			departments.add(new Department(4, "Sweets"));
			
			SmartDataManager.getInstance().saveDepartmentsToFile(SmartDataManager.DEPARTMENTS_FILE, departments);
		}
	}
	
	public static void loadProducts(){
		File prodFile = new File(SmartDataManager.PRODUCTS_FILE);
		
		if(prodFile.exists()){
			SmartDataManager.getInstance().readProductsFromFile(SmartDataManager.PRODUCTS_FILE);
		}
		else{
			// Creating the default data
			List<Product> products = new ArrayList<>();
			
			products.add(new Product(1, "Coca Cola", 0, 0.25, 
					//5, 13,
					5,9,
					1.5, Product.UnitType.L,
					// Number of items in stock
					10));
			products.add(new Product(2, "Banana", 3, 2.99, 
					1, 0, 
					1, Product.UnitType.KG,
					// Number of items in stock
					1));
			products.add(new Product(3, "Sprite", 0, 0.15, 
					//11, 13, 
					10, 9,
					1.5, Product.UnitType.L,
					// Number of items in stock (out of stock example)
					0));
			products.add(new Product(4, "Avokado", 2, 4.99, 
					3, 3, 
					1, Product.UnitType.KG,
					// Number of items in stock
					10));
			products.add(new Product(5, "Apple", 3, 3.99, 
					0, 1, 
					1, Product.UnitType.KG,
					// Number of items in stock
					10));
			products.add(new Product(6, "Cucumber", 2, 1.99, 
					4, 3, 
					1, Product.UnitType.KG,
					// Number of items in stock
					10));
			Product milk = new Product(7, "Milk", 1, 0.15, 
					//3, 13,
					9, 9,
					7, Product.UnitType.L,
					// Number of items in stock
					10);
			// Setting the milk as expired for demonstration
			milk.setExpired(true);
			products.add(milk);
			
			products.add(new Product(8, "Soy chocolate", 1, 0.15, 
					12, 9, 
					125, Product.UnitType.G,
					// Number of items in stock
					10));
			products.add(new Product(9, "Lindt chocolate", 4, 0.15, 
					3, 6, 
					100, Product.UnitType.G,
					// Number of items in stock
					10));
			products.add(new Product(10, "Loacker chocolate bites", 4, 0.15, 
					//12, 8,
					6, 6,
					250, Product.UnitType.G,
					// Number of items in stock
					10));
			products.add(new Product(11, "Twix minis", 4, 0.15, 
					9, 6, 
					187, Product.UnitType.G,
					// Number of items in stock
					10));
			
			SmartDataManager.getInstance().saveProductsToFile(SmartDataManager.PRODUCTS_FILE, products);
		}
	}

	public static void loadShoppers(){
		File shopFile = new File(SmartDataManager.SHOPPERS_FILE);
		
		if(shopFile.exists()){
			SmartDataManager.getInstance().readShoppersDetailsFromFile(SmartDataManager.SHOPPERS_FILE);
		}else{
			// Creating the default data
			List<ShopperDetails> shoppers = new ArrayList<>();
			
			shoppers.add(new ShopperDetails(0, "tamirmoa123@gmail.com", "1234", "19838394"));
			shoppers.add(new ShopperDetails(1, "tamir@gmail.com", "1234", "19838395"));
			shoppers.add(new ShopperDetails(2, "yunikr@gmail.com", "1234", "19838396"));
			shoppers.add(new ShopperDetails(3, "shiran0azulay@gmail.com", "1234", "19838397"));
			
			SmartDataManager.getInstance().saveShoppersDetailsToFile(SmartDataManager.SHOPPERS_FILE, shoppers);
		}		
	}
	
	public static void loadEmployees(){
		File empFile = new File(SmartDataManager.EMPLOYEES_FILE);
		
		if(empFile.exists()){
			SmartDataManager.getInstance().readEmployeeDetailsFromFile(SmartDataManager.EMPLOYEES_FILE);
		}else{
			// Creating the default data
			List<EmployeeDetails> employees = new ArrayList<>();
			
			employees.add(new EmployeeDetails(0, "moshe@gmail.com", "1234"));
			employees.add(new EmployeeDetails(1, "moti@gmail.com", "1234"));
			
			SmartDataManager.getInstance().saveEmployeesDetailsToFile(SmartDataManager.EMPLOYEES_FILE, employees);
		}
	}
	
	public static void loadDiscounts(){
		File discountsFile = new File(SmartDataManager.DISCOUNTS_FILE);
		
		if(discountsFile.exists()){
			SmartDataManager.getInstance().readDiscountsFromFile(SmartDataManager.DISCOUNTS_FILE);
		}else{
			// Creating the default data
			List<Discount> discounts = new ArrayList<>();
			
			discounts.add(new Discount(1, 0.25, 0.15, Discount.GENERAL_DISCOUNT_SHOPPER_ID));
			discounts.add(new Discount(2, 2.99, 1.99, Discount.GENERAL_DISCOUNT_SHOPPER_ID));
			// Personal discount for user 0
			discounts.add(new Discount(4, 4.99, 3.99, 0));
			// Personal discount for user 0
			discounts.add(new Discount(6, 1.99, 0.99, 0));
			
			SmartDataManager.getInstance().saveDiscountsToFile(SmartDataManager.DISCOUNTS_FILE, discounts);
		}
	}
}
