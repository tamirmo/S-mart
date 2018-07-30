package Class;

public class Employee {
	private String id;
	private String email;
	private String password;

	public Employee(String id, String email, String password) {
		this.id = id;
		this.email = email;
		this.password = password;
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

	public String toString() {
		return "Employee : EMPLOYEE_SERVER_ID = " + id + " : EMPLOYEE_EMAIL = " + email + " : EMPLOYEE_PASSWORD = "
				+ password;
	}
}
