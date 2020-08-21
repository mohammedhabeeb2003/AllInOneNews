package com.vpapps.AsyncTask;

import android.os.AsyncTask;

import com.vpapps.interfaces.SingleNewsListener;
import com.vpapps.item.ItemComment;
import com.vpapps.item.ItemNews;
import com.vpapps.utils.Constant;
import com.vpapps.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadSingleNews extends AsyncTask<String, String, String> {

    private SingleNewsListener singleNewsListener;
    private RequestBody requestBody;
    private ItemNews itemNews;
    private ArrayList<ItemComment> arrayListComments = new ArrayList<>();

    public LoadSingleNews(SingleNewsListener singleNewsListener, RequestBody requestBody) {
        this.singleNewsListener = singleNewsListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        singleNewsListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            ArrayList<String> array = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                JSONArray gallery = obj.getJSONArray(Constant.TAG_GALLERY);
                if (gallery.length() > 0) {
                    try {
                        for (int j = 0; j < gallery.length(); j++) {
                            JSONObject obj_gallery = gallery.getJSONObject(j);
                            String image = obj_gallery.getString("image_name");
                            array.add(image);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                JSONArray comment = obj.getJSONArray(Constant.TAG_COMMENT);
                if (comment.length() > 0) {
                    try {
                        for (int j = 0; j < comment.length(); j++) {
                            JSONObject obj_com = comment.getJSONObject(j);
                            String nid = obj_com.getString(Constant.TAG_COMMENT_ID);
                            String uid = obj_com.getString(Constant.TAG_USER_ID);
                            String user_name = obj_com.getString(Constant.TAG_USER_NAME);
                            String user_email = obj_com.getString(Constant.TAG_USER_EMAIL);
                            String user_profile = obj_com.getString(Constant.TAG_USER_DP);
                            String comment_text = obj_com.getString(Constant.TAG_COMMENT_TEXT);
                            String comment_date = obj_com.getString(Constant.TAG_COMMENT_ON);

                            ItemComment itemComment = new ItemComment(nid, uid, user_name, user_email, comment_text,user_profile, comment_date);
                            arrayListComments.add(itemComment);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            JSONObject obj = jsonArray.getJSONObject(0);
            String id = obj.getString(Constant.TAG_ID);
            String cat_id = obj.getString(Constant.TAG_CID);
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

            itemNews = new ItemNews(id, cat_id, cat_name, type, heading, desc, video_id, video_url, date, image, image_thumb, tot_views, array);
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        singleNewsListener.onEnd(s, itemNews, arrayListComments);
        super.onPostExecute(s);
    }
}