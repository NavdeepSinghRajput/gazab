
package com.gazabapp.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FollowUsers {

    @SerializedName("data")
    @Expose
    private List<FollowUsersData> data = null;
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

    public List<FollowUsersData> getData() {
        return data;
    }

    public void setData(List<FollowUsersData> data) {
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
