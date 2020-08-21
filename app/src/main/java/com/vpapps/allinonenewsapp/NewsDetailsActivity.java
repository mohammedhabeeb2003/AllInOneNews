package com.vpapps.allinonenewsapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.vpapps.AsyncTask.LoadCommentDelete;
import com.vpapps.AsyncTask.LoadCommentPost;
import com.vpapps.AsyncTask.LoadReport;
import com.vpapps.AsyncTask.LoadSingleNews;
import com.vpapps.adapter.AdapterComments;
import com.vpapps.fragments.FragmentComment;
import com.vpapps.interfaces.ClickListener;
import com.vpapps.interfaces.CommentDeleteListener;
import com.vpapps.interfaces.PostCommentListener;
import com.vpapps.interfaces.SingleNewsListener;
import com.vpapps.interfaces.SuccessListener;
import com.vpapps.item.ItemComment;
import com.vpapps.item.ItemEventBus;
import com.vpapps.item.ItemNews;
import com.vpapps.utils.Constant;
import com.vpapps.utils.DBHelper;
import com.vpapps.utils.GlobalBus;
import com.vpapps.utils.Methods;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class NewsDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbar;
    DBHelper dbHelper;
    private ArrayList<ItemComment> arrayListComments;
    private FloatingActionButton fab;
    private Boolean isFromPush = false;
    SliderLayout sliderLayout;
    Methods methods;
    String nid;
    RecyclerView rv_comment;
    AdapterComments adapterComm;
    EditText editText_comment;
    ImageView iv_post_comment;
    RoundedImageView iv_user;
    TextView textView_date, textView_title, textView_cat, textView_comment, textView_empty_comment;
    WebView webView;
    CircularProgressBar progressBar;
    MenuItem menuItem;
    AppBarLayout mAppBarLayout;
    Menu menu;
    BottomSheetDialog dialog_setas;
    ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        dbHelper = new DBHelper(this);

        methods = new Methods(this);
        methods.forceRTLIfSupported(getWindow());
        methods.setStatusColor(getWindow());

        progressDialog = new ProgressDialog(NewsDetailsActivity.this);
        progressDialog.setMessage(getString(R.string.loading));

        arrayListComments = new ArrayList<>();

        collapsingToolbar = findViewById(R.id.collapsing);
        toolbar = findViewById(R.id.toolbar_news);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sliderLayout = findViewById(R.id.slider);
        mAppBarLayout = findViewById(R.id.appbar);
        fab = findViewById(R.id.fab);
        FloatingActionButton fab_comment = findViewById(R.id.fab_comment);
        FloatingActionButton fab_share = findViewById(R.id.fab_share);
        rv_comment = findViewById(R.id.rv_comment_details);
        editText_comment = findViewById(R.id.et_details_comment);
        textView_empty_comment = findViewById(R.id.tv_empty_comment);
        textView_comment = findViewById(R.id.tv_viewall_comment);
        iv_post_comment = findViewById(R.id.iv_details_comment);
        iv_user = findViewById(R.id.iv_details_user);

        textView_date = findViewById(R.id.tv_date_details);
        textView_title = findViewById(R.id.tv_title_detail);
        textView_cat = findViewById(R.id.tv_cat_detail);
        webView = findViewById(R.id.webView_news_details);
        webView.getSettings().setJavaScriptEnabled(true);
        progressBar = findViewById(R.id.pb_details);

        textView_title.setTypeface(methods.getFontMedium());
        Picasso.get().load(Constant.URL_IMAGE + Constant.itemUser.getDp()).placeholder(R.drawable.comment2).into(iv_user);

        rv_comment.setLayoutManager(new LinearLayoutManager(NewsDetailsActivity.this));

        iv_post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constant.isLogged) {
                    methods.openLogin();
                } else if (editText_comment.getText().toString().trim().isEmpty()) {
                    Toast.makeText(NewsDetailsActivity.this, getResources().getString(R.string.enter_comment), Toast.LENGTH_SHORT).show();
                } else {
                    if (methods.isNetworkAvailable()) {
                        loadPostComment();
                    } else {
                        Toast.makeText(NewsDetailsActivity.this, getResources().getString(R.string.conn_net_post_comment), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        textView_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCommentList();
            }
        });

        fab_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentComment fragmentComment = new FragmentComment();
                fragmentComment.show(getSupportFragmentManager(), fragmentComment.getTag());
            }
        });

        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.shareNews(Constant.itemNewsCurrent);
            }
        });

        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    showOption();
                } else if (isShow) {
                    isShow = false;
                    hideOption();
                }
            }
        });

        initSlider();

        if (!Constant.pushNID.equals("0")) {
            isFromPush = true;
            nid = Constant.pushNID;
            Constant.selected_news_pos = 0;
            Constant.pushNID = "0";
        } else {
            dbHelper.addRecentNews(Constant.itemNewsCurrent);
            setVariables();
            nid = Constant.itemNewsCurrent.getId();
        }

        loadSingleNews();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                methods.startVideoPlay(Constant.itemNewsCurrent.getVideoId());
//                Intent intent;
//                if (methods.isPackageInstalled("com.google.android.youtube")) {
//                    intent = YouTubeStandalonePlayer.createVideoIntent(NewsDetailsActivity.this, BuildConfig.API_KEY, Constant.itemNewsCurrent.getVideoId());
//                } else {
//                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + Constant.itemNewsCurrent.getVideoId()));
//                }
//                startActivity(intent);
            }
        });

        try {
            setFavImage(dbHelper.isFav(Constant.itemNewsCurrent.getId()), menuItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_details, menu);
        menuItem = menu.findItem(R.id.item_fav);

        MenuItem menuItem_comm = menu.findItem(R.id.item_comment);

        Drawable drawable = menuItem_comm.getIcon();
        drawable.mutate();
        drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);

        try {
            setFavImage(dbHelper.isFav(Constant.itemNewsCurrent.getId()), menuItem);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.item_share:
                methods.shareNews(Constant.itemNewsCurrent);
                break;
            case R.id.item_comment:
                openCommentList();
                break;
            case R.id.item_report:
                showReportDialog();
                break;
            case R.id.item_fav:
                if (dbHelper.isFav(Constant.itemNewsCurrent.getId())) {
                    dbHelper.removeFav(Constant.itemNewsCurrent.getId());
                    setFavImage(false, menuItem);
                } else {
                    dbHelper.addFav(Constant.itemNewsCurrent);
                    setFavImage(true, menuItem);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSlider() {
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setDuration(3000);
    }

    private void setSlider() {
        sliderLayout.removeAllSliders();

        DefaultSliderView textSlider = new DefaultSliderView(this);
        // initialize a SliderLayout
        textSlider
                .image(Constant.URL_IMAGE + Constant.itemNewsCurrent.getImage())
                .setScaleType(BaseSliderView.ScaleType.CenterCrop);

        //add your extra information
        textSlider.bundle(new Bundle());
        textSlider.getBundle().putString("extra", Constant.itemNewsCurrent.getImage());

        sliderLayout.addSlider(textSlider);

        if (Constant.itemNewsCurrent.getGalleryList() != null) {
            if (Constant.itemNewsCurrent.getGalleryList().size() == 0) {
                sliderLayout.stopAutoCycle();
                sliderLayout.setEnabled(false);
            }
            for (int i = 0; i < Constant.itemNewsCurrent.getGalleryList().size(); i++) {
                DefaultSliderView textSliderView = new DefaultSliderView(this);
                // initialize a SliderLayout
                textSliderView
                        .image(Constant.URL_IMAGE + Constant.itemNewsCurrent.getGalleryList().get(i))
                        .setScaleType(BaseSliderView.ScaleType.CenterCrop);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", Constant.itemNewsCurrent.getGalleryList().get(i));

                sliderLayout.addSlider(textSliderView);
            }
        }
    }

    private void setVariables() {
        setFab();

        textView_cat.setText(Constant.itemNewsCurrent.getCatName());
        textView_title.setText(Constant.itemNewsCurrent.getHeading());
        textView_date.setText(Constant.itemNewsCurrent.getDate());

        String myCustomStyleString = "<style channelType=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/pop_med.ttf\")}body,* {font-family: MyFont; font-size: 13px;text-align: justify;}img{max-width:100%;height:auto; border-radius: 3px;}</style>";
        webView.loadDataWithBaseURL("", myCustomStyleString + "<div>" + Constant.itemNewsCurrent.getDesc() + "</div>", "text/html", "utf-8", null);
    }

    private void setCommentAdapter() {
        adapterComm = new AdapterComments(NewsDetailsActivity.this, arrayListComments, new ClickListener(){
            @Override
            public void onClick(int pos) {
                loadDeleteComment(pos);
            }
        });
        rv_comment.setAdapter(adapterComm);
        progressBar.setVisibility(View.GONE);

        setEmpty();
    }

    private void setEmpty() {
        if (arrayListComments.size() == 0) {
            rv_comment.setVisibility(View.GONE);
            textView_empty_comment.setVisibility(View.VISIBLE);
        } else {
            rv_comment.setVisibility(View.VISIBLE);
            textView_empty_comment.setVisibility(View.GONE);
        }
    }

    private void loadDeleteComment(final int pos) {
        LoadCommentDelete loadCommentDelete = new LoadCommentDelete(new CommentDeleteListener() {
            @Override
            public void onStart() {
                progressDialog.show();
            }

            @Override
            public void onEnd(String success, String isDeleted, String message, ItemComment itemComment) {
                progressDialog.dismiss();
                if(success.equals("1")) {
                    if(isDeleted.equals("1")) {
                        arrayListComments.remove(pos);
                        adapterComm.notifyItemRemoved(pos);
                        if(itemComment != null && !itemComment.getId().equals("null")) {
                            boolean isNew = true;

                            for (int i = 0; i < arrayListComments.size(); i++) {
                                if(arrayListComments.get(i).getId().equals(itemComment.getId())) {
                                    isNew = false;
                                    break;
                                }
                            }
                            if(isNew) {
                                arrayListComments.add(itemComment);
                                adapterComm.notifyItemInserted(arrayListComments.size() - 1);
                            }
                        }
                        setEmpty();
                    }
                    Toast.makeText(NewsDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewsDetailsActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                }
            }
        }, methods.getAPIRequest(Constant.METHOD_DELETE_COMMENTS, 0,"",Constant.itemNewsCurrent.getId(),"","",arrayListComments.get(pos).getId(),"","","","","","","", null));
        loadCommentDelete.execute();
    }

    private void setFab() {
        if (Constant.itemNewsCurrent != null) {
            if (Constant.itemNewsCurrent.getType().equals("image")) {
                fab.setVisibility(View.GONE);
            } else {
                fab.setVisibility(View.VISIBLE);
            }
        }
    }

    private void loadSingleNews() {
        if (methods.isNetworkAvailable()) {
            LoadSingleNews loadSingleNews = new LoadSingleNews(new SingleNewsListener() {
                @Override
                public void onStart() {
                    arrayListComments.clear();
                }

                @Override
                public void onEnd(String success, ItemNews itemNews, ArrayList<ItemComment> arrayListComm) {
                    if (success.equals("1")) {
                        arrayListComments.addAll(arrayListComm);
                        Constant.itemNewsCurrent = itemNews;

                        if (isFromPush) {
                            dbHelper.addRecentNews(Constant.itemNewsCurrent);
                            setVariables();
                        }
                        setCommentAdapter();
                    } else {
                        Toast.makeText(NewsDetailsActivity.this, getResources().getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                    }
                    setSlider();
                }

            }, methods.getAPIRequest(Constant.METHOD_SINGLE_NEWS, 0, "", nid, "", "", "", "", "", "", "", "", "", "", null));
            loadSingleNews.execute();
        } else {
            Toast.makeText(NewsDetailsActivity.this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private void hideOption() {
        MenuItem item = menu.findItem(R.id.item_share);
        MenuItem item_comm = menu.findItem(R.id.item_comment);
        item.setVisible(false);
        item_comm.setVisible(false);
        collapsingToolbar.setTitle("");
    }

    private void showOption() {
        MenuItem item = menu.findItem(R.id.item_share);
        MenuItem item_comm = menu.findItem(R.id.item_comment);
        item.setVisible(true);
        item_comm.setVisible(true);
        collapsingToolbar.setTitle(Constant.itemNewsCurrent.getCatName());
    }

    private void openCommentList() {
        FragmentComment fragmentComment = new FragmentComment();
        fragmentComment.show(getSupportFragmentManager(), fragmentComment.getTag());
    }

    private void loadPostComment() {
        LoadCommentPost loadCommentPost = new LoadCommentPost(new PostCommentListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onEnd(String success, String isCommentPosted, String message, ItemComment itemComment) {
                if (success.equals("1")) {

                    if (isCommentPosted.equals("1")) {
                        arrayListComments.add(0, itemComment);
                        adapterComm.notifyDataSetChanged();
                        rv_comment.setVisibility(View.VISIBLE);
                        textView_empty_comment.setVisibility(View.GONE);
                        editText_comment.setText("");
                        rv_comment.smoothScrollToPosition(0);
                    }
                    Toast.makeText(NewsDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewsDetailsActivity.this, getString(R.string.err_server_no_conn), Toast.LENGTH_SHORT).show();
                }
            }

        }, methods.getAPIRequest(Constant.METHOD_POST_COMMENTS, 0, "", Constant.itemNewsCurrent.getId(), editText_comment.getText().toString(), "", "", "", "", "", "", "", Constant.itemUser.getId(), "", null));
        loadCommentPost.execute();
    }

    public void loadReportSubmit(String report) {
        if (methods.isNetworkAvailable()) {
            LoadReport loadReport = new LoadReport(new SuccessListener() {
                @Override
                public void onStart() {
                    progressDialog.show();
                }

                @Override
                public void onEnd(String success, String registerSuccess, String message) {
                    progressDialog.dismiss();
                    if (success.equals("1")) {
                        if (registerSuccess.equals("1")) {
                            try {
                                dialog_setas.dismiss();
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                            Toast.makeText(NewsDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(NewsDetailsActivity.this, getString(R.string.server_error), Toast.LENGTH_SHORT).show();
                    }
                }
            }, methods.getAPIRequest(Constant.METHOD_REPORT, 0, "", Constant.itemNewsCurrent.getId(), "", "", "", "", "", "", "", "", Constant.itemUser.getId(),report, null));
            loadReport.execute();
        } else {
            Toast.makeText(this, getString(R.string.err_internet_not_conn), Toast.LENGTH_SHORT).show();
        }
    }

    private void showReportDialog() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        View view = inflater.inflate(R.layout.layout_report, null);

        dialog_setas = new BottomSheetDialog(NewsDetailsActivity.this);
        dialog_setas.setContentView(view);
        dialog_setas.getWindow().findViewById(R.id.design_bottom_sheet).setBackgroundResource(android.R.color.transparent);
        dialog_setas.show();

        final EditText editText_report;
        Button button_submit;

        button_submit = dialog_setas.findViewById(R.id.button_report_submit);
        editText_report = dialog_setas.findViewById(R.id.et_report);

        button_submit.setBackground(methods.getRoundDrawable(getResources().getColor(R.color.colorPrimary)));

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText_report.getText().toString().trim().isEmpty()) {
                    Toast.makeText(NewsDetailsActivity.this, getString(R.string.enter_report), Toast.LENGTH_SHORT).show();
                } else {
                    if(Constant.isLogged) {
//                        loadReportSubmit(editText_report.getText().toString());
                        Toast.makeText(NewsDetailsActivity.this, "Report is not available in demo app", Toast.LENGTH_SHORT).show();
                    } else {
                        methods.clickLogin();
                    }
                }
            }
        });
    }

    public void setFavImage(Boolean isFav, MenuItem menuItem) {
        if (isFav) {
            menuItem.setIcon(getResources().getDrawable(R.drawable.fav_hover));
        } else {
            menuItem.setIcon(getResources().getDrawable(R.drawable.fav));
        }
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onProfileChange(ItemEventBus itemEventBus) {
        if(itemEventBus.getMessage().equals(getString(R.string.comments)) || itemEventBus.getMessage().equals(getString(R.string.comment_old))) {
            boolean isNew = true;

            for (int i = 0; i < arrayListComments.size(); i++) {
                if(arrayListComments.get(i).getId().equals(itemEventBus.getItemComment().getId())) {
                    isNew = false;
                    break;
                }
            }

            if(isNew) {
                if(itemEventBus.getMessage().equals(getString(R.string.comment_old))) {
                    arrayListComments.add(itemEventBus.getItemComment());
                } else {
                    arrayListComments.add(0, itemEventBus.getItemComment());
                }
                adapterComm.notifyDataSetChanged();
                rv_comment.setVisibility(View.VISIBLE);
                textView_empty_comment.setVisibility(View.GONE);
                editText_comment.setText("");

                rv_comment.smoothScrollToPosition(0);
            }

        } else if(itemEventBus.getMessage().equals(getString(R.string.delete))) {
            if(arrayListComments.get(itemEventBus.getPos()).getId().equals(itemEventBus.getItemComment().getId())) {
                arrayListComments.remove(itemEventBus.getPos());
                adapterComm.notifyDataSetChanged();
                setEmpty();
            }
        }
        GlobalBus.getBus().removeStickyEvent(itemEventBus);
    }

    @Override
    public void onStart() {
        super.onStart();
        GlobalBus.getBus().register(this);
    }

    @Override
    public void onStop() {
        GlobalBus.getBus().unregister(this);
        super.onStop();
    }
}