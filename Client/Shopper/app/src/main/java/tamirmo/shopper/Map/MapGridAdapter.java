package tamirmo.shopper.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;

import tamirmo.shopper.Database.Class.CartItem;
import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.Database.Class.Sale;
import tamirmo.shopper.Database.Class.UserLocation;
import tamirmo.shopper.R;

// Controls which data to show in the fragment, and how to show it
public class MapGridAdapter extends BaseAdapter implements View.OnClickListener {
    private static final int INFINITY = 1000000000;

    private LayoutInflater layoutinflater;
    private Context context;

    private int gridRows;
    private int gridColumns;

    private List<Discount> discounts;
    private List<Product> products;
    private List<Sale> sales;
    private List<CartItem> cart;
    private UserLocation userLocation;

    private HashMap<String, Product> productMap;
    private Discount[][] discountsMatrix;
    private CartItem[][] cartMatrix;
    private Sale[][] saleMatrix;

    MapGridAdapter(Context context, int gridRows, int gridColumns, List<Discount> discounts, List<Sale> sales, List<Product> products, List<CartItem> cart, UserLocation userLocation) {
        this.context = context;
        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.gridRows = gridRows;
        this.gridColumns = gridColumns;

        this.discounts = discounts;
        this.sales = sales;
        this.products = products;
        this.cart = cart;
        this.products = products;

        productMap = getProductMap(products);
        discountsMatrix = getDiscountMatrix(discounts);
        cartMatrix = getCartMatrix(cart);
        saleMatrix = getSaleMatrix(sales);

        this.userLocation = userLocation;
    }

    // Returns a HashMap of a List of products
    public HashMap<String, Product> getProductMap(List<Product> productList){
        HashMap<String, Product> productsMap = new HashMap<String, Product>();

        for(Product product : productList){
            productsMap.put(product.getProductId(),product);
        }

        return productsMap;
    }

    // Returns a HashMap of a List of discounts
    public Discount[][] getDiscountMatrix(List<Discount> discountList){
        Discount[][] matrix = new Discount[gridRows][gridColumns];

        for(Discount discount : discountList){
            Product product = productMap.get(discount.getProductId());
            matrix[Integer.parseInt(product.getLocationX())][Integer.parseInt(product.getLocationY())] = discount;
        }

        return matrix;
    }

    // Returns a HashMap of a List of cart's items
    public CartItem[][] getCartMatrix (List<CartItem> cartList){
        CartItem[][] matrix = new CartItem[gridRows][gridColumns];

        for(CartItem item : cartList){
            Product product = productMap.get(item.getProductID());
            matrix[Integer.parseInt(product.getLocationX())][Integer.parseInt(product.getLocationY())] = item;
        }

        return matrix;
    }

    // Returns a HashMap of a List of sales
    public Sale[][] getSaleMatrix (List<Sale> saleList){
        Sale[][] matrix = new Sale[gridRows][gridColumns];

        for(Sale sale : saleList){
            Product product = productMap.get(sale.getProductID());
            matrix[Integer.parseInt(product.getLocationX())][Integer.parseInt(product.getLocationY())] = sale;
        }

        return matrix;
    }

    // Returns next item name and amount
    public String getNextItemText(){
        CartItem nextItem = selectNextItem();
        String nextItemText = context.getString(R.string.no_next_item);

        if(nextItem != null)
            nextItemText = productMap.get(nextItem.getProductID()).getName()+ " X " + (nextItem.getAmount()-nextItem.getPickedAmount());

        return nextItemText;
    }

    // Selects an item from the cart items
    public CartItem selectNextItem(){
        CartItem selectedItem = null;
        int nextItemDistance = INFINITY, productDistance;
        Product product = null;

        for(CartItem item : cart){
            if(item.getPickedAmount() != item.getAmount()){
                product = productMap.get(item.getProductID());
                productDistance = calculateDistance(Integer.parseInt(product.getLocationX()), Integer.parseInt(product.getLocationY()));
                if(productDistance < nextItemDistance){
                    selectedItem = item;
                    nextItemDistance = productDistance;
                }
            }
        }

        return selectedItem;
    }

    // Calculates a distance to a product from current user location
    // Manhattan distance : |x1-x2| + |y1+y2|
    public int calculateDistance(int productLocationX, int productLocationY){
        int distanceX = Math.abs(productLocationX-userLocation.getLocationX());
        int distanceY = Math.abs(productLocationY-userLocation.getLocationY());
        int distance = distanceX + distanceY;

        return distance;
    }

    // Defines what to show on the grid
    private static class GridViewModel {
        ImageView discountIcon;
        ImageView cartItemIcon;
        ImageView saleIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridViewModel gridViewModel;

        if (convertView == null) {
            gridViewModel = new GridViewModel();
            convertView = layoutinflater.inflate(R.layout.navigation_map_cell_layout, parent, false);
            gridViewModel.discountIcon = convertView.findViewById(R.id.discount_map_image);
            gridViewModel.cartItemIcon = convertView.findViewById(R.id.item_map_image);
            gridViewModel.saleIcon = convertView.findViewById(R.id.sale_map_image);
            convertView.setTag(gridViewModel);
        } else {
            gridViewModel = (GridViewModel) convertView.getTag();
        }

        CartItem cartItem = (CartItem) getItem(position);
        CartItem nextCartItem = selectNextItem();

        // If there is a cart item for the
        if (cartItem != null) {
            int itemImageResourceId = R.drawable.cart_item_location;
            gridViewModel.cartItemIcon.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

            if (cartItem.getPickedAmount() == cartItem.getAmount()) {
                itemImageResourceId = R.drawable.picked_item_v_green;
                gridViewModel.cartItemIcon.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            }
            // Checking if we are in the next item's view
            else if (nextCartItem != null && nextCartItem == cartItem) {
                itemImageResourceId = R.drawable.cart_item_selected;
            }

            gridViewModel.cartItemIcon.setImageResource(itemImageResourceId);

            gridViewModel.discountIcon.setVisibility(View.INVISIBLE);
            gridViewModel.cartItemIcon.setVisibility(View.VISIBLE);
            gridViewModel.saleIcon.setVisibility(View.INVISIBLE);
        } else{
            Sale positionSale = getSaleFromMatrix(position);
            if (positionSale != null){
                // Stetting the sale icon visibility according to the current sale location
                gridViewModel.saleIcon.setVisibility(View.VISIBLE);
                gridViewModel.discountIcon.setVisibility(View.INVISIBLE);
            }
            else{
                Discount positionDiscount = getDiscountFromMatrix(position);
                if (positionDiscount != null){
                    gridViewModel.discountIcon.setVisibility(View.VISIBLE);
                }
                else
                    gridViewModel.discountIcon.setVisibility(View.INVISIBLE);
                gridViewModel.saleIcon.setVisibility(View.INVISIBLE);
            }
            gridViewModel.cartItemIcon.setVisibility(View.INVISIBLE);
        }

        // Registering the on click of the items and setting the items positions as tag
        // (for later use when clicking on an item)
        gridViewModel.cartItemIcon.setOnClickListener(this);
        gridViewModel.discountIcon.setOnClickListener(this);
        gridViewModel.saleIcon.setOnClickListener(this);
        gridViewModel.cartItemIcon.setTag(position);
        gridViewModel.discountIcon.setTag(position);
        gridViewModel.saleIcon.setTag(position);

        return convertView;
    }

    @Override
    public int getCount() {
        int rows = gridRows;
        int columns = gridColumns;

        return rows * columns;
    }

    @Override
    public Object getItem(int position) {
        return getCartItemFromMatrix(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Returns a cart item from the matrix
    public CartItem getCartItemFromMatrix(int position){
        int row = position / gridColumns;
        int column = position % gridColumns;
        CartItem cartItem = cartMatrix[row][column];

        return cartItem;
    }

    // Returns a discount from the matrix
    public Discount getDiscountFromMatrix(int position){
        int row = position / gridColumns;
        int column = position % gridColumns;
        Discount discount = discountsMatrix[row][column];

        return discount;
    }

    // Returns a sale from the matrix
    public Sale getSaleFromMatrix(int position){
        int row = position / gridColumns;
        int column = position % gridColumns;
        Sale sale = saleMatrix[row][column];

        return sale;
    }

    @Override
    public void onClick(View v) {
        // The position of the images have their positions as tag
        int itemClickedPosition = (int) v.getTag();

        CartItem clickedCartItem = getCartItemFromMatrix(itemClickedPosition);

        // Checking if the user clicked on a cart item
        if (clickedCartItem != null) {
                // Displaying a dialog with the item details
                CartItemMapDialog dialog = new CartItemMapDialog(context, clickedCartItem, productMap.get(clickedCartItem.getProductID()));
                dialog.show();
        } else {
            Sale clickedSale = getSaleFromMatrix(itemClickedPosition);
            if(clickedSale != null){
                SaleMapDialog dialog = new SaleMapDialog(context, clickedSale, productMap.get(clickedSale.getProductID()));
                dialog.show();
            }
            else{
                Discount positionDiscount = getDiscountFromMatrix(itemClickedPosition);
                // If the user has clicked on a discount
                if (positionDiscount != null) {
                    // Displaying a dialog with the discount details
                    DiscountMapDialog dialog = new DiscountMapDialog(context, positionDiscount, productMap.get(positionDiscount.getProductId()), cart);
                    dialog.show();
                }
            }
        }
    }

    // Updates the class data
    public void updateData(){
        productMap = getProductMap(products);
        discountsMatrix = getDiscountMatrix(discounts);
        cartMatrix = getCartMatrix(cart);
        saleMatrix = getSaleMatrix(sales);

        // Changes the view based on the new data
        this.notifyDataSetChanged();
    }
}
