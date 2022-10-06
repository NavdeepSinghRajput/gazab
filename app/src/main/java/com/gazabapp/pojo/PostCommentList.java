
package com.gazabapp.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostCommentList {

    @SerializedName("data")
    @Expose
    private List<PostCommentListData> data = null;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("total")
    @Expose
    private Integer total;

    public List<PostCommentListData> getData() {
        return data;
    }

    public void setData(List<PostCommentListData> data) {
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
