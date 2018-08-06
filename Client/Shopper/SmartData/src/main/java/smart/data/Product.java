package smart.data;
/**
 * Created by Tamir on 13/06/2018.
 */

public class Product {

    // Indicating if this product amount is measured in Kilograms, Litters or default (separated units)
    public enum UnitType {DEFAULT, KG, L, G};

    private long productId;
    private String name;
    // The id of the department the product belongs to
    private int departmentId;
    // The price of one product (for example sprite is 0.25$ per bottle)
    private double pricePerUnit;
    // The coordinates of the product in the supermarket's map
    private int locationX;
    private int locationY;
    // (for example sprite is 1.5 [every bottle has 1.5 litters])
    private double amountPerUnit;
    // (for example sprite is L [every bottle has 1.5 litters])
    private UnitType unitType;
    // The number of products available for this product
    private int stockCount;
    // Indicating if there are expired products of this product
    private boolean isExpired;

    public Product(long productId, String name, int departmentId, double pricePerUnit, int locationX, int locationY, double amountPerUnit, UnitType unitType, int stockCount){
        this.productId = productId;
        this.name = name;
        this.departmentId = departmentId;
        this.pricePerUnit = pricePerUnit;
        this.locationX = locationX;
        this.locationY = locationY;
        this.amountPerUnit = amountPerUnit;
        this.unitType = unitType;
        this.stockCount = stockCount;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public int getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(int departmentId) {
		this.departmentId = departmentId;
	}

	public double getPricePerUnit() {
        return pricePerUnit;
    }

    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    public int getLocationX() {
        return locationX;
    }

    public void setLocationX(int locationX) {
        this.locationX = locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    public void setLocationY(int locationY) {
        this.locationY = locationY;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public double getAmountPerUnit() {
        return amountPerUnit;
    }

    public void setAmountPerUnit(double amountPerUnit) {
        this.amountPerUnit = amountPerUnit;
    }

	public int getStockCount() {
		return stockCount;
	}

	public void setStockCount(int stockCount) {
		// Updating only if the amount is positive or zero 
		// (not allowing negative count)
		if(stockCount >= 0) {
			this.stockCount = stockCount;
		}
	}

	public boolean isExpired() {
		return isExpired;
	}

	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}
	
	/**
	 * Indicating if this item can be bought (in stock)
	 * @return boolean, True if the item can be bought, False if not
	 */
	public boolean isAvailable() 
	{
		return this.stockCount > 0;
	}
}