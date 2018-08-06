package tamirmo.employee;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import tamirmo.employee.connection.ServerConnectionHandler;
import tamirmo.employee.settings.NotificationSettingsHandler;
import tamirmo.employee.settings.SettingsFragment;
import tamirmo.employee.tasks.IOnTaskAdded;
import tamirmo.employee.tasks.Task;
import tamirmo.employee.tasks.TasksHandler;
import tamirmo.employee.tasks.TasksOptionsFragment;

/**
 * Created by Tamir on 09/06/2018.
 * The main activity, after the user logs in.
 * This activity holds the navigation view.
 */

public class MainActivity extends AppCompatActivity implements IOnTaskAdded /*implements IOnItemPickedListener */{
    private static final int NOTIFICATION_ID = 1500;

    // Views
    private SettingsFragment settingsFragment;
    private TasksOptionsFragment tasksOptionsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TasksHandler.getInstance().setOnTaskAddedListener(this);

        settingsFragment = new SettingsFragment();
        tasksOptionsFragment = new TasksOptionsFragment();

        // First fragment is the options
        replaceFragment(tasksOptionsFragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = settingsFragment;

                        switch (item.getItemId()) {
                            case R.id.navigation_tasks:
                                selectedFragment = tasksOptionsFragment;
                                break;
                            case R.id.navigation_settings:
                                selectedFragment = settingsFragment;
                                break;
                        }
                        replaceFragment(selectedFragment);
                        return true;
                    }
                });

        // Loading the notification's settings at first to use later
        NotificationSettingsHandler.getInstance().loadSettings(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServerConnectionHandler.getInstance().stop();

        // Clearing notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.cancel(NOTIFICATION_ID);
    }

    private void replaceFragment(Fragment fragmentToReplaceIn){
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragmentToReplaceIn);
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        // Only if there are no more fragments to pop from the back stack
        if (count == 0) {
            // Checking if the user really wants to exit
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getResources().getString(R.string.exit_dialog_title))
                    .setMessage(getResources().getString(R.string.exit_dialog_msg))
                    .setPositiveButton(getResources().getString(R.string.exit_dialog_positive_text),
                            new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(getResources().getString(R.string.exit_dialog_negative_text), null)
                    .show();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onTaskAdded(Task addedTask) {
        // When a task is added, checking if the employee has it's
        // settings as vibrate or sound on and acting accordingly:

        if(NotificationSettingsHandler.getInstance().isSoundOn()){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.task_added);
            mp.start();
        }

        if(NotificationSettingsHandler.getInstance().isVibrationOn()){
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if(v != null) {
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
            }
        }

        // Displaying a notification of the task:

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "SMART_CHANNEL")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Displaying a suitable title (with the product's name and task type):
        String notificationTitle = null;

        if(addedTask.getTaskType() == Task.TaskType.EXPIRED){
            notificationTitle = addedTask.getProductOfTask().getName() + " Expired!";
        }else if(addedTask.getTaskType() == Task.TaskType.OUT_OF_STOCK) {
            notificationTitle = addedTask.getProductOfTask().getName() + " Out of stock!";
        }

        mBuilder.setSmallIcon(R.drawable.tasks_menu_icon)
                .setContentTitle(notificationTitle);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);

            NotificationChannel channel = new NotificationChannel("SMART_CHANNEL",
                    "S-mart channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            if(notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
                notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
            }
        }else{
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
            notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        }
    }
}
