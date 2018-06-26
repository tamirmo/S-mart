package tamirmo.employee.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import tamirmo.employee.R;

/**
 * Created by Tamir on 18/06/2018.
 * A fragment for editing the notification settings (vibration, sounds).
 */

public class NotificationSettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener{
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.notification_settings_fragment, container, false);

        // Getting the views
        SwitchCompat vibrationSwitch = rootView.findViewById(R.id.vibrations_switch);
        SwitchCompat soundSwitch = rootView.findViewById(R.id.sound_switch);

        // Registering to the checked events (to save changes):
        vibrationSwitch.setOnCheckedChangeListener(this);
        soundSwitch.setOnCheckedChangeListener(this);

        // Setting the current values
        vibrationSwitch.setChecked(NotificationSettingsHandler.getInstance().isVibrationOn());
        soundSwitch.setChecked(NotificationSettingsHandler.getInstance().isSoundOn());

        return rootView;

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.vibrations_switch:
                NotificationSettingsHandler.getInstance().setVibrationOn(isChecked, getActivity());
                break;
            case R.id.sound_switch:
                NotificationSettingsHandler.getInstance().setSoundOn(isChecked, getActivity());
                break;
        }

    }
}
