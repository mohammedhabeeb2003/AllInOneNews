package com.vpapps.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpapps.AsyncTask.LoadCat;
import com.vpapps.adapter.AdapterCategories;
import com.vpapps.allinonenewsapp.NewsByCat;
import com.vpapps.allinonenewsapp.R;
import com.vpapps.interfaces.CatListener;
import com.vpapps.interfaces.InterAdListener;
import com.vpapps.item.ItemCat;
import com.vpapps.utils.Constant;
import com.vpapps.utils.Methods;
import com.vpapps.utils.RecyclerItemClickListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentCat extends Fragment {

    Methods methods;
    private RecyclerView recyclerView;
    private AdapterCategories adapterCat;
    private ArrayList<ItemCat> arrayList;
    private SearchView searchView;

    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;
    private CircularProgressBar progressBar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cat, container, false);

        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                Constant.arrayList_cat.clear();
                Constant.arrayList_cat.addAll(arrayList);
                int reali = getPosition(adapterCat.getId(position));
                Intent intent = new Intent(getActivity(), NewsByCat.class);
                intent.putExtra("pos", reali);
                startActivity(intent);
            }
        });
        arrayList = new ArrayList<>();
        progressBar = rootView.findViewById(R.id.progressBar_cat);

        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);

        recyclerView = rootView.findViewById(R.id.recyclerView_cat);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInterAd(position, "");
            }
        }));

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCategories();
            }
        });

        loadCategories();

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_tv, menu);

        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        if(!Constant.channelStatus) {
            menu.findItem(R.id.menu_tv).setVisible(false);
        }

        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_tv:
                methods.openTV();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {

            if (!searchView.isIconified()) {
                adapterCat.getFilter().filter(s);
                adapterCat.notifyDataSetChanged();
            }
            return true;
        }
    };

    private void loadCategories() {
        if (methods.isNetworkAvailable()) {
            LoadCat loadCat = new LoadCat(new CatListener() {
                @Override
                public void onStart() {
                    arrayList.clear();
                    ll_empty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message, ArrayList<ItemCat> arrayListCat) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                arrayList.addAll(arrayListCat);
                                errr_msg = getString(R.string.no_cat_found);
                            } else {
                                errr_msg = getString(R.string.err_server_no_conn);
                                methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        }
                        setAdapter();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_CATEGORY,0,"","","","","","","","","","","","",null));
            loadCat.execute();
        } else {
            errr_msg = getString(R.string.err_internet_not_conn);
            setEmpty();
        }
    }

    private void setAdapter() {
        adapterCat = new AdapterCategories(getActivity(), arrayList);
        recyclerView.setAdapter(adapterCat);
        setEmpty();
    }

    public void setEmpty() {
        progressBar.setVisibility(View.GONE);
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    private int getPosition(String id) {
        int count = 0;
        int rid = Integer.parseInt(id);
        for (int i = 0; i < arrayList.size(); i++) {
            try {
                if (rid == Integer.parseInt(arrayList.get(i).getId())) {
                    count = i;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return count;
    }
}
