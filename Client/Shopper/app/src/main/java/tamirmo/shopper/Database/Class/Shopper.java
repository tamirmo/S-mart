package tamirmo.shopper.Database.Class;

public class Shopper {

    // Class attributes
	private String id;
	private String email;
	private String password;
	private String creditCard;

	// Class Builder
	public Shopper(String id, String email, String password, String creditCard) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.creditCard = creditCard;
	}

	// Returns shopper's id
	public String getId() {
		return id;
	}

	// Sets shopper's email
	public void setEmail(String email) {
		this.email = email;
	}

	// Returns shopper's password
	public String getPassword() {
		return password;
	}

	// Sets shopper's password
	public void setPassword(String password) {
		this.password = password;
	}

	// Sets shopper's credit card
	public void setCreditCard(String creditCard) {
		this.creditCard = creditCard;
	}
}
