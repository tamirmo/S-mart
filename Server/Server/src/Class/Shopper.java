package Class;

public class Shopper {
	private String id;
	private String email;
	private String password;
	private String creditCard;

	public Shopper(String id, String email, String password, String creditCard) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.creditCard = creditCard;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String toString() {
		return "Shopper : SHOPPER_SERVER_ID = " + id + " : SHOPPER_EMAIL = " + email + " : SHOPPER_PASSWORD = "
				+ password + " : SHOPPER_CREDIT_CARD = " + creditCard;
	}
}
