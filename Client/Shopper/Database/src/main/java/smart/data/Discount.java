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
    private double normalPrice;
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
    
    public double getNormalPrice() {
        return normalPrice;
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

    public Discount(long productId, double normalPrice, double discountedPrice, int shopperId){
        this.productId = productId;
        this.normalPrice = normalPrice;
        this.discountedPrice = discountedPrice;
        this.shopperId = shopperId;
    }
    
    public String toString() {
    	String id = "All";
    	
    	if(shopperId != -1)
    		id = "" + shopperId;
    	
		return "Discount : PRODUCT_ID = " + productId + " : NORMAL_PRICE = " + normalPrice + " : DISCOUNT_PRICE = " + discountedPrice + " : SHOPPER_SERVER_ID = "+ id;  
	}
}
