package tamirmo.shopper.discounts;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import tamirmo.shopper.R;

/**
 * Created by Tamir on 17/06/2018.
 * A fragment displaying all discounts (personal and general).
 */

public class DiscountsFragment extends Fragment {

    private DiscountsListAdapter discountsListAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.discounts_fragment, container, false);

        // Initializing and setting the list adapter:
        discountsListAdapter = new DiscountsListAdapter(getContext());
        ListView discountsListView = rootView.findViewById(R.id.discounts_list);
        discountsListView.setAdapter(discountsListAdapter);

        return rootView;
    }
}
