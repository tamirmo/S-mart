package tamirmo.shopper.Map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

import tamirmo.shopper.CartList.CartListFragment;
import tamirmo.shopper.Database.Class.CartItem;
import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.Database.Class.Sale;
import tamirmo.shopper.Database.Class.UserLocation;
import tamirmo.shopper.FragmentWithUpdates;
import tamirmo.shopper.MainActivity;
import tamirmo.shopper.R;
import tamirmo.shopper.Receipt.ReceiptFragment;


public class MapFragment extends FragmentWithUpdates implements View.OnClickListener {

    // Views:
    private TextView nextItemTextView;
    private GridView mapGrid;

    public static final int MAP_ROWS_COUNT = 14;
    public static final int MAP_COLS_COUNT = 10;
    public static final int STARTING_USER_LOCATION_X = MAP_COLS_COUNT/2;
    public static final int STARTING_USER_LOCATION_Y = MAP_ROWS_COUNT-1;

    private MapGridAdapter mapGridAdapter;

    // The fragment to move to when edit cart is pressed
    private CartListFragment cartItemsFragment;
    private ReceiptFragment receiptFragment;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.navigation_fragment, container, false);

        List<Discount> discounts = ((MainActivity)getActivity()).getDiscounts();
        List<Sale> sales = ((MainActivity)getActivity()).getSales();
        List<Product> products = ((MainActivity)getActivity()).getProducts();
        List<CartItem> cart = ((MainActivity)getActivity()).getCart();
        UserLocation userLocation = ((MainActivity)getActivity()).getUserLocation();
        mapGridAdapter = new MapGridAdapter(getContext(), MAP_ROWS_COUNT, MAP_COLS_COUNT, discounts, sales, products, cart, userLocation);

        nextItemTextView = rootView.findViewById(R.id.next_item_text_view);

        // Registering to the click events of the buttons:
        rootView.findViewById(R.id.show_list_btn).setOnClickListener(this);
        rootView.findViewById(R.id.finish_navigation_btn).setOnClickListener(this);

        mapGrid = rootView.findViewById(R.id.map_grid);
        mapGrid.setNumColumns(MAP_COLS_COUNT);
        mapGrid.setAdapter(mapGridAdapter);

        cartItemsFragment = new CartListFragment();
        receiptFragment = new ReceiptFragment();

        updateNextItem();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction;
        switch (v.getId()){
            case R.id.show_list_btn:
                // Moving to cart items fragment (showing current cart)
                ((MainActivity)getActivity()).replaceFragment(cartItemsFragment, getString(R.string.second_menu),getString(R.string.second_menu), true);
                break;
            case R.id.finish_navigation_btn:
                // Moving to receipt fragment (showing final cart)
                ((MainActivity)getActivity()).replaceFragment(receiptFragment, getString(R.string.second_menu),getString(R.string.second_menu), false);
                receiptFragment.sendReceipt();
                break;
        }
    }

    // Updates the fragment
    public void updateFragment(){
        mapGridAdapter.updateData();
        updateNextItem();
    }

    // Selects next Item and
    private void updateNextItem(){
        String nextItem = mapGridAdapter.getNextItemText();
        nextItemTextView.setText(nextItem);
    }
}
