package tamirmo.shopper.Database.Class;

public class Product {

    // Product's weight type
    public enum UnitType {
        DEFAULT, KG, L, G
    };

    // Class attributes
    private String productId;
    private String name;
    private String departmentId;
    private String pricePerUnit;
    private String locationX;
    private String locationY;
    private String amountPerUnit;
    private UnitType unitType;

    // Class Builder
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

    // Returns product's id
    public String getProductId() {
        return productId;
    }

    // Returns product's name
    public String getName() {
        return name;
    }

    // Returns department's id
    public String getDepartmentId() {
        return departmentId;
    }

    // Returns product's price
    public String getPricePerUnit() {
        return pricePerUnit;
    }

    // Returns product's location on axis-X
    public String getLocationX() {
        return locationX;
    }

    // Returns product's location on axis-Y
    public String getLocationY() {
        return locationY;
    }

    // Returns product's weight type
    public UnitType getUnitType() {
        return unitType;
    }

    // Returns product's amount
    // Example : Coca cola 1.5 L -> 1.5
    public String getAmountPerUnit() {
        return amountPerUnit;
    }
}
