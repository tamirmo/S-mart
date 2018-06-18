package tamirmo.shopper.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import tamirmo.shopper.R;
import tamirmo.shopper.connection.ServerConnectionHandler;

/**
 * Created by Tamir on 17/06/2018.
 * The fragment for changing password.
 */

public class ChangePasswordFragment extends AbsChangeSettingsFragmentBase  {

    // Views:
    private EditText oldPasswordEditText;
    private EditText newPasswordEditText;

    @Override
    public View provideFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.change_password_fragment, parent, false);

        oldPasswordEditText = rootView.findViewById(R.id.old_password_edit_text);
        newPasswordEditText = rootView.findViewById(R.id.edit_password_edit_text);

        return rootView;
    }

    @Override
    public void onConfirmClicked() {
        // Changing the password, registering to the ChangeSettings event to get the response
        ServerConnectionHandler.getInstance().sendChangePassword(oldPasswordEditText.getText().toString(),
                newPasswordEditText.getText().toString());
    }

    @Override
    public int getSettingChangedDialogMessageResId() {
        return R.string.password_changed_dialog_text;
    }

    @Override
    public int getSettingWrongDialogMessageResId() {
        return R.string.password_change_dialog_err;
    }
}
