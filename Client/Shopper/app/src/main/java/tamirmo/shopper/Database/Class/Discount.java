package tamirmo.shopper.Database.Class;

public class Discount {
    public static final String GENERAL_DISCOUNT_SHOPPER_ID = "ALL";

    // Class attributes
    private String productId;
    private String shopperId;
    private String normalPrice;
    private String discountedPrice;

    // Class Builder
    public Discount(String productId, String normalPrice, String discountedPrice, String shopperId) {
        this.productId = productId;
        this.normalPrice = normalPrice;
        this.discountedPrice = discountedPrice;
        this.shopperId = shopperId;
    }

    // Returns product's id
    public String getProductId() {
        return productId;
    }

    // Returns shopper's id
    public String getShopperId(){ return shopperId; }

    // Returns product's original price
    public String getNormalPrice() {
        return normalPrice;
    }

    // Returns product's discounted price
    public String getDiscountedPrice() {
        return discountedPrice;
    }
}
