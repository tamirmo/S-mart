package Security;

import BusinessLogic.Server;

// Responsible for server security
public class ServerSecurity {
	
	// Class Messages
	private static final String MESSAGE_DELIMITER = " ";
	
	// Class Exceptions
	private static final String UNAUTHORIZED_USER_EXCEPTION = "Unauthorized user";
	private static final String INVALID_USER_EXCEPTION = "Invalid user";
	private static final String EXCEPTION_DELIMITER = " : ";
	
	// Message parts 
	private static final int MESSAGE_SPLIT_AMOUNT = 4;
	private static final int USER_TYPE_PART = 0;
	private static final int USER_IP_PART = 1;
	private static final int USER_ID_PART = 2;
	private static final int USER_COMMAND_PART = 3;
	
	// Class attributes
	private Server server;
	private String className;
	
	// Builds the server security to check all incoming requests/commands
	public ServerSecurity(String serverName, String serverIP, int serverPort) throws Exception {
		
		// Gets class name for future exceptions messages
		className = new Object(){}.getClass().getEnclosingClass().getSimpleName();
		
		// Continues to build the server from the Business Logic layer to the Database layer
		server = new Server(serverName, serverIP, serverPort);
	}
	
	// Extracts message's user parts
	public String processMessageUser(String message, String senderIP) {
		String[] messageParts = message.split(MESSAGE_DELIMITER, MESSAGE_SPLIT_AMOUNT);
		String userType, userIP, userID, userCommand, error = "", response = "";
		
		try {
			userType = messageParts[USER_TYPE_PART];
			userIP = messageParts[USER_IP_PART];
			userID = messageParts[USER_ID_PART];
			userCommand = messageParts[USER_COMMAND_PART];
			
			// Checks for a man-in-the-middle attack
			response = server.processCommandType(userType, userIP, userID, userCommand);
			
			// Checks user recorded IP-Address versus received IP-Address
			// Close it if you work from android studio or have several IP-Addresses
			/*
			if(userIP.equals(senderIP))
				response = server.processCommandType(userType, userIP, userID, userCommand);
			else
				throw new Exception(UNAUTHORIZED_USER_EXCEPTION + EXCEPTION_DELIMITER + senderIP);
			*/
		}catch(ArrayIndexOutOfBoundsException e) {
			error = INVALID_USER_EXCEPTION + EXCEPTION_DELIMITER + message;
		}catch(Exception e) {
			error = e.getMessage();
		}
		
		if(!error.isEmpty())
			response = className + EXCEPTION_DELIMITER + error;
		
		return response;
	}
	
	public String exit() {
		String response = server.exit();
		
		return response;
	}
}