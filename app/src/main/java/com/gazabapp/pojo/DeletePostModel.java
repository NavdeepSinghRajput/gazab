package com.gazabapp.pojo;

import com.google.gson.annotations.Expose;

public class DeletePostModel {

    DeleteData data;

    public class DeleteData {

        @Expose
        String community_id;
        @Expose
        String community_name;
        @Expose
        String community_owner;
        @Expose
        String community_slug;
        @Expose
        String created;
        @Expose
        String description;
     /*   @Expose
        String dimension;
     */   @Expose
        String id;
        @Expose
        String link;
        @Expose
        String media;
        @Expose
        String nsfw;
        @Expose
        String published_on;

        /*String reactions": {"down": 0, "up": 0}, "*/
        @Expose
        String status;
        @Expose
        String title;
        @Expose
        String total_spend_time;
        @Expose
        String type;
        @Expose
        String updated;
        @Expose
        String updated_by;
        @Expose
        String user_id;
        @Expose
        String user_name;
        @Expose
        String videoThumbnail;
    }

    @Expose
    public String message;
    @Expose
   public String status;

}
