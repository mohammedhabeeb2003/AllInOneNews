package com.vpapps.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.view.MenuItemCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tiagosantos.enchantedviewpager.EnchantedViewPager;
import com.vpapps.AsyncTask.LoadHome;
import com.vpapps.adapter.AdapterCatHome;
import com.vpapps.adapter.AdapterNewsHome;
import com.vpapps.adapter.AdapterTop;
import com.vpapps.adapter.HomePagerAdapter;
import com.vpapps.allinonenewsapp.MainActivity;
import com.vpapps.allinonenewsapp.NewsByCat;
import com.vpapps.allinonenewsapp.NewsDetailsActivity;
import com.vpapps.allinonenewsapp.R;
import com.vpapps.interfaces.HomeListener;
import com.vpapps.interfaces.InterAdListener;
import com.vpapps.item.ItemCat;
import com.vpapps.item.ItemNews;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.Methods;
import com.vpapps.utils.RecyclerItemClickListener;
import com.vpapps.utils.SharedPref;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class FragmentHome extends Fragment {

    private DBHelper dbHelper;
    private RecyclerView rv_latest, rv_top, rv_cat, rv_recent;
    private AdapterNewsHome adapterLatest, adapterRecent;
    private AdapterCatHome adapterCatHome;
    private AdapterTop adapterTop;
    private ArrayList<ItemCat> arrayList_cat;
    private ArrayList<ItemNews> arrayList_latest, arrayList_recent, arrayList_topstories, arrayList_trending;
    private EnchantedViewPager enchantedViewPager;
    private HomePagerAdapter homePagerAdapter;
    private CardView cardView_cat;
    private LinearLayout ll_top, ll_latest, ll_trending, ll_recent;
    private TextView textView_viewall, textView_topall, textView_recentall;
    private Methods methods;
    private CircularProgressBar progressBar;
    private FragmentManager fm;
    private NestedScrollView scrollView;
    private SharedPref sharedPref;

    private TextView textView_empty;
    private AppCompatButton button_try;
    private LinearLayout ll_empty;
    private String errr_msg;
    private Boolean isTimerStart = false;
    private final Handler handler = new Handler();
    private Runnable Update;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        methods = new Methods(getActivity(), new InterAdListener() {
            @Override
            public void onClick(int position, String type) {
                switch (type) {
                    case "top":
                        Constant.itemNewsCurrent = arrayList_topstories.get(position);
                        Constant.selected_news_pos = position;
                        Intent intent_top = new Intent(getActivity(), NewsDetailsActivity.class);
                        startActivity(intent_top);
                        break;
                    case "all":

                        FragmentLatest f1 = new FragmentLatest();
                        FragmentTransaction ft = fm.beginTransaction();

                        Bundle bundl = new Bundle();
                        bundl.putString("type", "latest");
                        f1.setArguments(bundl);
//                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft.hide(fm.getFragments().get(fm.getFragments().size() - 1));
                        ft.add(R.id.frame_nav, f1, getResources().getString(R.string.latest_news));
                        ft.addToBackStack(getResources().getString(R.string.latest_news));
                        ft.commit();
                        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.latest_news));
                        break;
                    case "topall":

                        FragmentLatest f2 = new FragmentLatest();
                        FragmentTransaction ft2 = fm.beginTransaction();

                        Constant.arrayList_topstories.clear();
                        Constant.arrayList_topstories.addAll(arrayList_topstories);
                        Bundle bund2 = new Bundle();
                        bund2.putString("type", "top");
                        f2.setArguments(bund2);
//                        ft2.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft2.hide(fm.getFragments().get(fm.getFragments().size() - 1));
                        ft2.add(R.id.frame_nav, f2, getResources().getString(R.string.latest_news));
                        ft2.addToBackStack(getResources().getString(R.string.latest_news));
                        ft2.commitAllowingStateLoss();
                        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.top_stories));
                        break;
                    case "recentall":

                        FragmentLatest f_recent = new FragmentLatest();
                        FragmentTransaction ft_recent = fm.beginTransaction();

                        Constant.arrayList_topstories.clear();
                        Constant.arrayList_topstories.addAll(arrayList_topstories);
                        Bundle bundle_recent = new Bundle();
                        bundle_recent.putString("type", "recent");
                        f_recent.setArguments(bundle_recent);
//                        ft_recent.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        ft_recent.hide(fm.getFragments().get(fm.getFragments().size() - 1));
                        ft_recent.add(R.id.frame_nav, f_recent, getResources().getString(R.string.latest_news));
                        ft_recent.addToBackStack(getResources().getString(R.string.latest_news));
                        ft_recent.commitAllowingStateLoss();
                        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.top_stories));
                        break;
                    case "cat":

                        Constant.arrayList_cat.clear();
                        Constant.arrayList_cat.addAll(arrayList_cat);
                        Intent intent = new Intent(getActivity(), NewsByCat.class);
                        intent.putExtra("pos", position);
                        startActivity(intent);
                        break;
                }
            }
        });

        dbHelper = new DBHelper(getActivity());
        sharedPref = new SharedPref(getActivity());
        fm = getFragmentManager();


        arrayList_latest = new ArrayList<>();
        arrayList_topstories = new ArrayList<>();
        arrayList_trending = new ArrayList<>();
        arrayList_cat = new ArrayList<>();
        arrayList_recent = new ArrayList<>();

        progressBar = rootView.findViewById(R.id.progressBar_home);

        enchantedViewPager = rootView.findViewById(R.id.viewPager_home);
        enchantedViewPager.useAlpha();

        textView_topall = rootView.findViewById(R.id.textView_viewall_top);
        textView_viewall = rootView.findViewById(R.id.textView_viewall_latest);
        textView_recentall = rootView.findViewById(R.id.textView_viewall_recent);

        LinearLayout adView = rootView.findViewById(R.id.adView);
        methods.showBannerAd(adView);

        ll_empty = rootView.findViewById(R.id.ll_empty);
        ll_top = rootView.findViewById(R.id.ll_top);
        ll_latest = rootView.findViewById(R.id.ll_latest);
        ll_trending = rootView.findViewById(R.id.ll_trending);
        ll_recent = rootView.findViewById(R.id.ll_recent);
        cardView_cat = rootView.findViewById(R.id.cv_cat);
        textView_empty = rootView.findViewById(R.id.textView_empty_msg);
        button_try = rootView.findViewById(R.id.button_empty_try);
        errr_msg = getString(R.string.no_news_found);
        scrollView = rootView.findViewById(R.id.scrollView);

        rv_latest = rootView.findViewById(R.id.recyclerView_home);
        rv_latest.setNestedScrollingEnabled(false);
        LinearLayoutManager llm_latest = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_latest.setLayoutManager(llm_latest);
        rv_latest.setItemAnimator(new DefaultItemAnimator());

        rv_recent = rootView.findViewById(R.id.recyclerView_recent);
        rv_recent.setNestedScrollingEnabled(false);
        LinearLayoutManager llm_recent = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_recent.setLayoutManager(llm_recent);
        rv_recent.setItemAnimator(new DefaultItemAnimator());

        rv_top = rootView.findViewById(R.id.rv_home_top);
        rv_top.setNestedScrollingEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv_top.setLayoutManager(llm);
        rv_top.setItemAnimator(new DefaultItemAnimator());

        rv_cat = rootView.findViewById(R.id.rv_home_cat);
        rv_cat.setNestedScrollingEnabled(false);
        LinearLayoutManager llm_cat = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rv_cat.setLayoutManager(llm_cat);
        rv_cat.setItemAnimator(new DefaultItemAnimator());

        button_try.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadHome();
            }
        });

        textView_viewall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showInterAd(0, "all");
            }
        });

        textView_topall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showInterAd(0, "topall");
            }
        });

        textView_recentall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.showInterAd(0, "recentall");
            }
        });

        rv_top.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInterAd(position, "top");
            }
        }));

        rv_cat.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                methods.showInterAd(position, "cat");
            }
        }));

        loadHome();

        enchantedViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                handler.removeCallbacks(Update);
                handler.postDelayed(Update, 3000);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        Update = new Runnable() {
            public void run() {
                try {
                    isTimerStart = true;
                    if (enchantedViewPager.getCurrentItem() == (homePagerAdapter.getCount() - 1)) {
                        enchantedViewPager.setCurrentItem(0, true);
                    } else {
                        enchantedViewPager.setCurrentItem(enchantedViewPager.getCurrentItem() + 1, true);
                    }

                    handler.removeCallbacks(Update);
                    handler.postDelayed(Update, 3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        setHasOptionsMenu(true);
        return rootView;
    }

    private void loadHome() {
        if (methods.isNetworkAvailable()) {
            LoadHome loadHome = new LoadHome(new HomeListener() {
                @Override
                public void onStart() {
                    scrollView.setVisibility(View.GONE);
                    ll_empty.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);

                    arrayList_recent.addAll(dbHelper.getRecentNews("10"));
                }

                @Override
                public void onEnd(String success, ArrayList<ItemNews> arrayListLatest, ArrayList<ItemNews> arrayListTrending, ArrayList<ItemNews> arrayListTop, ArrayList<ItemCat> arrayListCat) {
                    if (getActivity() != null) {
                        if (success.equals("1")) {
                            arrayList_latest.addAll(arrayListLatest);
                            arrayList_trending.addAll(arrayListTrending);
                            arrayList_topstories.addAll(arrayListTop);
                            arrayList_cat.addAll(arrayListCat);

                            adapterLatest = new AdapterNewsHome(getActivity(), arrayList_latest);
                            rv_latest.setAdapter(adapterLatest);

                            adapterRecent = new AdapterNewsHome(getActivity(), arrayList_recent);
                            rv_recent.setAdapter(adapterRecent);

                            adapterTop = new AdapterTop(getActivity(), arrayList_topstories);
                            rv_top.setAdapter(adapterTop);

                            adapterCatHome = new AdapterCatHome(getActivity(), arrayList_cat);
                            rv_cat.setAdapter(adapterCatHome);

                            homePagerAdapter = new HomePagerAdapter(getActivity(), arrayList_trending);
                            enchantedViewPager.setAdapter(homePagerAdapter);
                            if (homePagerAdapter.getCount() > 2) {
                                enchantedViewPager.setCurrentItem(1);
                            }

                            if (homePagerAdapter.getCount() > 1) {
                                handler.removeCallbacks(Update);
                                handler.postDelayed(Update, 3000);
                            }

                        } else {
                            Toast.makeText(getActivity(), getResources().getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                        }
                        setEmpty();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_HOME, 0, "", "", "", "", sharedPref.getCat(), "", "", "", "", "", "", "", null));
            loadHome.execute();
        } else {
            errr_msg = getString(R.string.err_internet_not_conn);
            ll_empty.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_tv, menu);

        if (!Constant.channelStatus) {
            menu.findItem(R.id.menu_tv).setVisible(false);
        }

        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
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
            if (!s.trim().equals("") && getActivity() != null) {
                Constant.search_text = s.replace(" ", "%20");

                FragmentSearch f1 = new FragmentSearch();
                FragmentTransaction ft = fm.beginTransaction();

//                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                ft.hide(fm.getFragments().get(fm.getFragments().size() - 1));
                ft.add(R.id.frame_nav, f1, getString(R.string.search));
                ft.addToBackStack(getString(R.string.search));
                ft.commit();
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.search));
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String s) {
            return false;
        }
    };


    public void setEmpty() {
        if (arrayList_trending.size() > 0 || arrayList_topstories.size() > 0 || arrayList_latest.size() > 0) {
            ll_empty.setVisibility(View.GONE);
            scrollView.setVisibility(View.VISIBLE);

            if (arrayList_trending.size() == 0) {
                ll_trending.setVisibility(View.GONE);
            }

            if (arrayList_recent.size() == 0) {
                ll_recent.setVisibility(View.GONE);
            }

            if (arrayList_latest.size() == 0) {
                ll_latest.setVisibility(View.GONE);
            }

            if (arrayList_topstories.size() == 0) {
                ll_top.setVisibility(View.GONE);
            }

            if (arrayList_cat.size() == 0) {
                cardView_cat.setVisibility(View.GONE);
            }

        } else {
            scrollView.setVisibility(View.GONE);
            textView_empty.setText(errr_msg);
            ll_empty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        if (isTimerStart) {
            try {
                handler.removeCallbacks(Update);
                handler.postDelayed(Update, 3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        if (isTimerStart) {
            try {
                handler.removeCallbacks(Update);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (isTimerStart) {
            try {
                handler.removeCallbacks(Update);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}