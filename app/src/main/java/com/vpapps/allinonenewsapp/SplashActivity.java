package com.vpapps.allinonenewsapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vpapps.AsyncTask.LoadAbout;
import com.vpapps.AsyncTask.LoadLogin;
import com.vpapps.interfaces.AboutListener;
import com.vpapps.interfaces.LoginListener;
import com.vpapps.item.ItemUser;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.Methods;
import com.vpapps.utils.SharedPref;

public class SplashActivity extends AppCompatActivity {


    SharedPref sharedPref;
    Methods methods;
    DBHelper dbHelper;
    ProgressBar progressbar_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        hideStatusBar();

        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        dbHelper = new DBHelper(this);

        progressbar_login = findViewById(R.id.progressbar_login);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPref.getIsFirst()) {
                    loadAboutData();
                } else {
                    if (!sharedPref.getIsAutoLogin()) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                openMainActivity();
                            }
                        }, 2000);
                    } else {
                        if (methods.isNetworkAvailable()) {
                            loadLogin();
                        } else {
                            openMainActivity();
                        }
                    }
                }
            }
        },500);
    }

    private void loadLogin() {
        if (methods.isNetworkAvailable()) {
            LoadLogin loadLogin = new LoadLogin(new LoginListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String loginSuccess, String message, String user_id, String user_name, String dp, String user_cat) {

                    if (success.equals("1")) {
                        if (loginSuccess.equals("1")) {
                            Constant.itemUser = new ItemUser(user_id, user_name, sharedPref.getEmail(), "",dp);

                            Constant.isLogged = true;
                            openMainActivity();
                        } else {
                            openMainActivity();
                        }
                    } else {
                        openMainActivity();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_LOGIN, 0, "", "", "", "", "", "", sharedPref.getEmail(), sharedPref.getPassword(), "", "", "","",null));
            loadLogin.execute();
        } else {
            Toast.makeText(SplashActivity.this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    public void loadAboutData() {
        if (methods.isNetworkAvailable()) {
            LoadAbout loadAbout = new LoadAbout(SplashActivity.this, new AboutListener() {
                @Override
                public void onStart() {

                }

                @Override
                public void onEnd(String success, String verifyStatus, String message) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            dbHelper.addtoAbout();
                            openLoginActivity();
                        } else {
                            errorDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        errorDialog(getString(R.string.server_error), getString(R.string.server_error));
                    }

                }
            }, methods.getAPIRequest(Constant.METHOD_APP_DETAILS, 0, "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(),"",null));
            loadAbout.execute();
        } else {
            errorDialog(getString(R.string.err_internet_not_conn), getString(R.string.error_connect_net_tryagain));
        }
    }

    private void errorDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashActivity.this, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        if (title.equals(getString(R.string.err_internet_not_conn)) || title.equals(getString(R.string.server_error))) {
            alertDialog.setNegativeButton(getString(R.string.try_again), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    loadAboutData();
                }
            });
        }

        alertDialog.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        alertDialog.show();
    }

    private void openLoginActivity() {
        Intent intent;
        if(sharedPref.getIsFirst()) {
            sharedPref.setIsFirst(false);
            if(!sharedPref.getIsCat()) {
                sharedPref.setIsCat(true);
                intent = new Intent(SplashActivity.this, SelectCategoriesActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("from", "");
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private void openMainActivity() {
        Intent intent;
        if(!Constant.pushNID.equals("0")) {
            intent = new Intent(SplashActivity.this, NewsDetailsActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("from", "");
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }
}