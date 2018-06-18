package tamirmo.shopper.discounts;

import smart.data.Discount;

/**
 * Created by Tamir on 18/06/2018.
 * An event for when we need to alert the shopper of a discount.
 */

public interface IOnDiscountAlert {
    void onDiscountAlert(Discount discountToAlertOf);
}
