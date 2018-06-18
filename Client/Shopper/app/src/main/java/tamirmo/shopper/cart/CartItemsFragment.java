package tamirmo.shopper.cart;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import tamirmo.shopper.R;
import tamirmo.shopper.cart.departments.ChooseDepartmentFragment;
import tamirmo.shopper.connection.ICartUpdated;

/**
 * Created by User on 13/06/2018.
 * A fragment displaying the user's cart with all it's products.
 */

public class CartItemsFragment extends Fragment implements View.OnClickListener, ICartUpdated {

    // Views
    private ListView currItemsListView;

    private CartItemsAdapter itemsAdapter;
    private ChooseDepartmentFragment chooseDepartmentFragment;
    private TextView totalSunTextView;
    // Indicating if the fragment is displayed as receipt
    // (true - coming from finish click on the map fragment,
    // false - coming from CartOptionsFragment)
    private boolean isReceipt;

    public void setIsReceipt(boolean isReceipt){
        this.isReceipt = isReceipt;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.cart_items_fragment, container, false);

        itemsAdapter = new CartItemsAdapter(getActivity());

        totalSunTextView = rootView.findViewById(R.id.cart_total_sum_text_view);
        currItemsListView = rootView.findViewById(R.id.cart_list_view);
        currItemsListView.setAdapter(itemsAdapter);

        rootView.findViewById(R.id.add_item_btn).setOnClickListener(this);
        rootView.findViewById(R.id.add_item_text_view).setOnClickListener(this);

        chooseDepartmentFragment = new ChooseDepartmentFragment();

        // Registering to changes in the cart
        CartHandler.getInstance().addCartUpdatedListener(this);

        // Updating the sum of the new cart:
        setTotalSum();

        TextView cartHeaderTextView = rootView.findViewById(R.id.cart_header_text_view);

        // Hiding/showing the add cart items view according to the isReceipt value
        if(isReceipt){
            rootView.findViewById(R.id.add_cart_item_Layout).setVisibility(View.GONE);

            // Setting the header accordingly
            cartHeaderTextView.setText(getContext().getResources().getString(R.string.receipt_header_text));
        }else{
            rootView.findViewById(R.id.add_cart_item_Layout).setVisibility(View.VISIBLE);
            // Setting the header accordingly
            cartHeaderTextView.setText(getContext().getResources().getString(R.string.curr_cart_header_text));
        }

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // No need to listen to cart changes anymore
        CartHandler.getInstance().removeCartUpdatedListener(this);

        // After reviewing the receipt
        if(isReceipt){
            // A new cart is to be created
            CartHandler.getInstance().clearCart();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.add_item_btn:
            case R.id.add_item_text_view:
                if(!chooseDepartmentFragment.isAdded()) {
                    if(getActivity() != null) {
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.add(R.id.container, chooseDepartmentFragment);
                        transaction.addToBackStack(chooseDepartmentFragment.getClass().getSimpleName());
                        transaction.commit();
                    }
                }
                break;
        }
    }

    @Override
    public void onCartUpdated() {
        if(getActivity() != null) {
            // Updating the adapter
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    itemsAdapter.notifyDataSetChanged();
                }
            });
        }

        // Updating the sum of the new cart:
        setTotalSum();
    }

    private void setTotalSum(){
        // Updating the sum of the new cart:
        String cartSum = String.format("%.2f $", CartHandler.getInstance().getCartSum());
        totalSunTextView.setText(cartSum);
    }
}
