package BusinessLogic;

import java.net.InetAddress;
import java.util.Scanner;

public class ServerManager {

	// Distinguish exit console command from the rest
	private static final String EXIT_COMMAND = "exit";

	// Identify important messages for LogPrinter
	private static final String ERROR_MESSAGE_TYPE = "error";
	private static final String SYSTEM_MESSAGE_TYPE = "system";

	// Messages
	private static final String SERVER_READY = "Waiting for requests...";
	
	// Identity to present to the DataKeeper in order to skip authentication
	private static final String ADMIN_USER = "admin";
	
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
			server.printExternalMessage(ERROR_MESSAGE_TYPE, error.getMessage());
		}
		
	}

	// Starts serverManager to receive commands from the console
	public void start() {
		// Scans console for commands
		Scanner input;
		String command = "";

		if (server.getIsAvailable()) {
			input = new Scanner(System.in);
			server.printExternalMessage(SYSTEM_MESSAGE_TYPE, SERVER_READY);

			/*
			 * Reads commands from the console, and sends them to the server 
			 * Write "help" for a display of all server commands
			 */
			while (!command.toLowerCase().equals(EXIT_COMMAND)) {
				command = input.nextLine();
				if (!command.toLowerCase().equals(EXIT_COMMAND))
					server.processCommand(ADMIN_USER, command);
			}
			input.close();

			// Stops the server
			server.stopServer();
		}
	}
}