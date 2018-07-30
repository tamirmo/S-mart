package BusinessLogic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
	
	// Message Date Format
	private static final String MESSAGE_DELIMITER = " : ";
	private static final String DATE_FORMAT = "yyyy/MM/dd" + MESSAGE_DELIMITER + "HH:mm:ss";
	private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	
	// Message Types
	public static enum MessageType { COMMAND, REQUEST, RESPONSE, ERROR, INFOMATION;}
	public static enum UserType { ADMIN, SHOPPER, EMPLOYEE, SERVER;}
	
	// Prints a message on the console
	public static void printMessage(String userType, String userIP, String userID, MessageType messagetype, String message) {
		Date date = new Date();
		StringBuilder log = new StringBuilder();
		
		// Builds a log message
		log.append(dateFormat.format(date)+MESSAGE_DELIMITER);
		log.append(userType+MESSAGE_DELIMITER);
		if(userID.equals(UserType.SHOPPER.name()) || userID.equals(UserType.EMPLOYEE.name()))
			log.append(userID+MESSAGE_DELIMITER);
		log.append(userID + MESSAGE_DELIMITER);
		log.append(userIP+MESSAGE_DELIMITER);
		log.append(messagetype+MESSAGE_DELIMITER);
		log.append(message);
		
		// Prints the message on console
		System.out.println(log.toString());	
	}
}
