package tamirmo.employee.Database.Class;

public class MisplacedTask {

    // Class attributes
    private String productID;
    private int locationX;
    private int locationY;

    // Class Builder
    public MisplacedTask(String productID, int locationX, int locationY){
        this.productID = productID;
        this.locationX = locationX;
        this.locationY = locationY;
    }

    // Returns product's ID
    public String getProductID() {
        return productID;
    }

    // Returns product's wrong location X
    public int getLocationX(){
        return locationX;
    }

    // Returns product's wrong location Y
    public int getLocationY(){
        return locationY;
    }
}

