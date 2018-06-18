package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpSender
{
	// The single instance of the Sender
	private static UdpSender instance;

	// Creating a UDP socket
	private DatagramSocket udpSocket;

	private UdpSender()
	{
		initializeSocket();
	}

	/**
	 * This method initializes the udp socket
	 */
	private void initializeSocket()
	{
		try
		{
			// Initializing a UDP socket
			udpSocket = new DatagramSocket();
		}
		catch(SocketException e)
		{
			e.printStackTrace();
		}
	}

	public static UdpSender getInstance()
	{
		// If the single instance is null
		// (has not been created yet)
		if( instance == null )
		{
			// Creating it
			instance = new UdpSender();
		}

		// Returning the instance
		return instance;
	}

	/**
	 * This method sends the given string to the given address
	 * @param message String, The message to send
	 * @param address String, The IP to send to
	 * @param port integer, The port to send through
	 */
	public void sendMessage(String message, String address, int port)
	{
		try
		{
			sendMessage(message, InetAddress.getByName(address), port);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * This method sends the given string to the given address
	 * @param message String, The message to send
	 * @param address InetAddress, The IP to send to
	 * @param port integer, The port to send through
	 */
	public void sendMessage(String message, InetAddress address, int port)
	{
		try
		{
			// Creating the packets with the data
			DatagramPacket data = new DatagramPacket(message.getBytes(),
					message.length(),
					address,
					port);

			// Sending the data to the UDP socket
			udpSocket.send(data);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method sends the given bytes to the given address
	 * @param bytesToSend byte[], The bytes to send
	 * @param address String, The IP to send to
	 * @param port integer, The port to send through
	 */
	public void sendBytes(byte[] bytesToSend,
			String address,
			int port)
	{
		try
		{
			// Creating the packets with the data
			DatagramPacket data = new DatagramPacket(bytesToSend,
					bytesToSend.length,
					InetAddress.getByName(address),
					port);

			// Sending the data to the UDP socket
			udpSocket.send(data);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * This method closes the UDP socket
	 */
	public void stopAll()
	{
		// Closing the socket
		udpSocket.close();
	}
}
