package tamirmo.shopper.settings;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import tamirmo.shopper.R;

/**
 * Created by Tamir on 17/06/2018.
 * The main settings fragment (holds the options button for the settings)
 */

public class SettingsFragment extends Fragment implements View.OnClickListener{

    private ChangeEmailFragment changeEmailFragment;
    private ChangeCreditFragment changeCreditFragment;
    private ChangePasswordFragment changePasswordFragment;
    private NotificationSettingsFragment notificationSettingsFragment;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.settings_fragment, container, false);

        rootView.findViewById(R.id.change_credit_btn).setOnClickListener(this);
        rootView.findViewById(R.id.change_email_btn).setOnClickListener(this);
        rootView.findViewById(R.id.change_password_btn).setOnClickListener(this);
        rootView.findViewById(R.id.system_settings_btn).setOnClickListener(this);

        changeEmailFragment = new ChangeEmailFragment();
        changeCreditFragment = new ChangeCreditFragment();
        changePasswordFragment = new ChangePasswordFragment();
        notificationSettingsFragment = new NotificationSettingsFragment();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.change_credit_btn:
                moveToFragment(changeCreditFragment);
                break;
            case R.id.change_email_btn:
                moveToFragment(changeEmailFragment);
                break;
            case R.id.change_password_btn:
                moveToFragment(changePasswordFragment);
                break;
            case R.id.system_settings_btn:
                moveToFragment(notificationSettingsFragment);
                break;
        }
    }

    private void moveToFragment(Fragment fragmentToMoveTo){
        if(fragmentToMoveTo != null && !fragmentToMoveTo.isAdded() && getActivity() != null) {
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.container, fragmentToMoveTo);
            transaction.addToBackStack(fragmentToMoveTo.getClass().getSimpleName());
            transaction.commit();
        }
    }
}
