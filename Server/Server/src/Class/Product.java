package Class;

public class Product {

	// Product's weight type
	public enum UnitType {
		DEFAULT, KG, L, G
	};

	private String productId;
	private String name;
	private String departmentId;
	private String pricePerUnit;
	private String locationX;
	private String locationY;
	private String amountPerUnit;
	private UnitType unitType;

	public Product(String productId, String name, String departmentId, String pricePerUnit, String locationX,
			String locationY, String amountPerUnit, UnitType unitType) {
		this.productId = productId;
		this.name = name;
		this.departmentId = departmentId;
		this.pricePerUnit = pricePerUnit;
		this.locationX = locationX;
		this.locationY = locationY;
		this.amountPerUnit = amountPerUnit;
		this.unitType = unitType;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartmentId() {
		return departmentId;
	}

	public void setDepartmentId(String departmentId) {
		this.departmentId = departmentId;
	}

	public String getPricePerUnit() {
		return pricePerUnit;
	}

	public void setPricePerUnit(String pricePerUnit) {
		this.pricePerUnit = pricePerUnit;
	}

	public String getLocationX() {
		return locationX;
	}

	public void setLocationX(String locationX) {
		this.locationX = locationX;
	}

	public String getLocationY() {
		return locationY;
	}

	public void setLocationY(String locationY) {
		this.locationY = locationY;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
	}

	public String getAmountPerUnit() {
		return amountPerUnit;
	}

	public void setAmountPerUnit(String amountPerUnit) {
		this.amountPerUnit = amountPerUnit;
	}

	public String toString() {
		return "Product : PRODUCT_ID = " + productId + " : PRODUCT_NAME = " + name + " : DEPARTMENT_ID = "
				+ departmentId + " : UNIT_PRICE = " + pricePerUnit + " : LOCATION_X = " + locationX + " : LOCATION_Y = "
				+ locationY + " : UNIT_AMOUNT = " + amountPerUnit + " : UNIT_TYPE = " + unitType;
	}
}
