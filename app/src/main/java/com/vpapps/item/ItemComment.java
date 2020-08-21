package com.vpapps.item;

public class ItemComment {

    private String id, userid, username, useremail, comment_text, dp, date;

    public ItemComment(String id, String userid, String username, String useremail, String comment_text, String dp, String date) {
        this.id = id;
        this.userid = userid;
        this.username = username;
        this.useremail = useremail;
        this.comment_text = comment_text;
        this.dp = dp;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userid;
    }

    public String getUserName() {
        return username;
    }

    public String getUserEmail() {
        return useremail;
    }

    public String getCommentText() {
        return comment_text;
    }

    public String getDp() {
        return dp;
    }

    public String getDate() {
        return date;
    }
}