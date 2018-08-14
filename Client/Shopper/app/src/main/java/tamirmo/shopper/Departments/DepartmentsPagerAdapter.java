package tamirmo.shopper.Departments;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import tamirmo.shopper.Database.Class.Department;
import tamirmo.shopper.R;

public class DepartmentsPagerAdapter extends PagerAdapter implements View.OnClickListener {

    // Class listener
    private IOnDepartmentClicked departmentClickedListener;

    // Class attributes
    private Context context;
    private List<Department> departments;

    // Sets a data listener on the adapter
    public void setDepartmentClickedListener(IOnDepartmentClicked departmentClickedListener) {
        this.departmentClickedListener = departmentClickedListener;
    }

    // Class Builder
    public DepartmentsPagerAdapter(Context context, List<Department> departments) {
        this.context = context;
        this.departments = departments;
    }

    @Override public int getCount() {
        return departments.size();
    }

    @Override public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override public void destroyItem(@NonNull ViewGroup view, int position, @NonNull Object object) {
        view.removeView((View) object);
    }

    @NonNull
    @Override public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        // Getting the department from the list od departments in the manager
        Department department = departments.get(position);

        // Inflating the department view:
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.department_layout, collection, false);

        // Getting the views of the inflated layout
        ImageView departmentImage = layout.getRootView().findViewById(R.id.department_image);
        TextView departmentName = layout.getRootView().findViewById(R.id.department_name_text_view);

        departmentName.setText(department.getName());

        // Each department has an image in the resources with it's id "department_1"
        int resourceID = context.getResources().getIdentifier(
                "department_" + department.getId(),
                "drawable",
                "tamirmo.shopper"
        );

        departmentImage.setImageResource(resourceID);

        collection.addView(layout);

        // Registering for the click event
        departmentImage.setOnClickListener(this);
        departmentName.setOnClickListener(this);
        departmentImage.setTag(department.getId());
        departmentName.setTag(department.getId());

        return layout;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.department_image || v.getId() == R.id.department_name_text_view){
            // Sending the fragment the department that clicked
            departmentClickedListener.onDepartmentClicked(Integer.parseInt((String)v.getTag()));
        }
    }

    // Updates the class data
    public void updateData(){
        // Changes the view based on the new data
        this.notifyDataSetChanged();
    }
}
