package com.vpapps.AsyncTask;

import android.os.AsyncTask;

import com.vpapps.interfaces.CatListener;
import com.vpapps.item.ItemCat;
import com.vpapps.utils.Constant;
import com.vpapps.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class LoadCat extends AsyncTask<String, String, String> {

    private CatListener catListener;
    private RequestBody requestBody;
    private ArrayList<ItemCat> arrayList = new ArrayList<>();
    private String verifyStatus = "0", message = "";

    public LoadCat(CatListener catListener, RequestBody requestBody) {
        this.catListener = catListener;
        this.requestBody = requestBody;
    }

    @Override
    protected void onPreExecute() {
        catListener.onStart();
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
                    String id = obj.getString(Constant.TAG_CID);
                    String name = obj.getString(Constant.TAG_CAT_NAME);
                    String image = obj.getString(Constant.TAG_CAT_IMAGE);
                    String image_thumb = obj.getString(Constant.TAG_CAT_IMAGE_THUMB);

                    ItemCat itemCat = new ItemCat(id, name, image, image_thumb);
                    arrayList.add(itemCat);
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
        catListener.onEnd(s, verifyStatus, message, arrayList);
        super.onPostExecute(s);
    }
}