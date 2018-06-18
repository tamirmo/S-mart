package Multicast;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import communicationUtilities.Message;

/**
 * This class represents a multicast object of sending and receiving data
 * @author User
 *
 */
public class MulticastHandler
{
	// The IP address of the multicast
	private InetAddress address;
	
	// The multicast object
	// (that listens to the multicast)
	protected MulticastSocket socket;
	
	/**
	 * Constructs a new multicast handler with the given IP and port
	 * @param address String, The address to listen to
	 * @param port integer, The port of the multicast
	 */
	public MulticastHandler(String address,
			int port)
	{
		try
		{
			// Creating an IP address object from the given string of the address
			this.address = InetAddress.getByName(address);
			
			// Initializing the multicast object with the given port
			socket = new MulticastSocket(port);
			socket.setReuseAddress(true);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method starts listening to the multicast
	 */
	public void joinGroup()
	{
		try
		{
			// Joining the multicast
			socket.joinGroup(address);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method stops the multicast listening
	 */
	public void stopListeningToGroup()
	{
		try
		{
			// Closing the multicast connection
			socket.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method gets a string from the multicast
	 * @param timeout integer, The time to wait for a data to receive 
	 * @return Message The message received from the multicast
	 * (empty if none)
	 */
	public Message getData(int timeout)
	{
		// The message received is empty at first
		String message = "";
		
		// The address is null at first
		InetAddress senderAddress = null;
		
		//The message as bytes
		byte[] messageBytes = null;
		
		try
		{
			// Creating a buffer
			byte[] buffer = new byte[10*1024];
			
			// Creating a packet to receive
			DatagramPacket data = new DatagramPacket(buffer, buffer.length);
			
			// Waiting only a given number of miliseconds to get data
			socket.setSoTimeout(timeout);
			
			// Trying to receive the data from the multicast
			socket.receive(data);
			
			// Parsing the message as String
			message = new String(buffer, 0, data.getLength());
			
			// Initializing the message's bytes array
			messageBytes = new byte[data.getLength()];
			
			// Copying the data of the message to a new bytes array
			System.arraycopy(buffer, 0, messageBytes, 0, data.getLength());
			
			// Saving the address of the sender
			senderAddress = data.getAddress();
		}
		catch(Exception ex)
		{
			//ex.printStackTrace();
		}
		
		// Returning a new object with the address of the sender
		// and the message itself
		return new Message(senderAddress,message, messageBytes);
	}
}
