package com.vpapps.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vpapps.adapter.AdapterFav;
import com.vpapps.allinonenewsapp.R;
import com.vpapps.item.ItemNews;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.Methods;

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

public class FragmentFav extends Fragment {

    private DBHelper dbHelper;
    private RecyclerView recyclerView;
    private GridLayoutManager gridLayoutManager;
    private AdapterFav myAdapter;
    private ArrayList<ItemNews> arrayList;
    private Methods methods;
    private SearchView searchView;

    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_news_by_cat, container, false);

        methods = new Methods(getActivity());
        dbHelper = new DBHelper(getActivity());
        arrayList = new ArrayList<>();

        CircularProgressBar progressBar = rootView.findViewById(R.id.progressBar_news);
        progressBar.setVisibility(View.GONE);
        ll_empty = rootView.findViewById(R.id.ll_empty);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);
        button_try.setVisibility(View.GONE);
        errr_msg = getString(R.string.no_news_found);

        recyclerView = rootView.findViewById(R.id.recyclerView_newsbycat);
        gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return myAdapter.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        arrayList = dbHelper.getFav();
        myAdapter = new AdapterFav(getActivity(), arrayList);
        myAdapter.setHasStableIds(true);

        recyclerView.setAdapter(myAdapter);
        setEmpty();

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_tv, menu);

        if(!Constant.channelStatus) {
            menu.findItem(R.id.menu_tv).setVisible(false);
        }

        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(queryTextListener);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String s) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {

            if (searchView.isIconified()) {
                recyclerView.setAdapter(myAdapter);
                myAdapter.notifyDataSetChanged();
            } else {
                myAdapter.getFilter().filter(s);
                myAdapter.notifyDataSetChanged();
            }
            return true;
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_tv:
                methods.openTV();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setEmpty() {
        if (arrayList.size() > 0) {
            recyclerView.setVisibility(View.VISIBLE);
            ll_empty.setVisibility(View.GONE);
        } else {
            textView_empty.setText(errr_msg);
            recyclerView.setVisibility(View.GONE);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }
}