package Application;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import Network.ServerAdaptor;

// Responsible for all direct offline server commands
public class ServerManager {

	// Class Messages
	private static final String EXIT_MESSAGE = "DONE";
	private static final String MESSAGE_DELIMITER = " ";
	
	// Class Exceptions
	private static final String UNKNOWN_HOST_EXCEPTION = "Couldn't retrieve host's IP-Address";
	private static final String EXCEPTION_DELIMITER = " : ";

	// A string to distinguish exit command from the rest
	private static final String EXIT_COMMAND = "exit";

	// The type of messages this user can send
	private static final String COMMAND_MESSAGE = "COMMAND";

	// An identity for offline user
	private static final String ADMIN_USER = "ADMIN";
	private static final String ADMIN_ID = "666"; // Value doesn't matter for as long as it isn't empty
	
	// Class attributes 
	private String className;
	private String adminIPAddress;
	private ServerAdaptor connection;

	// Builds a server
	public ServerManager(String serverName, int serverPort) throws Exception {
		InetAddress serverIPAddress;
		
		// Gets class name for exception messages
		className = new Object(){}.getClass().getEnclosingClass().getSimpleName();
		
		try {
			// Gets IP-Address of current host on which the server will be built
			serverIPAddress = InetAddress.getLocalHost();
			adminIPAddress = serverIPAddress.getHostAddress();
			
			// Continues to build the server from the Network layer to the Database layer
			connection = new ServerAdaptor(serverName, adminIPAddress, serverPort);
		} catch (UnknownHostException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + UNKNOWN_HOST_EXCEPTION);
		}
	}

	// Starts receiving commands from the console
	public void start() {
		String command = "", message, response;
		Scanner input;

		// Reads commands from the console
		input = new Scanner(System.in);

		// Reads commands, and sends them to the serverAdaptor
		while (!command.toLowerCase().equals(EXIT_COMMAND)) {
			command = input.nextLine();
			if (!command.toLowerCase().equals(EXIT_COMMAND)) {
				// Builds the command in a way the server can process it correctly
				message = ADMIN_USER + MESSAGE_DELIMITER + adminIPAddress + MESSAGE_DELIMITER + ADMIN_ID
						+ MESSAGE_DELIMITER + COMMAND_MESSAGE + MESSAGE_DELIMITER + command;
				response = connection.processMessage(message, adminIPAddress);
				System.out.println(response);
			}
		}

		input.close();
		exit();
	}
	
	private void exit() {
		String response = connection.exit();
		
		System.out.println(response);
	}
}