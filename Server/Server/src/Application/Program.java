package Application;

// Starts S-Mart Server
public class Program {
	// Server's information on which it will be built
	private static final int SERVER_PORT = 5001;
	private static final String SERVER_NAME = "S-Mart"; 
	
	// A server manager to manage a server
	private static ServerManager manager;
	
	// Program starts here
	public static void main(String[] args) {		
		try {
			// Creates a serverManger to manage a server
			manager = new ServerManager(SERVER_NAME, SERVER_PORT);
			
			// Starts the serverManager for work
			manager.start();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
