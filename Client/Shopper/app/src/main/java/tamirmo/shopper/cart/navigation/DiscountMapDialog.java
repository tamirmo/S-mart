package tamirmo.shopper.cart.navigation;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import smart.data.CartItem;
import smart.data.Discount;
import tamirmo.shopper.R;
import tamirmo.shopper.cart.CartHandler;

/**
 * Created by Tamir on 17/06/2018.
 * A dialog that appears when the user clicks on a discount on the map.
 */

public class DiscountMapDialog extends Dialog implements android.view.View.OnClickListener{

    private Discount discount;
    private Context context;

    public DiscountMapDialog(@NonNull Context context, Discount discountToDisplay) {
        super(context);

        discount = discountToDisplay;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflating the layout and getting the view:
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.discount_map_dialog);
        ImageView itemImage = findViewById(R.id.discount_item_image);
        TextView itemName = findViewById(R.id.discount_item_name);
        TextView itemOriginalPrice = findViewById(R.id.discount_original_price);
        TextView itemDiscountedPrice = findViewById(R.id.discount_discounted_price);
        Button addBtn = findViewById(R.id.add_discount_to_cart_btn);
        Button cancelBtn = findViewById(R.id.discount_dialog_cancel_btn);

        // Registering to the on click of the items
        addBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        // Filling the views with the discount details
        itemName.setText(discount.getProduct().getName());
        itemOriginalPrice.setText(String.format("$%.2f", discount.getOriginalPrice()));
        itemDiscountedPrice.setText(String.format("$%.2f", discount.getDiscountedPrice()));

        int resourceID = context.getResources().getIdentifier(
                "item_" + discount.getProduct().getProductId(),
                "drawable",
                "tamirmo.shopper"
        );

        itemImage.setImageResource(resourceID);


        // Creating a line on top of the original price
        itemOriginalPrice.setPaintFlags(itemDiscountedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

        @Override
    public void onClick(View v) {

        if(v.getId() == R.id.add_discount_to_cart_btn) {
            // Adding the item to the cart
            CartHandler.getInstance().addItem(new CartItem(discount.getProductId(), discount.getProduct(), 1, false));
        }

        // Closing the dialog
        dismiss();
    }
}
