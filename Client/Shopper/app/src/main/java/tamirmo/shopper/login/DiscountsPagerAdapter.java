package tamirmo.shopper.login;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import smart.data.Discount;
import tamirmo.shopper.R;

/**
 * Created by Tamir on 09/06/2018.
 * An adapter for the discounts view pager on the log in layout.
 *
 * ####################### OLD CODE ! #######################
 *
 * Now the discounts are not displayed in a pager on the log in activity,
 * but only after log in.
 */

public class DiscountsPagerAdapter extends PagerAdapter {
    private List<Discount> discounts;
    private Context context;

    DiscountsPagerAdapter(Context context) {
        this.context = context;

        discounts = new ArrayList<>();
        // TODO: If we want to get the log in pager back, filling this list
        /*discounts.add(new Discount("Coca Cola", 0.25, 0.15));
        discounts.add(new Discount("Corn Flakes", 1.75, 1.00));
        discounts.add(new Discount("Rice", 0.25, 0.15));
        discounts.add(new Discount("Flour", 0.5, 0.25));
        discounts.add(new Discount("Pasta", 0.75, 0.5));*/
    }

    @Override public int getCount() {
        return discounts.size();
    }

    @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override public void destroyItem(@NonNull ViewGroup view, int position, @NonNull Object object) {
        view.removeView((View) object);
    }

    @NonNull
    @Override public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        Discount discount = discounts.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.discount_layout, collection, false);
        ImageView itemImage = layout.getRootView().findViewById(R.id.discount_item_image);
        TextView itemName = layout.getRootView().findViewById(R.id.discount_item_name);
        TextView itemOriginalPrice = layout.getRootView().findViewById(R.id.discount_original_price);
        TextView itemDiscountedPrice = layout.getRootView().findViewById(R.id.discount_discounted_price);

        itemName.setText(discount.getProduct().getName());
        itemOriginalPrice.setText(String.format("$%.2f", discount.getOriginalPrice()));
        itemDiscountedPrice.setText(String.format("$%.2f", discount.getDiscountedPrice()));

        int resourceID = context.getResources().getIdentifier(
                "item_" + position,
                "drawable",
                "tamirmo.shopper"
        );

        itemImage.setImageResource(resourceID);


        // Creating a line on top of the original price
        itemOriginalPrice.setPaintFlags(itemDiscountedPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        collection.addView(layout);
        return layout;
    }
}
