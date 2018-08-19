package tamirmo.shopper.Products;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import tamirmo.shopper.Database.Class.CartItem;
import tamirmo.shopper.Database.Class.Department;
import tamirmo.shopper.Database.Class.Discount;
import tamirmo.shopper.Database.Class.Product;
import tamirmo.shopper.FragmentWithUpdates;
import tamirmo.shopper.MainActivity;
import tamirmo.shopper.R;


// Displays a department products
public class ProductsFragment extends FragmentWithUpdates implements TextWatcher {

    // Class widgets
    private EditText searchTextView;
    private ListView departmentsProductsListView;

    // Class adapter
    private ProductsAdapter departmentProductsAdapter;

    // Class attributes
    private String departmentId;
    private String departmentName;

    // Sets which department's products should be displayed
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Department> departments = ((MainActivity) getActivity()).getDepartments();
        // Sets the headline of the fragment
        for (Department department : departments) {
            if (department.getId().equals(departmentId)) {
                departmentName = department.getName();
                break;
            }
        }

        // Creating the adapter for the list
        List<Product> products = ((MainActivity) getActivity()).getProducts();
        List<CartItem> cart = ((MainActivity) getActivity()).getCart();
        List<Discount> discounts = ((MainActivity) getActivity()).getDiscounts();
        departmentProductsAdapter = new ProductsAdapter(getActivity(), departmentId, products, discounts, cart);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.department_products_fragment, container, false);
        searchTextView = rootView.findViewById(R.id.search_department_edit_text);

        // Sets department name
        ((TextView) (rootView.findViewById(R.id.department_title_text_view))).setText(departmentName);

        departmentsProductsListView = rootView.findViewById(R.id.departments_products_list);
        departmentsProductsListView.setAdapter(departmentProductsAdapter);

        searchTextView.addTextChangedListener(this);

        return rootView;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Updating the adapter to show only filtered products
        departmentProductsAdapter.onSearchTermEntered(searchTextView.getText().toString());
    }

    // These are here because TextWatcher, we don't need them
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void updateFragment() {
        departmentProductsAdapter.updateData();
    }
}
