package tamirmo.employee.tasks.navigation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import smart.data.SmartDataManager;
import tamirmo.employee.R;
import tamirmo.employee.tasks.IOnTasksUpdated;
import tamirmo.employee.tasks.Task;
import tamirmo.employee.tasks.TasksHandler;
import tamirmo.employee.tasks.list.TasksListFragment;

/**
 * Created by Tamir on 16/06/2018.
 * A fragment for the navigation.
 * Containing the map, next item, finish and show list buttons.
 */

public class NavigationFragment extends Fragment implements View.OnClickListener, IOnTasksUpdated {

    // Views:
    private TextView nextTaskTextTextView;
    private TextView nextTaskTypeTextView;
    private GridView mapGrid;

    private NavigationMapAdapter navigationMapAdapter;

    // The fragment to move to when show list is pressed
    private TasksListFragment tasksListFragment;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.navigation_fragment, container, false);

        navigationMapAdapter = new NavigationMapAdapter(getContext());

        nextTaskTextTextView = rootView.findViewById(R.id.next_task_text_text_view);
        nextTaskTypeTextView = rootView.findViewById(R.id.next_task_type_text_view);

        // Registering to the click events of the buttons:
        rootView.findViewById(R.id.show_list_btn).setOnClickListener(this);
        mapGrid = rootView.findViewById(R.id.map_grid);
        mapGrid.setAdapter(navigationMapAdapter);

        TasksHandler.getInstance().addTasksUpdatedListener(this);

        // Refreshing for the first time
        refreshNextTask();

        mapGrid.setNumColumns(SmartDataManager.MAP_COLS_COUNT);

        tasksListFragment = new TasksListFragment();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TasksHandler.getInstance().removeTasksUpdatedListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction;
        switch (v.getId()){
            case R.id.show_list_btn:

                if(getActivity() != null) {
                    // Moving to cart items fragment (showing current cart)
                    transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.add(R.id.container, tasksListFragment);
                    transaction.addToBackStack(tasksListFragment.getClass().getSimpleName());
                    transaction.commit();
                }
                break;
        }
    }

    private void refreshNextTask(){
        Task task = TasksHandler.getInstance().getNextTask();
        if(getContext() != null) {
            if (task == null) {
                String noNextItem = getContext().getResources().getString(R.string.no_next_task);
                nextTaskTextTextView.setText(noNextItem);
                nextTaskTypeTextView.setText("");
            } else {
                nextTaskTextTextView.setText(task.getProductOfTask().getName());
                if (task.getTaskType() == Task.TaskType.EXPIRED) {
                    nextTaskTypeTextView.setText(getContext().getResources().getString(R.string.expired_next_task));
                } else if (task.getTaskType() == Task.TaskType.OUT_OF_STOCK) {
                    nextTaskTypeTextView.setText(getContext().getResources().getString(R.string.out_of_stock_next_task));
                }
            }
        }
    }

    @Override
    public void onTasksUpdated() {
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    refreshNextTask();

                    // Refreshing the map
                    navigationMapAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
