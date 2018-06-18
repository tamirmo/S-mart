package tcp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles a client connected to the TCP server
 * it received data from it and sends too
 * @author User
 *
 */
public class TcpServerConnectionHandler implements Runnable
{
	// This integer represents the number of milliseconds
	// until the receiving thread moves on
	// from the receive method
	private int receivingDataTimeout;

	// This is the socket of the client
	// for this thread
	private Socket clientSocket;

	// The output stream of the socket to send data through
	// (using BufferedOutputStream and not data because data cuts the string!)
	private BufferedOutputStream outputChannel;

	// The output stream of the socket to get data from
	private BufferedReader inputChannel;

	// This is the IP of the client this thread belongs to
	private InetAddress clientIp;

	// A thread object that gets data from the client
	private Thread listeningThread;

	// This boolean indicates if the receiving thread is running
	private volatile boolean isRunning;

	// A list of listeners to this client's incoming data
	private List<ITcpServerMessageReceived> messageReceivedListeners = 
			new ArrayList<ITcpServerMessageReceived>();

	// A list of listeners for a lost of connection
	private List<IOnClientDisconnected> onClientDisconnectedListeners = 
			new ArrayList<IOnClientDisconnected>();

	public InetAddress getClientIp()
	{
		return clientIp;
	}

	/**
	 * Constructs a new client thread with the given socket and starts
	 * receiving data from it
	 * @param clientSocket Socket The client's socket
	 * to receive and send data with
	 * @param receivingDataTimeout integer The number of milliseconds to wait receiving data 
	 */
	public TcpServerConnectionHandler(Socket clientSocket,
			int receivingDataTimeout)
	{
		try
		{
			// Saving the socket
			this.clientSocket = clientSocket;

			this.receivingDataTimeout = receivingDataTimeout;

			// Getting the output stream of the created socket
			// to send data through
			this.outputChannel = new BufferedOutputStream(
					clientSocket.getOutputStream());

			// Getting the input stream to get data from
			this.inputChannel = new BufferedReader(new 
					InputStreamReader(clientSocket.getInputStream()));

			// Getting the IP address of the client
			this.clientIp = clientSocket.getInetAddress();

			// Starting to listen to data coming from the client
			startListeningThread();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * This method initializes a thread to listen to data from the client
	 */
	public void startListeningThread()
	{
		try
		{
			// There is already a thread running
			if(listeningThread != null)
			{
				// Closing the last thread
				this.isRunning = false;

				// Disposing the last thread
				listeningThread = null;
			}

			// Applying a name for the thread and setting the run to this object
			listeningThread = new Thread(this, "TcpServerConnectionHandler " + 
					clientIp);

			// Setting the timer as daemon
			listeningThread.setDaemon(true);

			// Starting to listen for data
			listeningThread.start();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * This method checks if the socket is open
	 * @return boolean, True is the socket is open, False if not
	 */
	public synchronized boolean isSocketOpen()
	{
		// Initializing the result boolean with false
		// assuming the socket is closed
		boolean result = false;

		// If the socket hasn't been initialized
		if(this.clientSocket != null)
		{
			// Returning the connected indication of the socket
			result = this.clientSocket.isConnected();
		}

		return result;
	}

	/**
	 * This method sends the given data to the socket
	 * @param message String The message to send through the socket
	 */
	public synchronized void sendClientData(String message)
	{
		try
		{
			// Writing the message to the output channel of the TCP socket
			// using the bytes of the string
			sendClientData(message.getBytes());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public synchronized void sendClientData(byte[] message)
	{
		try
		{
			// Checking if the socket is open
			// and the output channel is initiated
			if(isSocketOpen() &&
					this.outputChannel != null)
			{
				// Writing the message to the output channel of the TCP socket
				this.outputChannel.write(message);

				// Clearing the buffer
				this.outputChannel.flush();
			}
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

			while(isRunning)
			{
				// Checking if the socket is open
				if(isSocketOpen())
				{
					// Getting data from the client,
					// waiting an amount of time a message
					String message = readStringClientData(receivingDataTimeout);

					// For a case when no data was received
					if(message != "")
					{
						// Notifying of a new message received
						fireMessageReceived(message);
					}
				}
				else
				{
					// Stopping the receiving thread
					isRunning = false;
				}

				Thread.sleep(200);
			}

			// Closing the socket at the end of the thread
			closeConnection();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private synchronized String readStringClientData(int timeout)
	{
		// The message received is empty at first
		String message = "";
		
		try
		{
			// Setting the amount of time to wait for a packet to arrive
			this.clientSocket.setSoTimeout(timeout);

			// Setting an array for the data
			char[] chars = new char[5000];

			// Reading the message from the input channel of the TCP socket
			int charsRead = this.inputChannel.read(chars);

			// If the end of the stream has arrived
			if(charsRead != -1)
			{
				// Parsing the message as String object
				message = new String(chars);

				// Removing the spaces left
				// out of the 5000 characters
				message = message.trim();
			}
			else
			{
				// Alerting of a client disconnection
				fireClientDisconnected();
				
				// Closing the connection
				closeConnection();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return message;
	}

	/**
	 * This method adds the given listener to the list of listeners
	 * so when there is a client disconnection it will get notified
	 * @param listener The listener implementation to add
	 */
	public synchronized void addClientDisconnectedListener(IOnClientDisconnected listener)
	{
		try
		{
			// Adding the given implementation to the list
			// so that it will get notified when there is a client disconnection
			onClientDisconnectedListeners.add(listener);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method removes the given listener to the list of listeners
	 * so when there is a client disconnection it will not get notified
	 * @param listener The listener implementation to remove
	 */
	public synchronized void removeClientDisconnectedListener(IOnClientDisconnected listener)
	{
		try
		{
			// Adding the given implementation to the list
			// so that it will not get notified when there is a client disconnection
			onClientDisconnectedListeners.remove(listener);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * This method notifies the objects who signed to the event of a client disconnection
	 */
	public synchronized void fireClientDisconnected()
	{
		try
		{
			// Going over the listeners
			for (IOnClientDisconnected currListener : onClientDisconnectedListeners)
			{
				// Notifying each of a client disconnection with the client
				currListener.clientDisconnected(this);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method adds the given listener to the list of listeners
	 * so when this client receives a message it will get it
	 * @param listener The listener implementation to add
	 */
	public synchronized void addMessageReceivedListener(ITcpServerMessageReceived listener)
	{
		try
		{
			// Adding the given implementation to the list
			// so that it will get notified when there is a new
			// message for this client
			messageReceivedListeners.add(listener);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method adds the given listener to the list of listeners
	 * so when this client receives a message it will not get it
	 * @param listener The listener implementation to add
	 */
	public synchronized void removeMessageReceivedListener(ITcpServerMessageReceived listener)
	{
		try
		{
			// Adding the given implementation to the list
			// so that it will not get notified when there is a new
			// message for this client
			messageReceivedListeners.remove(listener);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method notifies the objects who signed to the event of a client message received
	 * @param message String The message itself
	 */
	public synchronized void fireMessageReceived(String message)
	{
		try
		{
			// Going over the listeners
			for (ITcpServerMessageReceived currListener : messageReceivedListeners)
			{
				// Notifying each of a new message received 
				// with the client and the message
				currListener.messageReceived(message,this);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	public synchronized void closeConnection()
	{
		try
		{
			if(this.clientSocket != null)
			{
				// Closing the connection
				this.clientSocket.close();
			}

			if(this.inputChannel != null)
			{
				// Closing the input channel
				this.inputChannel.close();
			}

			if(this.outputChannel != null)
			{
				// Closing the output channel
				this.outputChannel.close();
			}
			
			// Stopping the receiving thread
			isRunning = false;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * This method stops all the handlers operations
	 * (stopping the receiving thread)
	 */
	public void stopAll()
	{
		// Stopping the receiving thread
		isRunning = false;
	}

}
