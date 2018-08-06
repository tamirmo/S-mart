package tamirmo.employee.tasks;

import smart.data.Product;

/**
 * Created by Tamir on 04/08/2018.
 * Holding task's data (type and product related to the task)
 */

public class Task {
    // Indicating the task's type
    public enum TaskType {OUT_OF_STOCK, EXPIRED};

    private long productId;
    private TaskType taskType;
    // Each task is related to one product
    private Product productOfTask;

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public void setTaskType(TaskType taskType) {
        this.taskType = taskType;
    }

    public Product getProductOfTask() {
        return productOfTask;
    }

    public void setProductOfTask(Product productOfTask) {
        this.productOfTask = productOfTask;
    }

    public Task(long productId,
                TaskType taskType,
                Product productOfTask){
        this.productId = productId;
        this.taskType = taskType;
        this.productOfTask = productOfTask;
    }
}
