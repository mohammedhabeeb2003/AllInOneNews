package com.vpapps.allinonenewsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.vpapps.AsyncTask.LoadAbout;
import com.vpapps.interfaces.AboutListener;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.Methods;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class AboutActivity extends AppCompatActivity {

    private Methods methods;
    private WebView webView;
    private DBHelper dbHelper;
    private TextView textView_appname, textView_email, textView_website, textView_company, textView_contact, textView_version;
    private ImageView imageView_logo;
    private CardView ll_email, ll_website, ll_company, ll_contact;
    private String website, email, desc, applogo, appname, appversion, appauthor, appcontact;
    private ProgressDialog pbar;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        dbHelper = new DBHelper(this);
        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        Toolbar toolbar = this.findViewById(R.id.toolbar_about);
        toolbar.setTitle(getString(R.string.about));
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pbar = new ProgressDialog(this);
        pbar.setMessage(getResources().getString(R.string.loading));
        pbar.setCancelable(false);

        webView = findViewById(R.id.webView);
        textView_appname = findViewById(R.id.textView_about_appname);
        textView_email = findViewById(R.id.textView_about_email);
        textView_website = findViewById(R.id.textView_about_site);
        textView_company = findViewById(R.id.textView_about_company);
        textView_contact = findViewById(R.id.textView_about_contact);
        textView_version = findViewById(R.id.textView_about_appversion);
        imageView_logo = findViewById(R.id.imageView_about_logo);

        textView_appname.setTypeface(methods.getFontMedium());

        ll_email = findViewById(R.id.ll_email);
        ll_website = findViewById(R.id.ll_website);
        ll_contact = findViewById(R.id.ll_contact);
        ll_company = findViewById(R.id.ll_company);
        dbHelper.getAbout();

        if (methods.isNetworkAvailable()) {
            loadAboutData();
        } else {
            if (dbHelper.getAbout()) {
                setTexts();
                setVariables();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }

    public void loadAboutData() {
        LoadAbout loadAbout = new LoadAbout(AboutActivity.this, new AboutListener() {
            @Override
            public void onStart() {
                pbar.show();
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message) {
                if (pbar.isShowing()) {
                    pbar.dismiss();
                }
                if (success.equals("1")) {
                    setTexts();
                    setVariables();
                    dbHelper.addtoAbout();
                }

                if (success.equals("1")) {
                    if (!verifyStatus.equals("-1")) {
                        setTexts();
                        setVariables();
                        dbHelper.addtoAbout();
                    } else {
                        methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                    }
                }

            }
        }, methods.getAPIRequest(Constant.METHOD_APP_DETAILS, 0, "", "", "", "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null));
        loadAbout.execute();
    }

    private void setTexts() {
        appname = Constant.itemAbout.getAppName();
        applogo = Constant.itemAbout.getAppLogo();
        desc = Constant.itemAbout.getAppDesc();
        appversion = Constant.itemAbout.getAppVersion();
        appauthor = Constant.itemAbout.getAuthor();
        appcontact = Constant.itemAbout.getContact();
        email = Constant.itemAbout.getEmail();
        website = Constant.itemAbout.getWebsite();
    }

    public void setVariables() {
        textView_appname.setText(appname);
        if (!email.trim().isEmpty()) {
            ll_email.setVisibility(View.VISIBLE);
            textView_email.setText(email);
        }

        if (!website.trim().isEmpty()) {
            ll_website.setVisibility(View.VISIBLE);
            textView_website.setText(website);
        }

        if (!appauthor.trim().isEmpty()) {
            ll_company.setVisibility(View.VISIBLE);
            textView_company.setText(appauthor);
        }

        if (!appcontact.trim().isEmpty()) {
            ll_contact.setVisibility(View.VISIBLE);
            textView_contact.setText(appcontact);
        }

        if (!appversion.trim().isEmpty()) {
            textView_version.setText(appversion);
        }

        if (applogo.trim().isEmpty()) {
            imageView_logo.setVisibility(View.GONE);
        } else {
            Picasso
                    .get()
                    .load(Constant.URL_IMAGE + applogo)
                    .into(imageView_logo);
        }

        String mimeType = "text/html;charset=UTF-8";
        String encoding = "utf-8";

        String text = "<html><head>"
                + "<style> body{color:#000 !important;text-align:left}"
                + "</style></head>"
                + "<body>"
                + desc
                + "</body></html>";


        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadData(text, mimeType, encoding);
    }
}