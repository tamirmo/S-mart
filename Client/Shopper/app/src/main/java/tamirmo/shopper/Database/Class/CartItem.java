package tamirmo.shopper.Database.Class;

public class CartItem {

    // Class attributes
    private String productID;
    private int amount;
    private int pickedAmount;

    // Class Builder
    public CartItem(String productID){
        this.productID = productID;
        amount = 1;
        pickedAmount = 0;
    }

    // Returns product id
    public String getProductID() {
        return productID;
    }

    // Returns the amount of products to purchase
    public int getAmount() {
        return amount;
    }

    // Sets the amount of products to purchase
    public void setAmount(int amount) {
        this.amount = amount;
    }

    // Returns how many products have been taken
    public int getPickedAmount(){
        return pickedAmount;
    }

    // Sets how many products have been taken
    public void setPickedAmount(int pickedAmount){
        this.pickedAmount = pickedAmount;
    }
}
