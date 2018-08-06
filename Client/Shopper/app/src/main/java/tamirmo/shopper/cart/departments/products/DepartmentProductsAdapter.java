package tamirmo.shopper.cart.departments.products;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import smart.data.CartItem;
import smart.data.Product;
import smart.data.SmartDataManager;
import tamirmo.shopper.R;
import tamirmo.shopper.cart.CartHandler;
import tamirmo.shopper.cart.ProductAdapterUtilities;

/**
 * Created by Tamir on 15/06/2018.
 * An adapter for the list of products of a chosen department.
 * It holds all products first and than the filtered ones when the user types search term.
 */

public class DepartmentProductsAdapter extends ArrayAdapter<Product> implements View.OnClickListener {

    private Context context;
    private List<Product> departmentProducts;
    private List<Product> filteredProducts;

    DepartmentProductsAdapter(Context context, int departmentId) {
        super(context, R.layout.cart_item_layout, new Product[0]);
        this.context = context;

        departmentProducts = new ArrayList<>();

        // Getting all products
        List<Product> products = SmartDataManager.getInstance().getProducts();

        // Going over the products, pulling only the ones in the chosen department that are available:
        for(Product product: products){
            if(product.getDepartmentId() == departmentId && product.isAvailable()){
                departmentProducts.add(product);
            }
        }

        // By default the list of filtered is all products, cause there is no search term yet
        filteredProducts = new ArrayList<>(departmentProducts);
    }

    void onSearchTermEntered(String term){
        // When the term is empty, displaying all products
        if(term.isEmpty()){
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
        Product productItem = getItem(position);

        // Getting the cart item for this product if in cart
        CartItem cartItem = getProductCartItem(productItem);

        // Creating the view holder and the view for the row
        ProductAdapterUtilities.ProductViewObjects productViewObjects = ProductAdapterUtilities.createViewHolder(convertView, parent, context);
        ProductAdapterUtilities.ProductItemViewHolder viewHolder = productViewObjects.productItemViewHolder;

        ProductAdapterUtilities.setProductData(productItem, viewHolder, context);

        if(cartItem == null){
            // No need to show these cause the product is not in the cart
            viewHolder.minus.setVisibility(View.INVISIBLE);
            viewHolder.amount.setVisibility(View.INVISIBLE);
            viewHolder.pickedImage.setVisibility(View.INVISIBLE);

            viewHolder.price.setText(String.format("$ %.2f", productItem.getPricePerUnit()));
        }else{
            if(!cartItem.isPicked()) {
                viewHolder.minus.setVisibility(View.VISIBLE);
                viewHolder.amount.setVisibility(View.VISIBLE);
                viewHolder.pickedImage.setVisibility(View.INVISIBLE);
            }else{
                viewHolder.minus.setVisibility(View.INVISIBLE);
                viewHolder.amount.setVisibility(View.INVISIBLE);
                viewHolder.pickedImage.setVisibility(View.VISIBLE);
            }

            viewHolder.price.setText(String.format("$ %.2f", cartItem.getItemPrice()));
            viewHolder.amount.setText(String.valueOf(cartItem.getAmount()));
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
        // Getting all cart items
        List<CartItem> cart = CartHandler.getInstance().getCart();

        // Searching for the product's id in the cart items:
        if(cart != null){
            for(CartItem item: cart){
                if(item.getProductId() == product.getProductId()){
                    cartItem = item;
                }
            }
        }

        return cartItem;
    }

    @Override
    public void onClick(View v) {
        int position=(Integer) v.getTag();
        Object object= getItem(position);
        Product dataModel=(Product)object;

        // Getting the cart item for this product if in cart
        CartItem cartItem = getProductCartItem(dataModel);

        if(cartItem != null) {
            ProductAdapterUtilities.onPlusMinusClicked(v, cartItem, context);
        }else{
            if(v.getId() == R.id.curr_cart_item_plus){
                // Creating a new CartItem with amount one, not picked
                cartItem = new CartItem(dataModel.getProductId(), dataModel, 1, false);
                CartHandler.getInstance().addItem(cartItem);
            }
        }

        this.notifyDataSetChanged();
    }
}
