package com.optimove.android.optimovemobilesdk;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.optimove.android.Optimove;

import com.optimove.android.optimovemobilesdk.databinding.ActivityMainBinding;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    private static final int WRITE_EXTERNAL_PERMISSION_REQUEST_CODE = 169;

    // index of the selected bottom navigation
    // only used for animating the toolbar title
//    private int pageNavIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideKeyboard(this);

//        Toolbar myToolbar = (Toolbar) binding.myToolbar;
//        myToolbar.setTitle("Optimove SDK");

//        TextView titleTv = binding.toolbarTitle;

//        Animation slideLeftAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_right_to_left_anim);
//        Animation slideRightAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left_to_right_anim);

        // BottomNavigationView navView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
//            titleTv.setText(destination.getLabel());

//            switch (destination.getLabel().toString()) {
//                case "Inbox":
//
//                    break;
//                default:
//
//                    break;
//            }

//            int nextPageNavIndex = 0;

//            switch (destination.getLabel().toString()) {
//                case "Home":
//                    nextPageNavIndex = 1;
//                    break;
//                case "Dashboard":
//                    nextPageNavIndex = 2;
//                    break;
//                case "Inbox":
//                    nextPageNavIndex = 3;
//                    break;
//                case "Profile":
//                    nextPageNavIndex = 4;
//                    break;
//                default:
//                    nextPageNavIndex = 0;
//                    break;
//            }
//            if (nextPageNavIndex > pageNavIndex) {
//                titleTv.startAnimation(slideLeftAnimation);
//            } else if (nextPageNavIndex < pageNavIndex) {
//                titleTv.startAnimation(slideRightAnimation);
//            }

            // after the navigation, record the index of the fragment
//            pageNavIndex = nextPageNavIndex;
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION_REQUEST_CODE);
        }

        //deferred deep links
        Optimove.getInstance().seeIntent(getIntent(), savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //deferred deep links
        Optimove.getInstance().seeInputFocus(hasFocus);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        //deferred deep links
        Optimove.getInstance().seeIntent(intent);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}
