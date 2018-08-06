package tamirmo.employee.tasks.navigation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import smart.data.Product;
import tamirmo.employee.R;
import tamirmo.employee.tasks.Task;

/**
 * Created by Tamir on 05/08/2018.
 * A dialog that appears when the user clicks on a task on the map.
 */

public class TaskMapDialog extends Dialog {

    private Task task;
    private Context context;

    TaskMapDialog(@NonNull Context context, Task task) {
        super(context);

        this.task = task;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflating the layout and getting the view:
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.task_map_dialog);
        ImageView productImage = findViewById(R.id.task_product_image);

        TextView productName = findViewById(R.id.task_product_name);
        ImageView taskTypeImage = findViewById(R.id.task_type_image);

        Product productData = task.getProductOfTask();

        // The items images has special names: "item_id"
        int resourceID = context.getResources().getIdentifier(
                "item_" + task.getProductOfTask().getProductId(),
                "drawable",
                "tamirmo.employee"
        );

        productImage.setImageResource(resourceID);
        productName.setText(productData.getName());

        if(task.getTaskType() == Task.TaskType.EXPIRED){
            taskTypeImage.setImageResource(R.drawable.expired_list_icon);
        }else if(task.getTaskType() == Task.TaskType.OUT_OF_STOCK){
            taskTypeImage.setImageResource(R.drawable.out_of_stock_list_icon);
        }
    }
}
