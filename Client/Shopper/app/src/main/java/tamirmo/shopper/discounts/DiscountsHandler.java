package tamirmo.shopper.discounts;

import java.util.List;
import java.util.Random;

import smart.data.CartItem;
import smart.data.Discount;
import smart.data.Database;
import tamirmo.shopper.cart.CartHandler;
import tamirmo.shopper.cart.IOnItemPickedListener;

/**
 * Created by Tamir on 17/06/2018.
 * Holding discounts list.
 */

public class DiscountsHandler implements IOnItemPickedListener {
    // Singleton:
    private static DiscountsHandler instance;
    public static DiscountsHandler getInstance(){
        if (instance == null){
            instance = new DiscountsHandler();
        }

        return instance;
    }

    private List<Discount> discounts;
    private Discount[][] discountsAsMatrix;
    private IOnDiscountAlert onDiscountAlertListener;
    private Random rand = new Random();



    public void setOnDiscountAlertListener(IOnDiscountAlert listener){
        this.onDiscountAlertListener = listener;
    }

    public List<Discount> getDiscounts(){
        return discounts;
    }

    public void setDiscounts(String jsonCart){
        discounts = Database.getInstance().readDiscountsFromJsonString(jsonCart);

        // Placing the discounts in their place in the map matrix
        for(Discount currDiscount: discounts) {
            discountsAsMatrix[currDiscount.getProduct().getLocationX()][currDiscount.getProduct().getLocationY()] = currDiscount;
        }
    }

    private DiscountsHandler(){
        discountsAsMatrix = new Discount[Database.MAP_ROWS_COUNT][Database.MAP_COLS_COUNT];
        CartHandler.getInstance().addItemPickedListener(this);
    }

    public Discount getDiscountByMapCoordinates(int position){
        int row = position / Database.MAP_COLS_COUNT;
        int column = position % Database.MAP_COLS_COUNT;

        return discountsAsMatrix[row][column];
    }

    @Override
    public void onItemPicked(CartItem cartItem) {
        // Checking there are discounts
        if(discounts.size() > 0) {
            // Creating a random discount to alert and notifying listener:
            int randomDiscount = rand.nextInt(discounts.size() - 1);
            if(onDiscountAlertListener != null){
                onDiscountAlertListener.onDiscountAlert(discounts.get(randomDiscount));
            }
        }
    }
}
