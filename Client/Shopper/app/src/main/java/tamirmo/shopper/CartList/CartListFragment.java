package tamirmo.shopper.CartList;

import android.os.Bundle;
import android.support.annotation.NonNull;
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

    // Menu Fragments
    private DepartmentsFragment departmentsFragment;

    // Class attributes
    private CartListAdapter dataAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.cart_items_fragment, container, false);

        // Gets all needed data to create the adapter
        List<Product> products = ((MainActivity)getActivity()).getProducts();
        List<Discount> discounts = ((MainActivity)getActivity()).getDiscounts();
        List<Sale> sales = ((MainActivity)getActivity()).getSales();
        List<CartItem> cart = ((MainActivity)getActivity()).getCart();
        dataAdapter = new CartListAdapter(getActivity(), cart, products, discounts, sales);
        dataAdapter.setDataChangedListener(this);

        // Sets the adapter
        currItemsListView = rootView.findViewById(R.id.cart_list_view);
        currItemsListView.setAdapter(dataAdapter);

        totalSumTextView = rootView.findViewById(R.id.cart_total_sum_text_view);
        updateTotalSum(dataAdapter.calculateTotalSum());

        // Sets a click listener fragment's widgets
        rootView.findViewById(R.id.add_item_btn).setOnClickListener(this);
        rootView.findViewById(R.id.add_item_text_view).setOnClickListener(this);

        // Creates a department fragment
        departmentsFragment = new DepartmentsFragment();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            // Moves to the departments fragment
            case R.id.add_item_btn:
            case R.id.add_item_text_view:
                ((MainActivity)getActivity()).replaceFragment(departmentsFragment, getString(R.string.third_menu), getString(R.string.third_menu), true);
                break;
        }
    }

    @Override
    public void updateTotalSum(double sum) {
        // Updates the sum of the new cart
        String cartSum = String.format("$%.2f", sum);
        totalSumTextView.setText(cartSum);
    }

    @Override
    public void updateFragment() {
        dataAdapter.updateData();
    }
}
