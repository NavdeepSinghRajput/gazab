package com.gazabapp;

import java.text.SimpleDateFormat;

/**
 * Created by nilesh on 2/28/18.
 */

public class Constants
{

//   public final static String API = "https://stagingapi.gazabapp.com/"; // testing
   public final static String API = "https://www.gazabapp.com/"; // Production

    public static int WIDTH = 0 , HEIGHT = 0;
    public final static String PACKAGE_NAME = "com.gazabapp";

    public static final int VIEW_TYPE_IMAGE = 1;
    public static final int VIEW_TYPE_GIF = 2;
    public static final int VIEW_TYPE_TEXT = 3;
    public static final int VIEW_TYPE_URL = 4;
    public static final int VIEW_TYPE_VIDEO = 5;
    public static final int VIEW_TYPE_HEADER = 6;
    public static final int VIEW_TYPE_COMMENTS = 7;

    public static final String DP_LINK_POST = API+"posts/";
    public static final String DP_LINK_COMMUNITY = API+"communities/";

    public static final String shk_FCM_REGISTRATION = "fcm_registration";
    public static final String shk_ACCESS_TOKEN = "access_token";
    public static final String shk_ID = "id";
    public static final String shk_MOBILE_NO = "mobile_no";
    public static final String shk_DISPLAY_NAME = "display_name";
    public static final String shk_EMAIL = "email";
    public static final String shk_NAME = "name";
    public static final String shk_PICTURE = "picture";
    public static final String shk_AGE_GROUP = "age_group";
    public static final String shk_NOTIFICATION = "notification";
    public static final String shk_DARK_THEME = "dark_theme";
    public static final String shk_BIO = "bio";
    public static final String ERROR_LOG = "LogApiError";

    public static final String COPY_INTENT = "Gazab";

    public static String getTodayDateTime()
    {
        SimpleDateFormat sCurrDate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        long date = System.currentTimeMillis();
        return  ""+sCurrDate.format(date);
    }

   public static String getTodayDateTimeFormat()
   {
    SimpleDateFormat sCurrDate = new SimpleDateFormat("dd/MMM/yyyy HH:mm");
    long date = System.currentTimeMillis();
    return  ""+sCurrDate.format(date);
   }

   public static void PRINT(String sText)
   {
//     System.out.println(""+sText);
   }


}
