package com.hakerjack.myplayground;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by kjia on 4/2/16.
 */
public interface FlickrService {
    public static String key = "22e621483168c785f5ee0bda6a469e62";
    public static String secret = "dc29c476c0ef1ec8";

    @GET("rest/?method=flickr.photos.getRecent&format=json&nojsoncallback=1&api_key=" + key)
    Call<FlickrRecentResponse> getRecentPhotos(@Query("page") Integer page);

}
