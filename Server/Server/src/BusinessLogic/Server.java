package BusinessLogic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import Database.DataKeeper;

public class Server {
	// Excludes error messages from serverManager
	private static final String ERROR_MESSAGE_TYPE = "error";
	private static final String SYSTEM_MESSAGE_TYPE = "system";
	
	// Messages
	private static final String SUCCESSFUL_BUILD = "A server has been successfully created";
	private static final String SERVER_NAME = "Server Name : ";
	private static final String SERVER_IP = "IP Address : ";
	private static final String SERVER_PORT = "Port Address : ";
	private static final String HELP_RECOMMENDATION = "type \"help\" to see available commands";
	private static final String PRINT_COMMANDS = "Available commands : ";
	private static final String START_SERVER = "Starts the server";
	private static final String VIEW_ITEMS = "Available items : ";
	private static final String SUCCESSFUL_ADD_ITEM = "Item has been successfully added";
	private static final String SUCCESSFUL_EDIT_ITEM = "Item has been successfully edited";
	private static final String SUCCESSFUL_REMOVE_ITEM = "Item has been successfully removed";
	private static final String PICK_ITEM = "Item has been picked up from a shelf";
	private static final String RETURN_ITEM = "Item has been returned to a shelf";
	private static final String PLACE_ITEM = "Item has been placed on a shelf";
	private static final String SUCCESSFUL_EXIT = "A server has been successfully shut down";
	private static final String UNKNOWN_COMMAND = "Unknown command";
	private static final String INVALID_COMMAND = "Invalid command";
	
	// String Part :
	private static final int HEAD_PART = 0;
	private static final int TAIL_PART = 1;
	
	// Command type :
	private static final String HELP_COMMAND = "help";
	private static final String START_COMMAND = "start";
	private static final String VIEW_COMMAND = "view";
	private static final String ADD_COMMAND = "add";
	private static final String EDIT_COMMAND = "edit";
	private static final String REMOVE_COMMAND = "remove";
	private static final String PICK_COMMAND = "pick";
	private static final String RETURN_COMMAND = "return";
	private static final String PLACE_COMMAND = "place";
	
	// Server Files :
	private static final String COMMAND_FILE = "commands.txt";
	
	// Delimiter :
	private static final String END_OF_FILE = "\\Z";
	
	/* Identities of users
	*  It is used for distinguishing their requests in the console log*/ 
	private static final String ADMIN = "admin";
	private static final String SHOPPER= "shopper";
	private static final String EMPLOYEE = "employee";
	
	private String serverName;
	private String serverIPAddress;
	private int serverPort;
	private boolean isAvailable;
	
	private ServerAdapter connection;
	private DataKeeper dataKeeper;
	
	public Server(String serverName, String serverIPAddress ,int serverPort) {
		isAvailable = false;
		
		try {
			this.serverName = serverName;
			this.serverIPAddress = serverIPAddress;
			this.serverPort = serverPort;
			
			dataKeeper = new DataKeeper();
			
			LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, SUCCESSFUL_BUILD);
			LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, SERVER_NAME + this.serverName);
			LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, SERVER_IP + this.serverIPAddress);
			LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, SERVER_PORT + this.serverPort);
			LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, HELP_RECOMMENDATION);
			isAvailable = true;
		} catch (Exception error) {
			LogPrinter.printMessage(LogPrinter.MessageType.ERROR, error.getMessage());
		}
	}
	
	// Checks if the server is available
	public boolean getIsAvailable() {
		return isAvailable;
	}
	
	// Process a command from the serverManager
	public void processCommand(String accountType, String command) {
		// Splits the command into head, and tail, to get the command type
		String commandParts[] = command.split(" ", 2);
		String error = "";

		try {
			switch (commandParts[HEAD_PART].toLowerCase()) {
			case HELP_COMMAND:
				printHelp();
				break;
			case START_COMMAND:
				startServer();
				break;
			case VIEW_COMMAND:
				viewItems(accountType, commandParts[TAIL_PART]);
				break;
			case ADD_COMMAND:
				addItem(accountType, commandParts[TAIL_PART]);
				break;
			case EDIT_COMMAND:
				editItem(accountType, commandParts[TAIL_PART]);
				break;
			case REMOVE_COMMAND:
				removeItem(accountType, commandParts[TAIL_PART]);
				break;
			case PICK_COMMAND:
				pickItem();
				break;
			case RETURN_COMMAND:
				returnItem();
				break;
			case PLACE_COMMAND:
				placeItem();
				break;
			default:
				throw new Exception(UNKNOWN_COMMAND);
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			error = INVALID_COMMAND;
		} catch (Exception ex) {
			error = ex.getMessage();
		}

		if (!error.isEmpty())
			LogPrinter.printMessage(LogPrinter.MessageType.ERROR, error + " : " + command);
	}
	
	// Presents all server commands
	public void printHelp() throws Exception {
		Scanner input = new Scanner(new File(COMMAND_FILE));
		
		// Reads all commands file
		String content = input.useDelimiter(END_OF_FILE).next();
		
		LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, PRINT_COMMANDS+"\n"+content);
			
		input.close();
	}
	
	// Starts the server
	public void startServer() {
		LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, START_SERVER);
	}
		
	// Presents all items of a certain type from the database
	public void viewItems(String accountType, String dataType) throws Exception {
		String items = dataKeeper.viewItems(accountType, dataType);

		LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, VIEW_ITEMS + "\n" + items);
	}
	
	// Adds an item to the database
	public void addItem(String accountType, String command) throws Exception {
		String commandParts[] = command.split(" ", 2);
		String dataType = commandParts[HEAD_PART];
		
		dataKeeper.addItem(accountType, dataType, commandParts[TAIL_PART]);
		
		LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, SUCCESSFUL_ADD_ITEM);
	}

	// Edits an item from the database
	public void editItem(String accountType, String command) {
		LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, SUCCESSFUL_EDIT_ITEM);
	}

	// Removes an item from the database
	public void removeItem(String accountType, String command) {
		LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, SUCCESSFUL_REMOVE_ITEM);
	}

	// Creates an event - a shopper picked up a product from a shelf
	public void pickItem() {
		LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, PICK_ITEM);
	}	

	// Creates an event - a shopper placed a product on a shelf
	public void returnItem() {
		LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, RETURN_ITEM);
	}

	// Creates an event - an employee placed a product on a shelf
	public void placeItem() {
		LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, PLACE_ITEM);
	}

	// Stops the server
	public void stopServer() {
		LogPrinter.printMessage(LogPrinter.MessageType.SYSTEM, SUCCESSFUL_EXIT);
	}
	
	// Prints a message
	public void printExternalMessage(String messageType, String message) {
		LogPrinter.MessageType type = LogPrinter.MessageType.UNKNOWN;
		
		switch(messageType.toLowerCase()) {
		case ERROR_MESSAGE_TYPE : type = LogPrinter.MessageType.ERROR;
			break;
		case SYSTEM_MESSAGE_TYPE : type = LogPrinter.MessageType.SYSTEM;
			break;
		}
		
		// Prints the message on console by using LogPrinter
		LogPrinter.printMessage(type, message);
	}
}


/*
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


// UDP Socket for all communications
UdpSocketHandler socketHandler;

// Builds S-Mart main server
public ServerManager() {
	// Get Server IP
	
	
	System.out.println("\n1. Builds S-Mart main server");
	System.out.println(" 1. Loads all server data : ");
	SmartDataLoader.loadDepartments();
	SmartDataLoader.loadEmployees();
	SmartDataLoader.loadProducts();
	SmartDataLoader.loadShoppers();
	SmartDataLoader.loadDiscounts();
	
	System.out.println(" 2. Creates online user lists");
	
	
	System.out.println(" 3. Creates a UDP socket for communication");
	socketHandler = new UdpSocketHandler(SERVER_PORT);
	socketHandler.addNewMessageRecievedListener(this);
}

// Starts S-Mart main server
public void start(){
	System.out.println("\n2. Starts S-Mart main server");
	socketHandler.startListening();
	
	System.out.println("\n3. Waits for connections : ");
	pickup();
	
	System.out.println("\n4. Closes S-Mart main server\n");
	exit();
}

// Creates a shopper picked up a product event
public void pickup() {
	Scanner input = new Scanner(System.in);
	String line = "";

	// Receives input from the console until "End" is received
	while (!line.equals("End")) {
		line = input.nextLine();

		// Action alerting a shopper has picked an item
		if (line.startsWith("pick")) {
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

// Closes S-Mart main server
public void exit() {
	socketHandler.stopListening();
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
 *
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
 *
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
 *
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
 *
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
 *
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
 *
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
*/
