package tamirmo.employee.Settings.UserSettings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import tamirmo.employee.Database.Class.Employee;
import tamirmo.employee.FragmentWithUpdates;
import tamirmo.employee.MainActivity;
import tamirmo.employee.R;

public class ChangePasswordFragment extends FragmentWithUpdates implements View.OnClickListener {
    // Class constants
    private static final String CREDENTIAL_TYPE = "password";

    // Class widgets
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;
    private FrameLayout mainFrame;
    private LinearLayout changeSettingLayout;
    private LinearLayout loadingLayout;

    // Class attribute
    Employee account;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.change_password_fragment, container, false);

        account = ((MainActivity)getActivity()).getAccount();

        // Getting the widgets of the fragment
        mainFrame = rootView.findViewById(R.id.main_layout);
        changeSettingLayout = rootView.findViewById(R.id.change_setting_layout);
        loadingLayout = rootView.findViewById(R.id.loading_layout);
        oldPasswordEditText = rootView.findViewById(R.id.old_password_edit_text);
        newPasswordEditText = rootView.findViewById(R.id.edit_password_edit_text);

        // Registers confirm button to onClick event - Starts set request
        rootView.findViewById(R.id.confirm_btn).setOnClickListener(this);

        // Shows first the login screen
        showChangeSettingLayout();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.confirm_btn){
           SetTask task = new SetTask();
           task.execute();
        }
    }

    // Shows change setting screen
    private void showChangeSettingLayout(){
        mainFrame.bringChildToFront(changeSettingLayout);
        changeSettingLayout.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
    }

    // Shows loading screen
    private void showLoadingLayout(){
        mainFrame.bringChildToFront(loadingLayout);
        changeSettingLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    // Clears text fields to start fresh
    public void clearFields() {
        oldPasswordEditText.setText("");
        newPasswordEditText.setText("");
    }

    @Override
    public void updateFragment() {
        // It doesn't need to do anything
    }

    // Responsible for set requests and responses
    // AsyncTask is used because main thread can't be used for communication with the server
    private class SetTask extends AsyncTask<Void, Void, Integer> {
        private String oldPassword;
        private String newPassword;

        // Gets all needed information for a set request, and switch to loading screen
        protected void onPreExecute(){
            // Gets passwords from their widgets
            oldPassword = oldPasswordEditText.getText().toString();
            newPassword = newPasswordEditText.getText().toString();

            // Shows the loading screen while the message is being sent to the server
            showLoadingLayout();
        }

        // Sends a set request to the server
        protected Integer doInBackground(Void... voids){
            try {
                if(oldPassword.equals(account.getPassword())) {
                    if (((MainActivity) getActivity()).setRequest(CREDENTIAL_TYPE, newPassword)) {
                        account.setPassword(newPassword);
                        return R.string.password_changed_dialog_text;
                    }
                }
                return R.string.password_change_dialog_err;
            }catch(Exception e) {
                return Integer.getInteger(e.getMessage());
            }
        }

        // Deals with the result of the set request
        protected void onPostExecute(Integer result) {
            showChangeSettingLayout();

            ((MainActivity)getActivity()).popUpMessageDialog(getResources().getString(result));
        }
    }
}
