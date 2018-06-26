package tamirmo.employee;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import smart.data.CartItem;
import smart.data.Discount;
import tamirmo.employee.connection.ServerConnectionHandler;
import tamirmo.employee.settings.SettingsFragment;

/**
 * Created by Tamir on 09/06/2018.
 * The main activity, after the user logs in.
 * This activity holds the navigation view.
 */

// TODO: Handle item missing or needs replacement
public class MainActivity extends AppCompatActivity /*implements IOnItemPickedListener */{
    private static final int NOTIFICATION_ID = 1500;

    // Views
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        settingsFragment = new SettingsFragment();

        // TODO: Call replace with the list of tasks fragment
        //replaceFragment(cartOptionsFragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = settingsFragment;

                        switch (item.getItemId()) {
                            case R.id.navigation_tasks:
                                // TODO: Tasks fragment
                                break;
                            case R.id.navigation_settings:
                                selectedFragment = settingsFragment;
                                break;
                        }
                        replaceFragment(selectedFragment);
                        return true;
                    }
                });

        // TODO: Load
        // Loading the notification's settings at first to use later
        //NotificationSettingsHandler.getInstance().loadSettings(this);
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

    // TODO: Handle item missing or needs replacement alert
    /*@Override
    public void onItemPicked(CartItem cartItem) {
        // When an item is picked, checking if the shopper has it's
        // settings as vibrate or sound on and acting accordingly:

        if(NotificationSettingsHandler.getInstance().isSoundOn()){
            MediaPlayer mp = MediaPlayer.create(this, R.raw.pick);
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
    }

    @Override
    public void onDiscountAlert(final Discount discountToAlertOf) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Checking if the shopper allows active notification
                // (modifiable on the notification settings fragment)
                if(NotificationSettingsHandler.getInstance().isActiveNotificationOn()) {
                    // Displaying a notification of the discount:

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "SMART_CHANNEL")
                            .setContentText(String.format("$%.2f instead of $%.2f", discountToAlertOf.getDiscountedPrice(), discountToAlertOf.getOriginalPrice()))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                    // Displaying an icon according to the discount type:
                    if (discountToAlertOf.isPersonal()) {
                        mBuilder.setSmallIcon(R.drawable.discount_menu_icon)
                                .setContentTitle(discountToAlertOf.getProduct().getName() + " Discount!");
                    } else {
                        mBuilder.setSmallIcon(R.drawable.personal_discount)
                                .setContentTitle(discountToAlertOf.getProduct().getName() + " Personal discount!");
                    }

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
        });
    }*/
}
