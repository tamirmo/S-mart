package udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import communicationUtilities.Message;

public class UdpSocketHandler implements Runnable{

	private static final int READ_TIMEOUT = 50;

	private int port;
	private DatagramSocket dsocket;
	private Thread listeningThred;
	private volatile boolean isListening;
	// A list of objects listening to a new data received from the multicast
	private List<IUdpMessageReceived> list = new
			ArrayList<>();

	public UdpSocketHandler(int port){
		this.port = port;
	}

	@Override
	public void run() {
		try {
			dsocket = new DatagramSocket(port);
			byte[] buffer = new byte[2048];
			
			System.out.println("Server address " + InetAddress.getLocalHost().getHostAddress() + " listening to port " + port);
			
			// Setting the time to wait in a blocking read
			dsocket.setSoTimeout(READ_TIMEOUT);
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			isListening = true;
			while (isListening) {
				String lText = null;

				try {
					dsocket.receive(packet);

					lText = new String(buffer, 0, packet.getLength());
					//data.setText(lText);

					packet.setLength(buffer.length);
				}catch(SocketTimeoutException ex){
					// Ignoring timeout exception
				}

				// For a case when no data was received
				if(lText != null && lText != "") {
					// Notifying of a new message received
					fireNewMessageReceived(new Message(packet.getAddress(), lText, buffer));
				}
			}

			dsocket.close();

		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	public void stopListening(){
		isListening = false;
	}

	public void startListening(){
		listeningThred = new Thread(this);
		listeningThred.start();
	}

	/**
	 * This method adds the given listener to the list of listeners
	 * so when a message received it will get it
	 * @param listener The listener implementation to add
	 */
	public synchronized void addNewMessageRecievedListener(IUdpMessageReceived listener)
	{
		try
		{
			// Adding the given implementation to the list
			// so that it will get notified when a new message is received
			list.add(listener);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * This method removes the given listener to the list of listeners
	 * so when a message received it will not get it
	 * @param listener The listener implementation to remove
	 */
	public synchronized void removeNewMessageRecievedListener(IUdpMessageReceived listener)
	{
		try
		{
			// Adding the given implementation to the list
			// so that it will not get notified when a new message is received
			list.remove(listener);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public synchronized void fireNewMessageReceived(Message messageReceived)
	{
		try
		{
			// Going over the listeners
			for (IUdpMessageReceived currListener : list)
			{
				// Notifying each of a new message received
				currListener.messageReceived(messageReceived);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

}
