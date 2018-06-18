package tamirmo.shopper.connection;

/**
 * Created by Tamir on 16/06/2018.
 * Event interface for when the current cart was updated.
 */

public interface ICartUpdated {
    // Called when adding/removing an item from the cart or received an update from the server on an item picked.
    void onCartUpdated();
}
