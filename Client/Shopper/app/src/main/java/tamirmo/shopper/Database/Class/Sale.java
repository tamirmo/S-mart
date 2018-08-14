package tamirmo.shopper.Database.Class;

public class Sale {

    // Class attributes
    private String productID;
    private int payAmount;
    private int freeAmount;

    // Class Builder
    public Sale(String productID, int payAmount, int freeAmount){
        this.productID = productID;
        this.payAmount = payAmount;
        this.freeAmount = freeAmount;
    }

    // Returns product id;
    public String getProductID() {
        return productID;
    }

    // Returns how many products need to be bought
    // Example : Coca Cola 1.5L 3+1 -> 3
    public int getPayAmount() {
        return payAmount;
    }

    // Returns how many products can be taken for free
    // Example : Coca Cola 1.5L 3 + 1 -> 1
    public int getFreeAmount(){
        return freeAmount;
    }
}
