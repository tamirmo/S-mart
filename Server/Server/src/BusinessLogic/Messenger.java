package BusinessLogic;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;  
import javax.mail.*;  
import javax.mail.internet.*;   

// Responsible to send messages from the server
public class Messenger {
	// Class Messages 
	private static final String MAIL_SUBJECT_LINE = "S-Mart Receipt : ";
	private static final String OPENNING_MESSAGE = "Dear Shopper";
	private static final String RECEIPT_MESSAGE = "\n\nHere is your receipt :\n";
	private static final String CLOSING_MESSAGE = "\n\nThank You\nS-Mart";
	private static final String MESSAGE_DELIMITER = " ";
	
	// Class Exceptions
	private static final String MESSAGING_EXCEPTION = "Couldn't send the mail";
	private static final String CONNECTION_EXCEPTION = "Couldn't connect to the client";
	private static final String EXCEPTION_DELIMITER = " : ";
	
	// Class attributes
	private static final String COMPANY_EMAIL = "afekasmart2018@gmail.com";
	private static final String COMPANY_PASSWORD = "afeka2018";
	private static final String className = new Object(){}.getClass().getEnclosingClass().getSimpleName();
	private static final int CLIENT_PORT = 5002;
	
	// Sends a receipt to the user's email, and a copy to the company email by using TLS sockets
	public static void sendReciept(String userId, String userMail, String receipt)
			throws Exception {
		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(COMPANY_EMAIL, COMPANY_PASSWORD);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(COMPANY_EMAIL)); // From EMAIL
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(userMail)); // To EMAIL
			message.setSubject(MAIL_SUBJECT_LINE+MESSAGE_DELIMITER + userId + MESSAGE_DELIMITER + userMail);
			message.setText(OPENNING_MESSAGE + MESSAGE_DELIMITER + userId+ MESSAGE_DELIMITER +userMail+RECEIPT_MESSAGE+receipt+CLOSING_MESSAGE);

			Transport.send(message);

			// Sends a copy of the receipt to the company mail
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(COMPANY_EMAIL));
			Transport.send(message);
		} catch (MessagingException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + MESSAGING_EXCEPTION + EXCEPTION_DELIMITER + userMail);
		}
	}
	
	// Sends an event to a user
	// Events : A shopper pick up an item, A shopper return an item somewhere, A shopper get a sale alert
	// Events : An employee place an item somewhere, an item is expired, an item is misplaced
	public static void sendEvent(String userIP, String message) throws Exception {
		OutputStream output;
		PrintWriter writer;
		
		try(Socket connection = new Socket()){
			connection.connect(new InetSocketAddress(userIP,CLIENT_PORT),3000);
			output = connection.getOutputStream();
			writer = new PrintWriter(output, true);
			writer.println(message);
		} catch (IOException e) {
			throw new Exception(className + EXCEPTION_DELIMITER + CONNECTION_EXCEPTION + EXCEPTION_DELIMITER + userIP);
		}
	}
}
