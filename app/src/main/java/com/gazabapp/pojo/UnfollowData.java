
package com.gazabapp.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnfollowData {

    @SerializedName("unfollow")
    @Expose
    private String unfollow;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("type")
    @Expose
    private String type;

    public String getUnfollow() {
        return unfollow;
    }

    public void setUnfollow(String follow) {
        this.unfollow = follow;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
