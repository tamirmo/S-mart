package tamirmo.shopper.Departments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.relex.circleindicator.CircleIndicator;
import tamirmo.shopper.Database.Class.Department;
import tamirmo.shopper.FragmentWithUpdates;
import tamirmo.shopper.MainActivity;
import tamirmo.shopper.Products.ProductsFragment;
import tamirmo.shopper.R;


public class DepartmentsFragment extends FragmentWithUpdates implements IOnDepartmentClicked {

    // Menu Fragment
    private ProductsFragment departmentProductsFragment;

    // Class attributes
    private DepartmentsPagerAdapter departmentsPagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gets all needed data to create the adapter
        List<Department> departments = ((MainActivity)getActivity()).getDepartments();
        departmentsPagerAdapter = new DepartmentsPagerAdapter(getActivity(), departments);
        departmentsPagerAdapter.setDepartmentClickedListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.choose_department_fragment, container, false);

        // Sets the adapter
        ViewPager viewPager = rootView.findViewById(R.id.departments_view_pager);
        CircleIndicator indicator = rootView.findViewById(R.id.departments_indicator);
        viewPager.setAdapter(departmentsPagerAdapter);
        indicator.setViewPager(viewPager);

        return rootView;
    }

    @Override
    public void onDepartmentClicked(int departmentId) {
        // Creates the fragment for the products displaying
        departmentProductsFragment = new ProductsFragment();
        departmentProductsFragment.setDepartmentId(""+departmentId);

        // Replaces current fragment
        ((MainActivity)getActivity()).replaceFragment(departmentProductsFragment, R.string.unique_chain_transaction,true);
    }

    @Override
    public void updateFragment() {
        departmentsPagerAdapter.updateData();
    }
}
