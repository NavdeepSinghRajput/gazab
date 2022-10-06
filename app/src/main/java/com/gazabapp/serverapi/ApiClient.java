package com.gazabapp.serverapi;

public class ApiClient {

//    public final static String BASE_URL_API = "https://stagingapi.gazabapp.com/"; // testing
   public final static String BASE_URL_API = "https://www.gazabapp.com/"; // Production

    public static final String DP_LINK_POST = BASE_URL_API + "posts/";
    public static final String DP_LINK_COMMUNITY = BASE_URL_API + "communities/";


    public static RetroApis getGazabApi() {
        return RetrofitApiClient.getClient(BASE_URL_API)
                .create(RetroApis.class);
    }

    public static RetroApis getGazabDpPost() {
        return RetrofitApiClient.getClient(DP_LINK_POST)
                .create(RetroApis.class);
    }

    public static RetroApis getGazabDpCOMMUNITY() {
        return RetrofitApiClient.getClient(DP_LINK_COMMUNITY)
                .create(RetroApis.class);
    }

}
