package tamirmo.shopper.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.Database.Class.Sale;

import tamirmo.shopper.R;

public class SaleMapDialog extends Dialog {

    private Context context;
    private Sale sale;
    private Product product;

    public SaleMapDialog(@NonNull Context context, Sale sale, Product product) {
        super(context);

        this.context = context;
        this.sale = sale;
        this.product = product;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sale_map_dialog);
        ImageView itemImage = findViewById(R.id.sale_item_image);
        TextView itemName = findViewById(R.id.sale_item_name);
        TextView itemDetails = findViewById(R.id.item_details);
        TextView itemSale = findViewById(R.id.sale_details);

        itemName.setText(product.getName());
        itemDetails.setText(product.getAmountPerUnit()+ " " + product.getUnitType());
        itemSale.setText(sale.getPayAmount()+ " + "+ sale.getFreeAmount());

        int resourceID = context.getResources().getIdentifier(
                "item_" + product.getProductId(),
                "drawable",
                "tamirmo.shopper"
        );
        itemImage.setImageResource(resourceID);
    }
}
