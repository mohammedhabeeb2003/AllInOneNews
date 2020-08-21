package com.vpapps.allinonenewsapp;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.vpapps.adapter.AdapterCatNewsBy;
import com.vpapps.fragments.FragmentNewsByCat;
import com.vpapps.item.ItemCat;
import com.vpapps.utils.Constant;
import com.vpapps.utils.Methods;
import com.vpapps.utils.RecyclerItemClickListener;

import java.util.ArrayList;

import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class NewsByCat extends AppCompatActivity {

    int pos=0;
    private ViewPager viewPager;
    Toolbar toolbar;
    ArrayList<ItemCat> arrayList;
    Methods methods;
    RecyclerView rv_cat;
    AdapterCatNewsBy adapterCatNewsBy;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_by_cat);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());

        pos = getIntent().getIntExtra("pos",0);

        toolbar = findViewById(R.id.toolbar_cat);
        toolbar.setTitle(getString(R.string.categories));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        arrayList = new ArrayList<>();
        arrayList.clear();
        arrayList.addAll(Constant.arrayList_cat);

        rv_cat = findViewById(R.id.rv_cat_newsby);
        rv_cat.setNestedScrollingEnabled(false);
        LinearLayoutManager llm_cat = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv_cat.setLayoutManager(llm_cat);
        rv_cat.setItemAnimator(new DefaultItemAnimator());
        adapterCatNewsBy = new AdapterCatNewsBy(this, arrayList);
        rv_cat.setAdapter(adapterCatNewsBy);
        adapterCatNewsBy.setSelected(pos);

        rv_cat.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                viewPager.setCurrentItem(position);
            }
        }));

        viewPager = findViewById(R.id.viewpager);

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(arrayList.size()-1);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(pos);
        rv_cat.smoothScrollToPosition(pos);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                pos = position;
                adapterCatNewsBy.setSelected(position);
                rv_cat.smoothScrollToPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return new FragmentNewsByCat().newInstance(position);
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return arrayList.get(position).getName();
        }
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
}