package smart.data;

/**
 * Created by Tamir on 09/06/2018.
 * Holding all discount data to display.
 */

public class Discount {
	public static final int GENERAL_DISCOUNT_SHOPPER_ID = -1;
	
    private long productId;
	// An id for the shopper this discount belongs to,
	// (GENERAL_DISCOUNT_SHOPPER_ID for general discounts)
	private int shopperId;
    private double originalPrice;
    private double discountedPrice;
    // The product this discount belongs to
    // (transient for Gson not to save in the Json file)
    private transient Product product;

    public long getProductId() {
        return productId;
    }

    public int getShopperId(){
    	return shopperId;
    }
    
    public double getOriginalPrice() {
        return originalPrice;
    }
    
    public double getDiscountedPrice() {
        return discountedPrice;
    }
    
    public Product getProduct() {
        return product;
    }
    
    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isPersonal(){
        return shopperId != GENERAL_DISCOUNT_SHOPPER_ID;
    }

    public Discount(long productId,
            double originalPrice,
            double discountedPrice,
            int shopperId){
        this.productId = productId;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.shopperId = shopperId;
    }
}
