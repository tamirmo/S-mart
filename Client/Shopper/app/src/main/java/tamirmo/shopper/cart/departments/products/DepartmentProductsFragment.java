package tamirmo.shopper.cart.departments.products;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import smart.data.Department;
import smart.data.Database;
import tamirmo.shopper.R;
import tamirmo.shopper.cart.CartHandler;
import tamirmo.shopper.cart.departments.ChooseDepartmentFragment;
import tamirmo.shopper.connection.ICartUpdated;

/**
 * Created by Tamir on 14/06/2018.
 * A fragment responsible for displaying department's products.
 * (inflating department_products_fragment).
 * Called after the user chooses a department in {@link ChooseDepartmentFragment}
 */

public class DepartmentProductsFragment extends Fragment implements TextWatcher, ICartUpdated {

    // Views
    private EditText searchTextView;
    private ListView departmentsProductsListView;

    private int departmentId;
    private DepartmentProductsAdapter departmentProductsAdapter;

    public void setDepartmentId(int departmentId){
        this.departmentId = departmentId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.department_products_fragment, container, false);
        searchTextView = rootView.findViewById(R.id.search_department_edit_text);

        // Finding the name of the department chosing and setting the headline
        for(Department department : Database.getInstance().getDepartments()){
            if(department.getId() == departmentId){
                ((TextView)(rootView.findViewById(R.id.department_title_text_view))).setText(department.getName());
                break;
            }
        }

        // Creating the adapter for the list and setting it in the list
        departmentProductsAdapter = new DepartmentProductsAdapter(getActivity(), departmentId);
        departmentsProductsListView = rootView.findViewById(R.id.departments_products_list);
        departmentsProductsListView.setAdapter(departmentProductsAdapter);

        searchTextView.addTextChangedListener(this);

        // Registering to cart changes to refresh list
        CartHandler.getInstance().addCartUpdatedListener(this);

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // No need to listen to cart changes anymore
        CartHandler.getInstance().removeCartUpdatedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable s) {
        // Updating the adapter to show only filtered products
        departmentProductsAdapter.onSearchTermEntered(searchTextView.getText().toString());
    }

    @Override
    public void onCartUpdated() {
        if(getActivity() != null) {

            // Updating the adapter
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    departmentProductsAdapter.notifyDataSetChanged();
                }
            });
        }
    }

}
