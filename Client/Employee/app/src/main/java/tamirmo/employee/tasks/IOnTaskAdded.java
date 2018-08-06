package tamirmo.employee.tasks;

/**
 * Created by Tamir on 04/08/2018.
 * Event interface for when the tasks is added (alerting for notification).
 */

public interface IOnTaskAdded {
    void onTaskAdded(Task addedTask);
}
