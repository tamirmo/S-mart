package tcp;

import java.util.ArrayList;
import java.util.List;

public class TcpHandler implements Runnable, IOnTcpDisconnected
{
	// The TCP handler that opens the TCP socket
	// and receives the data
	private TcpClient tcpObject;

	// A list of listeners to the new data received from the TCP
	private List<ITcpMessageReceived> dataReceivedListeners = 
			new ArrayList<ITcpMessageReceived>();

	// A boolean indicating weather the thread is receiving 
	// data from the socket or not
	private volatile boolean isRunning = false;

	// The time to wait before getting another message
	private int timeout;

	// A thread object that gets data from the TCP server
	private Thread listeningThread;

	// The TCP server's address
	private String ipAddress;

	// A list of listeners for a list of connection
	private List<IOnTcpDisconnected> tcpDisconnectionListeners = 
			new ArrayList<IOnTcpDisconnected>();

	public TcpHandler(String ipAddress,
			int port,
			int timeout)
	{
		// Saving the IP of the server
		this.ipAddress = ipAddress;

		try
		{
			// Initializing the TCP object
			tcpObject = new TcpClient(ipAddress, port);

			//Signing to the TCP disconnection event
			tcpObject.addTcpDisconnectedListener(this);

			//Setting the time to wait between messages
			this.timeout = timeout;
		}
		catch(Exception ex )
		{
			ex.printStackTrace();
		}
	}

	/**
	 * This method initializes a thread to listen to data from the TCP server
	 */
	public void startListeningThread()
	{
		try
		{
			if(!isRunning)
			{
				// Connecting to the TCP
				tcpObject.connect();

				// Applying a name for the thread and setting
				// the run to this object
				this.listeningThread = new Thread(this,
						"TcpListenerThread" + this.ipAddress);

				// Setting the timer as deamon
				// (the program will exit if only this thread is running)
				this.listeningThread.setDaemon(true);

				// Starting to listen to data
				this.listeningThread.start();
			}
		}
		catch(Exception ex )
		{
			ex.printStackTrace();
		}
	}

	@Override
	public void onTcpDisconnected()
	{
		// Stopping the thread that reads data from the TCP socket
		this.isRunning = false;
		
		// Alerting of a TCP disconnection
		fireTcpDisconnection();
	}

	@Override
	public void run()
	{
		try
		{
			// Setting the boolean of the thread to true so it will start
			isRunning = true;
			
			// Checking if the state of the socket is closed
			if(!tcpObject.isSocketOpen())
			{
				// Connecting to the TCP server
			}
			
			// Getting the data while the boolean is true,
			// the user can change it from outside
			while(isRunning)
			{
				// Checking if the TCP is connected
				if(isConnected())
				{
					// Getting data from the TCP,
					// waiting an amount of time for a message
					String message = tcpObject.readStringTcpData(timeout);
					
					// For a case when no data was received
					if (message != "")
					{
						// Notifying of a new message received
						fireMessageReceived(message);
					}
					else
					{
						// Stopping the TCP
						isRunning = false;
					}
					
					Thread.sleep(200);
				}
			}
		}
		catch(Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	public void startListening()
	{
		// Staring to listen to data from the TCP server
		startListeningThread();
	}
	
	/**
	 * This method stops the thread that receives data and closing the connection
	 */
	public void disconnect()
	{
		try
		{
			// This boolean is checked every time
			// the TCP read operation reached the timeout
			// or gets a new message
			// (happens in the run method)
			
			if(this.tcpObject != null)
			{
				// Closing the connection only if is is opened
				if(this.tcpObject.isSocketOpen())
				{
					// Closing the resources
					this.tcpObject.closeConnection();
				}
			}
		}
		catch(Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	public void sendMessage(String message)
	{
		try
		{
			// If the socket if open
			if(this.tcpObject.isSocketOpen())
			{
				// Sending the message
				this.tcpObject.sendTcpData(message);
			}
		}
		catch(Exception ex )
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method checks if the TCP is connected
	 * or not and returns the result
	 * @return boolean, True if the connection is opens, False if not
	 */
	public boolean isConnected()
	{
		return this.tcpObject.isSocketOpen();
	}

	/**
	 * This method adds the given listener to the list of listeners
	 * so that when there is a new message from the server
	 * if will get it
	 * @param listener ITcpMessageReceived The implementation to add
	 */
	public synchronized void addMessageReceivedListener(ITcpMessageReceived listener)
	{
		try
		{
			// Adding the given implementation to the list
			// so that it will get notified when 
			// there is a new message from the server if will get it
			dataReceivedListeners.add(listener);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method removes the given listener to the list of listeners
	 * so that when there is a new message if will not get it
	 * @param listener ITcpMessageReceived The implementation to remove
	 */
	public synchronized void removeMessageReceivedListener(ITcpMessageReceived listener)
	{
		try
		{
			// Removing the given implementation to the list
			// so that it will not get notified when 
			// there is a new message from the server it will not get it
			dataReceivedListeners.remove(listener);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method fires the message received event with the given message
	 * to all the listeners
	 * @param message String, The new message received
	 */
	public synchronized void fireMessageReceived(String message)
	{
		try
		{
			// Going over the listeners
			for (ITcpMessageReceived currListener : 
				dataReceivedListeners)
			{
				// Notifying each of a message received
				// with the message
				currListener.messageReceived(message);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method adds the given listener to the list of listeners
	 * so that when there is a TCP disconnection if will get notified
	 * @param listener IOnTcpDisconnected The implementation to add
	 */
	public synchronized void addTcpDisconnectedListener(IOnTcpDisconnected listener)
	{
		try
		{
			// Adding the given implementation to the list
			// so that it will get notified when there is a TCP disconnection
			tcpDisconnectionListeners.add(listener);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method removes the given listener to the list of listeners
	 * so that when there is a TCP disconnection if will not get notified
	 * @param listener IOnTcpDisconnected The implementation to remove
	 */
	public synchronized void removeTcpDisconnectedListener(IOnTcpDisconnected listener)
	{
		try
		{
			// Removing the given implementation to the list
			// so that it will not get notified when there is a TCP disconnection
			tcpDisconnectionListeners.remove(listener);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method fires the TCP disconnected event
	 */
	public synchronized void fireTcpDisconnection()
	{
		try
		{
			// Going over the listeners
			for (IOnTcpDisconnected currListener : 
				tcpDisconnectionListeners)
			{
				// Notifying each of a TCP disconnection
				currListener.onTcpDisconnected();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
}
