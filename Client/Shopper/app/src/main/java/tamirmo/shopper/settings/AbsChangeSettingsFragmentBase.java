package tamirmo.shopper.settings;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import tamirmo.shopper.R;
import tamirmo.shopper.connection.IServerChangeSettingsEventsListener;
import tamirmo.shopper.connection.ServerConnectionHandler;

/**
 * Created by Tamir on 18/06/2018.
 * Abstract fragment holding the common features of all settings fragments.
 */

public abstract class AbsChangeSettingsFragmentBase extends Fragment implements View.OnClickListener, IServerChangeSettingsEventsListener {

    private FrameLayout mainFrame;
    private LinearLayout changeSettingLayout;
    private LinearLayout loadingLayout;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState)
    {
        View view = provideFragmentView(inflater,parent,savedInstanseState);
        view.findViewById(R.id.confirm_btn).setOnClickListener(this);
        mainFrame = view.findViewById(R.id.main_layout);
        changeSettingLayout = view.findViewById(R.id.change_setting_layout);
        loadingLayout = view.findViewById(R.id.loading_layout);

        showChangeSettingLayout();

        // Registering to the ChangeSettings event to get the response
        ServerConnectionHandler.getInstance().setServerChangeSettingsEvents(this);

        return view;
    }

    protected void showLoading(){
        mainFrame.bringChildToFront(loadingLayout);
        changeSettingLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    private void showChangeSettingLayout(){
        mainFrame.bringChildToFront(changeSettingLayout);
        changeSettingLayout.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.confirm_btn) {

            // The sub classes call the server code here (network calls)
            // that is not allowed on the main thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    onConfirmClicked();
                }
            }).start();

            showLoading();
        }
    }

    @Override
    public void onSettingsChange() {
        showResponseDialog(getSettingChangedDialogMessageResId());
    }

    @Override
    public void onSettingsWrongRes() {
        showResponseDialog(getSettingWrongDialogMessageResId());
    }

    @Override
    public void onServerResultTimeout() {
        showResponseDialog(R.string.connection_err);
    }

    /**
     * Displaying a regular dialog with ok button.
     * @param messageStringRedId integer, the resource id of the header to display.
     */
    private void showResponseDialog(final int messageStringRedId){
        if(getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                    }
                    builder.setTitle(getResources().getString(messageStringRedId))
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                    // Loading is over
                    showChangeSettingLayout();
                }
            });
        }
    }

    public abstract View provideFragmentView(LayoutInflater inflater,ViewGroup parent, Bundle savedInstanceState);
    public abstract void onConfirmClicked();
    public abstract int getSettingChangedDialogMessageResId();
    public abstract int getSettingWrongDialogMessageResId();
}
