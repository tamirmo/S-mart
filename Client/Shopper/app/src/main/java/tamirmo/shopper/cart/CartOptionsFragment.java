package tamirmo.shopper.cart;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tamirmo.shopper.R;
import tamirmo.shopper.cart.navigation.NavigationFragment;

/**
 * Created by Tamir on 09/06/2018.
 * A fragment with all cart menu options.
 */

public class CartOptionsFragment extends Fragment implements View.OnClickListener {
    // The fragment to move to when edit cart is pressed
    private CartItemsFragment cartItemsFragment;

    // The fragment to move to when start shopping is pressed
    private NavigationFragment navigationFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.cart_options_fragment, container, false);

        rootView.findViewById(R.id.cart_edit_btn).setOnClickListener(this);
        rootView.findViewById(R.id.start_shopping_btn).setOnClickListener(this);

        cartItemsFragment = new CartItemsFragment();
        // The fragment is not a receipt when moving from this fragment
        cartItemsFragment.setIsReceipt(false);

        navigationFragment = new NavigationFragment();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Fragment fragmentToMoveTo = null;

        switch (v.getId()){
            case R.id.cart_edit_btn:
                fragmentToMoveTo = cartItemsFragment;
                break;
            case R.id.start_shopping_btn:
                CartHandler.getInstance().startNavigation();
                fragmentToMoveTo = navigationFragment;
                break;
        }
        moveToFragment(fragmentToMoveTo);
    }

    private void moveToFragment(Fragment fragmentToMoveTo){
        if(fragmentToMoveTo != null && !fragmentToMoveTo.isAdded() && getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, fragmentToMoveTo);
            transaction.addToBackStack(fragmentToMoveTo.getClass().getSimpleName());
            transaction.commit();
        }
    }
}
