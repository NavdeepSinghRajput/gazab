
package com.gazabapp.pojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentListData {

    @SerializedName("childern")
    @Expose
    private List<Object> childern = null;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("community_id")
    @Expose
    private String communityId;
    @SerializedName("community_name")
    @Expose
    private String communityName;
    @SerializedName("community_owner")
    @Expose
    private String communityOwner;
    @SerializedName("community_pic")
    @Expose
    private String communityPic;
    @SerializedName("community_slug")
    @Expose
    private String communitySlug;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("favourite")
    @Expose
    private List<Object> favourite = null;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("parent_id")
    @Expose
    private String parentId;
    @SerializedName("post_id")
    @Expose
    private String postId;
    @SerializedName("reactions")
    @Expose
    private Reactions reactions;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("updated")
    @Expose
    private String updated;
    @SerializedName("user_display_name")
    @Expose
    private String userDisplayName;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_pic")
    @Expose
    private String userPic;

    public List<Object> getChildern() {
        return childern;
    }

    public void setChildern(List<Object> childern) {
        this.childern = childern;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public String getCommunityOwner() {
        return communityOwner;
    }

    public void setCommunityOwner(String communityOwner) {
        this.communityOwner = communityOwner;
    }

    public String getCommunityPic() {
        return communityPic;
    }

    public void setCommunityPic(String communityPic) {
        this.communityPic = communityPic;
    }

    public String getCommunitySlug() {
        return communitySlug;
    }

    public void setCommunitySlug(String communitySlug) {
        this.communitySlug = communitySlug;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public List<Object> getFavourite() {
        return favourite;
    }

    public void setFavourite(List<Object> favourite) {
        this.favourite = favourite;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Reactions getReactions() {
        return reactions;
    }

    public void setReactions(Reactions reactions) {
        this.reactions = reactions;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public void setUserDisplayName(String userDisplayName) {
        this.userDisplayName = userDisplayName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPic() {
        return userPic;
    }

    public void setUserPic(String userPic) {
        this.userPic = userPic;
    }

}
