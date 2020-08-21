package com.vpapps.utils;

import com.vpapps.allinonenewsapp.BuildConfig;
import com.vpapps.item.ItemAbout;
import com.vpapps.item.ItemCat;
import com.vpapps.item.ItemNews;
import com.vpapps.item.ItemUser;

import java.io.Serializable;
import java.util.ArrayList;

public class Constant implements Serializable {

    public static String SERVER_URL = BuildConfig.SERVER_URL + "api.php";

    public static final String METHOD_APP_DETAILS = "get_app_details";
    public static final String METHOD_LOGIN = "user_login";
    public static final String METHOD_REGISTER = "user_register";
    public static final String METHOD_FORGOT_PASSWORD = "forgot_pass";
    public static final String METHOD_REPORT = "user_report";
    public static final String METHOD_PROFILE = "user_profile";
    public static final String METHOD_PROFILE_EDIT = "edit_profile";

    public static final String METHOD_HOME = "get_home";
    public static final String METHOD_LATEST = "get_latest";
    public static final String METHOD_LATEST_USER = "get_category_latest";
    public static final String METHOD_VIDEO_NEWS = "get_video_news";
    public static final String METHOD_CATEGORY = "get_category";
    public static final String METHOD_NEWS_BY_CAT = "get_news";
    public static final String METHOD_SINGLE_NEWS = "get_single_news";
    public static final String METHOD_SEARCH = "get_search";
    public static final String METHOD_CHANNEL = "get_channel";
    public static final String METHOD_SAVE_CATEGORY = "save_category";

    public static final String METHOD_COMMENTS = "get_comments";
    public static final String METHOD_POST_COMMENTS = "user_comment";
    public static final String METHOD_DELETE_COMMENTS = "remove_comment";

    public static final String URL_IMAGE = BuildConfig.SERVER_URL;

    public static final String TAG_ROOT = "ALL_IN_ONE_NEWS";
    public static String TAG_TRENDING = "trending_news";
    public static String TAG_TOPSTORIES = "top_story";
    public static String TAG_LATEST = "latest_news";

    public static final String TAG_CID = "cid";
    public static final String TAG_CAT_ID = "cat_id";
    public static final String TAG_CATEGORY = "category";
    public static final String TAG_CAT_NAME = "category_name";
    public static final String TAG_CAT_IMAGE = "category_image";
    public static final String TAG_CAT_IMAGE_THUMB = "category_image_thumb";

    public static final String TAG_MSG = "MSG";
    public static final String TAG_SUCCESS = "success";

    public static final String TAG_USER_ID = "user_id";
    public static final String TAG_USER_NAME = "user_name";
    public static final String TAG_USER_EMAIL = "user_email";
    public static final String TAG_USER_DP = "user_profile";

    public static final String TAG_ID = "id";
    public static final String TAG_NEWS_TYPE = "news_type";
    public static final String TAG_NEWS_HEADING = "news_heading";
    public static final String TAG_NEWS_DESC = "news_description";
    public static final String TAG_NEWS_VIDEO_ID = "news_video_id";
    public static final String TAG_NEWS_VIDEO_URL = "news_video_url";
    public static final String TAG_NEWS_DATE = "news_date";
    public static final String TAG_NEWS_IMAGE = "news_featured_image";
    public static final String TAG_NEWS_IMAGE_THUMB = "news_featured_thumb";
    public static final String TAG_GALLERY = "galley_image";
    public static final String TAG_COMMENT = "user_comments";
    public static final String TAG_COMMENT_ID = "comment_id";
    public static final String TAG_COMMENT_ON = "comment_on";
    public static final String TAG_COMMENT_TEXT = "comment_text";
    public static final String TAG_IMAGE_NAME = "image_name";
    public static final String TAG_TOTAL_VIEWS = "total_views";

    public static final String TAG_CHANNEL_NAME = "channel_name";
    public static final String TAG_CHANNEL_URL = "channel_url";
    public static final String TAG_CHANNEL_DESC = "channel_description";
    public static final String TAG_CHANNEL_TYPE = "channel_type";


    public static ArrayList<ItemCat> arrayList_cat = new ArrayList<>();

    public static ArrayList<ItemNews> arrayList_topstories = new ArrayList<>();
    public static ItemNews itemNewsCurrent;
    public static int selected_news_pos = 0;
    public static ItemUser itemUser = new ItemUser("","","","","null");

    public static Boolean isUpdate = false, isLogged = false;
    public static Boolean isBannerAd = true, isInterAd = true, channelStatus = true;

    public static ItemAbout itemAbout;
    public static String pushNID = "0";

    public static String packageName = "";
    public static String search_text = "";
    public static String ad_publisher_id = "";
    public static String ad_banner_id = "";
    public static String ad_inter_id = "";

    public static int adCount = 1;
    public static int adShow = 3;
}