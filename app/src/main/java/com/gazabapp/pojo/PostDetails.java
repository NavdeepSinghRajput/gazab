
package com.gazabapp.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostDetails {

    @SerializedName("data")
    @Expose
    private PostListData data;
    @SerializedName("status")
    @Expose
    private String status;

    public PostListData getData() {
        return data;
    }

    public void setData(PostListData data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
