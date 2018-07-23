package Database;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.util.HashMap;

public class DataKeeper {
	
	// Identities of users
	private static final String ADMIN_USER = "admin";
	private static final String SHOPPER_USER = "shopper";
	private static final String EMPLOYEE_USER = "employee";
	
	// Exception messages
	private static final String UNKNOWN_USER_EXCEPTION = "DataKeeper couldn't identify this user type";
	
	// Messages
	private static final String INVALID_USER = "Invalid user ";
	
	// A dataManager to trace activities
	private DataManager dataManager;
	
	// Maps to track online shoppers, and employees, using their IP-Address 
	private HashMap<Long, InetAddress> onlineShoppers;
	private HashMap<Long, InetAddress> onlineEmployees;
	
	public DataKeeper() throws Exception {
		dataManager = new DataManager();
		
		onlineShoppers = new HashMap<>();
		onlineEmployees = new HashMap<>();
	}
	
	// Returns a string of all data of a certain type
	public String viewItems(String accountType, String dataType) throws Exception {
		String items;
		
		if(!accountType.toLowerCase().equals(ADMIN_USER))
			throw new Exception(INVALID_USER + " : " + accountType);
		
		items = dataManager.viewItems(dataType);
		return items;
	}
	
	// Adds an item to the database
	public void addItem(String accountType, String dataType, String args) throws Exception {
		if(!accountType.toLowerCase().equals(ADMIN_USER))
			throw new Exception(INVALID_USER + " : " + accountType);
		
		dataManager.addItem(dataType, args);
	}
	
	/*
	public boolean validateUser() {
		switch(accountType) {
		case SHOPPER_USER:
			
			break;
		case EMPLOYEE_USER:
			
			break;
		default:
		
		return false;
		}
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
