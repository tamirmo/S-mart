package tamirmo.employee.Map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import java.util.List;

import tamirmo.employee.Database.Class.EmptyTask;
import tamirmo.employee.Database.Class.ExpiredTask;
import tamirmo.employee.Database.Class.MisplacedTask;
import tamirmo.employee.Database.Class.Product;
import tamirmo.employee.Database.Class.UserLocation;
import tamirmo.employee.FragmentWithUpdates;
import tamirmo.employee.MainActivity;
import tamirmo.employee.R;

public class MapFragment extends FragmentWithUpdates {

    // Class constants
    public static final int MAP_ROWS_COUNT = 14;
    public static final int MAP_COLS_COUNT = 10;

    // Class widgets
    private TextView nextItemTextView;
    private GridView mapGrid;

    // Class grid adapter
    private MapGridAdapter mapGridAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Product> products = ((MainActivity) getActivity()).getProducts();
        List<EmptyTask> emptyProducts = ((MainActivity) getActivity()).getEmptyProducts();
        List<ExpiredTask> expiredProducts = ((MainActivity) getActivity()).getExpiredProducts();
        List<MisplacedTask> misplacedProducts = ((MainActivity) getActivity()).getMisplacedProducts();
        UserLocation userLocation = ((MainActivity) getActivity()).getUserLocation();

        mapGridAdapter = new MapGridAdapter(getContext(), MAP_ROWS_COUNT, MAP_COLS_COUNT, products, emptyProducts, expiredProducts, misplacedProducts, userLocation);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.navigation_fragment, container, false);

        nextItemTextView = rootView.findViewById(R.id.next_item_text_view);

        mapGrid = rootView.findViewById(R.id.map_grid);
        mapGrid.setNumColumns(MAP_COLS_COUNT);
        mapGrid.setAdapter(mapGridAdapter);

        updateNextItem();

        return rootView;
    }

    // Updates the fragment
    public void updateFragment() {
        mapGridAdapter.updateData();
        updateNextItem();
    }

    // Selects next Item
    private void updateNextItem() {
        String nextItem = mapGridAdapter.getNextTaskText();
        nextItemTextView.setText(nextItem);
    }
}
