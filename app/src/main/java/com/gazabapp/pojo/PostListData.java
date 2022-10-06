
package com.gazabapp.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostListData {
    @SerializedName("comment_count")
    @Expose
    private Integer commentCount;
    @SerializedName("community_id")
    @Expose
    private String communityId;
    @SerializedName("community_image")
    @Expose
    private String communityImage;
    @SerializedName("community_name")
    @Expose
    private String communityName;
    @SerializedName("community_owner")
    @Expose
    private String communityOwner;
    @SerializedName("community_slug")
    @Expose
    private String communitySlug;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("is_community_follow")
    @Expose
    private Boolean isCommunityFollow;

    public Boolean getCommunityFollow() {
        return isCommunityFollow;
    }

    public void setCommunityFollow(Boolean communityFollow) {
        isCommunityFollow = communityFollow;
    }

    public Boolean getIs_person_follow() {
        return is_person_follow;
    }

    public void setIs_person_follow(Boolean is_person_follow) {
        this.is_person_follow = is_person_follow;
    }

    @SerializedName("is_person_follow")
    @Expose
    private Boolean is_person_follow;


    @SerializedName("is_reacted")
    @Expose
    private String isReacted;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("media")
    @Expose
    private String media;
    @SerializedName("nsfw")
    @Expose
    private Integer nsfw;
    @SerializedName("published_on")
    @Expose
    private String publishedOn;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("total_reactions")
    @Expose
    private Integer totalReactions;
    @SerializedName("total_spend_time")
    @Expose
    private Integer totalSpendTime;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("updated")
    @Expose
    private String updated;
    @SerializedName("updated_by")
    @Expose
    private String updatedBy;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_picture")
    @Expose
    private String userPicture;
    @SerializedName("dimension")
    @Expose
    private Dimension dimension;
    @SerializedName("reaction_count")
    @Expose
    private ReactionCount reactionCount;
    @SerializedName("videoThumbnail")
    @Expose
    private String videoThumbnail;

    public ReactionCount getReactionCount() {
        return reactionCount;
    }

    public void setReactionCount(ReactionCount reactionCount) {
        this.reactionCount = reactionCount;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getCommunityId() {
        return communityId;
    }

    public void setCommunityId(String communityId) {
        this.communityId = communityId;
    }

    public String getCommunityImage() {
        return communityImage;
    }

    public void setCommunityImage(String communityImage) {
        this.communityImage = communityImage;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getIsCommunityFollow() {
        return isCommunityFollow;
    }

    public void setIsCommunityFollow(Boolean isCommunityFollow) {
        this.isCommunityFollow = isCommunityFollow;
    }

    public String getIsReacted() {
        return isReacted;
    }

    public void setIsReacted(String isReacted) {
        this.isReacted = isReacted;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public Integer getNsfw() {
        return nsfw;
    }

    public void setNsfw(Integer nsfw) {
        this.nsfw = nsfw;
    }

    public String getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(String publishedOn) {
        this.publishedOn = publishedOn;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTotalReactions() {
        return totalReactions;
    }

    public void setTotalReactions(Integer totalReactions) {
        this.totalReactions = totalReactions;
    }

    public Integer getTotalSpendTime() {
        return totalSpendTime;
    }

    public void setTotalSpendTime(Integer totalSpendTime) {
        this.totalSpendTime = totalSpendTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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

    public String getUserPicture() {
        return userPicture;
    }

    public void setUserPicture(String userPicture) {
        this.userPicture = userPicture;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public String getVideoThumbnail() {
        return videoThumbnail;
    }

    public void setVideoThumbnail(String videoThumbnail) {
        this.videoThumbnail = videoThumbnail;
    }
}
