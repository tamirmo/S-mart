package tamirmo.shopper.Settings.NotificationSettings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import tamirmo.shopper.Database.Class.UserSettings;
import tamirmo.shopper.FragmentWithUpdates;
import tamirmo.shopper.MainActivity;
import tamirmo.shopper.R;
import tamirmo.shopper.SettingsFragment;


public class NotificationSettingsFragment extends FragmentWithUpdates implements CompoundButton.OnCheckedChangeListener, SettingsFragment {

    // Class attributes
    private UserSettings userSettings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userSettings = ((MainActivity) getActivity()).getUserSettings();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.notification_settings_fragment, container, false);

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
        switch (buttonView.getId()) {
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
