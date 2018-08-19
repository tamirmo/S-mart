package tamirmo.shopper.Products;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tamirmo.shopper.Database.Class.CartItem;
import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.R;

public class ProductsAdapter extends ArrayAdapter<Product> implements View.OnClickListener {

    private Context context;

    // Database/Server Data references
    private String departmentId;
    private List<Product> products;
    private List<Discount> discounts;
    private List<CartItem> cart;

    // Class Data
    private List<Product> departmentProducts;
    private List<Product> filteredProducts;
    private HashMap<String, Discount> discountMap;


    ProductsAdapter(Context context, String departmentId, List<Product> products, List<Discount> discounts , List<CartItem> cart) {
        super(context, R.layout.cart_item_layout, new Product[0]);
        this.context = context;

        // Gets all needed data
        this.departmentId = departmentId;
        this.products = products;
        this.cart = cart;
        this.discounts = discounts;

        // Fillters department's products from all other
        departmentProducts = getDepartmentProducts(products);
        filteredProducts = new ArrayList<>(departmentProducts);

        // Converts discounts to HashMap to ease the search for items
        discountMap = getDiscountMap(discounts);
    }

    // Returns a HashMap of discounts from a list
    public HashMap<String, Discount> getDiscountMap(List<Discount> discountList){
        HashMap<String, Discount> discountMap = new HashMap<String, Discount>();

        for(Discount discount : discountList){
            discountMap.put(discount.getProductId(), discount);
        }

        return discountMap;
    }

    // Fillters department's products from all other
    public List<Product> getDepartmentProducts(List<Product> productList){
        List<Product> departmentProductsList = new ArrayList<>();

        for(Product product: productList){
            if(product.getDepartmentId().equals(departmentId)){
                departmentProductsList.add(product);
            }
        }

        return departmentProductsList;
    }

    // Changes which products should be shown based on a search term
    void onSearchTermEntered(String term){
        if(term.isEmpty()){
            // Displays all products
            filteredProducts = new ArrayList<>(departmentProducts);
        }else{
            // First clearing the last results
            filteredProducts.clear();

            // Going over the products, pulling only the ones with matching name:
            for(Product product: departmentProducts){
                if(product.getName().toUpperCase().contains(term.toUpperCase())){
                    filteredProducts.add(product);
                }
            }
        }

        // Refreshing the view, cause the list has changed
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredProducts.size();
    }

    @Nullable
    @Override
    public Product getItem(int position) {
        return filteredProducts.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Product product = getItem(position);
        Discount discount = discountMap.get(product.getProductId());

        // Getting the cart item for this product if in cart
        CartItem cartItem = getProductCartItem(product);

        // Creating the view holder and the view for the row
        ProductAdapterUtilities.ProductViewObjects productViewObjects = ProductAdapterUtilities.createViewHolder(convertView, parent, context);
        ProductAdapterUtilities.ProductItemViewHolder viewHolder = productViewObjects.productItemViewHolder;

        ProductAdapterUtilities.setProductData(product, viewHolder, context);

        if(discount != null){
            viewHolder.price.setText(String.format("$%s", discount.getDiscountedPrice()));
        }
        else
            viewHolder.price.setText(String.format("$%s", product.getPricePerUnit()));

        if(cartItem != null) {
            viewHolder.amount.setText(String.valueOf(cartItem.getAmount()));
            viewHolder.pickedAmount.setText(context.getString(R.string.curr_cart_picked_amount) + " " + cartItem.getPickedAmount()+" "+context.getString(R.string.products));
        }
        else {
            viewHolder.amount.setText("0");
            viewHolder.pickedAmount.setText(context.getString(R.string.curr_cart_picked_amount) + " 0 " + context.getString(R.string.products));
        }



        viewHolder.plus.setOnClickListener(this);
        viewHolder.minus.setOnClickListener(this);
        viewHolder.plus.setTag(position);
        viewHolder.minus.setTag(position);

        if(cartItem != null){
        if(cartItem.getPickedAmount() != cartItem.getAmount()) {
            viewHolder.minus.setVisibility(View.VISIBLE);
            viewHolder.pickedImage.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.minus.setVisibility(View.INVISIBLE);
            viewHolder.pickedImage.setVisibility(View.VISIBLE);
            }
        }

        // This method is called more than once, avoiding setting the on click every time.
        // once should be fine (when convertView is null, the view is first created)
        if(convertView == null) {
            viewHolder.plus.setOnClickListener(this);
            viewHolder.minus.setOnClickListener(this);
        }

        viewHolder.plus.setTag(position);
        viewHolder.minus.setTag(position);

        // Return the completed view to render on screen
        return productViewObjects.convertView;
    }

    /**
     * Getting the cartItem of the given product, null if not in the cart
     * @param product Product, The product to get it's cart item
     * @return CartItem the CartItem of the given product
     */
    private CartItem getProductCartItem(Product product){
        CartItem cartItem = null;

        // Searching for the product's id in the cart items:
            for(CartItem item: cart){
                if(item.getProductID().equals(product.getProductId())){
                    cartItem = item;
                    break;
                }
            }

        return cartItem;
    }

    @Override
    public void onClick(View v) {
        int position =(Integer) v.getTag();
        Object object = getItem(position);
        Product dataModel =(Product)object;

        // Getting the cart item for this product if in cart
        CartItem cartItem = getProductCartItem(dataModel);

        if(cartItem != null) {
            ProductAdapterUtilities.onPlusMinusClicked(v, cartItem, context, cart);
        }else{
            if(v.getId() == R.id.curr_cart_item_plus){
                // Creating a new CartItem with amount one, not picked
                cartItem = new CartItem(dataModel.getProductId());
                cart.add(cartItem);
            }
        }

        this.notifyDataSetChanged();
    }

    // Updates the class data
    public void updateData(){
        // Fillters department's products from all other
        departmentProducts = getDepartmentProducts(products);
        filteredProducts = new ArrayList<>(departmentProducts);

        // Converts discounts to HashMap to ease the search for items
        discountMap = getDiscountMap(discounts);

        // Changes the view based on the new data
        this.notifyDataSetChanged();
    }
}
