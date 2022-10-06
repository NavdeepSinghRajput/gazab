
package com.gazabapp.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VerifyOTP {

    @SerializedName("data")
    @Expose
    private VerifyOTPData verifyOTPData;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public VerifyOTPData getVerifyOTPData() {
        return verifyOTPData;
    }

    public void setVerifyOTPData(VerifyOTPData verifyOTPData) {
        this.verifyOTPData = verifyOTPData;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
