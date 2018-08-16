package tamirmo.employee.Settings.NotificationSettings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import tamirmo.employee.Database.Class.UserSettings;
import tamirmo.employee.FragmentWithUpdates;
import tamirmo.employee.MainActivity;
import tamirmo.employee.R;


public class NotificationSettingsFragment extends FragmentWithUpdates implements CompoundButton.OnCheckedChangeListener {

    // Class attributes
    private UserSettings userSettings;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.notification_settings_fragment, container, false);

        userSettings = ((MainActivity) getActivity()).getUserSettings();

        // Getting the views
        SwitchCompat vibrationSwitch = rootView.findViewById(R.id.vibrations_switch);
        SwitchCompat activeDiscountsSwitch = rootView.findViewById(R.id.active_discounts_switch);
        SwitchCompat soundSwitch = rootView.findViewById(R.id.sound_switch);


        // Setting the current values
        vibrationSwitch.setChecked(userSettings.getToVibrate());
        activeDiscountsSwitch.setChecked(userSettings.getToNotify());
        soundSwitch.setChecked(userSettings.getToSound());

        // Registering to the checked events (to save changes):
        vibrationSwitch.setOnCheckedChangeListener(this);
        activeDiscountsSwitch.setOnCheckedChangeListener(this);
        soundSwitch.setOnCheckedChangeListener(this);

        return rootView;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.vibrations_switch:
                userSettings.setToVibrate(isChecked);
                break;
            case R.id.active_discounts_switch:
                userSettings.setToNotify(isChecked);
                break;
            case R.id.sound_switch:
                userSettings.setToSound(isChecked);
                break;
        }
    }

    @Override
    public void updateFragment() {
        // It doesn't need to do anything
    }
}