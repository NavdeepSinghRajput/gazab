package com.gazabapp.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrivacyPolicy {

    @SerializedName("data")
    @Expose
    private PrivacyPolicyData data;
    @SerializedName("status")
    @Expose
    private String status;

    public PrivacyPolicyData getData() {
        return data;
    }

    public void setData(PrivacyPolicyData data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
