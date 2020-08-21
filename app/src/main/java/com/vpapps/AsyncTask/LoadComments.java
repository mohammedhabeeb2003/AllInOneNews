package com.vpapps.AsyncTask;

import android.os.AsyncTask;

import com.vpapps.interfaces.CommentListener;
import com.vpapps.item.ItemComment;
import com.vpapps.utils.Constant;
import com.vpapps.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadComments extends AsyncTask<String, String, String> {

    private CommentListener commentListener;
    private RequestBody requestBody;
    private ArrayList<ItemComment> arrayList = new ArrayList<>();

    public LoadComments(CommentListener commentListener, RequestBody requestBody) {
        this.commentListener = commentListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        commentListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject jsonObject = new JSONObject(json);

            JSONArray jsonArray = jsonObject.getJSONArray(Constant.TAG_ROOT);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj_com = jsonArray.getJSONObject(i);

                String nid = obj_com.getString(Constant.TAG_COMMENT_ID);
                String uid = obj_com.getString(Constant.TAG_USER_ID);
                String user_name = obj_com.getString(Constant.TAG_USER_NAME);
                String user_email = obj_com.getString(Constant.TAG_USER_EMAIL);
                String user_dp = obj_com.getString(Constant.TAG_USER_DP);
                String comment_text = obj_com.getString(Constant.TAG_COMMENT_TEXT);
                String comment_date = obj_com.getString(Constant.TAG_COMMENT_ON);

                ItemComment itemComment = new ItemComment(nid, uid, user_name, user_email, comment_text, user_dp, comment_date);
                arrayList.add(itemComment);
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        commentListener.onEnd(s, arrayList);
        super.onPostExecute(s);
    }
}
