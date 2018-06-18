package tamirmo.shopper.cart.navigation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import smart.data.CartItem;
import smart.data.Product;
import tamirmo.shopper.R;

/**
 * Created by Tamir on 17/06/2018.
 * A dialog that appears when the user clicks on a cart item on the map.
 */

public class CartItemMapDialog extends Dialog {

    private CartItem cartItem;
    private Context context;

    CartItemMapDialog(@NonNull Context context, CartItem cartItem) {
        super(context);

        this.cartItem = cartItem;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflating the layout and getting the view:
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cart_item_map_dialog);
        ImageView itemImage = findViewById(R.id.curr_cart_item_image);
        ImageView pickedImage = findViewById(R.id.curr_cart_item_picked_image);

        TextView itemName = findViewById(R.id.curr_cart_item_name);
        TextView unit = findViewById(R.id.curr_cart_item_unit);
        TextView price = findViewById(R.id.curr_cart_item_price);
        TextView amount = findViewById(R.id.curr_cart_item_amount);

        Product productData = cartItem.getProduct();

        // The items images has special names: "item_id"
        int resourceID = context.getResources().getIdentifier(
                "item_" + cartItem.getProduct().getProductId(),
                "drawable",
                "tamirmo.shopper"
        );

        price.setText(String.format("$ %.2f", cartItem.getItemPrice()));

        itemImage.setImageResource(resourceID);
        itemName.setText(productData.getName());

        double productAmountPerUnit = productData.getAmountPerUnit();
        if(Math.round(productAmountPerUnit) == productAmountPerUnit){
            unit.setText(String.format("%.0f %s", productData.getAmountPerUnit(), productData.getUnitType()));
        }else {
            unit.setText(String.format("%.1f %s", productData.getAmountPerUnit(), productData.getUnitType()));
        }
        amount.setText(String.valueOf(cartItem.getAmount()));

        if(cartItem.isPicked()) {
            pickedImage.setVisibility(View.VISIBLE);
        }else{
            pickedImage.setVisibility(View.INVISIBLE);
        }
    }
}
