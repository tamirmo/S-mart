package tamirmo.employee.tasks;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tamirmo.employee.R;
import tamirmo.employee.tasks.list.TasksListFragment;
import tamirmo.employee.tasks.navigation.NavigationFragment;


/**
 * Created by Tamir on 09/06/2018.
 * A fragment with tasks menu options (navigate and show list).
 */

public class TasksOptionsFragment extends Fragment implements View.OnClickListener {
    // The fragment to move to when list is pressed
    private TasksListFragment tasksListFragment;

    // The fragment to move to when start tasks is pressed
    private NavigationFragment navigationFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.tasks_options_fragment, container, false);

        rootView.findViewById(R.id.tasks_list_btn).setOnClickListener(this);
        rootView.findViewById(R.id.start_tasks_btn).setOnClickListener(this);

        tasksListFragment = new TasksListFragment();
        navigationFragment = new NavigationFragment();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Fragment fragmentToMoveTo = null;

        switch (v.getId()){
            case R.id.tasks_list_btn:
                fragmentToMoveTo = tasksListFragment;
                break;
            case R.id.start_tasks_btn:
                TasksHandler.getInstance().startNavigation();
                fragmentToMoveTo = navigationFragment;
                break;
        }
        moveToFragment(fragmentToMoveTo);
    }

    private void moveToFragment(Fragment fragmentToMoveTo){
        if(fragmentToMoveTo != null && !fragmentToMoveTo.isAdded() && getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, fragmentToMoveTo);
            transaction.addToBackStack(fragmentToMoveTo.getClass().getSimpleName());
            transaction.commit();
        }
    }
}
