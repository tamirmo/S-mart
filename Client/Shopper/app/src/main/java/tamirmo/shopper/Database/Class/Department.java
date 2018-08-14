package tamirmo.shopper.Database.Class;

public class Department {

    // Class attributes
	private String id;
	private String name;

	// Class Builder
	public Department(String id, String name) {
		this.id = id;
		this.name = name;
	}

	// Returns department id
	public String getId() {
		return id;
	}

	// Returns department name
	public String getName() {
		return name;
	}
}
