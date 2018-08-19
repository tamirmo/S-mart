package tamirmo.shopper.Discounts;

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

import java.util.HashMap;
import java.util.List;

import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.R;


public class DiscountsListAdapter extends ArrayAdapter<Discount> {

    private Context context;
    private LayoutInflater layoutinflater;

    // Class attribute
    private List<Discount> discounts;
    private List<Product> products;
    private HashMap<String, Product> productMap; // Key = productID, value = product

    DiscountsListAdapter(@NonNull Context context, List<Discount> discounts, List<Product> products) {
        super(context, R.layout.discount_list_item_layout);
        layoutinflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;

        this.discounts = discounts;
        this.products = products;
        productMap = getProductMap(products);
    }

    // Returns a HashMap from a List of products
    public HashMap<String, Product> getProductMap(List<Product> productList) {
        HashMap<String, Product> productMap = new HashMap<String, Product>();

        for (Product product : products) {
            productMap.put(product.getProductId(), product);
        }

        return productMap;
    }

    // The items each slot in the listView holds/presents
    private static class DiscountViewHolder {
        ImageView image;
        TextView name;
        TextView originalPrice;
        TextView discountedPrice;
        ImageView personalImage;
    }

    @Override
    public int getCount() {
        return discounts.size();
    }

    @Nullable
    @Override
    public Discount getItem(int position) {
        return discounts.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Discount discount = getItem(position);
        Product product = productMap.get(discount.getProductId());

        DiscountViewHolder listViewHolder;
        if (convertView == null) {
            listViewHolder = new DiscountViewHolder();
            convertView = layoutinflater.inflate(R.layout.discount_list_item_layout, parent, false);
            listViewHolder.image = convertView.findViewById(R.id.discount_item_image);
            listViewHolder.name = convertView.findViewById(R.id.discount_item_name);
            listViewHolder.originalPrice = convertView.findViewById(R.id.discount_original_price);
            listViewHolder.discountedPrice = convertView.findViewById(R.id.discount_discounted_price);
            listViewHolder.personalImage = convertView.findViewById(R.id.personal_discount_icon);

            convertView.setTag(listViewHolder);
        } else {
            listViewHolder = (DiscountViewHolder) convertView.getTag();
        }


        listViewHolder.originalPrice.setText(String.format("$%s", discount.getNormalPrice()));
        // Creating a line on top of the original price
        listViewHolder.originalPrice.setPaintFlags(listViewHolder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        listViewHolder.discountedPrice.setText(String.format("$%s", discount.getDiscountedPrice()));

        if (product != null) {
            listViewHolder.name.setText(product.getName());
            int resourceID = context.getResources().getIdentifier(
                    "item_" + product.getProductId(),
                    "drawable",
                    "tamirmo.shopper"
            );
            listViewHolder.image.setImageResource(resourceID);
        }

        // Shows Personal Discount icon
        if (!discount.getShopperId().equals(Discount.GENERAL_DISCOUNT_SHOPPER_ID)) {
            listViewHolder.personalImage.setVisibility(View.VISIBLE);
        } else {
            listViewHolder.personalImage.setVisibility(View.INVISIBLE);
        }

        // Return the completed view to render on screen
        return convertView;
    }

    // Updates the class data
    public void updateData() {
        productMap = getProductMap(products);

        // Changes the view based on the new data
        this.notifyDataSetChanged();
    }
}