
package com.gazabapp.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResendOTP {

    @SerializedName("data")
    @Expose
    private ResendOTPData resendOTPData;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("status")
    @Expose
    private String status;

    public ResendOTPData getResendOTPData() {
        return resendOTPData;
    }

    public void setResendOTPData(ResendOTPData resendOTPData) {
        this.resendOTPData = resendOTPData;
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
class ResendOTPData
{

    @SerializedName("access_token")
    @Expose
    private String accessToken;
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}