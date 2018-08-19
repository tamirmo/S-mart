package tamirmo.shopper.MainMenu;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tamirmo.shopper.FragmentWithUpdates;
import tamirmo.shopper.MainActivity;
import tamirmo.shopper.R;
import tamirmo.shopper.CartList.CartListFragment;
import tamirmo.shopper.Map.MapFragment;

public class MainMenuFragment extends FragmentWithUpdates implements View.OnClickListener {

    // Menu Fragments
    private CartListFragment cartListFragment;
    private MapFragment mapFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Creates menu fragments
        cartListFragment = new CartListFragment();
        mapFragment = new MapFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.cart_options_fragment, container, false);

        // Sets a click listener on fragment's widgets
        rootView.findViewById(R.id.cart_edit_btn).setOnClickListener(this);
        rootView.findViewById(R.id.start_shopping_btn).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Fragment fragmentToMoveTo = null;

        // Checks which fragment should be displayed
        switch (v.getId()) {
            case R.id.cart_edit_btn:
                fragmentToMoveTo = cartListFragment;
                ((MainActivity) getActivity()).replaceFragment(fragmentToMoveTo, R.string.unique_chain_transaction, true);
                break;
            case R.id.start_shopping_btn:
                fragmentToMoveTo = mapFragment;
                ((MainActivity) getActivity()).replaceFragment(fragmentToMoveTo, R.string.unique_chain_transaction, true);
                break;
        }
    }

    @Override
    public void updateFragment() {
        // It doesn't need to do anything
    }
}
