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
 * The fragment for changing email.
 */

public class ChangeEmailFragment extends AbsChangeSettingsFragmentBase {

    // Views:
    private EditText emailEditText;

    @Override
    public View provideFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.change_email_fragment, parent, false);

        emailEditText = rootView.findViewById(R.id.edit_email_edit_text);

        return rootView;
    }

    @Override
    public void onConfirmClicked() {
        // Changing the email
        ServerConnectionHandler.getInstance().sendChangeEmail(emailEditText.getText().toString());
    }

    @Override
    public int getSettingChangedDialogMessageResId() {
        return R.string.email_changed_dialog_text;
    }

    @Override
    public int getSettingWrongDialogMessageResId() {
        return R.string.email_change_dialog_err;
    }
}
