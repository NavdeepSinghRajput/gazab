package com.gazabapp.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserDetailData {

    @SerializedName("age_group")
    @Expose
    private String ageGroup;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("birthdate")
    @Expose
    private String birthdate;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("last_login")
    @Expose
    private String lastLogin;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("picture")
    @Expose
    private String picture;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("subscribe")
    @Expose
    private List<String> subscribe = null;
    @SerializedName("total_community_following")
    @Expose
    private Integer totalCommunityFollowing;
    @SerializedName("total_followers")
    @Expose
    private Integer totalFollowers;
    @SerializedName("total_following")
    @Expose
    private Integer totalFollowing;
    @SerializedName("updated")
    @Expose
    private String updated;
    @SerializedName("verified")
    @Expose
    private Integer verified;

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(String lastLogin) {
        this.lastLogin = lastLogin;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<String> getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(List<String> subscribe) {
        this.subscribe = subscribe;
    }

    public Integer getTotalCommunityFollowing() {
        return totalCommunityFollowing;
    }

    public void setTotalCommunityFollowing(Integer totalCommunityFollowing) {
        this.totalCommunityFollowing = totalCommunityFollowing;
    }

    public Integer getTotalFollowers() {
        return totalFollowers;
    }

    public void setTotalFollowers(Integer totalFollowers) {
        this.totalFollowers = totalFollowers;
    }

    public Integer getTotalFollowing() {
        return totalFollowing;
    }

    public void setTotalFollowing(Integer totalFollowing) {
        this.totalFollowing = totalFollowing;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public Integer getVerified() {
        return verified;
    }

    public void setVerified(Integer verified) {
        this.verified = verified;
    }

}