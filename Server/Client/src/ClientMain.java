import java.util.Scanner;

import communicationUtilities.Message;
import udp.IUdpMessageReceived;
import udp.UdpSocketHandler;

public class ClientMain {

	public static void main(String[] args) {
		UdpSocketHandler udpSocketHandler = new UdpSocketHandler(5001);
		udpSocketHandler.addNewMessageRecievedListener(receiver);
		udpSocketHandler.startListening();
		
		Scanner sc = new Scanner(System.in);
		String line = "";
		while(line != "End"){
			line = sc.nextLine();
		}
		
		sc.close();
	}
	
	private static IUdpMessageReceived receiver = new IUdpMessageReceived() {
		
		@Override
		public void messageReceived(Message messageReceived) {
			// TODO Auto-generated method stub
			System.out.println("Received " + messageReceived.getMessageContent());
		}
	};

}
