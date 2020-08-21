package com.vpapps.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vpapps.allinonenewsapp.BuildConfig;
import com.vpapps.allinonenewsapp.LiveChannelActivity;
import com.vpapps.allinonenewsapp.LoginActivity;
import com.vpapps.allinonenewsapp.R;
import com.vpapps.interfaces.InterAdListener;
import com.vpapps.item.ItemNews;
import com.vpapps.item.ItemUser;
import com.yakivmospan.scytale.Crypto;
import com.yakivmospan.scytale.Options;
import com.yakivmospan.scytale.Store;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;

import androidx.appcompat.app.AlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;


public class Methods {

    private Context context;
    private InterstitialAd mInterstitial;
    private InterAdListener interAdListener;
    private ItemNews itemNews;
    private Typeface typeface;
    private SecretKey key;

    public Methods(Context context) {
        this.context = context;
        typeface = Typeface.createFromAsset(this.context.getAssets(), "fonts/pop_med.ttf");

        Store store = new Store(context);
        if (!store.hasKey(BuildConfig.ENC_KEY)) {
            key = store.generateSymmetricKey(BuildConfig.ENC_KEY, null);
        } else {
            key = store.getSymmetricKey(BuildConfig.ENC_KEY, null);
        }
    }

    public Methods(Context context, InterAdListener interAdListener) {
        this.context = context;
        loadInter();
        this.interAdListener = interAdListener;

        Store store = new Store(context);
        if (!store.hasKey(BuildConfig.ENC_KEY)) {
            key = store.generateSymmetricKey(BuildConfig.ENC_KEY, null);
        } else {
            key = store.getSymmetricKey(BuildConfig.ENC_KEY, null);
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfoMob = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo netInfoWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return (netInfoMob != null && netInfoMob.isConnectedOrConnecting()) || (netInfoWifi != null && netInfoWifi.isConnectedOrConnecting());
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }

    public void openTV() {
        Intent intent = new Intent(context, LiveChannelActivity.class);
        context.startActivity(intent);
    }

    public void openLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("from", "app");
        context.startActivity(intent);
    }

    public void setLoginButton(MenuItem buttonLogin, MenuItem buttonProfile, Context context) {
        if (Constant.isLogged) {
            buttonLogin.setTitle(context.getResources().getString(R.string.logout));
            buttonLogin.setIcon(context.getResources().getDrawable(R.drawable.logout));
            buttonProfile.setVisible(true);
        } else {
            buttonLogin.setTitle(context.getResources().getString(R.string.login));
            buttonLogin.setIcon(context.getResources().getDrawable(R.drawable.login));
            buttonProfile.setVisible(false);
        }
    }

    public void logout(Activity activity) {
        SharedPref sharedPref = new SharedPref(context);
        sharedPref.setCat("");
        changeAutoLogin(false);
        Constant.isLogged = false;
        Constant.itemUser = new ItemUser("", "", "", "", "null");
        Intent intent1 = new Intent(context, LoginActivity.class);
        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent1.putExtra("from", "");
        context.startActivity(intent1);
        activity.finish();
    }

    public void changeAutoLogin(Boolean isAutoLogin) {
        SharedPref sharePref = new SharedPref(context);
        sharePref.setIsAutoLogin(isAutoLogin);
    }

    public void clickLogin() {
        if (Constant.isLogged) {
            logout((Activity) context);
            Toast.makeText(context, context.getString(R.string.logout_success), Toast.LENGTH_SHORT).show();
        } else {
            openLogin();
        }
    }

    public void setStatusColor(Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    public boolean isPackageInstalled(String packagename) {
        try {
            return context.getPackageManager().getApplicationInfo(packagename, 0).enabled;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void startVideoPlay(String vid) {
        Intent intent;
        if (isPackageInstalled("com.google.android.youtube")) {
            intent = YouTubeStandalonePlayer.createVideoIntent((Activity) context, BuildConfig.API_KEY, vid, 0, true, false);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + vid));
        }
        context.startActivity(intent);
    }

    public void setFavImage(Boolean isFav, ImageView imageView) {
        if (isFav) {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.fav_hover));
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.fav));
        }
    }

    public void shareNews(ItemNews News) {
        itemNews = News;
        saveImage(Constant.URL_IMAGE + itemNews.getImage(), itemNews.getImage().replace("images/", ""));
    }

    private void saveImage(String img_url, String name) {
        new LoadShare().execute(img_url, name);
    }

    public class LoadShare extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String filepath = "";
            String name = strings[1];
            try {
                File SDCardRoot = context.getExternalCacheDir().getAbsoluteFile();
                File file = new File(SDCardRoot, name);
                if (!file.exists()) {
                    URL url = new URL(strings[0]);

                    InputStream inputStream;
                    int totalSize;

                    if (BuildConfig.SERVER_URL.contains("https://")) {
                        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoOutput(true);
                        urlConnection.connect();
                        inputStream = urlConnection.getInputStream();
                        totalSize = urlConnection.getContentLength();
                    } else {
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoOutput(true);
                        urlConnection.connect();
                        inputStream = urlConnection.getInputStream();
                        totalSize = urlConnection.getContentLength();
                    }

                    Log.i("Local filename:", "" + name);
                    if (file.createNewFile()) {
                        file.createNewFile();
                    }
                    FileOutputStream fileOutput = new FileOutputStream(file);

                    int downloadedSize = 0;
                    byte[] buffer = new byte[1024];
                    int bufferLength = 0;
                    while ((bufferLength = inputStream.read(buffer)) > 0) {
                        fileOutput.write(buffer, 0, bufferLength);
                        downloadedSize += bufferLength;
                        Log.i("Progress:", "downloadedSize:" + downloadedSize + "totalSize:" + totalSize);
                    }
                    fileOutput.close();
                    if (downloadedSize == totalSize) {
                        filepath = file.getPath();
                    }
                } else {
                    filepath = file.getPath();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                filepath = null;
                e.printStackTrace();
            }
            Log.i("filepath:", " " + filepath);
            return filepath;
        }

        @Override
        protected void onPostExecute(String s) {

            Spanned spanned = Html.fromHtml(itemNews.getDesc());
            char[] chars = new char[spanned.length()];
            TextUtils.getChars(spanned, 0, spanned.length(), chars, 0);
            String plainText = new String(chars);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");

            if (!itemNews.getVideoUrl().trim().isEmpty()) {
                intent.putExtra(Intent.EXTRA_TEXT, itemNews.getHeading() + "\n\n" + itemNews.getVideoUrl() + "\n\n" + plainText + "\n\n" + context.getString(R.string.share_message) + "\n" + context.getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + context.getPackageName());
            } else {
                intent.putExtra(Intent.EXTRA_TEXT, itemNews.getHeading() + "\n\n" + plainText + "\n\n" + context.getString(R.string.share_message) + "\n" + context.getString(R.string.app_name) + " - http://play.google.com/store/apps/details?id=" + context.getPackageName());
            }
            intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + s));
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.share_news)));
            super.onPostExecute(s);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void forceRTLIfSupported(Window window) {
        if (context.getResources().getString(R.string.isRTL).equals("true")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                window.getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }
    }

    public GradientDrawable getRoundDrawable(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.mutate();
        gd.setCornerRadius(10);
        return gd;
    }

    public GradientDrawable getRoundDrawableRadis(int color, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(color);
        gd.mutate();
        gd.setCornerRadius(radius);
        return gd;
    }

    public String encrypt(String value) {
        try {
            Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);
            String encryptedData = crypto.encrypt(value, key);
            return encryptedData;
        } catch (Exception e) {
            Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);
            String encryptedData = crypto.encrypt("null", key);
            return encryptedData;
        }
    }

    public String decrypt(String value) {
        try {
            Crypto crypto = new Crypto(Options.TRANSFORMATION_SYMMETRIC);
            String decryptedData = crypto.decrypt(value, key);
            return decryptedData;
        } catch (Exception e) {
            e.printStackTrace();
            return "null";
        }
    }

    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    private void showPersonalizedAds(LinearLayout linearLayout) {
        if (Constant.isBannerAd) {
            AdView adView = new AdView(context);
            AdRequest adRequest = new AdRequest.Builder().build();
            adView.setAdUnitId(Constant.ad_banner_id);
            adView.setAdSize(AdSize.BANNER);
            linearLayout.addView(adView);
            adView.loadAd(adRequest);
        }
    }

    private void showNonPersonalizedAds(LinearLayout linearLayout) {
        Bundle extras = new Bundle();
        extras.putString("npa", "1");
        if (Constant.isBannerAd) {
            AdView adView = new AdView(context);
            AdRequest adRequest = new AdRequest.Builder()
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .build();
            adView.setAdUnitId(Constant.ad_banner_id);
            adView.setAdSize(AdSize.BANNER);
            linearLayout.addView(adView);
            adView.loadAd(adRequest);
        }
    }

    public void showBannerAd(LinearLayout linearLayout) {
        if (isNetworkAvailable()) {
            if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.NON_PERSONALIZED) {
                showNonPersonalizedAds(linearLayout);
            } else {
                showPersonalizedAds(linearLayout);
            }
        }
    }

    private void loadInter() {
        mInterstitial = new InterstitialAd(context);
        if (Constant.isInterAd) {
            AdRequest adRequest;
            if (ConsentInformation.getInstance(context).getConsentStatus() == ConsentStatus.PERSONALIZED) {
                adRequest = new AdRequest.Builder().build();
            } else {
                Bundle extras = new Bundle();
                extras.putString("npa", "1");
                adRequest = new AdRequest.Builder()
                        .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                        .build();
            }
            mInterstitial.setAdUnitId(Constant.ad_inter_id);
            mInterstitial.loadAd(adRequest);
        }
    }

    public void showInterAd(final int pos, final String type) {
        Constant.adCount = Constant.adCount + 1;
        if (Constant.adCount % Constant.adShow == 0) {
            mInterstitial.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                    interAdListener.onClick(pos, type);
                    super.onAdClosed();
                }
            });
            if (mInterstitial.isLoaded()) {
                mInterstitial.show();
            } else {
                interAdListener.onClick(pos, type);
            }
            loadInter();
        } else {
            interAdListener.onClick(pos, type);
        }
    }

    public String getPathImage(Uri uri) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                String filePath = "";
                String wholeID = DocumentsContract.getDocumentId(uri);

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                String[] column = {MediaStore.Images.Media.DATA};

                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);

                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
                return filePath;
            } else {

                if (uri == null) {
                    return null;
                }
                // try to retrieve the image from the media store first
                // this will only work for images selected from gallery
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
                if (cursor != null) {
                    int column_index = cursor
                            .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    cursor.moveToFirst();
                    String retunn = cursor.getString(column_index);
                    cursor.close();
                    return retunn;
                }
                // this is our fallback here
                return uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (uri == null) {
                return null;
            }
            // try to retrieve the image from the media store first
            // this will only work for images selected from gallery
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                String returnn = cursor.getString(column_index);
                cursor.close();
                return returnn;
            }
            // this is our fallback here
            return uri.getPath();
        }
    }

    public Typeface getFontMedium() {
        return typeface;
    }

    public String getImageThumbSize(String imagePath, String type) {
        if (type.equals(context.getString(R.string.categories))) {
            imagePath = imagePath.replace("&size=300x300", "&size=400x300");
        } else if (type.equals("homecat")) {
            imagePath = imagePath.replace("&size=300x300", "&size=250x150");
        } else if (type.equals("banner")) {
            imagePath = imagePath.replace("&size=300x300", "&size=550x350");
        } else if (type.equals(context.getString(R.string.home))) {
            imagePath = imagePath.replace("&size=300x300", "&size=300x250");
        } else if (type.equals("header")) {
            imagePath = imagePath.replace("&size=300x300", "&size=500x250");
        } else if (type.equals("notheader")) {
            imagePath = imagePath.replace("&size=300x300", "&size=400x300");
        }

        return imagePath;
    }

    public void getVerifyDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.ThemeDialog);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                finish();
            }
        });
        alertDialog.show();
    }

    public RequestBody getAPIRequest(String method, int page, String deviceID, String newsID, String searchText, String searchType, String catID, String rate, String email, String password, String name, String phone, String userID, String reportMessage, File file) {
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("method_name", method);
        jsObj.addProperty("package_name", context.getPackageName());

        switch (method) {
            case Constant.METHOD_HOME:

                jsObj.addProperty("cat_id", catID);

                break;
            case Constant.METHOD_APP_DETAILS:

                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_LOGIN:

                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);

                break;
            case Constant.METHOD_REGISTER:

                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);

                break;
            case Constant.METHOD_FORGOT_PASSWORD:

                jsObj.addProperty("email", email);

                break;
            case Constant.METHOD_PROFILE:

                jsObj.addProperty("user_id", userID);

                break;
            case Constant.METHOD_PROFILE_EDIT:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);

                break;
            case Constant.METHOD_LATEST:

                jsObj.addProperty("page", page);

                break;
            case Constant.METHOD_VIDEO_NEWS:

                jsObj.addProperty("page", page);
                jsObj.addProperty("cat_id", catID);

                break;
            case Constant.METHOD_LATEST_USER:

                jsObj.addProperty("page", page);
                jsObj.addProperty("cat_id", catID);

                break;
            case Constant.METHOD_NEWS_BY_CAT:

                jsObj.addProperty("page", page);
                jsObj.addProperty("cat_id", catID);

                break;
            case Constant.METHOD_SEARCH:

                jsObj.addProperty("page", page);
                jsObj.addProperty("search_text", searchText);

                break;
            case Constant.METHOD_COMMENTS:

                jsObj.addProperty("page", page);
                jsObj.addProperty("news_id", newsID);

                break;
            case Constant.METHOD_POST_COMMENTS:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("news_id", newsID);
                jsObj.addProperty("comment_text", searchText);

                break;
            case Constant.METHOD_SINGLE_NEWS:

                jsObj.addProperty("news_id", newsID);

                break;
            case Constant.METHOD_SAVE_CATEGORY:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("cat_id", catID);

                break;
            case Constant.METHOD_REPORT:

                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("news_id", newsID);
                jsObj.addProperty("report", reportMessage);

                break;
            case Constant.METHOD_DELETE_COMMENTS:

                jsObj.addProperty("comment_id", catID);
                jsObj.addProperty("news_id", newsID);

                break;
        }

        if (file != null) {
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("profile_img", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file))
                    .addFormDataPart("data", API.toBase64(jsObj.toString()))
                    .build();
        } else {
            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("data", API.toBase64(jsObj.toString()))
                    .build();
        }
    }
}
