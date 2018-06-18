package tcp;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a TCP server class
 * that receives clients and handles them
 * @author User
 *
 */
public class TcpServer implements Runnable
{
	// The port to listen to
	private int port;

	// A thread object that accepts clients
	private Thread acceptThread;

	// The socket for accepting clients
	private ServerSocket serverSocket;

	// A list of clients threads
	private List<TcpServerConnectionHandler> clients = 
			new ArrayList<TcpServerConnectionHandler>();

	// This boolean indicates if the acceptiong thread is running
	private boolean isRunning;

	// A list of listeners for a lost of connection
	private List<IOnTcpClientConnected> onClientConnectedListeners = 
			new ArrayList<IOnTcpClientConnected>();

	/**
	 * Constructs a new server and starts accepting clients in the given port
	 * @param port integer The port of the server
	 */
	public TcpServer(int port)
	{
		try
		{
			// Saving the port to listen to
			this.port = port;

			// Creating the thread to accept clients
			// and starting it
			acceptThread = new Thread(this);
			acceptThread.start();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		try
		{
			// Setting the boolean as true at the beginning
			// to let the thread start
			isRunning = true;
			
			// Creating the server socket with the port given
			serverSocket = new ServerSocket(port);
			
			// Setting the time to wait for a new client
			serverSocket.setSoTimeout(100);
			
			while(isRunning)
			{
				// Acception clients
				acceptClients();
				
				Thread.sleep(200);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method accepts one client and creates a new handler for it
	 */
	private synchronized void acceptClients()
	{
		try
		{
			// Accepting clients and getting their sockets
			Socket client = serverSocket.accept();
			
			// Creating a handler instance
			// to handle the client's interactions
			TcpServerConnectionHandler clientHandler = 
					new TcpServerConnectionHandler(client, 100);
			
			// Adding the client to the clients list
			this.clients.add(clientHandler);
			
			// Notifying of a new client connected
			fireClientConnected(clientHandler);
		}
		catch(Exception ex)
		{
			//ex.printStackTrace();
		}
	}

	/**
	 * This method adds the given listener to the list of listeners
	 * so when there is a new client connected it will get notified
	 * @param listener The listener implementation to add
	 */
	public synchronized void addClientConnectedListener(IOnTcpClientConnected listener)
	{
		try
		{
			// Adding the given implementation to the list
			// when there is a new client connected it will get notified
			onClientConnectedListeners.add(listener);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method removes the given listener to the list of listeners
	 * so when there is a new client connected it will not get notified
	 * @param listener The listener implementation to remove
	 */
	public synchronized void removeClientConnectedListener(IOnTcpClientConnected listener)
	{
		try
		{
			// Adding the given implementation to the list
			// when there is a new client connected it will not get notified
			onClientConnectedListeners.add(listener);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method notifies the objects who signed to the event of a client connected to the server
	 * @param clientHandler TcpServerConnectionHandler The client's handler
	 */
	public synchronized void fireClientConnected(TcpServerConnectionHandler clientHandler)
	{
		try
		{
			// Going over the listeners
			for (IOnTcpClientConnected currListener : onClientConnectedListeners)
			{
				// Notifying each of a client connected to the server 
				// with the client's handler
				currListener.clientConnected(clientHandler);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method stops all operations for this TCP server
	 * it stops the accepting thread and all the clients threads
	 */
	public synchronized void stopAll()
	{
		// Stopping the accepting thread
		// to stop acceptiong new clients
		this.isRunning = false;
		
		if(clients != null)
		{
			// Going over all the clients
			for(TcpServerConnectionHandler currClient: clients)
			{
				// Stopping their operations
				currClient.stopAll();
			}
		}
	}

}
