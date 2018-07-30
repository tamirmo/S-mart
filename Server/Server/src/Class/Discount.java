package Class;

public class Discount {
	public static final String GENERAL_DISCOUNT_SHOPPER_ID = "ALL";

	private String productId;
	private String shopperId;
	private String normalPrice;
	private String discountedPrice;

	public Discount(String productId, String normalPrice, String discountedPrice, String shopperId) {
		this.productId = productId;
		this.normalPrice = normalPrice;
		this.discountedPrice = discountedPrice;
		this.shopperId = shopperId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getShopperId() {
		return shopperId;
	}

	public void setShopperId(String shopperId) {
		this.shopperId = shopperId;
	}

	public String getNormalPrice() {
		return normalPrice;
	}

	public void setNormalPrice(String normalPrice) {
		this.normalPrice = normalPrice;
	}

	public String getDiscountedPrice() {
		return discountedPrice;
	}

	public void setDiscountedPrice(String discountedPrice) {
		this.discountedPrice = discountedPrice;
	}

	public String toString() {
		return "Discount : PRODUCT_ID = " + productId + " : NORMAL_PRICE = " + normalPrice + " : DISCOUNT_PRICE = "
				+ discountedPrice + " : SHOPPER_SERVER_ID = " + shopperId;
	}
}
