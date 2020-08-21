package com.vpapps.item;

import java.io.Serializable;

public class ItemUser implements Serializable {

    private String id, name, email, mobile, dp;

    public ItemUser(String id, String name, String email, String mobile, String dp)
    {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.dp = dp;
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getMobile()
    {
        return mobile;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public void setMobile(String mobile)
    {
        this.mobile = mobile;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }
}
