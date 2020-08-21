package com.vpapps.AsyncTask;

import android.os.AsyncTask;

import com.vpapps.interfaces.NewsListener;
import com.vpapps.item.ItemNews;
import com.vpapps.utils.Constant;
import com.vpapps.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadNews extends AsyncTask<String, String, String> {

    private NewsListener newsListener;
    private RequestBody requestBody;
    private ArrayList<ItemNews> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";

    public LoadNews(NewsListener newsListener, RequestBody requestBody) {
        this.newsListener = newsListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        newsListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                if (!obj.has(Constant.TAG_SUCCESS)) {
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

                    ItemNews itemNews = new ItemNews(id, cat_id, cat_name, type, heading, desc, video_id, video_url, date, image,image_thumb,  tot_views, null);
                    arrayList.add(itemNews);
                } else {
                    verifyStatus = obj.getString(Constant.TAG_SUCCESS);
                    message = obj.getString(Constant.TAG_MSG);                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        newsListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}
