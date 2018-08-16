package tamirmo.employee.Database.Class;

public class EmptyTask {

    // Class attributes
    private String productID;

    // Class Builder
    public EmptyTask(String productID){
        this.productID = productID;
    }

    // Returns product's ID
    public String getProductID(){
        return productID;
    }
}
