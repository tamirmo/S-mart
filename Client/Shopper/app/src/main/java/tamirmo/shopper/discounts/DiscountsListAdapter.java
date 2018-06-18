package tamirmo.shopper.discounts;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import smart.data.Discount;
import tamirmo.shopper.R;

/**
 * Created by Tamir on 17/06/2018.
 * An adapter for the list of discounts in the discounts fragment.
 */

public class DiscountsListAdapter extends ArrayAdapter<Discount> {

    private Context context;
    private LayoutInflater layoutinflater;

    DiscountsListAdapter(@NonNull Context context) {
        super(context, R.layout.discount_list_item_layout);
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    @Override
    public int getCount() {
        return DiscountsHandler.getInstance().getDiscounts().size();
    }

    @Nullable
    @Override
    public Discount getItem(int position) {
        return DiscountsHandler.getInstance().getDiscounts().get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the discount for this position
        Discount discount = getItem(position);

        DiscountViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new DiscountViewHolder();
            convertView = layoutinflater.inflate(R.layout.discount_list_item_layout, parent, false);
            listViewHolder.image = convertView.findViewById(R.id.discount_item_image);
            listViewHolder.name = convertView.findViewById(R.id.discount_item_name);
            listViewHolder.originalPrice = convertView.findViewById(R.id.discount_original_price);
            listViewHolder.discountedPrice = convertView.findViewById(R.id.discount_discounted_price);
            listViewHolder.personalImage = convertView.findViewById(R.id.personal_discount_icon);

            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (DiscountViewHolder)convertView.getTag();
        }

        listViewHolder.name.setText(discount.getProduct().getName());
        listViewHolder.originalPrice.setText(String.format("$%.2f", discount.getOriginalPrice()));
        // Creating a line on top of the original price
        listViewHolder.originalPrice.setPaintFlags(listViewHolder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        listViewHolder.discountedPrice.setText(String.format("$%.2f", discount.getDiscountedPrice()));

        int resourceID = context.getResources().getIdentifier(
                "item_" + discount.getProduct().getProductId(),
                "drawable",
                "tamirmo.shopper"
        );

        listViewHolder.image.setImageResource(resourceID);

        if(discount.isPersonal()){
            listViewHolder.personalImage.setVisibility(View.VISIBLE);
        }else{
            listViewHolder.personalImage.setVisibility(View.INVISIBLE);
        }

        // Return the completed view to render on screen
        return convertView;
    }

    private static class DiscountViewHolder{
        ImageView image;
        TextView name;
        TextView originalPrice;
        TextView discountedPrice;
        ImageView personalImage;
    }
}
