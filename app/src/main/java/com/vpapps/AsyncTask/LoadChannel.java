package com.vpapps.AsyncTask;

import android.os.AsyncTask;

import com.vpapps.interfaces.ChannelListener;
import com.vpapps.utils.Constant;
import com.vpapps.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.RequestBody;

public class LoadChannel extends AsyncTask<String, String, String> {

    private ChannelListener channelListener;
    private RequestBody requestBody;
    private String name, desc, url, type;
    private String verifyStatus = "0", message = "";

    public LoadChannel(ChannelListener channelListener, RequestBody requestBody) {
        this.channelListener = channelListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        channelListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            JSONObject mainJson = new JSONObject(JsonUtils.okhttpPost(Constant.SERVER_URL, requestBody));
            JSONArray jsonArray = mainJson.getJSONArray(Constant.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);

                if (!obj.has(Constant.TAG_SUCCESS)) {
                    name = obj.getString(Constant.TAG_CHANNEL_NAME);
                    url = obj.getString(Constant.TAG_CHANNEL_URL);
                    desc = obj.getString(Constant.TAG_CHANNEL_DESC);
                    type = obj.getString(Constant.TAG_CHANNEL_TYPE);
                } else {
                    verifyStatus = obj.getString(Constant.TAG_SUCCESS);
                    message = obj.getString(Constant.TAG_MSG);
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
        channelListener.onEnd(s,verifyStatus,message, name, desc, url, type);
        super.onPostExecute(s);
    }
}