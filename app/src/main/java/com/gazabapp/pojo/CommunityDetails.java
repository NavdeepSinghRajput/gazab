
package com.gazabapp.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommunityDetails {

    @SerializedName("data")
    @Expose
    private CommunityDetailsData data;
    @SerializedName("status")
    @Expose
    private String status;

    public CommunityDetailsData getData() {
        return data;
    }

    public void setData(CommunityDetailsData data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}


