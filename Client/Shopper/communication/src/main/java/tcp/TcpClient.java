package tcp;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TcpClient
{
	// The IP address of the TCP server
	private InetAddress address;

	// The TCP client object
	// (that sends the data and listens to the server)
	private Socket socket;

	// The output stream of the socket to send data through
	// (using BufferedOutputStream and not data because data cuts the string!)
	private BufferedOutputStream outputChannel;

	// The output stream of the socket to get data from
	private BufferedReader inputChannel;

	// The port the user wants to connect to
	private int port;

	// A list of listeners for a lost of connection
	private List<IOnTcpDisconnected> onTcpDisconnectedListeners = 
			new ArrayList<IOnTcpDisconnected>();

	/**
	 * Constructs a new TCP client handler with the given IP and port
	 * @param address String, The address to listen to
	 * @param port integer, The port of the address
	 */
	public TcpClient(String address, int port)
	{
		// Saving the parameters
		this.port = port;

		try
		{
			// Creating an IP address object from the given string of the address
			this.address = InetAddress.getByName(address);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * This method connects a TCP client with
	 * the address and the port configured
	 */
	public synchronized void connect()
	{
		try
		{
			// Initializing the TCP client object with the given port
			this.socket = new Socket(this.address, this.port);
			this.socket.setReuseAddress(true);

			// Getting the output stream of the created socket to send data through
			this.outputChannel = new 
					BufferedOutputStream(socket.getOutputStream());

			// Getting the input stream to get data from
			this.inputChannel = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * This method sends the given data to the socket
	 * @param message String The message to send through the socket
	 */
	public synchronized void sendTcpData(String message)
	{
		try
		{
			// Writing the message to the output channel
			// of the TCP socket using the bytes of the string
			sendTcpData(message.getBytes());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public synchronized void sendTcpData(byte[] message)
	{
		try
		{
			// Writing the message to the output channel on the TCP socket
			this.outputChannel.write(message);

			// Clearing the buffer
			this.outputChannel.flush();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * This method reads data from the socket
	 * @param timeout integer The time to wait for the message
	 * @return String The message from the client 
	 * (empty if timeout expired)
	 */
	public synchronized String readStringTcpData(int timeout)
	{
		// The message received is empty at first
		String message = "";

		try
		{
			// Setting the amount of time to wait for a packet to arrive
			this.socket.setSoTimeout(timeout);

			// Setting an array for the data
			char[] chars = new char[5000];

			// Reading the message from the input channel of the TCP socket
			int charsRead = this.inputChannel.read(chars);

			// If the end of the stream has arrived
			if(charsRead != -1)
			{
				// Parsing the message as String object
				message = new String(chars);
			}
			else
			{
				// Alerting of a disconnection
				fireTcpDisconnection();

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
	 * This method reads bytes data from the socket
	 * @param timeout integer The time to wait for the bytes
	 * @return String The message from the client 
	 * (null if timeout expired)
	 */
	public synchronized byte[] readBytesTcpData(int timeout)
	{
		// The message received is null at first
		byte[] message = null;

		try
		{
			// Setting the amount of time to wait for a packet to arrive
			this.socket.setSoTimeout(timeout);

			// Reading the message form the input channel
			// of the TCP socket
			int x = this.inputChannel.read();

			// The end of the stream has arrived
			if(x == -1)
			{
				// Alerting of a disconnection
				fireTcpDisconnection();

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
	 * This method checks if the socket is open
	 * @return boolean, True is the socket is open, False if not
	 */
	public synchronized boolean isSocketOpen()
	{
		// Initializing the result boolean with false
		// assuming the socket is closed
		boolean result = false;

		// If the socket hasn't been initialized
		if(this.socket != null)
		{
			// Returning the connected indication of the socket
			result = this.socket.isConnected();
		}

		return result;
	}

	public synchronized void closeConnection()
	{
		try
		{
			if(this.socket != null)
			{
				// Closing the connection
				this.socket.close();
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
			onTcpDisconnectedListeners.add(listener);
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
			onTcpDisconnectedListeners.remove(listener);
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
				onTcpDisconnectedListeners)
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
