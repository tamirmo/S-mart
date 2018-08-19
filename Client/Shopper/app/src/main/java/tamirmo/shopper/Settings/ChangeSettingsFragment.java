package tamirmo.shopper.Settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tamirmo.shopper.FragmentWithUpdates;
import tamirmo.shopper.MainActivity;
import tamirmo.shopper.R;
import tamirmo.shopper.Settings.NotificationSettings.NotificationSettingsFragment;
import tamirmo.shopper.Settings.UserSettings.ChangeCreditFragment;
import tamirmo.shopper.Settings.UserSettings.ChangeEmailFragment;
import tamirmo.shopper.Settings.UserSettings.ChangePasswordFragment;

public class ChangeSettingsFragment extends FragmentWithUpdates implements View.OnClickListener{

    // Class Fragments
    private ChangeEmailFragment changeEmailFragment;
    private ChangeCreditFragment changeCreditFragment;
    private ChangePasswordFragment changePasswordFragment;
    private NotificationSettingsFragment notificationSettingsFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changeEmailFragment = new ChangeEmailFragment();
        changeCreditFragment = new ChangeCreditFragment();
        changePasswordFragment = new ChangePasswordFragment();
        notificationSettingsFragment = new NotificationSettingsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.settings_fragment, container, false);

        rootView.findViewById(R.id.change_credit_btn).setOnClickListener(this);
        rootView.findViewById(R.id.change_email_btn).setOnClickListener(this);
        rootView.findViewById(R.id.change_password_btn).setOnClickListener(this);
        rootView.findViewById(R.id.system_settings_btn).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Fragment selectedFragment = null;

        switch (v.getId()){
            case R.id.change_credit_btn:
                selectedFragment = changeCreditFragment;
                break;
            case R.id.change_email_btn:
                selectedFragment = changeEmailFragment;
                break;
            case R.id.change_password_btn:
                selectedFragment = changePasswordFragment;
                break;
            case R.id.system_settings_btn:
                selectedFragment = notificationSettingsFragment;
                break;
        }
        if(selectedFragment != null) {
            ((MainActivity) getActivity()).replaceFragment(selectedFragment,  R.string.unique_chain_transaction,true);
        }
    }

    @Override
    public void updateFragment() {
        // It doesn't need to do anything
    }
}
