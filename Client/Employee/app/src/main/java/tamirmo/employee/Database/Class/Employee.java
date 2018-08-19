package tamirmo.employee.Database.Class;

public class Employee {

    // Class attributes
    private String id;
    private String email;
    private String password;

    // Class Builder
    public Employee(String id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
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
}
