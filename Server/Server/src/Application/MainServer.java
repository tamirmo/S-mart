package Application;
import BusinessLogic.Server;

// Starts S-Mart Main Server
public class MainServer {
	public static void main(String[] args) {
		System.out.println("Welcome to S-Mart main server");
		Server server = new Server();
		server.start();
		System.out.println("Goodbye");
	}
}
