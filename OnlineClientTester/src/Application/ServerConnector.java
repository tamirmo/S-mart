package Application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

// Responsible for all online communication
public class ServerConnector {
	
	// Class Messages
	private static final String CONNECTION_SUCCESS = "A connection has been created successfully";
	private static final String HELP_SUGGESTTION = "Type \"help\" to see avaiable requests";
	private static final String MESSAGE_DELIMITER = " ";
	
	// Class Exceptions
	private static final String UNKNOWN_HOST_EXCEPTION = "Couldn't retrieve host's IP-Address";
	private static final String CONNECTION_EXCEPTION = "Couldn't connect to server";
	private static final String EXCEPTION_DELIMITER = " : ";
	
	// A string to distinguish logout request from the rest
	private static final String LOGOUT_REQUEST = "logout";
	
	// The type of messages this user can send
	private static final String REQUEST_MESSAGE = "REQUEST";

	// An identity for offline user
	private static final String SHOPPER_USER = "SHOPPER";
	private static final String EMPLOYEE_USER = "EMPLOYEE";
	private static final String SHOPPER_ID = "999"; // Value doesn't matter for as long as it isn't empty
	private static final int EVENT_PORT = 5002;

	// Class attributes
	private Socket connection;
	private String userIP;
	private String className;
	private String userID = "0";
	private SocketManager incomingEvent;

	// Builds a connector
	public ServerConnector(String serverIP, int serverPort) throws Exception {
		InetAddress ipAddress;

		// Gets class name for exception messages
		className = new Object() {
		}.getClass().getEnclosingClass().getSimpleName();

		try {
			// Gets IP-Address of current host
			ipAddress = InetAddress.getLocalHost();
			userIP = ipAddress.getHostAddress();
			
			// Builds the connection
			connection = new Socket(serverIP, serverPort);
			incomingEvent = new SocketManager(EVENT_PORT);
			incomingEvent.start();
			System.out.println(CONNECTION_SUCCESS);
			System.out.println(HELP_SUGGESTTION);
		} catch (UnknownHostException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + UNKNOWN_HOST_EXCEPTION);
		} catch (IOException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + CONNECTION_EXCEPTION);
		}
	}
	
	// Starts receiving requests from the console
	public void start() {
		String request = "", requestMessage, response;
		
		try(Scanner input =  new Scanner(System.in)){
			while (!request.toLowerCase().equals(LOGOUT_REQUEST)) {
				request = input.nextLine();
					requestMessage = EMPLOYEE_USER + MESSAGE_DELIMITER + userIP + MESSAGE_DELIMITER + userID
							+ MESSAGE_DELIMITER + REQUEST_MESSAGE + MESSAGE_DELIMITER + request;
					System.out.println(requestMessage);
					response = sendRequest(requestMessage);
					System.out.println(response);
			}
			
			exit();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	// Sends a request to the server, and receives a message back
	private String sendRequest(String request) throws Exception {
		OutputStream output;
		PrintWriter writer;
		InputStream input;
		BufferedReader reader;
		StringBuilder responseMessage;
		String line;

		try {
			// Sends request to a server
			output = connection.getOutputStream();
			writer = new PrintWriter(output, true);
			writer.println(request);

			// Reads a response from a server
			input = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input));
			responseMessage = new StringBuilder();
			do {
				line = reader.readLine();
				if(!line.equals("over")) {
					responseMessage.append(line+"\n");
				}
			}while (!line.equals("over"));
		} catch (IOException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + CONNECTION_EXCEPTION);
		}

		return responseMessage.toString();
	}
	
	private void exit() {
		try {
			connection.close();
			incomingEvent.eventsConnection.close();
			incomingEvent.interrupt();
		} catch (IOException e) {
			System.out.println(className + EXCEPTION_DELIMITER + CONNECTION_EXCEPTION);
		}
	}
	
	class SocketManager extends Thread{
		private ServerSocket eventsConnection;
		private Socket eventConnection;
		
		public SocketManager(int eventPort) throws IOException {
			eventsConnection = new ServerSocket(eventPort);
		}
		
		private void processEvent(String event) {
			System.out.println(event);
		}
		
		public void run() {
			InputStream input;
			BufferedReader reader;
			StringBuilder event;
			String line;
			
			try {
			while(true) {
				eventConnection = eventsConnection.accept();
				
				input = eventConnection.getInputStream();
				reader = new BufferedReader(new InputStreamReader(input));
				event = new StringBuilder();
				
				line = reader.readLine();
				event.append(line);
			
				processEvent(event.toString());
			}
			}catch(IOException e) {
				// Socket is closed
			}
		}	
	}
}
