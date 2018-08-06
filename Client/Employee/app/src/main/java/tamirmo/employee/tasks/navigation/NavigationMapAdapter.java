package tamirmo.employee.tasks.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import smart.data.SmartDataManager;
import tamirmo.employee.R;
import tamirmo.employee.tasks.Task;
import tamirmo.employee.tasks.TasksHandler;

/**
 * Created by Tamir on 04/08/2018.
 * An adapter for the map layout that is represented by a grid.
 */

class NavigationMapAdapter extends BaseAdapter implements View.OnClickListener {
    private LayoutInflater layoutinflater;
    private Context context;

    NavigationMapAdapter(Context context) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        int rows = SmartDataManager.MAP_ROWS_COUNT;
        int columns = SmartDataManager.MAP_COLS_COUNT;
        return rows * columns;
    }

    @Override
    public Object getItem(int position) {
        return TasksHandler.getInstance().getTaskByMapPosition(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NavigationMapViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new NavigationMapViewHolder();
            convertView = layoutinflater.inflate(R.layout.navigation_map_cell_layout, parent, false);
            listViewHolder.expired = convertView.findViewById(R.id.expired_map_image);
            listViewHolder.outOfStock = convertView.findViewById(R.id.out_of_stock_map_image);
            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (NavigationMapViewHolder)convertView.getTag();
        }

        // Getting the task items and the next task
        Task task = (Task) getItem(position);
        Task nextTask = TasksHandler.getInstance().getNextTask();

        // If there is a task for this position
        if(task != null){

            // Displaying the correct icon:
            if(task.getTaskType() == Task.TaskType.EXPIRED)
            {
                listViewHolder.expired.setVisibility(View.VISIBLE);
                listViewHolder.outOfStock.setVisibility(View.INVISIBLE);

                // Checking if we are in the next item's view
                if(nextTask != null && nextTask == task){
                    //itemImageResourceId = R.drawable.cart_item_selected;
                    listViewHolder.expired.setImageResource(R.drawable.task_selected);
                }else{
                    listViewHolder.expired.setImageResource(R.drawable.expired_map_icon);
                }
            }
            else if(task.getTaskType() == Task.TaskType.OUT_OF_STOCK)
            {
                listViewHolder.expired.setVisibility(View.INVISIBLE);
                listViewHolder.outOfStock.setVisibility(View.VISIBLE);

                // Checking if we are in the next item's view
                if(nextTask != null && nextTask == task){
                    //itemImageResourceId = R.drawable.cart_item_selected;
                    listViewHolder.outOfStock.setImageResource(R.drawable.task_selected);
                }else{
                    listViewHolder.outOfStock.setImageResource(R.drawable.out_of_stock_map_icon);
                }
            }
        }else{
            // Hiding the images cause this cell has no alerts
            listViewHolder.expired.setVisibility(View.INVISIBLE);
            listViewHolder.outOfStock.setVisibility(View.INVISIBLE);
        }

        // Registering the on click of the items and setting the items positions as tag
        // (for later use when clicking on an item)
        listViewHolder.expired.setOnClickListener(this);
        listViewHolder.outOfStock.setOnClickListener(this);
        listViewHolder.expired.setTag(position);
        listViewHolder.outOfStock.setTag(position);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        // The position of the images have their positions as tag
        int itemClickedPosition = (int)v.getTag();

        Task clickedTask = (Task) getItem(itemClickedPosition);

        // Checking if the user clicked on a task
        if(clickedTask != null){
            // Displaying a dialog with the item details
            TaskMapDialog dialog = new TaskMapDialog(context, clickedTask);
            dialog.show();
        }
    }

    private static class NavigationMapViewHolder{
        ImageView expired;
        ImageView outOfStock;
    }
}
