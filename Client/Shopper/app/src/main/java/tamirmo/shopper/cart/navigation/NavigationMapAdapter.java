package tamirmo.shopper.cart.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import smart.data.CartItem;
import smart.data.Discount;
import smart.data.SmartDataManager;
import tamirmo.shopper.R;
import tamirmo.shopper.cart.CartHandler;
import tamirmo.shopper.discounts.DiscountsHandler;

/**
 * Created by Tamir on 16/06/2018.
 * An adapter for the map layout that is represented by a grid.
 */

public class NavigationMapAdapter extends BaseAdapter implements View.OnClickListener {
    private LayoutInflater layoutinflater;
    private Context context;

    NavigationMapAdapter(Context context) {
        this.context = context;
        layoutinflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        int rows = SmartDataManager.MAP_ROWS_COUNT;
        int columns = SmartDataManager.MAP_COLS_COUNT;
        return rows * columns;
    }

    @Override
    public Object getItem(int position) {
        return CartHandler.getInstance().getItemByMapPosition(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NavigationMapViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new NavigationMapViewHolder();
            convertView = layoutinflater.inflate(R.layout.navigation_map_cell_layout, parent, false);
            listViewHolder.discount = convertView.findViewById(R.id.discount_map_image);
            listViewHolder.item = convertView.findViewById(R.id.item_map_image);
            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (NavigationMapViewHolder)convertView.getTag();
        }

        // Getting the cart items and the next cart
        CartItem cartItem = (CartItem) getItem(position);
        CartItem nextCartItem = CartHandler.getInstance().getNextCartItem();

        // If there is a cart item for the
        if(cartItem != null){
            int itemImageResourceId = R.drawable.cart_item_location;
            listViewHolder.item.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));

            if(cartItem.isPicked()){
                itemImageResourceId = R.drawable.picked_item_v_green;
                listViewHolder.item.setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
            }
            // Checking if we are in the next item's view
            else if(nextCartItem != null && nextCartItem == cartItem){
                itemImageResourceId = R.drawable.cart_item_selected;
            }

            listViewHolder.item.setImageResource(itemImageResourceId);

            // Hiding the discount image and displaying the item image
            listViewHolder.item.setVisibility(View.VISIBLE);
            listViewHolder.discount.setVisibility(View.INVISIBLE);
        }else{
            // Getting the discount in this position
            Discount positionDiscount = DiscountsHandler.getInstance().getDiscountByMapCoordinates(position);

            // Stetting the discount icon visibility according to the current location's discount
            if(positionDiscount != null){
                listViewHolder.discount.setVisibility(View.VISIBLE);
            }else{
                listViewHolder.discount.setVisibility(View.INVISIBLE);
            }

            // Anyways hiding the cart item icon cause there is no cart item in this position
            listViewHolder.item.setVisibility(View.INVISIBLE);
        }

        // TODO: This is for debug, needs to be deleted:
        //listViewHolder.item.setVisibility(View.VISIBLE);

        // Registering the on click of the items and setting the items positions as tag
        // (for later use when clicking on an item)
        listViewHolder.item.setOnClickListener(this);
        listViewHolder.discount.setOnClickListener(this);
        listViewHolder.item.setTag(position);
        listViewHolder.discount.setTag(position);

        return convertView;
    }

    @Override
    public void onClick(View v) {
        // TODO: This is for debug, needs to be deleted:
        int row = (int)v.getTag() / SmartDataManager.MAP_COLS_COUNT;
        int column = (int)v.getTag() % SmartDataManager.MAP_COLS_COUNT;
        Toast.makeText(context, row + " , " + column, Toast.LENGTH_LONG).show();


        // The position of the images have their positions as tag
        int itemClickedPosition = (int)v.getTag();

        CartItem clickedCartItem = (CartItem) getItem(itemClickedPosition);

        // Checking if the user clicked on a cart item
        if(clickedCartItem != null){
            // Displaying a dialog with the item details
            CartItemMapDialog dialog = new CartItemMapDialog(context, clickedCartItem);
            dialog.show();
        }else {
            // Getting the discount in this position
            Discount positionDiscount = DiscountsHandler.getInstance().getDiscountByMapCoordinates(itemClickedPosition);

            // If the user has clicked on a discount
            if (positionDiscount != null) {
                // Displaying a dialog with the discount details and an option to add to cart
                DiscountMapDialog dialog = new DiscountMapDialog(context, positionDiscount);
                dialog.show();
            }
        }
    }

    private static class NavigationMapViewHolder{
        ImageView discount;
        ImageView item;
    }

}
