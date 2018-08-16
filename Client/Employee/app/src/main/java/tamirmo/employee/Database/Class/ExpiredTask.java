package tamirmo.employee.Database.Class;

public class ExpiredTask {

    // Class attributes
    private String productID;

    // Class Builder
    public ExpiredTask(String productID){
        this.productID = productID;
    }

    // Returns product's id
    public String getProductID() {
        return productID;
    }
}
