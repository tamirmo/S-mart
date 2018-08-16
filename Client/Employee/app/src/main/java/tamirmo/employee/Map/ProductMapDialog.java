package tamirmo.employee.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import tamirmo.employee.Database.Class.Product;
import tamirmo.employee.R;


public class ProductMapDialog extends Dialog {

    private Context context;
    private Product product;
    private String taskCommand;

    ProductMapDialog(@NonNull Context context, Product product, String taskCommand) {
        super(context);

        this.product = product;
        this.context = context;
        this.taskCommand = taskCommand;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflating the layout and getting the view:
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.product_map_dialog);
        ImageView itemImage = findViewById(R.id.product_image);
        TextView itemName = findViewById(R.id.product_name);
        TextView unit = findViewById(R.id.product_unit);
        TextView task = findViewById(R.id.task_type);

        // The items images has special names: "item_id"
        int resourceID = context.getResources().getIdentifier(
                "item_" + product.getProductId(),
                "drawable",
                "tamirmo.employee"
        );
        itemImage.setImageResource(resourceID);
        itemName.setText(product.getName());
        unit.setText(String.format("%s %s", product.getAmountPerUnit(), product.getUnitType()));
        task.setText(taskCommand);
    }
}
