package tamirmo.shopper.CartList;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tamirmo.shopper.Database.Class.CartItem;
import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.R;


public final class ProductAdapterUtilities {

    // View lookup cache
    public static class ProductItemViewHolder {
        public ImageView image;
        public ImageView pickedImage;
        public TextView name;
        public TextView unit;
        public TextView price;
        public ImageView plus;
        public TextView amount;
        public ImageView minus;
    }

    /**
     * Holding the view holder and the row view
     */
    public static class ProductViewObjects{
        public ProductItemViewHolder productItemViewHolder;
        public View convertView;
    }

    /**
     * Getting a view holder from the given view (creating if not exist, getting if exist)
     * @param convertView View, The view of the row in the adapter (null if first created)
     * @param parent ViewGroup, The parent view of the row
     * @param context Context, activity context
     * @return ProductItemViewHolder, A view holder from the given view
     */
    public static ProductViewObjects createViewHolder(View convertView, @NonNull ViewGroup parent, Context context){
        // Check if an existing view is being reused, otherwise inflate the view
        ProductItemViewHolder viewHolder; // view lookup cache stored in tag

        // If the view was not created yet
        if (convertView == null) {

            // Creating a holder to hold all views
            viewHolder = new ProductItemViewHolder();
            // Getting the inflater
            LayoutInflater inflater = LayoutInflater.from(context);
            // Inflating the cartItem
            convertView = inflater.inflate(R.layout.cart_item_layout, parent, false);

            // Putting all views inside the holder:
            viewHolder.image = convertView.findViewById(R.id.curr_cart_item_image);
            viewHolder.name = convertView.findViewById(R.id.curr_cart_item_name);
            viewHolder.unit = convertView.findViewById(R.id.curr_cart_item_unit);
            viewHolder.price = convertView.findViewById(R.id.curr_cart_item_price);
            viewHolder.plus = convertView.findViewById(R.id.curr_cart_item_plus);
            viewHolder.amount = convertView.findViewById(R.id.curr_cart_item_amount);
            viewHolder.minus = convertView.findViewById(R.id.curr_cart_item_minus);
            viewHolder.pickedImage = convertView.findViewById(R.id.curr_cart_item_picked_image);

            // Saving the view for next time
            convertView.setTag(viewHolder);
        } else {
            // The view exists, getting it's children views
            viewHolder = (ProductItemViewHolder) convertView.getTag();
        }

        ProductViewObjects result = new ProductViewObjects();
        result.convertView = convertView;
        result.productItemViewHolder = viewHolder;

        return result;
    }

    /**
     * Setting the given holder's views the given product data
     * @param productData Product, The product to set it's details in the views
     * @param viewHolder ProductItemViewHolder, Holding the views to fill
     * @param context Context, activity context
     */
    public static void setProductData(Product productData, ProductItemViewHolder viewHolder, Context context){

        // The items images has special names: "item_id"
        int resourceID = context.getResources().getIdentifier(
                "item_" + productData.getProductId(),
                "drawable",
                "tamirmo.shopper"
        );

        viewHolder.image.setImageResource(resourceID);
        viewHolder.name.setText(productData.getName());
        viewHolder.unit.setText(String.format("%s %s", productData.getAmountPerUnit(), productData.getUnitType()));
    }

    /**
     * Handling a plus or minus click
     * @param clickedButton View, The clicked button
     * @param cartItem CartItem, The item the button belongs to
     */
    public static void onPlusMinusClicked(View clickedButton, CartItem cartItem, Context context, List<CartItem> cart){
        switch (clickedButton.getId()){
            case R.id.curr_cart_item_plus:
                cartItem.setAmount(cartItem.getAmount()+1);
                cartItem.setIsPicked(false);
                break;
            case R.id.curr_cart_item_minus:
                cartItem.setAmount(cartItem.getAmount()-1);
                if(cartItem.getAmount() == 0)
                    cart.remove(cartItem);
                break;

        }
    }
}
