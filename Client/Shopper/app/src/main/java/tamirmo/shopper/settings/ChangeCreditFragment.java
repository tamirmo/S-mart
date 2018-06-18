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
 * The fragment for changing credit.
 */

public class ChangeCreditFragment extends AbsChangeSettingsFragmentBase {

    // Views:
    private EditText creditEditText;

    @Override
    public View provideFragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.change_credit_fragment, parent, false);

        creditEditText = rootView.findViewById(R.id.edit_credit_edit_text);

        return rootView;
    }

    @Override
    public void onConfirmClicked() {
        ServerConnectionHandler.getInstance().sendChangeCredit(creditEditText.getText().toString());
    }

    @Override
    public int getSettingChangedDialogMessageResId() {
        return R.string.credit_changed_dialog_text;
    }

    @Override
    public int getSettingWrongDialogMessageResId() {
        return R.string.credit_change_dialog_err;
    }

}
