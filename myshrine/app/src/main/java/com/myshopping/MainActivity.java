package com.myshopping;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.myshopping.application.App;
import com.myshopping.utils.PreferencesKeys;
import com.myshopping.utils.keyboardutils.KeyboardDismisser;

public class MainActivity extends AppCompatActivity implements NavigationHost {
    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shr_main_activity);
        app = new App();
        KeyboardDismisser.useWith(this);

        if (savedInstanceState == null) {
            if (app.getSharePreferences().getStringPref(PreferencesKeys.getPref_is_login()).equalsIgnoreCase("1")) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, new ProductGridFragment())
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.container, new LoginFragment())
                        .commit();
            }
        }
    }

    /**
     * Navigate to the given fragment.
     *
     * @param fragment       Fragment to navigate to.
     * @param addToBackstack Whether or not the current fragment should be added to the backstack.
     */
    @Override
    public void navigateTo(Fragment fragment, boolean addToBackstack) {
        FragmentTransaction transaction =
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, fragment);

        if (addToBackstack) {
            transaction.addToBackStack(null);
        }

        transaction.commit();
    }
}
