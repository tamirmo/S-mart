package Application;
import BusinessLogic.ServerManager;

// Starts S-Mart Server
public class Program {
	private static final int SERVER_PORT = 5001;
	private static final String SERVER_NAME = "S-Mart";
	
	private static ServerManager serverManager;
	
	public static void main(String[] args) {		
		// Creates a server manger to manage server
		serverManager = new ServerManager(SERVER_NAME, SERVER_PORT);
		
		// Command the server manager to start S-Mart server
		serverManager.start();
	}
}
