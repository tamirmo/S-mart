package Network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import Security.ServerSecurity;

// Responsible for all server communications
public class ServerAdaptor  {
	
	// Class attributes
	private ServerSecurity securityManager;
	private SocketManager connectionManager;
	
	// Builds an adaptor
	public ServerAdaptor(String serverName, String serverIP, int serverPort) throws Exception  {
		
		// Continues to build the server from the Security layer to the Database layer
		securityManager = new ServerSecurity(serverName, serverIP, serverPort);
		
		// Starts an online server on a different thread
		connectionManager = new SocketManager(serverPort);
		connectionManager.start();
	}

	// Starts to process a message
	public String processMessage(String message, String senderIP) {
		String response = securityManager.processMessageUser(message, senderIP);
		
		return response;
	}
	
	public String exit() {
		String response;
		
		connectionManager.exit();
		connectionManager.interrupt();
		response = securityManager.exit();
		
		return response;
	}
	
	// A thread to deal with all incoming connections to the server
	class SocketManager extends Thread {
		// Class attributes
		private ServerSocket onlineServer;
		private List<Socket> connections;
		
		// Builds a thread to serve all incoming connection requests
		public SocketManager(int serverPort) throws NumberFormatException, IOException {
			connections = new ArrayList<Socket>();
			onlineServer = new ServerSocket(serverPort);
		}
		
		// Opens a thread for each connection
		public void run() {
			Socket connection;
			
			try {
				// Loops until being stopped with an exit command by the offline admin
				while (true) {
				// Waits for a connection, and returns a socket
				connection = onlineServer.accept();
				
				// Adds the socket to a list so it can be stopped
				connections.add(connection);
				
				// Creates a new thread on which this socket will run
				new ClientThread(connection).start();
				}
			} catch (IOException e) {
				// An exception because one side is disconnected
				}
			}
		
		public void exit() {

			// Closes connections
			for (Socket connection : connections) {
				try {
					connection.close();
				}catch (IOException e) {
					// An exception because one side is disconnected
				}
			}

			try {
				onlineServer.close();
			} catch (IOException e) {
				// Nothing to do with this exception
			}
		}
		
		// A thread to deal with all requests of a specific client
		class ClientThread extends Thread {
			// A word to point when it is the end of a stream/response
			private static final String END_OF_MESSAGE = "over";
			
			// Class attributes
			private Socket connection;
			
			// Builds a thread to serve all client requests
			public ClientThread(Socket connection) {
				this.connection = connection;
			}
			
			// Gets sender requests, process them, and send server response back
			public void run() {
				String request, response, senderIP;

				try {
					// Loops until one side is disconnected
					while (true) {
						// Gets sender IP-Address each time to do a security check on a man-in-the-middle attack
						senderIP = connection.getRemoteSocketAddress().toString().split("/|:")[1];
						
						// Gets sender Request
						request = getIncomingMessage();
						
						// Gets server response
						response = processMessage(request, senderIP);

						// Sends server response back to the sender
						sendResponse(response);
					}
				} catch (IOException e) {
					// An exception because one side is disconnected
				}
				
				try {
					connections.remove(connection);
					connection.close();
				} catch (IOException e) {
					// There is no one to return this message to
					// There is no reason to do anything for this exception
				}
			}
			
			// Gets an incoming request
			private String getIncomingMessage() throws IOException {
				InputStream input;
				BufferedReader reader;
				StringBuilder incomingRequest;
				String line = "";
				
				input = connection.getInputStream();
				reader = new BufferedReader(new InputStreamReader(input));
				incomingRequest = new StringBuilder();
				
				// Reads incoming requests
				line = reader.readLine();
				incomingRequest.append(line);
			
				return incomingRequest.toString();
			}

			// Sends the response
			private void sendResponse(String response) throws IOException {
				OutputStream output;
				PrintWriter writer;
				
				output = connection.getOutputStream();
				writer = new PrintWriter(output, true);
				
				// Sends the response
				writer.println(response + END_OF_MESSAGE);
			}
		}		
	}
}
