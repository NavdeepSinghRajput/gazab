
package com.gazabapp.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommunityMyList {

    @SerializedName("data")
    @Expose
    private List<CommunityListData> data = null;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("next_offset")
    @Expose
    private Integer nextOffset;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("total_records")
    @Expose
    private Integer totalRecords;

    public List<CommunityListData> getData() {
        return data;
    }

    public void setData(List<CommunityListData> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(Integer nextOffset) {
        this.nextOffset = nextOffset;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

}
