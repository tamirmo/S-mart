package tamirmo.shopper.Settings.UserSettings;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import tamirmo.shopper.Database.Class.Shopper;
import tamirmo.shopper.FragmentWithUpdates;
import tamirmo.shopper.MainActivity;
import tamirmo.shopper.R;
import tamirmo.shopper.SettingsFragment;

public class ChangeCreditFragment extends FragmentWithUpdates implements View.OnClickListener, SettingsFragment {
    // Class constants
    private static final String CREDENTIAL_TYPE = "card";

    // Class widgets
    private EditText creditEditText;
    private FrameLayout mainFrame;
    private LinearLayout changeSettingLayout;
    private LinearLayout loadingLayout;

    // Class attribute
    Shopper account;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        account = ((MainActivity) getActivity()).getAccount();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.change_credit_fragment, container, false);

        // Getting the widgets of the fragment
        mainFrame = rootView.findViewById(R.id.main_layout);
        changeSettingLayout = rootView.findViewById(R.id.change_setting_layout);
        loadingLayout = rootView.findViewById(R.id.loading_layout);
        creditEditText = rootView.findViewById(R.id.edit_credit_edit_text);

        // Registers confirm button to onClick event - Starts set request
        rootView.findViewById(R.id.confirm_btn).setOnClickListener(this);

        // Shows first the login screen
        showChangeSettingLayout();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.confirm_btn) {
            SetTask task = new SetTask();
            task.execute();
        }
    }

    // Shows change setting screen
    private void showChangeSettingLayout() {
        mainFrame.bringChildToFront(changeSettingLayout);
        changeSettingLayout.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
    }

    // Shows loading screen
    private void showLoadingLayout() {
        mainFrame.bringChildToFront(loadingLayout);
        changeSettingLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    // Clears text fields to start fresh
    public void clearFields() {
        creditEditText.setText("");
    }

    @Override
    public void updateFragment() {
        // It doesn't need to do anything
    }

    // Responsible for set requests and responses
    // AsyncTask is used because main thread can't be used for communication with the server
    private class SetTask extends AsyncTask<Void, Void, Integer> {

        private String creditCard;

        // Gets all needed information for a set request, and switch to loading screen
        protected void onPreExecute() {
            // Gets email from its widget
            creditCard = creditEditText.getText().toString();

            // Shows the loading screen while the message is being sent to the server
            showLoadingLayout();
        }

        // Sends a set request to the server
        protected Integer doInBackground(Void... voids) {
            try {
                if (((MainActivity) getActivity()).setRequest(CREDENTIAL_TYPE, creditCard)) {
                    account.setCreditCard(creditCard);
                    return R.string.credit_changed_dialog_text;
                }
                return R.string.credit_change_dialog_err;
            } catch (Exception e) {
                return Integer.getInteger(e.getMessage());
            }
        }

        // Deals with the result of the set request
        protected void onPostExecute(Integer result) {
            showChangeSettingLayout();

            ((MainActivity) getActivity()).popUpMessageDialog(getResources().getString(result));
        }
    }
}
