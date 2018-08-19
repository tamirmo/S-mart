package tamirmo.shopper.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import tamirmo.shopper.Database.Class.CartItem;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.R;


public class CartItemMapDialog extends Dialog {

    private CartItem cartItem;
    private Context context;
    private Product product;

    CartItemMapDialog(@NonNull Context context, CartItem cartItem, Product product) {
        super(context);

        this.product = product;
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

        // The items images has special names: "item_id"
        int resourceID = context.getResources().getIdentifier(
                "item_" + product.getProductId(),
                "drawable",
                "tamirmo.shopper"
        );

        price.setText(String.format("$%s", product.getPricePerUnit()));

        itemImage.setImageResource(resourceID);
        itemName.setText(product.getName());

        double productAmountPerUnit = Double.parseDouble(product.getAmountPerUnit());
        if (Math.round(productAmountPerUnit) == productAmountPerUnit) {
            unit.setText(String.format("%s %s", product.getAmountPerUnit(), product.getUnitType()));
        } else {
            unit.setText(String.format("%s %s", product.getAmountPerUnit(), product.getUnitType()));
        }
        amount.setText(String.valueOf(cartItem.getAmount()));

        if (cartItem.getPickedAmount() == cartItem.getAmount()) {
            pickedImage.setVisibility(View.VISIBLE);
        } else {
            pickedImage.setVisibility(View.INVISIBLE);
        }
    }
}
