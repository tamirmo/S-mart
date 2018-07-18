package BusinessLogic;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import Database.SmartDataLoader;
import communicationUtilities.Message;
import smart.data.EmployeeDetails;
import smart.data.ShopperDetails;
import smart.data.SmartDataManager;
import udp.IUdpMessageReceived;
import udp.UdpSocketHandler;

public class Server implements IUdpMessageReceived {
	private static final int SERVER_PORT = 5001;
	
	// Indicating a failed login request
	private static final int INVALID_LOGIN_ID = -1;
	
	// The requests codes that are sent between the server and the client app
	private static final int SHOPPER_LOGIN_REQUEST = 0;
	private static final int EMPLOYEE_LOGIN_REQUEST = 1;
	private static final int GET_DEPARTMENTS_REQUEST = 2;
	private static final int GET_PRODUCTS_REQUEST = 3;
	private static final int GET_DISCOUNTS_REQUEST = 4;
	private static final int UPDATE_CART = 5;
	private static final int GET_CART_REQUEST = 6;
	private static final int CHANGE_SETTINGS = 7;
	
	// After a change settings request one of these are sent:
	private static final int CHANGE_EMAIL = 0;
	private static final int CHANGE_PASSWORD = 1;
	private static final int CHANGE_CREDIT = 2;
	
	// Response codes sent back to the user
	private static final int CHANGE_SETTINGS_WRONG_RESPONSE = 0;
	private static final int CHANGE_SETTINGS_OK_RESPONSE = 1;
	private static final int LOGIN_WRONG_RESPONSE = 0;
	private static final int LOGIN_OK_RESPONSE = 1;
	
	// Maps to track online shoppers, and employees, using their IP-Address 
	private HashMap<Long, InetAddress> onlineShoppers;
	private HashMap<Long, InetAddress> onlineEmployees;
	
	// UDP Socket for all communications
	UdpSocketHandler socketHandler;
	
	// Builds S-Mart main server
	public Server() {
		System.out.println("\n1. Builds S-Mart main server");
		System.out.println(" 1. Loads all server data : ");
		SmartDataLoader.loadDepartments();
		SmartDataLoader.loadEmployees();
		SmartDataLoader.loadProducts();
		SmartDataLoader.loadShoppers();
		SmartDataLoader.loadDiscounts();
		
		System.out.println(" 2. Creates online user lists");
		onlineShoppers = new HashMap<>();
		onlineEmployees = new HashMap<>();
		
		System.out.println(" 3. Creates a UDP socket for communication");
		socketHandler = new UdpSocketHandler(SERVER_PORT);
		socketHandler.addNewMessageRecievedListener(this);
	}
	
	// Starts S-Mart main server
	public void run(){
		Scanner input = new Scanner(System.in);
		String line = "";
		
		System.out.println("\n2. Starts S-Mart main server");
		socketHandler.startListening();
		
		// Receives input from the console until "End" is received
		System.out.println("\n3. Waits for connections : ");
		while(!line.equals("End")){
			line = input.nextLine();
			System.out.println(line);
			
			// Action alerting a shopper has picked an item
			if(line.startsWith("pick")){
				System.out.println("Shopper Id: ");
				long shopperId = input.nextLong();
				
				System.out.println("Item Id: ");
				long itemId = input.nextInt();
				
				System.out.println("Employee Id: ");
				long employeeId = input.nextInt();
				
				onItemPicked(shopperId, employeeId, itemId);
			}
		}
		input.close();
		
		exit();
	}
	
	// Closes S-Mart main server
	public void exit() {
		System.out.println("\n4. Closes S-Mart main server\n");
		socketHandler.stopListening();
	}
	
	private void onItemPicked(long shopperPickerId, long employeeToAlertId, long itemPickedId){
		// Alerting the employee:
		if(onlineEmployees.containsKey(employeeToAlertId)){
			InetAddress employeeAddress = onlineEmployees.get(employeeToAlertId);
			udp.UdpSender.getInstance().sendMessage("OUT_OF_STOCK:" + itemPickedId, employeeAddress, SERVER_PORT);
		}else{
			System.out.println("Not sending item picked to employee, Not logged in");
		}
		
		// Alerting the shopper:
		if(onlineShoppers.containsKey(shopperPickerId)){
			InetAddress employeeAddress = onlineShoppers.get(employeeToAlertId);
			udp.UdpSender.getInstance().sendMessage("ITEM_PICKED:" + itemPickedId, employeeAddress, SERVER_PORT);
		}else{
			System.out.println("Not sending item picked to shopper, Not logged in");
		}
	}

	@Override
	public void messageReceived(Message messageReceived) {
		String reply = null;
		String reqCodeString = messageReceived.getMessageContent().substring(0,1);
		
		try{
			// The first character should be a request code 
			int reqCode = Integer.parseInt(reqCodeString);
			
			switch(reqCode)
			{
				case SHOPPER_LOGIN_REQUEST:
					reply = String.valueOf(SHOPPER_LOGIN_REQUEST);
					if(logInShooper(messageReceived)){
						reply += String.valueOf(LOGIN_OK_RESPONSE);
					}else{
						reply += String.valueOf(LOGIN_WRONG_RESPONSE);
					}
					break;
				case EMPLOYEE_LOGIN_REQUEST:
					reply = String.valueOf(SHOPPER_LOGIN_REQUEST);
					if(logInEmployee(messageReceived)){
						reply += String.valueOf(LOGIN_OK_RESPONSE);
					}else{
						reply += String.valueOf(LOGIN_WRONG_RESPONSE);
					}
					break;
				case GET_DEPARTMENTS_REQUEST:
					reply = GET_DEPARTMENTS_REQUEST + SmartDataManager.getInstance().getDepartmentsJsonString();
					break;
				case GET_PRODUCTS_REQUEST:
					reply = GET_PRODUCTS_REQUEST + SmartDataManager.getInstance().getProductsJsonString();
					break;
				case UPDATE_CART:
					saveCart(messageReceived);
					break;
				case GET_CART_REQUEST:
					reply = GET_CART_REQUEST + getCart(messageReceived);
					break;
				case GET_DISCOUNTS_REQUEST:
					reply = GET_DISCOUNTS_REQUEST + getShopperDiscounts(messageReceived.getAddress()); 
					break;
				case CHANGE_SETTINGS:
					reply = changeSettings(messageReceived);
					break;
				default:
					System.out.println("Message error: got wrong req code: " + reqCode);
			}
		}catch(NumberFormatException nfe){
			System.out.println("got wrong req code, not a number");
		}
		
		if(reply != null){
			udp.UdpSender.getInstance().sendMessage(reply, 
					messageReceived.getAddress(), 
					SERVER_PORT);
			System.out.println("reply of " + reqCodeString + 
					" sent to " + messageReceived.getAddress());
		}
	}

	/**
	 * Finds the shopper id with the given address
	 * @param shopperAddress InetAddress The address of the shopper to look for
	 * @return long The id of the shopper with the given address, INVALID_LOGIN_ID if not found
	 */
	private long getShopperId(InetAddress shopperAddress){
		long shopperId = INVALID_LOGIN_ID;
		
		// Pulling the id of the shopper by the ip:
		for(Entry<Long, InetAddress> entry : onlineShoppers.entrySet()){
			if (entry.getValue().equals(shopperAddress)){
				// Saving the id found
				shopperId = entry.getKey();
				break;
			}
		}
		
		return shopperId;
	}
	
	/**
	 * Finds the employee id with the given address
	 * @param employeeAddress InetAddress The address of the employee to look for
	 * @return long The id of the shopper with the given address, INVALID_LOGIN_ID if not found
	 */
	private long getEmployeeId(InetAddress employeeAddress){
		long employeeId = INVALID_LOGIN_ID;
		
		// Pulling the id of the employee by the ip:
		for(Entry<Long, InetAddress> entry : onlineEmployees.entrySet()){
			if (entry.getValue().equals(employeeAddress)){
				// Saving the id found
				employeeId = entry.getKey();
				break;
			}
		}
		
		return employeeId;
	}
	
	private void saveCart(Message cartMessage){
		long shopperId = getShopperId(cartMessage.getAddress());
		
		if(shopperId != INVALID_LOGIN_ID){
			// Saving the cart
			SmartDataManager.getInstance().saveCartToFile(shopperId + ".json", cartMessage.getMessageContent().substring(1));
		}else{
			System.out.println("Have not found shopper with ip (update cart req): "+ cartMessage.getAddress());
		}
	}
	
	private String getCart(Message getCartReq){
		long shopperId = getShopperId(getCartReq.getAddress());
		
		String shopperCart = null;
		
		if(shopperId != INVALID_LOGIN_ID){
			// Getting the cart of the shopper with the found id
			shopperCart = SmartDataManager.getInstance().readCartFromFile(shopperId + ".json");
		}else{
			System.out.println("Have not found shopper with ip (get cart req): "+ getCartReq.getAddress());
		}
		// No cart yet for this shopper or no shoper found
		if(shopperCart == null){
			shopperCart = "";
		}
		
		return shopperCart;
	}
	
	
	private String changeSettings(Message messageReceived){
		String response = String.valueOf(CHANGE_SETTINGS);
		
		// Checking if the request came from a shopper
		long shopperId = getShopperId(messageReceived.getAddress());
		if(shopperId != INVALID_LOGIN_ID){
			response += changeShopperSettings(shopperId, messageReceived);
		}else{
			// Checking if the request came from an employee
			long employeeId = getEmployeeId(messageReceived.getAddress());
			if(employeeId != INVALID_LOGIN_ID){
				response += changeEmployeeSettings(employeeId, messageReceived);
			}else{
				// Unknown user request (should not happen)
				response += String.valueOf(CHANGE_SETTINGS_WRONG_RESPONSE);
			}
		}
		
		return response;
	}
	
	private String changeShopperSettings(long shopperId, Message messageReceived){
		String response = "";
		
		try{
			// The second character has the type of settings change
			int reqCode = Character.getNumericValue(messageReceived.getMessageContent().charAt(1));
			
			switch(reqCode){
			case CHANGE_EMAIL:
				response = changeShopperEmail(shopperId, messageReceived);
				break;
			case CHANGE_CREDIT:
				response = changeShopperCredit(shopperId, messageReceived);
				break;
			case CHANGE_PASSWORD:
				response = changeShopperPassword(shopperId, messageReceived);
				break;
			}
		}catch(Exception ex){
			System.out.println("Error during changeShopperSettings");
			ex.printStackTrace();
			response = String.valueOf(CHANGE_SETTINGS_WRONG_RESPONSE);
		}
		return response;
	}
	
	/**
	 * Updates the email of the shopper with the given id
	 * to the email in the given message
	 * @param shopperId long, The id of the shopper to update
	 * @param messageReceived Message the message sent, containing the new email
	 * @return String, result string to send back to the shopper
	 */
	private String changeShopperEmail(long shopperId, Message messageReceived){
		String response = String.valueOf(CHANGE_SETTINGS_WRONG_RESPONSE);

		// Pulling the email from the message
		String newEmail = getParameterFromMessage(messageReceived.getMessageContent(), "email");
		
		ShopperDetails shoperDetails = SmartDataManager.getInstance().getShopperDetailsById(shopperId);
		
		if(shoperDetails != null){
			// Setting the email and saving to file
			shoperDetails.setEmail(newEmail);
			SmartDataManager.getInstance().saveShoppersDetailsToFile(SmartDataManager.SHOPPERS_FILE, 
					SmartDataManager.getInstance().getShopperDetails());
			// All went well
			response = String.valueOf(CHANGE_SETTINGS_OK_RESPONSE);
		}
		
		return response;
	}
	
	/**
	 * Updates the password of the shopper with the given id
	 * to the password in the given message
	 * @param shopperId long, The id of the shopper to update
	 * @param messageReceived Message the message sent, containing the new password
	 * @return String, result string to send back to the shopper
	 */
	private String changeShopperPassword(long shopperId, Message messageReceived){
		String response = String.valueOf(CHANGE_SETTINGS_WRONG_RESPONSE);
		
		// Pulling the passwords from the message
		String oldPass = getParameterFromMessage(messageReceived.getMessageContent(), "old_pass");
		String newPass = getParameterFromMessage(messageReceived.getMessageContent(), "new_pass");
		
		ShopperDetails shoperDetails = SmartDataManager.getInstance().getShopperDetailsById(shopperId);

		if(shoperDetails != null){
			// First checking if the password sent is the current one
			if(shoperDetails.getPassword().equals(oldPass)){
				// Setting the password and saving to file
				shoperDetails.setPassword(newPass);
				SmartDataManager.getInstance().saveShoppersDetailsToFile(SmartDataManager.SHOPPERS_FILE, 
						SmartDataManager.getInstance().getShopperDetails());
				
				// All went well
				response = String.valueOf(CHANGE_SETTINGS_OK_RESPONSE);
			}
		}
		
		return response;
	}
	
	/**
	 * Updates the credit of the shopper with the given id
	 * to the credit in the given message
	 * @param shopperId long, The id of the shopper to update
	 * @param messageReceived Message the message sent, containing the new credit
	 * @return String, result string to send back to the shopper
	 */
	private String changeShopperCredit(long shopperId, Message messageReceived){
		String response = String.valueOf(CHANGE_SETTINGS_WRONG_RESPONSE);
		
		// Getting the credit number from the message
		String newCredit = getParameterFromMessage(messageReceived.getMessageContent(), "credit");
		
		ShopperDetails shoperDetails = SmartDataManager.getInstance().getShopperDetailsById(shopperId);

		if(shoperDetails != null){
			// Setting the credit and saving to file
			shoperDetails.setCreditCard(newCredit);;
			SmartDataManager.getInstance().saveShoppersDetailsToFile(SmartDataManager.SHOPPERS_FILE, 
					SmartDataManager.getInstance().getShopperDetails());
			
			// All went well
			response = String.valueOf(CHANGE_SETTINGS_OK_RESPONSE);
		}
		
		return response;
	}

	private String changeEmployeeSettings(long employeeId, Message messageReceived){
		String response = "";
		
		try{
			// The second character has the type of settings change
			int reqCode = Character.getNumericValue(messageReceived.getMessageContent().charAt(1));
			
			switch(reqCode){
			case CHANGE_EMAIL:
				response = changeEmployeeEmail(employeeId, messageReceived);
				break;
			case CHANGE_PASSWORD:
				response = changeEmployeePassword(employeeId, messageReceived);
				break;
			}
		}catch(Exception ex){
			System.out.println("Error during changeShopperSettings");
			ex.printStackTrace();
			response = String.valueOf(CHANGE_SETTINGS_WRONG_RESPONSE);
		}
		return response;
	}
	
	/**
	 * Updates the email of the employee with the given id
	 * to the email in the given message
	 * @param employeeId long, The id of the employee to update
	 * @param messageReceived Message the message sent, containing the new email
	 * @return String, result string to send back to the employee
	 */
	private String changeEmployeeEmail(long employeeId, Message messageReceived){
		String response = String.valueOf(CHANGE_SETTINGS_WRONG_RESPONSE);

		// Pulling the email from the message
		String newEmail = getParameterFromMessage(messageReceived.getMessageContent(), "email");
		
		EmployeeDetails employeeDetails = SmartDataManager.getInstance().getEmployeeDetailsById(employeeId);
		
		if(employeeDetails != null){
			// Setting the email and saving to file
			employeeDetails.setEmail(newEmail);
			SmartDataManager.getInstance().saveEmployeesDetailsToFile(SmartDataManager.EMPLOYEES_FILE, 
					SmartDataManager.getInstance().getEmployees());
			// All went well
			response = String.valueOf(CHANGE_SETTINGS_OK_RESPONSE);
		}
		
		return response;
	}
	
	/**
	 * Updates the password of the employee with the given id
	 * to the password in the given message
	 * @param employeeId long, The id of the employee to update
	 * @param messageReceived Message the message sent, containing the new password
	 * @return String, result string to send back to the employee
	 */
	private String changeEmployeePassword(long employeeId, Message messageReceived){
		String response = String.valueOf(CHANGE_SETTINGS_WRONG_RESPONSE);
		
		// Pulling the passwords from the message
		String oldPass = getParameterFromMessage(messageReceived.getMessageContent(), "old_pass");
		String newPass = getParameterFromMessage(messageReceived.getMessageContent(), "new_pass");
		
		EmployeeDetails employeeDetails = SmartDataManager.getInstance().getEmployeeDetailsById(employeeId);

		if(employeeDetails != null){
			// First checking if the password sent is the current one
			if(employeeDetails.getPassword().equals(oldPass)){
				// Setting the password and saving to file
				employeeDetails.setPassword(newPass);
				SmartDataManager.getInstance().saveEmployeesDetailsToFile(SmartDataManager.EMPLOYEES_FILE, 
						SmartDataManager.getInstance().getEmployees());
				// All went well
				response = String.valueOf(CHANGE_SETTINGS_OK_RESPONSE);
			}
		}
		
		return response;
	}
	
	private boolean logInShooper(Message messageReceived){
		long loggenInShopperId = checkShopperLogin(messageReceived.getMessageContent());
		if(loggenInShopperId != INVALID_LOGIN_ID){
			onlineShoppers.put(loggenInShopperId, messageReceived.getAddress());
			return true;
		}
		return false;
	}
	
	private boolean logInEmployee(Message messageReceived){
		long loggenInEmployeeId = checkEmployeeLogin(messageReceived.getMessageContent());
		if(loggenInEmployeeId != INVALID_LOGIN_ID){
			onlineEmployees.put(loggenInEmployeeId, messageReceived.getAddress());
			return true;
		}
		return false;
	}
	
	/**
	 * This method checks if the given message contains the details of a shopper
	 * (email and password).
	 * @param messageContent
	 * @return integer, The id of the shopper logged in
	 */
	private long checkShopperLogin(String messageContent) {
		long loggenInShopperId = INVALID_LOGIN_ID;
		List<ShopperDetails> shoppers = SmartDataManager.getInstance().getShopperDetails();
		String email = null, password = null;
		if(shoppers != null){
			
			try{
				// The login message should look like that:
				// 0,email=15,tamir@gmail.com,pass=4,1234
				// First is the request, Login request = 0,
				// after is the "email=" and than the length of the email,
				// after that the password the same way
				
				// Parsing the emails and password
				email = getParameterFromMessage(messageContent, "email");
				password = getParameterFromMessage(messageContent, "pass");
			}
			catch(NumberFormatException nfe)
			{
				nfe.printStackTrace();
			}
			
			// Checking if parsing went well
			if(email != null && password != null){
				for(ShopperDetails shopperDetail: shoppers){
					// If the current user has the details gotten
					if(shopperDetail.isEmailPasswordEquals(email, password)){
						// Indicating the login is successful with the id logged in
						loggenInShopperId = shopperDetail.getId();
						break;
					}
				}
			}
		}
		
		return loggenInShopperId;
	}
	
	private long checkEmployeeLogin(String messageContent) {
		long loggenInEmployeeId = INVALID_LOGIN_ID;
		List<EmployeeDetails> employees = SmartDataManager.getInstance().getEmployees();
		String email = null, password = null;
		if(employees != null){
			
			try{
				// The login message should look like that:
				// 1,email=15,tamir@gmail.com,pass=4,1234
				// First is the request, Login request = 1,
				// after is the "email=" and than the length of the email,
				// after that the password the same way
				
				// Parsing the emails and password
				email = getParameterFromMessage(messageContent, "email");
				password = getParameterFromMessage(messageContent, "pass");
			}
			catch(NumberFormatException nfe)
			{
				nfe.printStackTrace();
			}
			
			// Checking if parsing went well
			if(email != null && password != null){
				for(EmployeeDetails employeeDetail: employees){
					// If the current user has the details gotten
					if(employeeDetail.isEmailPasswordEquals(email, password)){
						// Indicating the login is successful with the id logged in
						loggenInEmployeeId = employeeDetail.getId();
						break;
					}
				}
			}
		}
		
		return loggenInEmployeeId;
	}
	
	private String getParameterFromMessage(String messageContent, String parameterName){
		String value = null;
		try{
			// A message should look like that:
			// parameterName=5,value
			// first comes the parameter name than the length of the parameter
			// and after that the value
			
			// Getting the index of the length of the parameter
			// and the end of the length (the comma)
			int paramLengthStartIndex = messageContent.indexOf(parameterName+"=")+ (parameterName+"=").length();
			int paramLengthEndIndex = messageContent.indexOf(",", paramLengthStartIndex);
		
			// Parsing the lengths:
			int paramLen = Integer.parseInt(messageContent.substring(paramLengthStartIndex, paramLengthEndIndex));

			// Getting the value
			value = messageContent.substring(paramLengthEndIndex + 1, paramLengthEndIndex + 1 + paramLen);
		}
		catch(NumberFormatException nfe)
		{
			nfe.printStackTrace();
		}
		
		return value;
	}
	
	private String getShopperDiscounts(InetAddress shopperAddress){
		String discountsJson = null;
		
		// Getting the id of the shopper with this address
		long shopperId = getShopperId(shopperAddress);
		
		// Checking if this address is logged in
		if(shopperId != INVALID_LOGIN_ID){
			discountsJson = SmartDataManager.getInstance().getAllShopperDiscounts(shopperId);
		}
		
		return discountsJson;
	}
}
