package communicationUtilities;

import java.net.InetAddress;

public class Message
{
	// The address the message belongs to
	private InetAddress address;
	
	// The message itself
	private String messageContent;
	
	private byte[] messageBytes;
	
	public InetAddress getAddress()
	{
		return address;
	}

	public void setAddress(InetAddress address)
	{
		this.address = address;
	}

	public String getMessageContent()
	{
		return messageContent;
	}

	public void setMessageContent(String messageContent)
	{
		this.messageContent = messageContent;
	}

	public byte[] getMessageBytes()
	{
		return messageBytes;
	}

	public void setMessageBytes(byte[] messageBytes)
	{
		this.messageBytes = messageBytes;
	}

	/**
	 * Constructs a new Message object with the given data and address to pass on an event 
	 * @param address InetAddress, The address which the message belongs to
	 * @param messageContent String The message data
	 * @param messageBytes byte[], The bytes of the message
	 */
	public Message(InetAddress address,
			String messageContent,
			byte[] messageBytes)
	{
		// Saving the parameters:
		this.address = address;
		this.messageContent = messageContent;
		this.messageBytes = messageBytes;
	}
}
