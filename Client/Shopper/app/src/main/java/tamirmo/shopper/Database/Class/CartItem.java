package tamirmo.shopper.Database.Class;

public class CartItem {

    // Class attributes
    private String productID;
    private int amount;
    private boolean isPicked;

    // Class Builder
    public CartItem(String productID){
        this.productID = productID;
        amount = 1;
        isPicked = false;
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

    // Returns whether product's items were picked from their shelf
    public boolean getIsPicked(){
        return isPicked;
    }

    // Sets product's items picked status
    public void setIsPicked(boolean isPicked){
        this.isPicked = isPicked;
    }
}
