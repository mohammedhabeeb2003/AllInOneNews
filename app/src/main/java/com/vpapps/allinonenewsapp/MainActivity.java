package com.vpapps.allinonenewsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.navigation.NavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vpapps.AsyncTask.LoadAbout;
import com.vpapps.fragments.FragmentCat;
import com.vpapps.fragments.FragmentFav;
import com.vpapps.fragments.FragmentHome;
import com.vpapps.fragments.FragmentLatest;
import com.vpapps.fragments.FragmentProfile;
import com.vpapps.fragments.FragmentVideo;
import com.vpapps.interfaces.AboutListener;
import com.vpapps.interfaces.AdConsentListener;
import com.vpapps.utils.AdConsent;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.Methods;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    DBHelper dbHelper;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    FragmentManager fm;
    final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102;
    Methods methods;
    AdConsent adConsent;
    MenuItem menuItemLogin, menuItemProfile;
    LinearLayout ll_adView;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ll_adView = findViewById(R.id.ll_adView);
        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        dbHelper = new DBHelper(this);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fm = getSupportFragmentManager();

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        menuItemLogin = navigationView.getMenu().findItem(R.id.nav_login);
        menuItemProfile = navigationView.getMenu().findItem(R.id.nav_profile);
        methods.setLoginButton(menuItemLogin, menuItemProfile, MainActivity.this);

        checkPer();

        FragmentHome f1 = new FragmentHome();
        loadFrag(f1, getString(R.string.home), fm);
        navigationView.setCheckedItem(R.id.nav_home);

        adConsent = new AdConsent(this, new AdConsentListener() {
            @Override
            public void onConsentUpdate() {
                methods.showBannerAd(ll_adView);
            }
        });

        if (methods.isNetworkAvailable()) {
            loadAboutData();
        } else {
            adConsent.checkForConsent();
            dbHelper.getAbout();
            methods.showToast(getString(R.string.err_internet_not_conn));
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fm.getBackStackEntryCount() != 0) {
            String title = fm.getFragments().get(fm.getBackStackEntryCount() - 1).getTag();
            if(title.equals(getString(R.string.home))) {
                navigationView.setCheckedItem(R.id.nav_home);
            }
            getSupportActionBar().setTitle(title);
            super.onBackPressed();
        } else {
            exitDialog();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
            case R.id.nav_home:
                FragmentHome f1 = new FragmentHome();
                loadFrag(f1, getString(R.string.home), fm);
                break;
            case R.id.nav_latest:
                FragmentLatest flatest = new FragmentLatest();
                loadFrag(flatest, getString(R.string.latest), fm);
                break;
            case R.id.nav_login:
                methods.clickLogin();
                break;
            case R.id.nav_video:
                FragmentVideo fvideo = new FragmentVideo();
                loadFrag(fvideo, getString(R.string.video_news), fm);
                toolbar.setTitle(getString(R.string.video_news));
                break;
            case R.id.nav_cat:
                FragmentCat fcat = new FragmentCat();
                loadFrag(fcat, getString(R.string.categories), fm);
                toolbar.setTitle(getString(R.string.categories));
                break;
            case R.id.nav_fav:
                FragmentFav ffav = new FragmentFav();
                loadFrag(ffav, getString(R.string.favourite), fm);
                toolbar.setTitle(getString(R.string.favourite));
                break;
            case R.id.nav_profile:
                FragmentProfile fprof = new FragmentProfile();
                loadFrag(fprof, getString(R.string.profile), fm);
                break;
            case R.id.nav_rate:
                final String appName = getPackageName();//your application package name i.e play store application url
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("market://details?id="
                                    + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + appName)));
                }
                break;
            case R.id.nav_shareapp:
                Intent ishare = new Intent(Intent.ACTION_SEND);
                ishare.setType("text/plain");
                ishare.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + getPackageName());
                startActivity(ishare);
                break;
            case R.id.nav_settings:
                Intent intent_set = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent_set);
                break;
        }

        navigationView.setCheckedItem(item.getItemId());
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }

        FragmentTransaction ft = fm.beginTransaction();
//        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        if (!name.equals(getString(R.string.home))) {
            ft.hide(fm.getFragments().get(fm.getFragments().size() - 1));
            ft.add(R.id.frame_nav, f1, name);
            ft.addToBackStack(name);
        } else {
            ft.replace(R.id.frame_nav, f1, name);
        }
        ft.commit();

        getSupportActionBar().setTitle(name);
    }

    public void loadAboutData() {
        LoadAbout loadAbout = new LoadAbout(MainActivity.this, new AboutListener() {
            @Override
            public void onStart() {
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message) {
                if (!verifyStatus.equals("-1")) {
                    adConsent.checkForConsent();
                    dbHelper.addtoAbout();
                } else {
                    methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                }
            }
        }, methods.getAPIRequest(Constant.METHOD_APP_DETAILS, 0, "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null));
        loadAbout.execute();
    }

    private void exitDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this, R.style.ThemeDialog);
        alert.setTitle(getString(R.string.exit));
        alert.setMessage(getString(R.string.sure_exit));
        alert.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        alert.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        alert.show();
    }

    public void checkPer() {
        if ((ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.WRITE_EXTERNAL_STORAGE") != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.ACCESS_FINE_LOCATION") != PackageManager.PERMISSION_GRANTED)) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        boolean canUseExternalStorage = false;

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    canUseExternalStorage = true;
                }

                if (!canUseExternalStorage) {
                    Toast.makeText(MainActivity.this, getResources().getString(R.string.cannot_use_save_permission), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        if (menuItemLogin != null) {
            methods.setLoginButton(menuItemLogin, menuItemProfile, MainActivity.this);
        }
        super.onResume();
    }
}