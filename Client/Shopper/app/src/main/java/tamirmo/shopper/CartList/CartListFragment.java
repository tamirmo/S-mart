package tamirmo.shopper.CartList;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import tamirmo.shopper.Database.Class.CartItem;
import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.Database.Class.Sale;
import tamirmo.shopper.Departments.DepartmentsFragment;
import tamirmo.shopper.FragmentWithUpdates;
import tamirmo.shopper.MainActivity;
import tamirmo.shopper.R;

public class CartListFragment extends FragmentWithUpdates implements View.OnClickListener, IOnListChange {

    // Class widgets
    private ListView currItemsListView;
    private TextView totalSumTextView;
    private TextView currentSumTextView;

    // Menu Fragments
    private DepartmentsFragment departmentsFragment;

    // Class attributes
    private CartListAdapter dataAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gets all needed data to create the adapter
        List<Product> products = ((MainActivity) getActivity()).getProducts();
        List<Discount> discounts = ((MainActivity) getActivity()).getDiscounts();
        List<Sale> sales = ((MainActivity) getActivity()).getSales();
        List<CartItem> cart = ((MainActivity) getActivity()).getCart();
        dataAdapter = new CartListAdapter(getActivity(), cart, products, discounts, sales);
        dataAdapter.setDataChangedListener(this);

        // Creates a department fragment
        departmentsFragment = new DepartmentsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.cart_items_fragment, container, false);

        // Sets the adapter
        currItemsListView = rootView.findViewById(R.id.cart_list_view);
        currItemsListView.setAdapter(dataAdapter);

        currentSumTextView = rootView.findViewById(R.id.cart_current_sum_text_view);
        totalSumTextView = rootView.findViewById(R.id.cart_total_sum_text_view);
        updateSum(dataAdapter.calculateSum(R.string.total_sum_type), dataAdapter.calculateSum(R.string.current_sum_type));

        // Sets a click listener fragment's widgets
        rootView.findViewById(R.id.add_item_btn).setOnClickListener(this);
        rootView.findViewById(R.id.add_item_text_view).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // Moves to the departments fragment
            case R.id.add_item_btn:
            case R.id.add_item_text_view:
                ((MainActivity) getActivity()).replaceFragment(departmentsFragment, R.string.unique_chain_transaction, true);
                break;
        }
    }

    @Override
    public void updateSum(double totalSum, double currentSum) {
        // Updates the sum of the new cart
        String cartSum = String.format("$%.2f", totalSum);
        totalSumTextView.setText(cartSum);

        cartSum = String.format("$%.2f", currentSum);
        currentSumTextView.setText(cartSum);
    }

    @Override
    public void updateFragment() {
        dataAdapter.updateData();
    }
}
