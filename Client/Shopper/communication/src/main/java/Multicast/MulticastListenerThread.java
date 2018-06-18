package Multicast;

import java.util.ArrayList;
import java.util.List;

import communicationUtilities.Message;

public class MulticastListenerThread implements Runnable
{

	// The multicast handler that joining the multicast and receives the data
	private MulticastHandler multicastObject;

	// A list of objects listening to a new data received from the multicast
	private List<IMulticastMessageReceived> list = new 
			ArrayList<IMulticastMessageReceived>();

	// A boolean indicating weather the thread is 
	// receiving data from the socket or not
	private volatile boolean isRunning = false;

	// The time to wait before getting another message
	private int timeout;

	// A thread object that gets data from the multicast
	private Thread listeningThread;

	// The multicast address
	private String ipAddress;

	/**
	 * Constructs a new listener with the given address and port
	 * @param ipAddress String The address of the multicast
	 * @param port integer The port of the multicast
	 * @param timeout integer The time to wait between receiving messages
	 */
	public MulticastListenerThread(String ipAddress, 
			int port, 
			int timeout)
	{
		try
		{
			// Saving the multicast address
			this.ipAddress = ipAddress;

			// Initializing the multicast object
			multicastObject = new MulticastHandler(ipAddress, port);

			// Setting the time to wait between messages
			this.timeout = timeout;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void startListeningThread()
	{
		try
		{
			// There is already a thread running
			if(listeningThread != null)
			{
				// Closing the last thread
				this.stopListening();

				// Disposing the last thread
				listeningThread = null;
			}

			// Applying a name for the thread and setting the run to this object
			listeningThread = new Thread(this, "TcpListenerThread" + this.ipAddress);

			// Setting the timer as deamon
			// (The program will exit if only this thread is running)
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
	 * This method starts a thread that gets data from the multicast
	 */
	public void startListening()
	{
		// Starting to listen to data from the multicast
		startListeningThread();
	}

	@Override
	public void run()
	{
		try
		{
			// Setting the boolean of the thread to true so it will start
			isRunning = true;

			// Joining the multicast
			multicastObject.joinGroup();

			// Getting data while the boolean is true,
			// the user can change it from outside
			while(isRunning)
			{
				// Getting data from the multicast
				// eaiting an amount of time for a message
				Message message = multicastObject.getData(timeout);

				// For a case when no data was received
				if(message.getMessageContent() != "")
				{
					// Notifying of a new message received
					fireNewMessageReceived(message);
				}
				//System.out.println("Test!");
			}
		}
		catch(Exception ex)
		{
			//ex.printStackTrace();
		}
	}

	/**
	 * This method stops the thread that receives data
	 */
	public void stopListening()
	{
		try
		{
			// This boolean is checked every time
			// the multicast read operation reaches the timeout
			// or gets a message
			// (happens in the run method)
			this.isRunning = false;

			// If the multicast object has initiated
			if(multicastObject != null)
			{
				// Closing the connection
				multicastObject.stopListeningToGroup();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	/**
	 * This method adds the given listener to the list of listeners
	 * so when a message received it will get it
	 * @param listener The listener implementation to add
	 */
	public synchronized void addNewMessageRecievedListener(IMulticastMessageReceived listener)
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
	public synchronized void removeNewMessageRecievedListener(IMulticastMessageReceived listener)
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
			for (IMulticastMessageReceived currListener : list)
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
