package smart.data;

public class EmployeeDetails {
	private int id;
	private String email;
	private String password;
	
	public EmployeeDetails(int id, String email, String password) {
		this.id = id;
		this.email = email;
		this.password = password;
	}
	
	public int getId() {
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
	
	/**
	 * Indicating if the given password and email equals to this shopper details
	 * @param email String, The email to check
	 * @param password String, The password to check
	 * @return boolean, true if equals, False otherwise
	 */
	public boolean isEmailPasswordEquals(String email, String password){
		return this.email.equals(email) && this.password.equals(password);
	}
}