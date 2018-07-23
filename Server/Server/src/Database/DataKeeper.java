package Database;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.HashMap;

public class DataKeeper {
	
	// Identities of users
	private static final String ADMIN = "admin";
	private static final String SHOPPER = "shopper";
	private static final String EMPLOYEE = "employee";
	
	// A dataManager to trace activities
	private DataManager dataManager;
	
	// Maps to track online shoppers, and employees, using their IP-Address 
	private HashMap<Long, InetAddress> onlineShoppers;
	private HashMap<Long, InetAddress> onlineEmployees;
	
	public DataKeeper() throws FileNotFoundException, Exception {
		dataManager = new DataManager();
		
		onlineShoppers = new HashMap<>();
		onlineEmployees = new HashMap<>();
		
		
	}
	
	/*public boolean validateUser() {
		
	}
	
	public boolean addItem() {
		
	}
	
	public boolean editItem() {
		
	}
	
	public boolean removeItem() {
		
	}
	
	public String getItem() {
		
	}
	
	public String getItems() {
		
	}*/
}
