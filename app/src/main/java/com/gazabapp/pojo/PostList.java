
package com.gazabapp.pojo;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostList {

    @SerializedName("data")
    @Expose
    private List<PostListData> data = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("is_feed")
    @Expose
    private int is_feed;
    @SerializedName("next_page")
    @Expose
    private int next_page;
    @SerializedName("total")
    @Expose
    private Integer total;

    public int getIs_feed() {
        return is_feed;
    }

    public void setIs_feed(int is_feed) {
        this.is_feed = is_feed;
    }

    public int getNext_page() {
        return next_page;
    }

    public void setNext_page(int next_page) {
        this.next_page = next_page;
    }

    public List<PostListData> getData() {
        return data;
    }

    public void setData(List<PostListData> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

}