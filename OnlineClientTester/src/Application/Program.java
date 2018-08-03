package Application;

import java.util.Scanner;

// Starts S-Mart Shopper Client
public class Program {
	// Server's information
	private static final int SERVER_PORT = 5001;
	
	// A server connector to connect to the server
	private static ServerConnector connector;
	
	// Program starts here
	public static void main(String[] args) {
		Scanner input = new Scanner(System.in);
		String ipAddress;
		
		// Gets IP-Address from the user 
		System.out.print("Enter IP Address of the server : ");
		ipAddress = input.nextLine();
		
		try {
			// Creates a serverConnector to test a server
			connector = new ServerConnector(ipAddress, SERVER_PORT);
			
			// Starts the serverConnector for work
			connector.start();
			
			// Must be closed last or else it closes System.in for input
			input.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}
