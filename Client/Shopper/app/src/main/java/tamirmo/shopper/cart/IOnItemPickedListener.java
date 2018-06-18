package tamirmo.shopper.cart;

import smart.data.CartItem;

/**
 * Created by Tamir on 18/06/2018.
 * Event raised when the shopper picks an item
 */

public interface IOnItemPickedListener {
    void onItemPicked(CartItem cartItem);
}
