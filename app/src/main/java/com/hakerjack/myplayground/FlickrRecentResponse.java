package com.hakerjack.myplayground;

import java.util.List;

/**
 * Created by kjia on 4/2/16.
 */
public class FlickrRecentResponse {
    FlickrPhotos photos;

}

class FlickrPhotos {
    int page;
    int pages;
    int perpage;
    String total;
    List<FlickrPhoto> photo;
}
