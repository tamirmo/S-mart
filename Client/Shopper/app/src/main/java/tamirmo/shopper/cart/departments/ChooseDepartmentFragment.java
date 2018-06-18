package tamirmo.shopper.cart.departments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.relex.circleindicator.CircleIndicator;
import tamirmo.shopper.R;
import tamirmo.shopper.cart.departments.products.DepartmentProductsFragment;

/**
 * Created by Tamir on 13/06/2018.
 * A fragment responsible for choosing department to choose items for.
 */

public class ChooseDepartmentFragment extends Fragment implements IOnDepartmentClicked {

    private DepartmentsPagerAdapter departmentsPagerAdapter;
    private DepartmentProductsFragment departmentProductsFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.choose_department_fragment, container, false);

        // Creating the fragment for the products displaying
        departmentProductsFragment = new DepartmentProductsFragment();

        // Creating the adapter and registering for the department clicked event:
        departmentsPagerAdapter = new DepartmentsPagerAdapter(getActivity());
        departmentsPagerAdapter.setDepartmentClickedListener(this);

        // Setting the adapter to the view pager
        ViewPager viewPager = rootView.findViewById(R.id.departments_view_pager);
        CircleIndicator indicator = rootView.findViewById(R.id.departments_indicator);
        viewPager.setAdapter(departmentsPagerAdapter);
        indicator.setViewPager(viewPager);

        return rootView;
    }

    @Override
    public void onDepartmentClicked(int departmentId) {
        // Setting the department id and moving to the products fragment:

        departmentProductsFragment.setDepartmentId(departmentId);

        if(!departmentProductsFragment.isAdded()) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, departmentProductsFragment);
            transaction.addToBackStack(departmentProductsFragment.getClass().getSimpleName());
            transaction.commit();
        }
    }
}
