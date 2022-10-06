
package com.gazabapp.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reactions {

    @SerializedName("down")
    @Expose
    private List<Object> down = null;
    @SerializedName("up")
    @Expose
    private List<String> up = null;

    public List<Object> getDown() {
        return down;
    }

    public void setDown(List<Object> down) {
        this.down = down;
    }

    public List<String> getUp() {
        return up;
    }

    public void setUp(List<String> up) {
        this.up = up;
    }

}
