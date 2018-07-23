package smart.data;

public class Shopper {
	private long id;
	private String email;
	private String password;
	private String creditCard;
	
	public Shopper(long id, String email, String password, String creditCard) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.creditCard = creditCard;
	}
	
	public long getId() {
		return id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getCreditCard() {
		return creditCard;
	}
	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}
	
	/**
	 * Indicating if the given password and email equals to this shopper details
	 * @param email String, The email to check
	 * @param password String, The password to check
	 * @return boolean, true if equals, False otherwise
	 */
	public boolean isEmailPasswordEquals(String email, String password){
		return this.email.equals(email) && this.password.equals(password);
	}
	
	public String toString() {
		return "Shopper : SHOPPER_SERVER_ID = "+ id+" : SHOPPER_EMAIL = "+ email+" : SHOPPER_PASSWORD = " + password +" : SHOPPER_CREDIT_CARD = " + creditCard;
	}
}
