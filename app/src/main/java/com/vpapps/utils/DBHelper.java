package com.vpapps.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vpapps.item.ItemAbout;
import com.vpapps.item.ItemNews;

import java.util.ArrayList;
import java.util.Collections;

public class DBHelper extends SQLiteOpenHelper {

    private Methods methods;
    private static String DB_NAME = "newsall.db";
    private SQLiteDatabase db;

    private static final String TABLE_ABOUT = "about";
    private static final String TABLE_NEWS = "fav";
    private static final String TABLE_RECENT = "recent";

    private static String TAG_ID = "id";
    private static final String TAG_NID = "nid";
    private static final String TAG_CAT_ID = "cid";
    private static final String TAG_CAT_NAME = "cname";
    private static final String TAG_IMAGE_BIG = "img";
    private static final String TAG_IMAGE_SMALL = "img_thumb";
    private static final String TAG_TYPE = "type";
    private static final String TAG_HEADING = "heading";
    private static final String TAG_DESC = "description";
    private static final String TAG_VIDEO_ID = "vid";
    private static final String TAG_VIDEO_URL = "vurl";
    private static final String TAG_DATE = "date";
    private static final String TAG_TOT_VIEWS = "views";
    private static final String TAG_IMAGES = "images";

    private static final String TAG_ABOUT_NAME = "name";
    private static final String TAG_ABOUT_LOGO = "logo";
    private static final String TAG_ABOUT_VERSION = "version";
    private static final String TAG_ABOUT_AUTHOR = "author";
    private static final String TAG_ABOUT_CONTACT = "contact";
    private static final String TAG_ABOUT_EMAIL = "email";
    private static final String TAG_ABOUT_WEBSITE = "website";
    private static final String TAG_ABOUT_DESC = "description";
    private static final String TAG_ABOUT_DEVELOPED = "developed";
    private static final String TAG_ABOUT_PRIVACY = "privacy";
    private static final String TAG_ABOUT_PUB_ID = "ad_pub";
    private static final String TAG_ABOUT_BANNER_ID = "ad_banner";
    private static final String TAG_ABOUT_INTER_ID = "ad_inter";
    private static final String TAG_ABOUT_IS_BANNER = "isbanner";
    private static final String TAG_ABOUT_IS_INTER = "isinter";
    private static final String TAG_ABOUT_IS_PORTRAIT = "isportrait";
    private static final String TAG_ABOUT_IS_LANDSCAPE = "islandscape";
    private static final String TAG_ABOUT_IS_SQUARE = "issquare";
    private static final String TAG_ABOUT_CLICK = "click";

    private String[] columns_news = new String[]{TAG_ID, TAG_NID, TAG_CAT_ID, TAG_CAT_NAME, TAG_IMAGE_SMALL, TAG_IMAGE_BIG, TAG_TYPE,
            TAG_HEADING, TAG_DESC, TAG_VIDEO_ID, TAG_VIDEO_URL, TAG_DATE, TAG_IMAGES, TAG_TOT_VIEWS};

    private String[] columns_about = new String[]{TAG_ABOUT_NAME, TAG_ABOUT_LOGO, TAG_ABOUT_VERSION, TAG_ABOUT_AUTHOR, TAG_ABOUT_CONTACT,
            TAG_ABOUT_EMAIL, TAG_ABOUT_WEBSITE, TAG_ABOUT_DESC, TAG_ABOUT_DEVELOPED, TAG_ABOUT_PRIVACY, TAG_ABOUT_PUB_ID,
            TAG_ABOUT_BANNER_ID, TAG_ABOUT_INTER_ID, TAG_ABOUT_IS_BANNER, TAG_ABOUT_IS_INTER, TAG_ABOUT_CLICK, TAG_ABOUT_IS_PORTRAIT, TAG_ABOUT_IS_LANDSCAPE, TAG_ABOUT_IS_SQUARE};


    // Creating table about
    private static final String CREATE_TABLE_ABOUT = "create table " + TABLE_ABOUT + "(" + TAG_ABOUT_NAME
            + " TEXT, " + TAG_ABOUT_LOGO + " TEXT, " + TAG_ABOUT_VERSION + " TEXT, " + TAG_ABOUT_AUTHOR + " TEXT" +
            ", " + TAG_ABOUT_CONTACT + " TEXT, " + TAG_ABOUT_EMAIL + " TEXT, " + TAG_ABOUT_WEBSITE + " TEXT, " + TAG_ABOUT_DESC + " TEXT" +
            ", " + TAG_ABOUT_DEVELOPED + " TEXT, " + TAG_ABOUT_PRIVACY + " TEXT, " + TAG_ABOUT_PUB_ID + " TEXT, " + TAG_ABOUT_BANNER_ID + " TEXT" +
            ", " + TAG_ABOUT_INTER_ID + " TEXT, " + TAG_ABOUT_IS_BANNER + " TEXT, " + TAG_ABOUT_IS_INTER + " TEXT, " + TAG_ABOUT_IS_PORTRAIT + " TEXT, " + TAG_ABOUT_IS_LANDSCAPE + " TEXT, " + TAG_ABOUT_IS_SQUARE + " TEXT, " + TAG_ABOUT_CLICK + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_NEWS = "create table " + TABLE_NEWS + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_NID + " TEXT," +
            TAG_CAT_ID + " TEXT," +
            TAG_CAT_NAME + " TEXT," +
            TAG_IMAGE_SMALL + " TEXT," +
            TAG_IMAGE_BIG + " TEXT," +
            TAG_TYPE + " TEXT," +
            TAG_HEADING + " TEXT," +
            TAG_DESC + " TEXT," +
            TAG_VIDEO_ID + " TEXT," +
            TAG_VIDEO_URL + " TEXT," +
            TAG_DATE + " TEXT," +
            TAG_IMAGES + " TEXT," +
            TAG_TOT_VIEWS + " TEXT);";

    // Creating table query
    private static final String CREATE_TABLE_RECENT = "create table " + TABLE_RECENT + "(" +
            TAG_ID + " integer PRIMARY KEY AUTOINCREMENT," +
            TAG_NID + " TEXT," +
            TAG_CAT_ID + " TEXT," +
            TAG_CAT_NAME + " TEXT," +
            TAG_IMAGE_SMALL + " TEXT," +
            TAG_IMAGE_BIG + " TEXT," +
            TAG_TYPE + " TEXT," +
            TAG_HEADING + " TEXT," +
            TAG_DESC + " TEXT," +
            TAG_VIDEO_ID + " TEXT," +
            TAG_VIDEO_URL + " TEXT," +
            TAG_DATE + " TEXT," +
            TAG_IMAGES + " TEXT," +
            TAG_TOT_VIEWS + " TEXT);";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 6);
        methods = new Methods(context);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_RECENT);
            db.execSQL(CREATE_TABLE_ABOUT);
            db.execSQL(CREATE_TABLE_NEWS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addFav(ItemNews itemNews) {
        String imageBig = methods.encrypt(itemNews.getImage().replace(" ", "%20"));
        String imageSmall = methods.encrypt(itemNews.getImageThumb().replace(" ", "%20"));
        String video_url = methods.encrypt(itemNews.getVideoUrl());

        StringBuilder images = new StringBuilder(itemNews.getImage());
        if (itemNews.getGalleryList() != null) {
            for (int i = 0; i < itemNews.getGalleryList().size(); i++) {
                images.append(",").append(itemNews.getGalleryList().get(i));
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_NID, itemNews.getId());
        contentValues.put(TAG_CAT_ID, itemNews.getCatId());
        contentValues.put(TAG_CAT_NAME, itemNews.getCatName());
        contentValues.put(TAG_IMAGE_BIG, imageBig);
        contentValues.put(TAG_IMAGE_SMALL, imageSmall);
        contentValues.put(TAG_TYPE, itemNews.getType());
        contentValues.put(TAG_HEADING, itemNews.getHeading());
        contentValues.put(TAG_DESC, itemNews.getDesc());
        contentValues.put(TAG_VIDEO_ID, itemNews.getVideoId());
        contentValues.put(TAG_VIDEO_URL, video_url);
        contentValues.put(TAG_DATE, itemNews.getDate());
        contentValues.put(TAG_IMAGES, images.toString());
        contentValues.put(TAG_TOT_VIEWS, itemNews.getTotalViews());

        db.insert(TABLE_NEWS, null, contentValues);
    }

    private Boolean checkRecent(String id) {
        Cursor cursor = db.query(TABLE_RECENT, columns_news, TAG_NID + "='" + id + "'", null, null, null, null);
        Boolean isRecent = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return isRecent;
    }

    public void addRecentNews(ItemNews itemNews) {
        Cursor cursor_delete = db.query(TABLE_RECENT, columns_news, null, null, null, null, null);
        if (cursor_delete != null && cursor_delete.getCount() > 24) {
            cursor_delete.moveToFirst();
            db.delete(TABLE_RECENT, TAG_NID + "=" + cursor_delete.getString(cursor_delete.getColumnIndex(TAG_NID)), null);
        }
        cursor_delete.close();

        if (checkRecent(itemNews.getId())) {
            db.delete(TABLE_RECENT, TAG_NID + "='" + itemNews.getId() + "'", null);
        }

        String imageBig = methods.encrypt(itemNews.getImage().replace(" ", "%20"));
        String imageSmall = methods.encrypt(itemNews.getImageThumb().replace(" ", "%20"));
        String video_url = methods.encrypt(itemNews.getVideoUrl());

        StringBuilder images = new StringBuilder(itemNews.getImage());
        if (itemNews.getGalleryList() != null) {
            for (int i = 0; i < itemNews.getGalleryList().size(); i++) {
                images.append(",").append(itemNews.getGalleryList().get(i));
            }
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(TAG_NID, itemNews.getId());
        contentValues.put(TAG_CAT_ID, itemNews.getCatId());
        contentValues.put(TAG_CAT_NAME, itemNews.getCatName());
        contentValues.put(TAG_IMAGE_BIG, imageBig);
        contentValues.put(TAG_IMAGE_SMALL, imageSmall);
        contentValues.put(TAG_TYPE, itemNews.getType());
        contentValues.put(TAG_HEADING, itemNews.getHeading());
        contentValues.put(TAG_DESC, itemNews.getDesc());
        contentValues.put(TAG_VIDEO_ID, itemNews.getVideoId());
        contentValues.put(TAG_VIDEO_URL, video_url);
        contentValues.put(TAG_DATE, itemNews.getDate());
        contentValues.put(TAG_IMAGES, images.toString());
        contentValues.put(TAG_TOT_VIEWS, itemNews.getTotalViews());

        db.insert(TABLE_RECENT, null, contentValues);
    }

    public void removeFav(String id) {
        db.delete(TABLE_NEWS, TAG_NID + "=" + id, null);
    }

    public Boolean isFav(String id) {
        String where = TAG_NID + "=?";
        String[] args = {id};
        Cursor cursor = db.query(TABLE_NEWS, columns_news, where, args, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.close();
            return true;
        } else {
            try {
                cursor.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public ArrayList<ItemNews> getFav() {
        ArrayList<ItemNews> arrayList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NEWS, columns_news, null, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String nid = cursor.getString(cursor.getColumnIndex(TAG_NID));
                String cid = cursor.getString(cursor.getColumnIndex(TAG_CAT_ID));
                String cname = cursor.getString(cursor.getColumnIndex(TAG_CAT_NAME));

                String img = methods.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));
                String img_thumb = methods.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_SMALL)));
                String type = cursor.getString(cursor.getColumnIndex(TAG_TYPE));
                String heading = cursor.getString(cursor.getColumnIndex(TAG_HEADING));
                String desc = cursor.getString(cursor.getColumnIndex(TAG_DESC));
                String video_id = cursor.getString(cursor.getColumnIndex(TAG_VIDEO_ID));
                String video_url = methods.decrypt(cursor.getString(cursor.getColumnIndex(TAG_VIDEO_URL)));
                String date = cursor.getString(cursor.getColumnIndex(TAG_DATE));
                String images = cursor.getString(cursor.getColumnIndex(TAG_IMAGES));
                String views = String.valueOf(cursor.getInt(cursor.getColumnIndex(TAG_TOT_VIEWS)));

                String[] img_gallery = images.split(",");
                images = img_gallery[0];
                ArrayList<String> array = new ArrayList<>(Collections.singletonList(images).subList(1, img_gallery.length));

                ItemNews itemNews = new ItemNews(nid, cid, cname, type, heading, desc, video_id, video_url, date, img, img_thumb, views, array);
                arrayList.add(itemNews);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    public ArrayList<ItemNews> getRecentNews(String limit) {
        ArrayList<ItemNews> arrayList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_RECENT, columns_news, null, null, null, null, TAG_ID + " DESC", limit);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                String nid = cursor.getString(cursor.getColumnIndex(TAG_NID));
                String cid = cursor.getString(cursor.getColumnIndex(TAG_CAT_ID));
                String cname = cursor.getString(cursor.getColumnIndex(TAG_CAT_NAME));

                String img = methods.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_BIG)));
                String img_thumb = methods.decrypt(cursor.getString(cursor.getColumnIndex(TAG_IMAGE_SMALL)));
                String type = cursor.getString(cursor.getColumnIndex(TAG_TYPE));
                String heading = cursor.getString(cursor.getColumnIndex(TAG_HEADING));
                String desc = cursor.getString(cursor.getColumnIndex(TAG_DESC));
                String video_id = cursor.getString(cursor.getColumnIndex(TAG_VIDEO_ID));
                String video_url = methods.decrypt(cursor.getString(cursor.getColumnIndex(TAG_VIDEO_URL)));
                String date = cursor.getString(cursor.getColumnIndex(TAG_DATE));
                String images = cursor.getString(cursor.getColumnIndex(TAG_IMAGES));
                String views = String.valueOf(cursor.getInt(cursor.getColumnIndex(TAG_TOT_VIEWS)));

                String[] img_gallery = images.split(",");
                images = img_gallery[0];
                ArrayList<String> array = new ArrayList<>(Collections.singletonList(images).subList(1, img_gallery.length));

                ItemNews itemNews = new ItemNews(nid, cid, cname, type, heading, desc, video_id, video_url, date, img, img_thumb, views, array);
                arrayList.add(itemNews);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return arrayList;
    }

    public void addtoAbout() {
        try {
            db.delete(TABLE_ABOUT, null, null);

            ContentValues contentValues = new ContentValues();
            contentValues.put(TAG_ABOUT_NAME, Constant.itemAbout.getAppName());
            contentValues.put(TAG_ABOUT_LOGO, Constant.itemAbout.getAppLogo());
            contentValues.put(TAG_ABOUT_VERSION, Constant.itemAbout.getAppVersion());
            contentValues.put(TAG_ABOUT_AUTHOR, Constant.itemAbout.getAuthor());
            contentValues.put(TAG_ABOUT_CONTACT, Constant.itemAbout.getContact());
            contentValues.put(TAG_ABOUT_EMAIL, Constant.itemAbout.getEmail());
            contentValues.put(TAG_ABOUT_WEBSITE, Constant.itemAbout.getWebsite());
            contentValues.put(TAG_ABOUT_DESC, Constant.itemAbout.getAppDesc());
            contentValues.put(TAG_ABOUT_DEVELOPED, Constant.itemAbout.getDevelopedby());
            contentValues.put(TAG_ABOUT_PRIVACY, Constant.itemAbout.getPrivacy());
            contentValues.put(TAG_ABOUT_PUB_ID, Constant.ad_publisher_id);
            contentValues.put(TAG_ABOUT_BANNER_ID, Constant.ad_banner_id);
            contentValues.put(TAG_ABOUT_INTER_ID, Constant.ad_inter_id);
            contentValues.put(TAG_ABOUT_IS_BANNER, Constant.isBannerAd.toString());
            contentValues.put(TAG_ABOUT_IS_INTER, Constant.isInterAd.toString());
            contentValues.put(TAG_ABOUT_CLICK, Constant.adShow);

            db.insert(TABLE_ABOUT, null, contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean getAbout() {

        Cursor c = db.query(TABLE_ABOUT, columns_about, null, null, null, null, null);

        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                String appname = c.getString(c.getColumnIndex(TAG_ABOUT_NAME));
                String applogo = c.getString(c.getColumnIndex(TAG_ABOUT_LOGO));
                String desc = c.getString(c.getColumnIndex(TAG_ABOUT_DESC));
                String appversion = c.getString(c.getColumnIndex(TAG_ABOUT_VERSION));
                String appauthor = c.getString(c.getColumnIndex(TAG_ABOUT_AUTHOR));
                String appcontact = c.getString(c.getColumnIndex(TAG_ABOUT_CONTACT));
                String email = c.getString(c.getColumnIndex(TAG_ABOUT_EMAIL));
                String website = c.getString(c.getColumnIndex(TAG_ABOUT_WEBSITE));
                String privacy = c.getString(c.getColumnIndex(TAG_ABOUT_PRIVACY));
                String developedby = c.getString(c.getColumnIndex(TAG_ABOUT_DEVELOPED));

                Constant.ad_banner_id = c.getString(c.getColumnIndex(TAG_ABOUT_BANNER_ID));
                Constant.ad_inter_id = c.getString(c.getColumnIndex(TAG_ABOUT_INTER_ID));
                Constant.isBannerAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_BANNER)));
                Constant.isInterAd = Boolean.parseBoolean(c.getString(c.getColumnIndex(TAG_ABOUT_IS_INTER)));
                Constant.ad_publisher_id = c.getString(c.getColumnIndex(TAG_ABOUT_PUB_ID));
                Constant.adShow = Integer.parseInt(c.getString(c.getColumnIndex(TAG_ABOUT_CLICK)));

                Constant.itemAbout = new ItemAbout(appname, applogo, desc, appversion, appauthor, appcontact, email, website, privacy, developedby);
            }
            c.close();
            return true;
        } else {
            c.close();
            return false;
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_TABLE_RECENT);
    }
}