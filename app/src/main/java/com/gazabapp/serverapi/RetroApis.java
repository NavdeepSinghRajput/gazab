package com.gazabapp.serverapi;

/**
 * Created by Nilesh on 3/11/2018.
 */

import com.gazabapp.pojo.AddComment;
import com.gazabapp.pojo.CommentList;
import com.gazabapp.pojo.CommunityDetails;
import com.gazabapp.pojo.CommunityList;
import com.gazabapp.pojo.CommunityMyList;
import com.gazabapp.pojo.CreatePost;
import com.gazabapp.pojo.Deactivate;
import com.gazabapp.pojo.DeletePostModel;
import com.gazabapp.pojo.Follow;
import com.gazabapp.pojo.FollowUsers;
import com.gazabapp.pojo.Login;
import com.gazabapp.pojo.PostCommentList;
import com.gazabapp.pojo.PostCommentListData;
import com.gazabapp.pojo.PostDetails;
import com.gazabapp.pojo.PostList;
import com.gazabapp.pojo.PrivacyPolicy;
import com.gazabapp.pojo.ReactionAdd;
import com.gazabapp.pojo.ResendOTP;
import com.gazabapp.pojo.Unfollow;
import com.gazabapp.pojo.UploadFile;
import com.gazabapp.pojo.UserDetail;
import com.gazabapp.pojo.VerifyOTP;
import com.google.gson.JsonObject;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetroApis
{
    @GET("post?")
    Call<PostList> getPost_Fresh(@Header("Authorization") String Authorization, @Query("page") String page, @Query("count") String count, @Query("sort") String sort);

    @GET("trending?")
    Call<PostList> getPost_Hot(@Header("Authorization") String Authorization,@Query("user_id") String user_id,@Query("page") String page,@Query("count") String count);



    @GET("post")
    Call<PostList> getCommunityPost_fresh(@Header("Authorization") String Authorization, @Query("page") String page, @Query("count") String count, @Query("nsfw") String nsfw, @Query("sort") String sort, @Query("community_ids") String community_ids);

    @GET("trending?")
    Call<PostList> getCommunityPost_hot(@Header("Authorization") String Authorization,@Query("community_id") String community_id,@Query("page") String page,@Query("count") String count);


    @GET("user/post/{id}")
    Call<PostList> getMyPost(@Path("id") String id,@Header("Authorization") String Authorization,@Query("page") String page,@Query("count") String count);


    @GET("/post/{id}")
    Call<PostDetails> getPostDetails(@Header("Authorization") String Authorization,@Path("id") String id);

    @GET("/page/privacy-policy")
    Call<PrivacyPolicy> getPrivacyPolicy();
    @GET("/page/terms-of-use")
    Call<PrivacyPolicy> getUserArrangement();

    @GET("/follow/community/{id}/")
    Call<CommunityMyList> getMyProfileCommunity(@Path("id") String id, @Header("Authorization") String Authorization, @Query("offset") String next_offset);

    @GET("/follow/community/{id}/")
    Call<CommunityMyList> getMyProfileCommunity(@Path("id") String id, @Header("Authorization") String Authorization, @Query("offset") String next_offset, @Query("limit") String limit);

    @GET("/follow/person/{id}/")
    Call<FollowUsers> getFollowing(@Path("id") String id,@Header("Authorization") String Authorization,@Query("offset") String next_offset);

    @GET("/community/{id}")
    Call<CommunityDetails> getCommunityDetails(@Path("id") String id, @Header("Content-Type") String ContentType,@Header("Authorization") String Authorization);

    @GET("/users/{id}")
    Call<UserDetail> getUserDetail(@Path("id") String id, @Header("Authorization") String Authorization);

    @GET("/community?")
    Call<CommunityList> getPopularCommunity(@Header("Authorization") String Authorization,@Query("page") String page,@Query("count") String count,@Query("type") String type,@Query("name") String name);

    @GET("/community?type=trending")
    Call<CommunityList> getTrendingCommunity(@Header("Authorization") String Authorization, @Query("page") String page, @Query("count") String count);

    @GET("/follow/follower/{id}/")
    Call<FollowUsers> getMyFollowers(@Path("id") String id,@Header("Authorization") String Authorization,@Query("offset") String next_offset);


    @GET("post?type=video")
    Call<PostList> getVideo_fresh(@Header("Authorization") String Authorization, @Query("page") String page, @Query("count") String count, @Query("nsfw") String nsfw, @Query("sort") String sort);

    @GET("trending?type=video")
    Call<PostList> getVideo_Hot(@Header("Authorization") String Authorization,@Query("user_id") String user_id,@Query("page") String page,@Query("count") String count);

    @GET("community?")
    Call<CommunityList> getCommunityList(@Header("Authorization") String Authorization,@Query("page") String page, @Query("count") String count);


    @GET("comment?")
    Call<CommentList> getCommentList(@Query("page") String page, @Query("count") String count);

    @GET("post/{id}/comment")
    Call<PostCommentList> getPostCommentList(@Header("Authorization") String Authorization,@Path("id") String id, @Query("page") String page, @Query("count") String count);

    @POST("login?")
    Call<Login> getLogin(@Body JsonObject jsonObject);

    @POST("resendotp?")
    Call<ResendOTP> resendOTP(@Body JsonObject jsonObject);

    @POST("sociallogin")
    Call<VerifyOTP> socialLogin(@Header("Content-Type") String ContentType, @Body JsonObject jsonObject);

    @POST("verifyotp")
    Call<VerifyOTP> verifyOTP(@Header("Authorization") String Authorization, @Header("Content-Type") String ContentType, @Body JsonObject jsonObject);

    @POST("comment")
    Call<AddComment> addComment(@Header("Authorization") String Authorization, @Header("Content-Type") String ContentType, @Body JsonObject jsonObject);

    @POST("reaction")
    Call<ReactionAdd> addReaction(@Header("Authorization") String Authorization, @Header("Content-Type") String ContentType, @Body JsonObject jsonObject);

    @POST("post")
    Call<CreatePost> createPost(@Header("Authorization") String Authorization, @Header("Content-Type") String ContentType, @Body JsonObject jsonObject);

    @POST("follow/community/")
    Call<Follow> followCommunities(@Header("Authorization") String Authorization, @Header("Content-Type") String ContentType, @Body JsonObject jsonObject);
    @POST("unfollow/community/")
    Call<Unfollow> unfollowCommunities(@Header("Authorization") String Authorization, @Header("Content-Type") String ContentType, @Body JsonObject jsonObject);

    @POST("deactivate/{id}")
    Call<Deactivate> deactivateAccount(@Path("id") String id,@Header("Authorization") String Authorization, @Header("Content-Type") String ContentType);

    @POST("follow/person/")
    Call<Follow> followPersons(@Header("Authorization") String Authorization, @Header("Content-Type") String ContentType, @Body JsonObject jsonObject);
    @POST("unfollow/person/")
    Call<Unfollow> unfollowPersons(@Header("Authorization") String Authorization, @Header("Content-Type") String ContentType, @Body JsonObject jsonObject);

    @PUT("/users/{id}")
    Call<VerifyOTP> putUserDetail(@Path("id") String id, @Header("Authorization") String Authorization, @Header("Content-Type") String ContentType, @Body JsonObject jsonObject);

//    @POST("abuse")
//    Call<VerifyOTP> verifyOTP(@Header("Authorization") String Authorization, @Header("Content-Type") String ContentType, @Body JsonObject jsonObject);

    @Multipart
    @POST("/upload")
    Call<UploadFile> uploadFile(@Header("Authorization") String Authorization, @Part MultipartBody.Part file, @Part("type") RequestBody type);

    @GET("feeds")
    Call<PostList> getUserFeedPost(@Header("Authorization") String Authorization, @Query("is_feed") int is_feed, @Query("page") int page);

    @PUT("post/{post_id}")
    Call<DeletePostModel> deleteUserPost(@Path("post_id") String post_id, @Header("Authorization")String Authorization, @Body JsonObject jsonObject);

}
