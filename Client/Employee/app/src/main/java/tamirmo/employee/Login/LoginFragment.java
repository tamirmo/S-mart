package tamirmo.employee.Login;

import android.os.AsyncTask;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import tamirmo.employee.FragmentWithUpdates;
import tamirmo.employee.MainActivity;
import tamirmo.employee.R;

public class LoginFragment extends FragmentWithUpdates implements View.OnClickListener, View.OnLongClickListener {

    // Class Constants
    private static final String BASIC_SERVER_IP = "10.0.0.27";

    // Class Widgets
    private EditText emailEditText;
    private EditText passwordEditText;
    private FrameLayout mainFrame;
    private LinearLayout loginLayout;
    private LinearLayout loadingLayout;

    // Class attribute
    private String serverIP;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets a basic server IP-Address
        serverIP = BASIC_SERVER_IP;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_fragment, container, false);

        // Gets the widgets on the fragment
        mainFrame = rootView.findViewById(R.id.main_layout);
        loginLayout = rootView.findViewById(R.id.login_layout);
        loadingLayout = rootView.findViewById(R.id.loading_layout);
        passwordEditText = rootView.findViewById(R.id.password_edit_text);
        emailEditText = rootView.findViewById(R.id.email_edit_text);

        // Sets a click listener on login button
        rootView.findViewById(R.id.login_btn).setOnClickListener(this);

        // Sets a click listener on email edit text widget
        // Our server IP-Address is dynamic, and also helps with the testing process
        emailEditText.setOnLongClickListener(this);

        // Shows login screen
        showLoginLayout();

        return rootView;
    }

    // Shows login screen
    private void showLoginLayout() {
        mainFrame.bringChildToFront(loginLayout);
        loginLayout.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
    }

    // Shows loading screen
    private void showLoadingLayout() {
        mainFrame.bringChildToFront(loadingLayout);
        loginLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onLongClick(View v) {
        // Builds a dialog widget
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(v.getContext());
        alertDialog.setTitle("Server IP-Address");
        alertDialog.setMessage("Enter IP-Address");

        // Sets a server IP-Address
        final EditText serverEditText = new EditText(v.getContext());
        serverEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        serverEditText.setText(serverIP);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        serverEditText.setLayoutParams(lp);
        alertDialog.setView(serverEditText);

        // Sets a new server IP-Address from the user
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        serverIP = serverEditText.getText().toString();
                        dialog.cancel();
                    }
                });

        // Closes the dialog widget
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        // Shows the IP-Address request dialog
        alertDialog.show();

        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.login_btn) {
            // Starts a login task on a different thread
            // It can't be on the UI main thread, the operating system will kill it because UI stuck
            LoginTask task = new LoginTask();
            task.execute();
        }
    }

    @Override
    public void updateFragment() {
        // It doesn't need to do anything
    }

    // AsyncTask is used because main thread can't be used for communication with the server
    private class LoginTask extends AsyncTask<Void, Void, String> {
        private String email;
        private String password;

        // Gets all needed information for a login request, and switch to loading layout
        protected void onPreExecute() {
            // Gets all needed information for a login request
            email = emailEditText.getText().toString();
            password = passwordEditText.getText().toString();

            // Shows the loading screen while the message is being sent to the server
            showLoadingLayout();
        }

        // Sends a login request to the server
        // Can't change UI because it isn't the main thread
        protected String doInBackground(Void... voids) {
            String result = "";

            try {
                if (((MainActivity) getActivity()).loginRequest(serverIP, email, password)) {
                    ((MainActivity) getActivity()).downloadAllData();
                    result = getString(R.string.login_success);
                } else
                    result = getString(R.string.login_error);
            } catch (Exception e) {
                result = e.getMessage();
            }

            return result;
        }

        // Deals with the result of the login request
        protected void onPostExecute(String result) {
            // Changes back to login screen
            showLoginLayout();

            if (!result.equals(getString(R.string.login_success))) {
                // Notifies an error occurred
                ((MainActivity) getActivity()).popUpMessageDialog(result);
            } else {
                passwordEditText.setText("");
                emailEditText.setText("");

                // Starts main application
                ((MainActivity) getActivity()).start();
            }
        }
    }
}
