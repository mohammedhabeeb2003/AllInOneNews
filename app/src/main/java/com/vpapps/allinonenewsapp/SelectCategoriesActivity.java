package com.vpapps.allinonenewsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.vpapps.AsyncTask.LoadCat;
import com.vpapps.AsyncTask.LoadSaveCat;
import com.vpapps.adapter.AdapterSelectCategories;
import com.vpapps.interfaces.CatListener;
import com.vpapps.interfaces.SuccessListener;
import com.vpapps.item.ItemCat;
import com.vpapps.utils.Constant;
import com.vpapps.utils.Methods;
import com.vpapps.utils.SharedPref;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class SelectCategoriesActivity extends AppCompatActivity {

    Toolbar toolbar;
    Methods methods;
    RecyclerView recyclerView;
    AdapterSelectCategories adapterCat;
    FlexboxLayoutManager flexboxLayoutManager;
    ArrayList<ItemCat> arrayList;
    RelativeLayout rl_main;
    Button button_next, button_skip;

    TextView textView_empty;
    AppCompatButton button_try;
    LinearLayout ll_empty;
    String errr_msg;
    CircularProgressBar progressBar;
    SharedPref sharedPref;
    String from;
    ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_cat);

        from = getIntent().getStringExtra("from");

        sharedPref = new SharedPref(this);
        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        toolbar = this.findViewById(R.id.toolbar_select_cat);
        toolbar.setTitle(getString(R.string.select_categories));
        this.setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));

        arrayList = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar_select_cat);
        rl_main = findViewById(R.id.rl_main);
        button_next = findViewById(R.id.button_next);
        button_skip = findViewById(R.id.button_skip);

        ll_empty = findViewById(R.id.ll_empty);
        textView_empty = findViewById(R.id.textView_empty_msg);
        button_try = findViewById(R.id.button_empty_try);

        recyclerView = findViewById(R.id.recyclerView_select_cat);
        flexboxLayoutManager = new FlexboxLayoutManager(SelectCategoriesActivity.this);
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);
        recyclerView.setLayoutManager(flexboxLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        setButtonName();

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCategories();
            }
        });

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Constant.isLogged) {
                    String ids = "";
                    if (adapterCat.getSelectedItemCount() > 0) {
                        ids = arrayList.get(adapterCat.getSelectedItemList().get(0)).getId();
                        for (int i = 1; i < adapterCat.getSelectedItemCount(); i++) {
                            ids = ids + "," + arrayList.get(adapterCat.getSelectedItemList().get(i)).getId();
                        }
                    }
                    loadSaveCategories(ids);
                } else {
                    Intent intent = new Intent(SelectCategoriesActivity.this, LoginActivity.class);
                    intent.putExtra("from", "app");
                    startActivity(intent);
                }
            }
        });

        button_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPref.setIsSelectCatShown(true);
                Intent intent = new Intent(SelectCategoriesActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if(from.equals(getString(R.string.setting))) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            button_skip.setVisibility(View.GONE);
            button_next.setText(getString(R.string.save));
        }

        loadCategories();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadCategories() {
        if (methods.isNetworkAvailable()) {
            LoadCat loadCat = new LoadCat(new CatListener() {
                @Override
                public void onStart() {
                    arrayList.clear();
                    ll_empty.setVisibility(View.GONE);
                    rl_main.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> arrayListCat) {
                    if (success.equals("1")) {
                        if (!verifyStatus.equals("-1")) {
                            arrayList.addAll(arrayListCat);
                            errr_msg = getString(R.string.no_cat_found);
                        } else {
                            errr_msg = getString(R.string.err_server_no_conn);
                            methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                        }
                    } else {
                        errr_msg = getString(R.string.err_server_no_conn);
                    }
                    setAdapter();
                }
            }, methods.getAPIRequest(Constant.METHOD_CATEGORY, 0, "", "", "", "", "", "", "", "", "", "", "", "", null));
            loadCat.execute();
        } else {
            errr_msg = getString(R.string.err_internet_not_conn);
            setEmpty();
        }
    }

    private void loadSaveCategories(final String ids) {
        if (methods.isNetworkAvailable()) {
            LoadSaveCat loadCat = new LoadSaveCat(new SuccessListener() {


                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        sharedPref.setCat(ids);

                        if (from.equals(getString(R.string.setting))) {
                            Toast.makeText(SelectCategoriesActivity.this, message, Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(SelectCategoriesActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    } else {
                        errr_msg = getString(R.string.err_server_no_conn);
                    }
//                    setAdapter();
                }
            }, methods.getAPIRequest(Constant.METHOD_SAVE_CATEGORY, 0, "", "", "", "", ids, "", "", "", "", "", Constant.itemUser.getId(), "", null));
            loadCat.execute();
        } else {
            errr_msg = getString(R.string.err_internet_not_conn);
            setEmpty();
        }
    }

    private void setAdapter() {
        adapterCat = new AdapterSelectCategories(SelectCategoriesActivity.this, arrayList);
        recyclerView.setAdapter(adapterCat);
        if(sharedPref.getCat().equals("")) {
            adapterCat.selectAll();
        }
        setEmpty();
    }

    public void setEmpty() {
        progressBar.setVisibility(View.GONE);
        if (arrayList.size() > 0) {
            rl_main.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
            setSelectedCategories();
        } else {
            textView_empty.setText(errr_msg);
            rl_main.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    public void setSelectedCategories() {
        if (!sharedPref.getCat().equals("")) {
            List<String> list = Arrays.asList(sharedPref.getCat().split(","));
            if(list.size() > 0) {
                adapterCat.deselectAll();
                for(int i=0; i<arrayList.size(); i++) {
                    if(list.contains(arrayList.get(i).getId())) {
                        adapterCat.select(i);
                    }
                }
            } else {
                adapterCat.selectAll();
            }
        }
    }

    private void setButtonName() {
        if(button_next != null) {
            if(Constant.isLogged) {
                if(from.equals(getString(R.string.setting))) {
                    button_next.setText(getString(R.string.save));
                } else {
                    button_next.setText(getString(R.string.next));
                    setSelectedCategories();
                }
            } else {
                button_next.setText(getString(R.string.login));
            }
        }
    }

    @Override
    protected void onResume() {
        setButtonName();
        super.onResume();
    }
}