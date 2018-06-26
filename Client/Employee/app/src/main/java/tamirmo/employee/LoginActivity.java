package tamirmo.employee;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import tamirmo.employee.connection.IServerLoginEventsListener;
import tamirmo.employee.connection.ServerConnectionHandler;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener, IServerLoginEventsListener {

    // Views
    private EditText emailEditText;
    private EditText passwordEditText;
    private FrameLayout mainFrame;
    private LinearLayout logInLayout;
    private LinearLayout loadingLayout;

    // For testing purpose
    private String serverIp = "10.0.0.27";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Displaying the layout of the activity
        setContentView(R.layout.activity_login);

        // Registering for the log in button click
        (findViewById(R.id.log_in_btn)).setOnClickListener(this);

        // Getting the views from the layout:
        mainFrame = findViewById(R.id.main_layout);
        logInLayout = findViewById(R.id.login_layout);
        loadingLayout = findViewById(R.id.loading_layout);
        passwordEditText = findViewById(R.id.password_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);

        // Registering for the long click for testing purpose (used for setting server ip)
        emailEditText.setOnLongClickListener(this);

        // Registering to server events to get the login result
        ServerConnectionHandler.getInstance().setServerMessagesListener(this);

        // At first showing log in screen:
        showLogInLayout();
    }

    private void showLoading(){
        mainFrame.bringChildToFront(loadingLayout);
        logInLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
    }

    private void showLogInLayout(){
        mainFrame.bringChildToFront(logInLayout);
        logInLayout.setVisibility(View.VISIBLE);
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.log_in_btn){

            showLoading();

            // Starting a task to initiate server connection and send a log in request
            new LogInTask().execute();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("Server Ip");
        alertDialog.setMessage("Enter IP");

        final EditText input = new EditText(LoginActivity.this);
        input.setText(serverIp);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input); // uncomment this line

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        serverIp = input.getText().toString();
                        dialog.cancel();
                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
        return false;
    }

    @Override
    public void onLogInRequestResult(final boolean isSuccessful) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!isSuccessful){
                    showLogInFailedDialog(R.string.log_in_err);

                    // Anyways the loading is over
                    showLogInLayout();
                }
            }});
    }

    @Override
    public void onServerResultTimeout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showLogInFailedDialog(R.string.connection_err);
                // Loading is over
                showLogInLayout();
            }
        });
    }

    @Override
    public void onAllDataReceived() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Moving to the Main activity after the user has logged in:

                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);

                // TODO: Decide if we are sticking with finish (different from wireframe)
                // Loading is over
                //showLogInLayout();
                finish();
            }
        });
    }

    /**
     * Displaying a regular dialog with ok button.
     * @param errorStringId integer, the resource id of the header to display.
     */
    private void showLogInFailedDialog(int errorStringId){
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(getResources().getString(errorStringId))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    /**
     * A task initializing the server connection and sending a log in request
     */
    private class LogInTask extends AsyncTask<Void, Void, Void> {

        private String email;
        private String password;

        LogInTask() {
            this.email = emailEditText.getText().toString();
            this.password = passwordEditText.getText().toString();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Starting the connection with the configured server ip
            ServerConnectionHandler.getInstance().start(serverIp);
            // Sending a log in request
            ServerConnectionHandler.getInstance().sendLogInRequest(email,
                    password);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }
}
