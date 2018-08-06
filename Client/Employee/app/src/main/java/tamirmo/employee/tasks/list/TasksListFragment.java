package tamirmo.employee.tasks.list;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import tamirmo.employee.R;
import tamirmo.employee.tasks.IOnTasksUpdated;
import tamirmo.employee.tasks.TasksHandler;

/**
 * Created by User on 13/06/2018.
 * A fragment displaying the employee's tasks in a list view.
 */

public class TasksListFragment extends Fragment implements IOnTasksUpdated {

    // Views
    private ListView currItemsListView;

    private TasksListAdapter itemsAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.tasks_list_fragment, container, false);

        itemsAdapter = new TasksListAdapter(getActivity());

        currItemsListView = rootView.findViewById(R.id.tasks_list_view);
        currItemsListView.setAdapter(itemsAdapter);

        // Registering to changes in the cart
        TasksHandler.getInstance().addTasksUpdatedListener(this);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // No need to listen to cart changes anymore
        TasksHandler.getInstance().removeTasksUpdatedListener(this);
    }

    @Override
    public void onTasksUpdated() {
        if(getActivity() != null) {
            // Updating the adapter
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    itemsAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
