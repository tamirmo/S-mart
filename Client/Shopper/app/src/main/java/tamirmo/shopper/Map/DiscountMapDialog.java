package tamirmo.shopper.Map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tamirmo.shopper.Database.Class.CartItem;
import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.R;

public class DiscountMapDialog extends Dialog {

    private Discount discount;
    private Product product;
    private List<CartItem> cart;
    private Context context;

    public DiscountMapDialog(@NonNull Context context, Discount discountToDisplay, Product product, List<CartItem> cart) {
        super(context);

        discount = discountToDisplay;
        this.product = product;
        this.context = context;
        this.cart = cart;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflating the layout and getting the view:
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.discount_map_dialog);
        ImageView itemImage = findViewById(R.id.discount_item_image);
        TextView itemName = findViewById(R.id.discount_item_name);
        TextView itemDetails = findViewById(R.id.discount_item_details);
        TextView itemOriginalPrice = findViewById(R.id.discount_original_price);
        TextView itemDiscountedPrice = findViewById(R.id.discount_discounted_price);

        // Filling the views with the discount details
        itemName.setText(product.getName());
        itemDetails.setText(product.getAmountPerUnit()+" "+ product.getUnitType());
        itemOriginalPrice.setText(String.format("$%s", discount.getNormalPrice()));
        itemDiscountedPrice.setText(String.format("$%s", discount.getDiscountedPrice()));

        int resourceID = context.getResources().getIdentifier(
                "item_" + product.getProductId(),
                "drawable",
                "tamirmo.shopper"
        );

        itemImage.setImageResource(resourceID);


        // Creating a line on top of the original price
        itemOriginalPrice.setPaintFlags(itemDiscountedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }
}
