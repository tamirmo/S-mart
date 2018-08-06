package tamirmo.shopper;

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

import smart.data.CartItem;
import smart.data.Discount;
import tamirmo.shopper.cart.CartHandler;
import tamirmo.shopper.cart.CartOptionsFragment;
import tamirmo.shopper.cart.IOnItemPickedListener;
import tamirmo.shopper.connection.ServerConnectionHandler;
import tamirmo.shopper.discounts.DiscountsFragment;
import tamirmo.shopper.discounts.DiscountsHandler;
import tamirmo.shopper.discounts.IOnDiscountAlert;
import tamirmo.shopper.settings.NotificationSettingsHandler;
import tamirmo.shopper.settings.SettingsFragment;

/**
 * Created by Tamir on 09/06/2018.
 * The main activity, after the user logs in.
 * This activity holds the navigation view.
 */

public class MainActivity extends AppCompatActivity implements IOnItemPickedListener, IOnDiscountAlert {
    private static final int NOTIFICATION_ID = 1500;

    // Views
    private CartOptionsFragment cartOptionsFragment;
    private DiscountsFragment discountsFragment;
    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cartOptionsFragment = new CartOptionsFragment();
        discountsFragment = new DiscountsFragment();
        settingsFragment = new SettingsFragment();

        replaceFragment(cartOptionsFragment);

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {

                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = cartOptionsFragment;

                        switch (item.getItemId()) {
                            case R.id.navigation_shopping_cart:
                                // Moving to the main cart fragment:
                                selectedFragment = cartOptionsFragment;
                                break;
                            case R.id.navigation_settings:
                                selectedFragment = settingsFragment;
                                break;
                            case R.id.navigation_discount:
                                selectedFragment = discountsFragment;
                                break;
                        }
                        replaceFragment(selectedFragment);
                        return true;
                    }
                });

        // Loading the notification's settings at first to use later
        NotificationSettingsHandler.getInstance().loadSettings(this);

        CartHandler.getInstance().addItemPickedListener(this);

        DiscountsHandler.getInstance().setOnDiscountAlertListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServerConnectionHandler.getInstance().stop();
        CartHandler.getInstance().removeItemPickedListener(this);

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
    }
}
