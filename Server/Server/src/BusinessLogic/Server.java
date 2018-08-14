package BusinessLogic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

import Database.DatabaseManager;

// Responsible to all requests/commands processing, and sends back responses
public class Server {
	// Class Files 
	private static final String COMMAND_FILE = "commands.txt";
	private static final String REQUEST_FILE = "requests.txt";
	
	// Class Messages 
	private static final String SUCCESSFUL_BUILD = "A server has been successfully created";
	private static final String SERVER_NAME = "Server Name : ";
	private static final String SERVER_IP = "IP Address : ";
	private static final String SERVER_PORT = "Port Address : ";
	private static final String HELP_RECOMMENDATION = "type \"help\" to see available commands";
	private static final String SERVER_READY = "Waiting for requests...";
	private static final String SERVER_EXIT = "A server has been successfully closed";
	private static final String SUCCESSFUL_COMMAND = "Command executed successfully : ";
	private static final String SHOPPERS_TYPE = "Shoppers : ";
	private static final String EMPLOYEES_TYPE = "Employees : ";
	private static final String ERROR_RESPONSE = "error";
	private static final String SUCCESS_RESPONSE = "success";
	private static final String END_OF_MESSAGE = "\nover"; // End of a stream
	private static final String MESSAGE_DELIMITER = " ";
	private static final String RECEIPT_DELIMITER = " DELIMITER ";
	private static final String END_OF_FILE = "\\Z";
	
	// Class Exceptions
	private static final String UNKNOWN_COMMAND_TYPE = "Unknown command type";
	private static final String UNKNOWN_COMMAND = "Unknown command";
	private static final String INVALID_COMMAND = "Invalid command";
	private static final String UNKNOWN_REQUEST = "Unknown request";
	private static final String INVALID_REQUEST = "Invalid request";
	private static final String FILE_NOT_FOUND_EXCEPTION = "File not found";
	private static final String UNKNONW_USER_TYPE = "Unknown user type";
	private static final String UNKNONW_USER = "Unknown user";
	private static final String OFFLINE_USER = "User isn't online";
	private static final String EXCEPTION_DELIMITER = " : ";
	
	// Command Parts 
	private static final int COMMAND_PARTS_AMOUNT = 2;
	private static final int REQUEST_PARTS_AMOUNT = 2;
	private static final int COMMAND_TYPE_PART = 0;
	private static final int COMMAND_NAME_PART = 0;
	private static final int REQUEST_NAME_PART = 0;
	private static final int COMMAND_ARGS_PART = 1;
	private static final int REQUEST_ARGS_PART = 1;
	
	// Command Types 
	private static final String COMMAND_TYPE = "COMMAND";
	private static final String REQUEST_TYPE = "REQUEST";
	
	// Command Names 
	private static final String HELP_COMMAND = "help";
	private static final String VIEW_COMMAND = "view";
	private static final String ADD_COMMAND = "add";
	private static final String EDIT_COMMAND = "edit";
	private static final String REMOVE_COMMAND = "remove";
	private static final String CHECK_COMMAND = "check";
	private static final String PICK_COMMAND = "pick";
	private static final String RETURN_COMMAND = "return";
	private static final String SALE_COMMAND = "sale";
	private static final String PLACE_COMMAND = "place";
	private static final String EXPIRE_COMMAND = "expire";
	private static final String MISPLACE_COMMAND = "misplace";
	private static final String EMPTY_COMMAND = "empty";
	private static final String MOVE_COMMAND = "move";
	
	// Request Names 
	private static final String HELP_REQUEST = "help";
	private static final String LOGIN_REQUEST = "login";
	private static final String GET_REQUEST = "get";
	private static final String SET_REQUEST = "set";
	private static final String SEND_REQUEST = "send";
	private static final String LOGOUT_REQUEST = "logout";
	
	// Online user types
	private static final String SHOPPER_USER = "SHOPPER";
	private static final String EMPLOYEE_USER = "EMPLOYEE";
	
	// An identity for the server
	private static final String SERVER_USER = "SERVER";
	private static final String SERVER_ID = "111";
	
	// Class Attributes
	private String serverName;
	private String serverIP;
	private int serverPort;
	private String className;
	private DatabaseManager manager;

	// Traces online users
	// Map<K,V> : Key = userID, Value = userIP
	private HashMap<String, String> onlineShoppers;
	private HashMap<String, String> onlineEmployees;
	
	// Builds a server to process all requests/commands
	public Server(String serverName, String serverIP ,int serverPort) throws Exception {
		this.serverName = serverName;
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		
		// Gets class name for future exceptions messages
		className = new Object(){}.getClass().getEnclosingClass().getSimpleName();
		
		// Builds a database Manager to manage a database
		manager = new DatabaseManager();
		
		onlineShoppers = new HashMap<>();
		onlineEmployees = new HashMap<>();
		
		Logger.printMessage(SERVER_USER, serverIP, SERVER_ID, Logger.MessageType.RESPONSE, SUCCESSFUL_BUILD);
		Logger.printMessage(SERVER_USER, serverIP, SERVER_ID, Logger.MessageType.INFOMATION, SERVER_NAME + this.serverName);
		Logger.printMessage(SERVER_USER, serverIP, SERVER_ID, Logger.MessageType.INFOMATION, SERVER_IP + this.serverIP);
		Logger.printMessage(SERVER_USER, serverIP, SERVER_ID, Logger.MessageType.INFOMATION, SERVER_PORT + this.serverPort);
		Logger.printMessage(SERVER_USER, serverIP, SERVER_ID, Logger.MessageType.INFOMATION, HELP_RECOMMENDATION);
		Logger.printMessage(SERVER_USER, serverIP, SERVER_ID, Logger.MessageType.INFOMATION, SERVER_READY);
	}
		
	// Checks to see if it is an offline command, or online request
	public String processCommandType(String userType, String userIP, String userID, String userMessage) {
		String[] userMessageParts = userMessage.split(MESSAGE_DELIMITER, COMMAND_PARTS_AMOUNT);
		String commandType, commandArgs = "", error = "", response = "";
		
		try {
			commandType = userMessageParts[COMMAND_TYPE_PART];
			commandArgs = userMessageParts[COMMAND_ARGS_PART];
			
			// Checks the type of command is an online request
			switch(commandType) {
			case COMMAND_TYPE:
				Logger.printMessage(userType, userIP, userID, Logger.MessageType.COMMAND, commandArgs);
				response = processCommand(commandArgs);
				break;
			case REQUEST_TYPE:
				Logger.printMessage(userType, userIP, userID, Logger.MessageType.REQUEST, commandArgs);
				response = processRequest(userType, userIP, userID, commandArgs);
				break;
			default: throw new Exception(className + EXCEPTION_DELIMITER + UNKNOWN_COMMAND_TYPE + EXCEPTION_DELIMITER + commandType);
			}
			Logger.printMessage(userType, userIP, userID, Logger.MessageType.RESPONSE, SUCCESSFUL_COMMAND + commandArgs);
		}catch(ArrayIndexOutOfBoundsException e) {
			error = className + EXCEPTION_DELIMITER + INVALID_COMMAND + EXCEPTION_DELIMITER + commandArgs;
		}catch(Exception e) {
			error = e.getMessage();
		}
		
		if(!error.isEmpty()){
			Logger.printMessage(userType, userIP, userID, Logger.MessageType.ERROR, error + EXCEPTION_DELIMITER + commandArgs);
			response = ERROR_RESPONSE; 
		}
		
		return response;
	}
	
	// Processes a command from an offline user
	private String processCommand(String command) throws Exception {
		String[] commandParts = command.split(MESSAGE_DELIMITER, COMMAND_PARTS_AMOUNT);
		String response;

		try {
			// Checks the type of command
			switch (commandParts[COMMAND_NAME_PART].toLowerCase()) {
			case HELP_COMMAND:
				response = printHelp();
				break;
			case VIEW_COMMAND:
				response = viewItems(commandParts[COMMAND_ARGS_PART]);
				break;
			case ADD_COMMAND:
				response = addItem(commandParts[COMMAND_ARGS_PART]);
				break;
			case EDIT_COMMAND:
				response = editItem(commandParts[COMMAND_ARGS_PART]);
				break;
			case REMOVE_COMMAND:
				response = removeItem(commandParts[COMMAND_ARGS_PART]);
				break;
			case CHECK_COMMAND:
				response = checkOnlineUsers();
				break;
			case PICK_COMMAND:
			case RETURN_COMMAND:
			case SALE_COMMAND :
				response = sendShopperEvent(command, commandParts[COMMAND_ARGS_PART]);
				break;
			case PLACE_COMMAND:
			case EXPIRE_COMMAND:
			case MISPLACE_COMMAND:
			case EMPTY_COMMAND:
				response = sendEmployeeEvent(command, commandParts[COMMAND_ARGS_PART]);
				break;
			case MOVE_COMMAND:
				response = moveUserEvent(command, commandParts[COMMAND_ARGS_PART]);
				break;
			default:
				throw new Exception(className + EXCEPTION_DELIMITER + UNKNOWN_COMMAND + EXCEPTION_DELIMITER + commandParts[COMMAND_NAME_PART]);
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_COMMAND + EXCEPTION_DELIMITER + command);
		} 
		
		return response;
	}

	// Presents all server offline commands
	private String printHelp() throws Exception  {
		String response;
		
		try(Scanner input = new Scanner(new File(COMMAND_FILE))){
			// Reads all commands file
			response = input.useDelimiter(END_OF_FILE).next();
		} catch (FileNotFoundException e) {
			throw new Exception(className+EXCEPTION_DELIMITER+FILE_NOT_FOUND_EXCEPTION+EXCEPTION_DELIMITER+COMMAND_FILE);
		}
			
		return response;
	}

	// Presents all items of a certain type from the database
	private String viewItems(String itemType) throws Exception {
		String type = itemType.substring(0, itemType.length()-1); // Removes the "s" at the end
		String response = manager.viewItems(type);

		return response;
	}
	
	// Adds an item to the database
	private String addItem(String itemArgs) throws Exception {		
		String[] argsParts = itemArgs.split(MESSAGE_DELIMITER, 2);
		String itemType, args;
		
		try {
			itemType = argsParts[0];
			args = argsParts[1];
			manager.addItem(itemType, args);
		}catch (ArrayIndexOutOfBoundsException ex) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_COMMAND + EXCEPTION_DELIMITER + itemArgs);
		}
		
		return SUCCESS_RESPONSE;
	}

	// Edits an item from the database
	private String editItem(String itemArgs) throws Exception {
		String[] argsParts = itemArgs.split(MESSAGE_DELIMITER, 3);
		String itemType, itemNumber, args;
		
		try {
			itemType = argsParts[0];
			itemNumber = argsParts[1];
			args = argsParts[2];
			manager.editItem(itemType, itemNumber, args);
		}catch (ArrayIndexOutOfBoundsException ex) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_COMMAND + EXCEPTION_DELIMITER + itemArgs);
		}
		
		return SUCCESS_RESPONSE;
	}

	// Removes an item from the database
	private String removeItem(String itemArgs) throws Exception {
		String[] argsParts = itemArgs.split(MESSAGE_DELIMITER, 2);
		String itemType, itemNumber;
		
		try {
			itemType = argsParts[0];
			itemNumber = argsParts[1];
			manager.removeItem(itemType, itemNumber);
		}catch (ArrayIndexOutOfBoundsException ex) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_COMMAND + EXCEPTION_DELIMITER + itemArgs);
		}
		
		return SUCCESS_RESPONSE;
	}
	
	// Checks current online users
	private String checkOnlineUsers() {
		StringBuilder response = new StringBuilder();
		
		response.append(SHOPPERS_TYPE+"\n");
		for(HashMap.Entry<String, String> entry : onlineShoppers.entrySet())
			response.append(entry.getKey() + MESSAGE_DELIMITER + entry.getValue()+"\n");
		response.append(EMPLOYEES_TYPE+"\n");
		for(HashMap.Entry<String, String> entry : onlineEmployees.entrySet())
			response.append(entry.getKey() + MESSAGE_DELIMITER + entry.getValue()+"\n");
		
		return response.toString();
	}
	
	// Sends an event to a shopper
	private String sendShopperEvent(String command, String commandArgs) throws Exception {
		String[] commandParts = commandArgs.split(MESSAGE_DELIMITER, 3);
		String productID, shopperID;
		
		try {
			productID = commandParts[0];
			shopperID = commandParts[1];
		}catch (ArrayIndexOutOfBoundsException ex) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_COMMAND + EXCEPTION_DELIMITER + commandArgs);
		}
		
		manager.checkProductID(productID);
		if(!onlineShoppers.containsKey(shopperID))
			throw new Exception(className + EXCEPTION_DELIMITER + OFFLINE_USER + EXCEPTION_DELIMITER + commandArgs);
		
		Messenger.sendEvent(onlineShoppers.get(shopperID), command + END_OF_MESSAGE);
		
		return SUCCESS_RESPONSE;
	}
	
	// Sends an event to an employee
	private String sendEmployeeEvent(String command, String commandArgs) throws Exception {
		String[] commandParts = commandArgs.split(MESSAGE_DELIMITER, 3);
		String productID, employeeID;
		
		try {
			productID = commandParts[0];
			employeeID = commandParts[1];
		}catch (ArrayIndexOutOfBoundsException ex) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_COMMAND + EXCEPTION_DELIMITER + commandArgs);
		}
		
		manager.checkProductID(productID);
		if(!onlineEmployees.containsKey(employeeID))
			throw new Exception(className + EXCEPTION_DELIMITER + OFFLINE_USER + EXCEPTION_DELIMITER + commandArgs);
		
		Messenger.sendEvent(onlineEmployees.get(employeeID), command + END_OF_MESSAGE);
		
		return SUCCESS_RESPONSE;
	}
	
	// Moves a user digital location
	private String moveUserEvent(String command, String commandArgs) throws Exception {
		String[] commandParts = commandArgs.split(MESSAGE_DELIMITER, 3);
		String userType, userId;

		try {
			userType = commandParts[0];
			userId = commandParts[1];
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_COMMAND + EXCEPTION_DELIMITER + commandArgs);
		}

		switch (userType.toUpperCase()) {

		case SHOPPER_USER:
			if (!onlineShoppers.containsKey(userId))
				throw new Exception(className + EXCEPTION_DELIMITER + OFFLINE_USER + EXCEPTION_DELIMITER + commandArgs);
			Messenger.sendEvent(onlineShoppers.get(userId), command + END_OF_MESSAGE);
			break;

		case EMPLOYEE_USER:
			if (!onlineEmployees.containsKey(userId))
				throw new Exception(className + EXCEPTION_DELIMITER + OFFLINE_USER + EXCEPTION_DELIMITER + commandArgs);
			Messenger.sendEvent(onlineEmployees.get(userId), command + END_OF_MESSAGE);
			break;

		default:
			throw new Exception(className + EXCEPTION_DELIMITER + UNKNONW_USER_TYPE + EXCEPTION_DELIMITER + userType);
		}

		return SUCCESS_RESPONSE;
	}
	
	
	public String exit() {
		
		try{
			manager.exit();
			Logger.printMessage(SERVER_USER, serverIP, SERVER_ID, Logger.MessageType.INFOMATION, SERVER_EXIT);
		}catch(Exception e) {
			Logger.printMessage(SERVER_USER, serverIP, SERVER_ID, Logger.MessageType.ERROR, e.getMessage());
		}
		
		return SUCCESS_RESPONSE;
	}
	
	// Processes a request from an online user
	private String processRequest(String userType, String userIP, String userID, String request) throws Exception {
		String[] requestParts = request.split(MESSAGE_DELIMITER, REQUEST_PARTS_AMOUNT);
		String response;

		try {
			// Checks the type of command
			switch (requestParts[REQUEST_NAME_PART].toLowerCase()) {
			case HELP_REQUEST:
				response = getHelp();
				break;
			case LOGIN_REQUEST:
				response = login(userType, userIP, requestParts[REQUEST_ARGS_PART]);
				break;
			case GET_REQUEST:
				response = getItems(userType, userID, requestParts[REQUEST_ARGS_PART]);
				break;
			case SET_REQUEST:
				response = setCredentials(userType, userID, requestParts[REQUEST_ARGS_PART]);
				break;
			case SEND_REQUEST:
				response = sendReceipt(userType, userIP, userID,requestParts[REQUEST_ARGS_PART]);
				break;
			case LOGOUT_REQUEST:
				response = logout(userType, userID);
				break;
			default:
				throw new Exception(className + EXCEPTION_DELIMITER + UNKNOWN_REQUEST + EXCEPTION_DELIMITER + requestParts[REQUEST_NAME_PART]);
			}
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_REQUEST + EXCEPTION_DELIMITER + request);
		} 
		
		return response + END_OF_MESSAGE;
	}
	
	// Returns all server online requests
	private String getHelp() throws Exception {
		String response;
		
		try(Scanner input = new Scanner(new File(REQUEST_FILE))){
			// Reads all commands file
			response = input.useDelimiter(END_OF_FILE).next();
		} catch (FileNotFoundException e) {
			throw new Exception(className+EXCEPTION_DELIMITER+FILE_NOT_FOUND_EXCEPTION+EXCEPTION_DELIMITER+REQUEST_FILE);
		}
			
		return response;
	}
	
	// Logs into the server
	private String login(String userType, String userIP, String itemArgs) throws Exception {
		String[] argsParts = itemArgs.split(MESSAGE_DELIMITER, 2);
		String response, email, password, userId;
		String[] responseParts;

		try {
			email = argsParts[0];
			password = argsParts[1];
			response = manager.login(userType, email, password);
			responseParts = response.split(MESSAGE_DELIMITER, 2);
			userId = responseParts[0];
			switch (userType) {
			case SHOPPER_USER:
				onlineShoppers.put(userId, userIP);
				break;
			case EMPLOYEE_USER:
				onlineEmployees.put(userId, userIP);
				break;
			}
			response = responseParts[1]; // The returned JSON from the database
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_REQUEST + EXCEPTION_DELIMITER + itemArgs);
		} 

		return response;
	}
	
	// Gets all requested items of a certain type 
	private String getItems(String userType, String userID, String itemType) throws Exception {
		String type = itemType.substring(0, itemType.length()-1); // Removes the "s" at the end
		String response;
		
		switch(userType) {
		case SHOPPER_USER:
			if(!onlineShoppers.containsKey(userID))
				throw new Exception(className + EXCEPTION_DELIMITER + UNKNONW_USER + EXCEPTION_DELIMITER + userID);
			break;
		case EMPLOYEE_USER:
			if(!onlineEmployees.containsKey(userID))
				throw new Exception(className + EXCEPTION_DELIMITER + UNKNONW_USER + EXCEPTION_DELIMITER + userID);
			break;
		default:
			throw new Exception(className + EXCEPTION_DELIMITER + UNKNONW_USER_TYPE + EXCEPTION_DELIMITER + userType);
		}
		
		response = manager.getItems(userID, type);
		
		return response;
	}
	
	// Edits a specific attribute in a user data
	private String setCredentials(String userType, String userID, String itemArgs) throws Exception {
		String[] argsParts = itemArgs.split(MESSAGE_DELIMITER, REQUEST_PARTS_AMOUNT); 
		
		switch(userType) {
		case SHOPPER_USER:
			if(!onlineShoppers.containsKey(userID))
				throw new Exception(className + EXCEPTION_DELIMITER + UNKNONW_USER + EXCEPTION_DELIMITER + userID);
			break;
		case EMPLOYEE_USER:
			if(!onlineEmployees.containsKey(userID))
				throw new Exception(className + EXCEPTION_DELIMITER + UNKNONW_USER + EXCEPTION_DELIMITER + userID);
			break;
		default:
			throw new Exception(className + EXCEPTION_DELIMITER + UNKNONW_USER_TYPE + EXCEPTION_DELIMITER + userType);
		}
		
		try {
			manager.setCredentials(userType, userID, argsParts[0], argsParts[1]);
		}catch (ArrayIndexOutOfBoundsException ex) {
			throw new Exception(className + EXCEPTION_DELIMITER + INVALID_REQUEST + EXCEPTION_DELIMITER + itemArgs);
		} 
		
		return SUCCESS_RESPONSE;
	}
	
	// Sends a receipt to its owner E-MAIL
	private String sendReceipt(String userType, String userIP, String userID, String receipt) throws Exception {
		String email;
		String messageReceipt = receipt.replaceAll(RECEIPT_DELIMITER, "\n");
		
		switch(userType) {
		case SHOPPER_USER:
			if(!onlineShoppers.containsKey(userID))
				throw new Exception(className + EXCEPTION_DELIMITER + UNKNONW_USER + EXCEPTION_DELIMITER + userID);
			email = manager.getShopperMail(userID);
			Messenger.sendReciept(userID, email, messageReceipt);
			break;
		default:
			throw new Exception(className + EXCEPTION_DELIMITER + UNKNONW_USER_TYPE + EXCEPTION_DELIMITER + userType);
		}
		
		return SUCCESS_RESPONSE;
	}
	
	// Logging out a user 
	private String logout(String userType, String userID) throws Exception {
		switch (userType) {
		case SHOPPER_USER:
			if(!onlineShoppers.containsKey(userID))
				throw new Exception(className + EXCEPTION_DELIMITER + UNKNONW_USER + EXCEPTION_DELIMITER + userID);
			onlineShoppers.remove(userID);
			break;
		case EMPLOYEE_USER:
			if(!onlineEmployees.containsKey(userID))
				throw new Exception(className + EXCEPTION_DELIMITER + UNKNONW_USER + EXCEPTION_DELIMITER + userID);
			onlineEmployees.remove(userID);
			break;
		}
		
		return SUCCESS_RESPONSE;
	}
}
