package tamirmo.employee.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import tamirmo.employee.Database.Class.EmptyTask;
import tamirmo.employee.Database.Class.ExpiredTask;
import tamirmo.employee.Database.Class.MisplacedTask;
import tamirmo.employee.Database.Class.Product;
import tamirmo.employee.Database.Class.UserLocation;
import tamirmo.employee.R;

// Controls which data to show in the fragment, and how to show it
public class MapGridAdapter extends BaseAdapter implements View.OnClickListener {

    // Class constants
    private static final int INFINITY = 1000000000;
    private static final int EXPIRE_TASK = 4;
    private static final int MISPLACE_OUT_TASK = 3;
    private static final int MISPLACE_TASK = 2;
    private static final int EMPTY_TASK = 1;
    private static final int NO_TASK = 0;

    private LayoutInflater layoutinflater;
    private Context context;

    // Class attributes
    private List<Product> products;
    private List<EmptyTask> emptyProducts;
    private List<ExpiredTask> expiredProducts;
    private List<MisplacedTask> misplacedProducts;
    private UserLocation userLocation;
    private HashMap<String, Product> productMap;
    private int gridRows;
    private int gridColumns;
    private int[][] taskMatrix;
    private Product[][] productMatrix;

    // Class Builder
    MapGridAdapter(Context context, int gridRows, int gridColumns, List<Product> products, List<EmptyTask> emptyProducts, List<ExpiredTask> expiredProducts, List<MisplacedTask> misplacedProducts, UserLocation userLocation) {
        this.context = context;
        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.gridRows = gridRows;
        this.gridColumns = gridColumns;

        this.products = products;
        this.emptyProducts = emptyProducts;
        this.expiredProducts = expiredProducts;
        this.misplacedProducts = misplacedProducts;

        productMap = getProductMap(products);
        buildMapMatrix();

        this.userLocation = userLocation;
    }

    // Returns a HashMap of a List of products
    private HashMap<String, Product> getProductMap(List<Product> productList) {
        HashMap<String, Product> productsMap = new HashMap<String, Product>();

        for (Product product : productList) {
            productsMap.put(product.getProductId(), product);
        }

        return productsMap;
    }

    // Builds both task, and product matrices from all available tasks
    private void buildMapMatrix() {
        taskMatrix = new int[gridRows][gridColumns];
        productMatrix = new Product[gridRows][gridColumns];

        checkExpiredTasks();
        checkMisplacedTasks();
        checkEmptyTasks();
    }

    // Checks, and fills map matrices with expired tasks
    private void checkExpiredTasks() {
        Product product;
        int locationX, locationY;
        List<ExpiredTask> expiredTasks = new ArrayList<ExpiredTask>();

        expiredTasks.addAll(expiredProducts);

        for (ExpiredTask task : expiredTasks) {
            product = productMap.get(task.getProductID());
            locationX = Integer.parseInt(product.getLocationX());
            locationY = Integer.parseInt(product.getLocationY());
            try {
                if (taskMatrix[locationX][locationY] < EXPIRE_TASK) {
                    taskMatrix[locationX][locationY] = EXPIRE_TASK;
                    productMatrix[locationX][locationY] = product;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                expiredProducts.remove(task);
            }
        }
    }

    // Checks, and fills map matrices with misplaced tasks
    private void checkMisplacedTasks() {
        Product product;
        int locationX, locationY, taskLocationX, taskLocationY;
        List<MisplacedTask> misplacedTasks = new ArrayList<MisplacedTask>();

        misplacedTasks.addAll(misplacedProducts);

        for (MisplacedTask task : misplacedTasks) {
            product = productMap.get(task.getProductID());
            locationX = Integer.parseInt(product.getLocationX());
            locationY = Integer.parseInt(product.getLocationY());
            try {
                if (taskMatrix[locationX][locationY] < MISPLACE_TASK) {
                    taskLocationX = task.getLocationX();
                    taskLocationY = task.getLocationY();
                    if (taskMatrix[taskLocationX][taskLocationY] < MISPLACE_TASK) {
                        taskMatrix[taskLocationX][taskLocationY] = MISPLACE_OUT_TASK;
                        taskMatrix[locationX][locationY] = MISPLACE_TASK;
                        productMatrix[taskLocationX][taskLocationY] = product;
                        productMatrix[locationX][locationY] = product;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                misplacedProducts.remove(task);
            }
        }
    }

    // Checks, and fills map matrices with empty shelves tasks
    private void checkEmptyTasks() {
        Product product;
        int locationX, locationY;
        List<EmptyTask> emptyTasks = new ArrayList<EmptyTask>();

        emptyTasks.addAll(emptyProducts);

        for (EmptyTask task : emptyTasks) {
            product = productMap.get(task.getProductID());
            locationX = Integer.parseInt(product.getLocationX());
            locationY = Integer.parseInt(product.getLocationY());
            try {
                if (taskMatrix[locationX][locationY] < EMPTY_TASK) {
                    taskMatrix[locationX][locationY] = EMPTY_TASK;
                    productMatrix[locationX][locationY] = product;
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                emptyProducts.remove(task);
            }
        }
    }

    // Returns next task type, and product
    public String getNextTaskText() {
        String nextTaskText = null;
        Product product = null;

        ExpiredTask nextExpiredProduct = selectNextExpiredProduct();
        if (nextExpiredProduct == null) {
            MisplacedTask nextMisplacedProduct = selectNextMisplacedProduct();
            if (nextMisplacedProduct == null) {
                EmptyTask nextEmptyProduct = selectNextEmptyProduct();
                if (nextEmptyProduct != null) {
                    product = productMap.get(nextEmptyProduct.getProductID());
                    nextTaskText = context.getString(R.string.empty);
                }
            } else {
                product = productMap.get(nextMisplacedProduct.getProductID());
                nextTaskText = context.getString(R.string.misplaced);
            }
        } else {
            product = productMap.get(nextExpiredProduct.getProductID());
            nextTaskText = context.getString(R.string.expired);
        }

        if (product != null) {
            nextTaskText = product.getName() + " " + product.getAmountPerUnit() + product.getUnitType() + " " + nextTaskText;
        } else {
            nextTaskText = context.getString(R.string.no_next_item);
        }

        return nextTaskText;
    }

    // Returns the closest expired task
    private ExpiredTask selectNextExpiredProduct() {
        int distance = INFINITY, newDistance;
        Product product;
        int locationX, locationY;
        ExpiredTask nextExpiredProduct = null;

        for (ExpiredTask expiredProduct : expiredProducts) {
            product = productMap.get(expiredProduct.getProductID());
            locationX = Integer.parseInt((product.getLocationX()));
            locationY = Integer.parseInt((product.getLocationY()));
            newDistance = calculateDistance(locationX, locationY);
            if (newDistance < distance) {
                distance = newDistance;
                nextExpiredProduct = expiredProduct;
            }
        }

        return nextExpiredProduct;
    }

    // Returns the closest misplaced task
    private MisplacedTask selectNextMisplacedProduct() {
        int distance = INFINITY, newDistance;
        int locationX, locationY;
        MisplacedTask nextMisplacedProduct = null;

        for (MisplacedTask misplacedProduct : misplacedProducts) {
            locationX = misplacedProduct.getLocationX();
            locationY = misplacedProduct.getLocationY();
            newDistance = calculateDistance(locationX, locationY);
            if (newDistance < distance) {
                distance = newDistance;
                nextMisplacedProduct = misplacedProduct;
            }
        }

        return nextMisplacedProduct;
    }

    // Returns the closest empty shelf task
    private EmptyTask selectNextEmptyProduct() {
        int distance = INFINITY, newDistance;
        int locationX, locationY;
        Product product;
        EmptyTask nextEmptyProduct = null;

        for (EmptyTask emptyProduct : emptyProducts) {
            product = productMap.get(emptyProduct.getProductID());
            locationX = Integer.parseInt((product.getLocationX()));
            locationY = Integer.parseInt((product.getLocationY()));
            newDistance = calculateDistance(locationX, locationY);
            if (newDistance < distance) {
                distance = newDistance;
                nextEmptyProduct = emptyProduct;
            }
        }

        return nextEmptyProduct;
    }

    // Calculates a distance to a product from current user location
    // Manhattan distance : |x1-x2| + |y1+y2|
    private int calculateDistance(int productLocationX, int productLocationY) {
        int distanceX = Math.abs(productLocationX - userLocation.getLocationX());
        int distanceY = Math.abs(productLocationY - userLocation.getLocationY());
        int distance = distanceX + distanceY;

        return distance;
    }

    // Defines what to show on the grid
    private static class GridViewModel {
        ImageView emptyTaskIcon;
        ImageView misplaceTaskOutIcon;
        ImageView misplaceTaskInIcon;
        ImageView expiredTaskIcon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridViewModel gridViewModel;

        if (convertView == null) {
            gridViewModel = new GridViewModel();
            convertView = layoutinflater.inflate(R.layout.navigation_map_cell_layout, parent, false);
            gridViewModel.emptyTaskIcon = convertView.findViewById(R.id.empty_map_image);
            gridViewModel.misplaceTaskOutIcon = convertView.findViewById(R.id.misplace_out_map_image);
            gridViewModel.misplaceTaskInIcon = convertView.findViewById(R.id.misplace_in_map_image);
            gridViewModel.expiredTaskIcon = convertView.findViewById(R.id.expired_map_image);
            convertView.setTag(gridViewModel);
        } else {
            gridViewModel = (GridViewModel) convertView.getTag();
        }

        // Getting the cart items and the next cart
        int taskType = (int) getItem(position);

        gridViewModel.emptyTaskIcon.setVisibility(View.INVISIBLE);
        gridViewModel.misplaceTaskInIcon.setVisibility(View.INVISIBLE);
        gridViewModel.misplaceTaskOutIcon.setVisibility(View.INVISIBLE);
        gridViewModel.expiredTaskIcon.setVisibility(View.INVISIBLE);

        switch (taskType) {
            case EXPIRE_TASK:
                gridViewModel.expiredTaskIcon.setVisibility(View.VISIBLE);
                break;
            case EMPTY_TASK:
                gridViewModel.emptyTaskIcon.setVisibility(View.VISIBLE);
                break;
            case MISPLACE_TASK:
                gridViewModel.misplaceTaskInIcon.setVisibility(View.VISIBLE);
                break;
            case MISPLACE_OUT_TASK:
                gridViewModel.misplaceTaskOutIcon.setVisibility(View.VISIBLE);
                break;
        }

        // Registering the on click of the items and setting the items positions as tag
        // (for later use when clicking on an item)
        gridViewModel.expiredTaskIcon.setOnClickListener(this);
        gridViewModel.emptyTaskIcon.setOnClickListener(this);
        gridViewModel.misplaceTaskInIcon.setOnClickListener(this);
        gridViewModel.misplaceTaskOutIcon.setOnClickListener(this);
        gridViewModel.expiredTaskIcon.setTag(position);
        gridViewModel.emptyTaskIcon.setTag(position);
        gridViewModel.misplaceTaskInIcon.setTag(position);
        gridViewModel.misplaceTaskOutIcon.setTag(position);

        return convertView;
    }

    @Override
    public int getCount() {
        int rows = gridRows;
        int columns = gridColumns;

        return rows * columns;
    }

    @Override
    public Object getItem(int position) {
        return getTaskFromMatrix(position);
    }

    // Returns a task type number from task matrix
    private int getTaskFromMatrix(int position) {
        int row = position / gridColumns;
        int column = position % gridColumns;

        return taskMatrix[row][column];
    }

    // Returns a product from products matrix
    private Product getProductFromMatrix(int position) {
        int row = position / gridColumns;
        int column = position % gridColumns;

        return productMatrix[row][column];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View v) {
        int itemClickedPosition = (int) v.getTag();
        int clickedTask = getTaskFromMatrix(itemClickedPosition);
        String taskCommand = "";

        if (clickedTask != NO_TASK) {
            Product product = getProductFromMatrix(itemClickedPosition);
            if (product != null) {
                switch (clickedTask) {
                    case EXPIRE_TASK:
                        taskCommand = context.getString(R.string.expired_command);
                        break;
                    case EMPTY_TASK:
                        taskCommand = context.getString(R.string.empty_command);
                        break;
                    case MISPLACE_TASK:
                        taskCommand = context.getString(R.string.misplaced_in_command);
                        break;
                    case MISPLACE_OUT_TASK:
                        taskCommand = context.getString(R.string.misplaced_out_command);
                        break;
                }

                ProductMapDialog dialog = new ProductMapDialog(context, product, taskCommand);
                dialog.show();
            }
        }
    }

    // Updates the class data
    public void updateData() {
        productMap = getProductMap(products);
        buildMapMatrix();

        // Changes the view based on the new data
        this.notifyDataSetChanged();
    }
}
