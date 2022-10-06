package com.gazabapp.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetail {

    @SerializedName("data")
    @Expose
    private UserDetailData data;
    @SerializedName("status")
    @Expose
    private String status;

    public UserDetailData getData() {
        return data;
    }

    public void setData(UserDetailData data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}