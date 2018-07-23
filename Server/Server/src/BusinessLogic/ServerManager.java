package BusinessLogic;

import java.net.InetAddress;
import java.util.Scanner;

/* Server Commands :
 * a. "help" 										- Displays all server commands on console
 * b. "start" 										- Starts the server
 * c. "view itemType" 								- Displays items of a certain type from the database  
 * d. "add itemType itemID itemArgs" 				- Adds an item to the database
 * e. "edit itemType itemID itemArgs" 				- Edits an item from the database
 * f. "remove itemType itemID" 						- Removes an item from the database 
 * g. "pick itemID shopperID" 						- Picks up a product from its shelf
 * h. "return itemID locationX locationY shopperID" - Returns a product to a shelf
 * i. "place itemID locationX locationY employeeID" - Places a product on a shelf
 * j. "exit" 										- Stops the server 	
 */

public class ServerManager {

	// Distinguish exit console command from the rest
	private static final String EXIT_COMMAND = "exit";

	// Identify important messages for LogPrinter
	private static final String ERROR = "error";
	private static final String SYSTEM = "system";

	// Messages
	private static final String READY = "Waiting for requests...";
	
	// Identity to present to the DataKeeper in order to skip authentication
	private static final String ADMIN = "admin";
	
	// A server to manage
	private Server server;

	// Builds a server
	public ServerManager(String serverName, int serverPort) {
		InetAddress serverIPAddress;
		
		try {
			// Gets IP Address of current host
			serverIPAddress = InetAddress.getLocalHost();

			// Builds the server
			server = new Server(serverName, serverIPAddress.getHostAddress(), serverPort);
		} catch (Exception error) {
			server.printExternalMessage(ERROR, error.getMessage());
		}
		
	}

	// Starts serverManager to receive commands from the console
	public void start() {
		//Scans console for commands
		Scanner input = new Scanner(System.in);
		String command = "";
		
		server.printExternalMessage(SYSTEM, READY);
		
		/* Reads commands from the console, and sends them to the server 
		 * Write "help" for a display of all server commands */
		while (!command.toLowerCase().equals(EXIT_COMMAND)) {
			command = input.nextLine();
			if(!command.toLowerCase().equals(EXIT_COMMAND))
				server.processCommand(ServerManager.ADMIN, command);
		}
		input.close();

		// Stops the server
		server.stopServer();
	}
}