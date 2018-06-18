package smart.data;
/**
 * Created by Tamir on 13/06/2018.
 */

public class CartItem {
	// (transient for Gson not to save in the Json file)
    private transient Product product;
    private int amount;
    // Indicating if the shopper has already picked up the item
    private boolean isPicked;
    private long productId;

    public CartItem(long productId, Product product, int amount, boolean isTaken){
        this.product = product;
        this.amount = amount;
        this.isPicked = isTaken;
        this.productId = productId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public double getItemPrice(){
        return product.getPricePerUnit() * amount;
    }

    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        isPicked = picked;
    }

	public long getProductId() {
		return productId;
	}

	public void setProductId(long productId) {
		this.productId = productId;
	}
}
