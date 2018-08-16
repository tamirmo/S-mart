package tamirmo.employee.Settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tamirmo.employee.FragmentWithUpdates;
import tamirmo.employee.MainActivity;
import tamirmo.employee.R;
import tamirmo.employee.Settings.NotificationSettings.NotificationSettingsFragment;
import tamirmo.employee.Settings.UserSettings.ChangeEmailFragment;
import tamirmo.employee.Settings.UserSettings.ChangePasswordFragment;

public class SettingsFragment extends FragmentWithUpdates implements View.OnClickListener{

    // Class Fragments
    private ChangeEmailFragment changeEmailFragment;
    private ChangePasswordFragment changePasswordFragment;
    private NotificationSettingsFragment notificationSettingsFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.settings_fragment, container, false);

        rootView.findViewById(R.id.change_email_btn).setOnClickListener(this);
        rootView.findViewById(R.id.change_password_btn).setOnClickListener(this);
        rootView.findViewById(R.id.system_settings_btn).setOnClickListener(this);

        changeEmailFragment = new ChangeEmailFragment();
        changePasswordFragment = new ChangePasswordFragment();
        notificationSettingsFragment = new NotificationSettingsFragment();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        Fragment selectedFragment = null;

        switch (v.getId()){
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
            ((MainActivity) getActivity()).replaceFragment(selectedFragment, R.string.different_chain_transaction,true);
        }
    }

    @Override
    public void updateFragment() {
        // It doesn't need to do anything
    }
}
