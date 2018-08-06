package tamirmo.employee.tasks;

/**
 * Created by Tamir on 04/08/2018.
 * Event interface for when the tasks list is updated.
 */

public interface IOnTasksUpdated {
    /**
     * Called when receiving an update from the server
     * (when first getting all products or shopper picks up an item or employee fills inventory)
     */
    void onTasksUpdated();
}
