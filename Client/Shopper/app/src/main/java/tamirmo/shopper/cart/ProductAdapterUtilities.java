package tamirmo.shopper.cart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import smart.data.CartItem;
import smart.data.Product;
import tamirmo.shopper.R;

/**
 * Created by Tamir on 15/06/2018.
 * A utilities class holding common methods for products adapters.
 */

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
        double productAmountPerUnit = productData.getAmountPerUnit();
        if(Math.round(productAmountPerUnit) == productAmountPerUnit){
            viewHolder.unit.setText(String.format("%.0f %s", productData.getAmountPerUnit(), productData.getUnitType()));
        }else {
            viewHolder.unit.setText(String.format("%.1f %s", productData.getAmountPerUnit(), productData.getUnitType()));
        }
    }

    /**
     * Handling a plus or minus click
     * @param clickedButton View, The clicked button
     * @param cartItem CartItem, The item the button belongs to
     */
    public static void onPlusMinusClicked(View clickedButton, CartItem cartItem, Context context){
        switch (clickedButton.getId())
        {
            case R.id.curr_cart_item_plus:
                // Checking we have enough items in stock
                if(cartItem.getProduct().getStockCount() > cartItem.getAmount()) {
                    CartHandler.getInstance().increaseAmount(cartItem);
                }
                break;
            case R.id.curr_cart_item_minus:
                boolean wasItemRemoved = CartHandler.getInstance().decreaseAmount(cartItem);
                if(wasItemRemoved){
                    Snackbar.make(clickedButton, context.getText(R.string.item_removed_snack_bar_title), Snackbar.LENGTH_LONG)
                            // Adding undo action to get back the deleted item
                            .setAction(context.getText(R.string.item_removed_snack_bar_action), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CartHandler.getInstance().undoLastRemovedItem();
                                }
                            }).show();
                }

                break;
        }
    }
}
