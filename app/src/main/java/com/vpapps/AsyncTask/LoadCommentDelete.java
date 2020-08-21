package com.vpapps.AsyncTask;

import android.os.AsyncTask;

import com.vpapps.interfaces.CommentDeleteListener;
import com.vpapps.item.ItemComment;
import com.vpapps.utils.Constant;
import com.vpapps.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadCommentDelete extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private ItemComment itemComment;
    private CommentDeleteListener commentDeleteListener;
    private String success = "0", message = "";

    public LoadCommentDelete(CommentDeleteListener commentDeleteListener, RequestBody requestBody) {
        this.commentDeleteListener = commentDeleteListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        commentDeleteListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody);
            JSONObject mainJson = new JSONObject(json);
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject c = jsonArray.getJSONObject(i);

                success = c.getString(Constant.TAG_SUCCESS);
                message = c.getString(Constant.TAG_MSG);

                try {
                    String nid = c.getString(Constant.TAG_COMMENT_ID);
                    String uid = c.getString(Constant.TAG_USER_ID);
                    String user_name = c.getString(Constant.TAG_USER_NAME);
                    String user_email = c.getString(Constant.TAG_USER_EMAIL);
                    String user_dp = c.getString(Constant.TAG_USER_DP);
                    String comment_text = c.getString(Constant.TAG_COMMENT_TEXT);
                    String comment_date = c.getString(Constant.TAG_COMMENT_ON);

                    itemComment = new ItemComment(nid, uid, user_name, user_email, comment_text, user_dp, comment_date);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return "1";
        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        commentDeleteListener.onEnd(s, success, message, itemComment);
        super.onPostExecute(s);
    }
}