package com.vpapps.AsyncTask;

import android.os.AsyncTask;

import com.vpapps.interfaces.HomeListener;
import com.vpapps.item.ItemCat;
import com.vpapps.item.ItemNews;
import com.vpapps.utils.Constant;
import com.vpapps.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadHome extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private HomeListener homeListener;
    private ArrayList<ItemNews> arrayListLatest = new ArrayList<>();
    private ArrayList<ItemNews> arrayListTrending = new ArrayList<>();
    private ArrayList<ItemNews> arrayListTopstories = new ArrayList<>();
    private ArrayList<ItemCat> arrayListCat = new ArrayList<>();

    public LoadHome(HomeListener homeListener, RequestBody requestBody) {
        this.homeListener = homeListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        homeListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);

            JSONObject mainJson = new JSONObject(json);
            JSONObject jsonObj = mainJson.getJSONObject(Constant.TAG_ROOT);

            JSONArray jsonArray = jsonObj.getJSONArray(Constant.TAG_LATEST);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                String id = obj.getString(Constant.TAG_ID);
                String cat_id = obj.getString(Constant.TAG_CAT_ID);
                String cat_name = obj.getString(Constant.TAG_CAT_NAME);
                String type = obj.getString(Constant.TAG_NEWS_TYPE);
                String heading = obj.getString(Constant.TAG_NEWS_HEADING);
                String desc = obj.getString(Constant.TAG_NEWS_DESC);
                String video_id = obj.getString(Constant.TAG_NEWS_VIDEO_ID);
                String video_url = obj.getString(Constant.TAG_NEWS_VIDEO_URL);
                String date = obj.getString(Constant.TAG_NEWS_DATE);
                String image = obj.getString(Constant.TAG_NEWS_IMAGE);
                String image_thumb = obj.getString(Constant.TAG_NEWS_IMAGE_THUMB);
                String tot_views = obj.getString(Constant.TAG_TOTAL_VIEWS);

                ItemNews itemNews = new ItemNews(id, cat_id, cat_name, type, heading, desc, video_id, video_url, date, image, image_thumb, tot_views, null);
                arrayListLatest.add(itemNews);
            }

            JSONArray jsonArrayTren = jsonObj.getJSONArray(Constant.TAG_TRENDING);
            for (int i = 0; i < jsonArrayTren.length(); i++) {
                JSONObject obj = jsonArrayTren.getJSONObject(i);
                String id = obj.getString(Constant.TAG_ID);
                String cat_id = obj.getString(Constant.TAG_CAT_ID);
                String cat_name = obj.getString(Constant.TAG_CAT_NAME);
                String type = obj.getString(Constant.TAG_NEWS_TYPE);
                String heading = obj.getString(Constant.TAG_NEWS_HEADING);
                String desc = obj.getString(Constant.TAG_NEWS_DESC);
                String video_id = obj.getString(Constant.TAG_NEWS_VIDEO_ID);
                String video_url = obj.getString(Constant.TAG_NEWS_VIDEO_URL);
                String date = obj.getString(Constant.TAG_NEWS_DATE);
                String image = obj.getString(Constant.TAG_NEWS_IMAGE);
                String image_thumb = obj.getString(Constant.TAG_NEWS_IMAGE_THUMB);
                String tot_views = obj.getString(Constant.TAG_TOTAL_VIEWS);

                ItemNews itemNews = new ItemNews(id, cat_id, cat_name, type, heading, desc, video_id, video_url, date, image, image_thumb, tot_views, null);
                arrayListTrending.add(itemNews);
            }

            JSONArray jsonArrayTop = jsonObj.getJSONArray(Constant.TAG_TOPSTORIES);
            for (int i = 0; i < jsonArrayTop.length(); i++) {
                JSONObject obj = jsonArrayTop.getJSONObject(i);
                String id = obj.getString(Constant.TAG_ID);
                String cat_id = obj.getString(Constant.TAG_CAT_ID);
                String cat_name = obj.getString(Constant.TAG_CAT_NAME);
                String type = obj.getString(Constant.TAG_NEWS_TYPE);
                String heading = obj.getString(Constant.TAG_NEWS_HEADING);
                String desc = obj.getString(Constant.TAG_NEWS_DESC);
                String video_id = obj.getString(Constant.TAG_NEWS_VIDEO_ID);
                String video_url = obj.getString(Constant.TAG_NEWS_VIDEO_URL);
                String date = obj.getString(Constant.TAG_NEWS_DATE);
                String image = obj.getString(Constant.TAG_NEWS_IMAGE);
                String image_thumb = obj.getString(Constant.TAG_NEWS_IMAGE_THUMB);
                String tot_views = obj.getString(Constant.TAG_TOTAL_VIEWS);

                ItemNews itemNews = new ItemNews(id, cat_id, cat_name, type, heading, desc, video_id, video_url, date, image, image_thumb, tot_views, null);
                arrayListTopstories.add(itemNews);
            }

            JSONArray jsonArrayCat = jsonObj.getJSONArray(Constant.TAG_CATEGORY);
            for (int i = 0; i < jsonArrayCat.length(); i++) {
                JSONObject obj = jsonArrayCat.getJSONObject(i);
                String id = obj.getString(Constant.TAG_CID);
                String cat_name = obj.getString(Constant.TAG_CAT_NAME);
                String cat_image = obj.getString(Constant.TAG_CAT_IMAGE);
                String cat_image_thumb = obj.getString(Constant.TAG_CAT_IMAGE_THUMB);

                ItemCat itemCat = new ItemCat(id, cat_name, cat_image, cat_image_thumb);
                arrayListCat.add(itemCat);
            }

            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        homeListener.onEnd(s, arrayListLatest, arrayListTrending, arrayListTopstories, arrayListCat);
        super.onPostExecute(s);
    }
}