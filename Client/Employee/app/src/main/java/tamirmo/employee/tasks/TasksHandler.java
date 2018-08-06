package tamirmo.employee.tasks;

import java.util.ArrayList;
import java.util.List;

import smart.data.Product;
import smart.data.SmartDataManager;

/**
 * Created by Tamir on 04/08/2018.
 * Handling the tasks of the employee
 * (holding the tasks as a list and matrix [for the map]).
 * This class in NOT on SmartData module cause the tasks are not saved on the server,
 * not on coming from the server and therefore should not be in the server's or shopper's code
 * (SmartData is in both).
 */

public class TasksHandler {
    // Singleton:
    private static TasksHandler instance;
    public static TasksHandler getInstance(){
        if (instance == null){
            instance = new TasksHandler();
        }

        return instance;
    }

    private List<Task> tasksList;
    private List<IOnTasksUpdated> tasksUpdatedListeners;
    // (For navigation)
    private Task nextTask;
    private Task[][] tasksAsMatrix;
    private IOnTaskAdded onTaskAddedListener;

    public void setOnTaskAddedListener(IOnTaskAdded onTaskAddedListener){
        this.onTaskAddedListener = onTaskAddedListener;
    }

    public List<Task> getTasks() {
        return tasksList;
    }

    public Task getNextTask(){
        return nextTask;
    }

    private TasksHandler(){
        tasksList = new ArrayList<>();
        tasksAsMatrix = new Task[SmartDataManager.MAP_ROWS_COUNT][SmartDataManager.MAP_COLS_COUNT];
        tasksUpdatedListeners = new ArrayList<>();
    }

    public Task getTaskByMapPosition(int position){
        int row = position / SmartDataManager.MAP_COLS_COUNT;
        int column = position % SmartDataManager.MAP_COLS_COUNT;

        return tasksAsMatrix[row][column];
    }

    /**
     * Called when the products list is received from the server.
     * It calculates the tasks of the worker
     * (by going over the products and checking which needs to be refilled)
     * @param products List<Product>, The new list of products
     */
    public void onProductsListReceived(List<Product> products){
        // Recalculating the tasks list:

        tasksList = new ArrayList<>();
        for(Product product: products){
            // Adding out of stock tasks
            if(!product.isAvailable()){
                Task outOfStockProductTask = new Task(product.getProductId(), Task.TaskType.OUT_OF_STOCK, product);
                tasksList.add(outOfStockProductTask);
                tasksAsMatrix[product.getLocationX()][product.getLocationY()] = outOfStockProductTask;
            }

            // Adding expired tasks
            if(product.isExpired()){
                Task expiredProductTask = new Task(product.getProductId(), Task.TaskType.EXPIRED, product);
                tasksList.add(expiredProductTask);
                tasksAsMatrix[product.getLocationX()][product.getLocationY()] = expiredProductTask;
            }
        }
    }

    /**
     * Initializing the next cart item
     */
    void startNavigation(){
        // Starting new navigation only if not in progress of a navigation already
        if(nextTask == null) {
            if(tasksList.size() > 0) {
                // Taking the first task
                nextTask = tasksList.get(0);
            }
        }
    }

    /**
     * Called when an update of the a product's stock is received
     * @param productId The id of the product
     * @param productStock The number of items in stock for this product
     */
    public void onProductUpdated(long productId, int productStock){
        Task productTask = null;

        // Searching for a task for this product:
        for(Task task: tasksList){
            if(task.getProductId() == productId){
                productTask = task;
            }
        }

        // Checking if the product is out of stock
        if(productStock == 0){
            // If the task for this item not exist
            if(productTask == null){
                // Adding a new task for this item
                Product product = SmartDataManager.getInstance().getProductById(productId);
                Task itemTask = new Task(productId,
                        Task.TaskType.OUT_OF_STOCK,
                        SmartDataManager.getInstance().getProductById(productId));
                tasksList.add(itemTask);
                tasksAsMatrix[product.getLocationX()][product.getLocationY()] = itemTask;

                // If this is the first task,
                // Adding it to navigation
                startNavigation();

                // Alerting listeners (map and list fragments)
                fireTasksUpdated();

                // Alerting of a new task added to set a notification (Main activity is responsible for it)
                if(onTaskAddedListener != null){
                    onTaskAddedListener.onTaskAdded(itemTask);
                }
            }
        }
        // If the product is in stock
        else {
            // No need for a task for this item anymore
            // (when an item is filled, the expired items are also taking care of)
            if(productTask != null){
                // Clearing the task from the map and from the list
                tasksList.remove(productTask);
                tasksAsMatrix[productTask .getProductOfTask().getLocationX()][productTask .getProductOfTask().getLocationY()] = null;

                // Updating the next task:
                if(tasksList.size() > 0){
                    // Taking the first task
                    nextTask = tasksList.get(0);
                }
                // There are no tasks left
                else{
                    // No next task
                    nextTask = null;
                }

                // Alerting listeners (map and list fragments)
                fireTasksUpdated();
            }
        }
    }

    // Tasks updated listeners list methods:

    public void addTasksUpdatedListener(IOnTasksUpdated listener){
        tasksUpdatedListeners.add(listener);
    }

    public void removeTasksUpdatedListener(IOnTasksUpdated listener){
        tasksUpdatedListeners.remove(listener);
    }

    private void fireTasksUpdated(){
        for(IOnTasksUpdated listener : tasksUpdatedListeners){
            listener.onTasksUpdated();
        }
    }
}
