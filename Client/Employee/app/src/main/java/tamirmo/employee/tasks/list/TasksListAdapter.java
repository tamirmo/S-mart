package tamirmo.employee.tasks.list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import tamirmo.employee.R;
import tamirmo.employee.tasks.Task;
import tamirmo.employee.tasks.TasksHandler;

/**
 * Created by Tamir on 13/06/2018.
 * An adapter for the user's tasks.
 */

public class TasksListAdapter extends ArrayAdapter<Task>{

    // View lookup cache
    public static class TaskItemViewHolder {
        ImageView productImage;
        TextView productName;
        ImageView outOfStockImage;
        ImageView expiredImage;
    }

    private Context context;

    TasksListAdapter(Context context) {
        super(context, R.layout.task_list_item, new Task[0]);
        this.context = context;
    }

    @Override
    public int getCount() {
        if(TasksHandler.getInstance().getTasks() == null){
            return 0;
        }
        return TasksHandler.getInstance().getTasks().size();
    }

    @Nullable
    @Override
    public Task getItem(int position) {
        return TasksHandler.getInstance().getTasks().get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        Task task = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        TaskItemViewHolder viewHolder; // view lookup cache stored in tag

        // If the view was not created yet
        if (convertView == null) {

            // Creating a holder to hold all views
            viewHolder = new TaskItemViewHolder();
            // Getting the inflater
            LayoutInflater inflater = LayoutInflater.from(context);
            // Inflating the cartItem
            convertView = inflater.inflate(R.layout.task_list_item, parent, false);

            // Putting all views inside the holder:
            viewHolder.productImage = convertView.findViewById(R.id.product_image);
            viewHolder.productName = convertView.findViewById(R.id.product_name);
            viewHolder.outOfStockImage = convertView.findViewById(R.id.task_type_out_of_stock_image);
            viewHolder.expiredImage = convertView.findViewById(R.id.task_type_expired_image);

            // Saving the view for next time
            convertView.setTag(viewHolder);
        } else {
            // The view exists, getting it's children views
            viewHolder = (TaskItemViewHolder) convertView.getTag();
        }

        if(task != null) {
            // Setting the image of the type for this task
            if (task.getTaskType() == Task.TaskType.EXPIRED) {
                viewHolder.expiredImage.setVisibility(View.VISIBLE);
                viewHolder.outOfStockImage.setVisibility(View.INVISIBLE);
            } else if (task.getTaskType() == Task.TaskType.OUT_OF_STOCK) {
                viewHolder.expiredImage.setVisibility(View.INVISIBLE);
                viewHolder.outOfStockImage.setVisibility(View.VISIBLE);
            }

            // The items images has special names: "item_id"
            int resourceID = context.getResources().getIdentifier(
                    "item_" + task.getProductId(),
                    "drawable",
                    "tamirmo.employee"
            );

            viewHolder.productImage.setImageResource(resourceID);
            // Setting the name of the product
            viewHolder.productName.setText(task.getProductOfTask().getName());
        }

        return convertView;
    }
}
