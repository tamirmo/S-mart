package tamirmo.shopper.cart;

import java.util.ArrayList;
import java.util.List;

import smart.data.CartItem;
import smart.data.Database;
import tamirmo.shopper.connection.ICartUpdated;
import tamirmo.shopper.connection.ServerConnectionHandler;

/**
 * Created by Tamir on 16/06/2018.
 * Holding the current cart, responsible for updating it on the server and manage navigation.
 */

public class CartHandler {
    // Singleton:
    private static CartHandler instance;
    public static CartHandler getInstance(){
        if (instance == null){
            instance = new CartHandler();
        }

        return instance;
    }

    private List<CartItem> cart;
    private CartItem lastRemovedItem;
    private List<ICartUpdated> iCartUpdatedListeners;
    // (For navigation)
    private CartItem nextCartItem;
    private CartItem[][] cartItemsAsMatrix;
    private List<IOnItemPickedListener> onItemPickedListeners;

    public List<CartItem> getCart() {
        return cart;
    }

    public void setCart(String jsonCart){
        cart = Database.getInstance().readCartFromJsonString(jsonCart);

        // Placing the cart items in their place in the map matrix
        for(CartItem cartItem: cart) {
            cartItemsAsMatrix[cartItem.getProduct().getLocationX()][cartItem.getProduct().getLocationY()] = cartItem;
        }
    }

    public CartItem getNextCartItem(){
        return nextCartItem;
    }

    private CartHandler(){
        iCartUpdatedListeners = new ArrayList<>();
        onItemPickedListeners = new ArrayList<>();
        cartItemsAsMatrix = new CartItem[Database.MAP_ROWS_COUNT][Database.MAP_COLS_COUNT];
    }

    public CartItem getItemByMapPosition(int position){
        int row = position / Database.MAP_COLS_COUNT;
        int column = position % Database.MAP_COLS_COUNT;

        return cartItemsAsMatrix[row][column];
    }

    public double getCartSum(){
        double sum = 0;

        for(CartItem cartItem: cart){
            sum += cartItem.getItemPrice();
        }

        return sum;
    }

    public void addItem(CartItem itemToAdd){
        cart.add(itemToAdd);

        // Adding the item to map
        cartItemsAsMatrix[itemToAdd.getProduct().getLocationX()][itemToAdd.getProduct().getLocationY()] = itemToAdd;

        // Alerting the change to refresh view
        fireCartUpdated();
    }

    private void removeItem(CartItem itemToRemove){
        // Saving it for the undo action
        lastRemovedItem = itemToRemove;
        // Removing
        cart.remove(itemToRemove);

        // Adding the item to map
        cartItemsAsMatrix[itemToRemove.getProduct().getLocationX()][itemToRemove.getProduct().getLocationY()] = null;

        if(itemToRemove == nextCartItem){
            // We need to recalculate the next item
            nextCartItem = null;
            startNavigation();
        }
    }

    public void increaseAmount(CartItem itemToIncrease){
        // Increasing by one
        itemToIncrease.setAmount(itemToIncrease.getAmount() + 1);
        // Alerting the change to refresh view
        fireCartUpdated();
    }

    public void onItemPicked(long itemId){
        // Searching the item and updating it
        for (CartItem cartItem : cart){
            if(cartItem.getProductId() == itemId){
                cartItem.setPicked(true);

                // When an item picked, we need to recalculate the next item
                calculateNextCartItem(cartItem);

                fireItemPicked(cartItem);

                // Alerting the change to refresh view
                fireCartUpdated();
                break;
            }
        }
    }

    /**
     * Calculating the next item for the shopper to grab.
     * @param pickedItem CartItem The last picked item, null if none
     */
    private void calculateNextCartItem(CartItem pickedItem){
        CartItem nextItem = null;

        int currItemPickedIndex = cart.indexOf(pickedItem);

        // Checking if there is a next item on the cart
        if (cart.size() > currItemPickedIndex + 1) {
            // Setting it as the next item
            nextItem = cart.get(currItemPickedIndex + 1);
        }

        nextCartItem = nextItem;
    }

    /**
     * Initializing the next cart item
     */
    public void startNavigation(){
        // Starting new navigation only if not in progress of a navigation already
        if(nextCartItem == null) {
            // Searching for an item, taking the first that is not taken
            for (CartItem cartItem : cart) {
                if (!cartItem.isPicked()) {
                    nextCartItem = cartItem;
                    break;
                }
            }
        }
    }

    /**
     * Decreasing the amount of the item given (removing if amount is 0)
     * @param itemToDecrease CartItem, The item to decrease amount from
     * @return boolean, True if the item was removed, False if not
     */
    public boolean decreaseAmount(CartItem itemToDecrease){
        boolean wasItemRemoved = false;

        // Decreasing
        itemToDecrease.setAmount(itemToDecrease.getAmount() - 1);

        // Checking if this item has 0 amount (no more in cart)
        if(itemToDecrease.getAmount() == 0){
            // Removing it
            removeItem(itemToDecrease);
            wasItemRemoved = true;
        }

        // Alerting the change to refresh view
        fireCartUpdated();

        return wasItemRemoved;
    }

    /**
     * Reverting the last remove request
     */
    public void undoLastRemovedItem(){
        if(lastRemovedItem != null){
            // Updating the amount back to 1 and adding
            lastRemovedItem.setAmount(1);
            addItem(lastRemovedItem);
        }
    }

    public void endShopping(){
        List<CartItem> unpickedItems = new ArrayList<>();

        // Saving all items the user has not taken
        for (CartItem cartItem : cart){
            if(!cartItem.isPicked()){
                unpickedItems.add(cartItem);
            }
        }

        // Removing them
        cart.removeAll(unpickedItems);

        fireCartUpdated();
    }

    /**
     * Updates the cart on the server from the current cart held here (sent as Json string).
     */
    private void updateServerCart(){
        String cartJson = Database.getInstance().getCartAsJson(cart);
        ServerConnectionHandler.getInstance().sendCartUpdate(cartJson);
    }

    /**
     * Clear the cart (removing all items). Called when the shopper ends it's shopping
     * (after reviewing the receipt)
     */
    public void clearCart(){
        cart.clear();
        cartItemsAsMatrix = new CartItem[Database.MAP_ROWS_COUNT][Database.MAP_COLS_COUNT];
        nextCartItem = null;
        fireCartUpdated();
    }

    // item picked listeners list methods:

    public void addItemPickedListener(IOnItemPickedListener listener){
        onItemPickedListeners.add(listener);
    }

    public void removeItemPickedListener(IOnItemPickedListener listener){
        onItemPickedListeners.remove(listener);
    }

    private void fireItemPicked(CartItem itemPicked){
        for(IOnItemPickedListener listener : onItemPickedListeners){
            listener.onItemPicked(itemPicked);
        }
    }

    // Cart updated listeners list methods:

    public void addCartUpdatedListener(ICartUpdated listener){
        iCartUpdatedListeners.add(listener);
    }

    public void removeCartUpdatedListener(ICartUpdated listener){
        iCartUpdatedListeners.remove(listener);
    }

    private void fireCartUpdated(){
        // Socket operations are not allowed on the main thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Updating the server of the new cart
                updateServerCart();
            }
        }).start();

        for(ICartUpdated listener : iCartUpdatedListeners){
            listener.onCartUpdated();
        }
    }
}
