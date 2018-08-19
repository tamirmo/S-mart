package tamirmo.shopper.Discounts;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.FragmentWithUpdates;
import tamirmo.shopper.MainActivity;
import tamirmo.shopper.R;


public class DiscountsFragment extends FragmentWithUpdates {

    // Class attribute
    private DiscountsListAdapter discountsListAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gets needed data
        List<Discount> discounts = ((MainActivity) getActivity()).getDiscounts();
        List<Product> products = ((MainActivity) getActivity()).getProducts();

        // Builds the list adapter, which controls how each slot look like
        discountsListAdapter = new DiscountsListAdapter(getContext(), discounts, products);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.discounts_fragment, container, false);

        // Building the list
        ListView discountsListView = rootView.findViewById(R.id.discounts_list);
        discountsListView.setAdapter(discountsListAdapter);

        return rootView;
    }

    @Override
    public void updateFragment() {
        discountsListAdapter.updateData();
    }
}
