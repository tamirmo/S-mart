package BusinessLogic;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogPrinter {
	
	// Message Date Format
	private static final String DATE_FORMAT = "yyyy/MM/dd : HH:mm:ss";
	private static final DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
	
	// Message Types, and their colors
	public static enum MessageType { SYSTEM, LOGIN, LOGOUT, REQUEST, RESPONSE, ERROR, UNKNOWN;}
	
	// Prints a message on console
	public static void printMessage(MessageType type, String message) {
		Date date = new Date();
		
		/* Prints a message on console
		 * Format : "Year/Month/Day : Hour::Minute::Seconds : MessageType : Message" */
		System.out.println(dateFormat.format(date) + " : " + type.name() + " : " + message);	
	}
}
