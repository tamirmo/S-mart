package tamirmo.shopper.cart.navigation;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import smart.data.CartItem;
import smart.data.SmartDataManager;
import tamirmo.shopper.R;
import tamirmo.shopper.cart.CartHandler;
import tamirmo.shopper.cart.CartItemsFragment;
import tamirmo.shopper.connection.ICartUpdated;

/**
 * Created by Tamir on 16/06/2018.
 * A fragment for the navigation.
 * Containing the map, next item, finish and show list buttons.
 */

public class NavigationFragment extends Fragment implements View.OnClickListener, ICartUpdated {

    // Views:
    private TextView nextItemAmountTextView;
    private TextView nextItemTextTextView;
    private GridView mapGrid;

    private NavigationMapAdapter navigationMapAdapter;

    // The fragment to move to when edit cart is pressed
    private CartItemsFragment cartItemsFragment;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.navigation_fragment, container, false);

        navigationMapAdapter = new NavigationMapAdapter(getContext());

        nextItemAmountTextView = rootView.findViewById(R.id.next_item_amount_text_view);
        nextItemTextTextView = rootView.findViewById(R.id.next_item_text_text_view);

        // Registering to the click events of the buttons:
        rootView.findViewById(R.id.show_list_btn).setOnClickListener(this);
        rootView.findViewById(R.id.finish_navigation_btn).setOnClickListener(this);
        mapGrid = rootView.findViewById(R.id.map_grid);
        mapGrid.setAdapter(navigationMapAdapter);

        CartHandler.getInstance().addCartUpdatedListener(this);

        // Refreshing for the first time
        refreshNextCartItem();

        mapGrid.setNumColumns(SmartDataManager.MAP_COLS_COUNT);

        cartItemsFragment = new CartItemsFragment();
        // The fragment is a receipt when moving from this fragment
        cartItemsFragment.setIsReceipt(true);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CartHandler.getInstance().removeCartUpdatedListener(this);
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction;
        switch (v.getId()){
            case R.id.show_list_btn:

                // Moving to cart items fragment (showing current cart)
                cartItemsFragment.setIsReceipt(false);
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container, cartItemsFragment);
                transaction.addToBackStack(cartItemsFragment.getClass().getSimpleName());
                transaction.commit();
                break;
            case R.id.finish_navigation_btn:
                // Deleting all unpicked items
                CartHandler.getInstance().endShopping();

                // Moving to cart items fragment (showing receipt)
                cartItemsFragment.setIsReceipt(true);
                transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.add(R.id.container, cartItemsFragment);
                transaction.addToBackStack(cartItemsFragment.getClass().getSimpleName());
                transaction.commit();
                break;
        }
    }

    @Override
    public void onCartUpdated() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                refreshNextCartItem();

                // Refreshing the map
                navigationMapAdapter.notifyDataSetChanged();
            }
        });
    }

    private void refreshNextCartItem(){
        CartItem cartItem = CartHandler.getInstance().getNextCartItem();
        if(cartItem == null){
            String noNextItem = getContext().getResources().getString(R.string.no_next_item);
            nextItemTextTextView.setText(noNextItem);
            nextItemAmountTextView.setText("");
        }else{
            nextItemTextTextView.setText(cartItem.getProduct().getName());
            nextItemAmountTextView.setText(cartItem.getAmount() + " X");
        }
    }
}
