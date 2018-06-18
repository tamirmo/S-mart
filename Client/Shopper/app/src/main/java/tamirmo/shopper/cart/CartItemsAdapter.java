package tamirmo.shopper.cart;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import smart.data.CartItem;
import tamirmo.shopper.R;

/**
 * Created by Tamir on 13/06/2018.
 * An adapter for the user's current cart (displaying all products chosen with price and amount).
 */

public class CartItemsAdapter extends ArrayAdapter<CartItem> implements View.OnClickListener{

    private Context context;

    CartItemsAdapter(Context context) {
        super(context, R.layout.cart_item_layout, new CartItem[0]);
        this.context = context;
    }

    @Override
    public int getCount() {
        if(CartHandler.getInstance().getCart() == null){
            return 0;
        }
        return CartHandler.getInstance().getCart().size();
    }

    @Nullable
    @Override
    public CartItem getItem(int position) {
        return CartHandler.getInstance().getCart().get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Get the data item for this position
        CartItem cartItem = getItem(position);

        // Creating the view holder and the view for the row
        ProductAdapterUtilities.ProductViewObjects productViewObjects = ProductAdapterUtilities.createViewHolder(convertView, parent, context);
        ProductAdapterUtilities.ProductItemViewHolder viewHolder = productViewObjects.productItemViewHolder;

        ProductAdapterUtilities.setProductData(cartItem.getProduct(), viewHolder, context);

        viewHolder.price.setText(String.format("$ %.2f", cartItem.getItemPrice()));
        viewHolder.amount.setText(String.valueOf(cartItem.getAmount()));
        viewHolder.plus.setOnClickListener(this);
        viewHolder.minus.setOnClickListener(this);
        viewHolder.plus.setTag(position);
        viewHolder.minus.setTag(position);

        if(!cartItem.isPicked()) {
            viewHolder.plus.setVisibility(View.VISIBLE);
            viewHolder.minus.setVisibility(View.VISIBLE);
            viewHolder.amount.setVisibility(View.VISIBLE);
            viewHolder.pickedImage.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.plus.setVisibility(View.INVISIBLE);
            viewHolder.minus.setVisibility(View.INVISIBLE);
            viewHolder.amount.setVisibility(View.INVISIBLE);
            viewHolder.pickedImage.setVisibility(View.VISIBLE);
        }

        // Return the completed view to render on screen
        return productViewObjects.convertView;
    }

    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        CartItem dataModel=(CartItem)object;

        ProductAdapterUtilities.onPlusMinusClicked(v, dataModel, context);

        this.notifyDataSetChanged();
    }
}
