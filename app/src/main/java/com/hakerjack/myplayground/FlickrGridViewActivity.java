package com.hakerjack.myplayground;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A Demo activity that infuses Grid View and Infinite Scroll experience of Flickr's photos
 * Created by kjia on 4/2/16.
 */
public class FlickrGridViewActivity extends AppCompatActivity {
    private static final int DEFAULT_NUM_OF_COLUMNS = 3;
    private static final String FLICKR_PHOTO_SIZE_SUFFIX = "z";

    @Bind(R.id.grid_view) GridView mGridView;
    @Bind(R.id.progress_bar) ProgressBar mProgressBar;

    private ImageAdapter mImageAdapter;
    private Retrofit mRetrofit;
    private FlickrService mFlickrService;

    private int imageViewSizePx;
    private int mNumOfColumn;
    private int mTotalPages;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flickr_grid_view);
        ButterKnife.bind(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.flickr_grid_activity_name);
        }

        mImageAdapter = new ImageAdapter(this);
        mGridView.setAdapter(mImageAdapter);

        setUpService();

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        imageViewSizePx = metrics.widthPixels / DEFAULT_NUM_OF_COLUMNS;
        mGridView.setColumnWidth(imageViewSizePx);

        mGridView.setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public boolean loadMore(int page, int totalItemsCount) {
                if (page > mTotalPages) {
                    return false;
                } else {
                    fetchPhotos(page);
                    return true;
                }
            }
        });

    }

    private void setUpService() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.flickr.com/services/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mFlickrService = mRetrofit.create(FlickrService.class);
        fetchInitialPhotos();

    }

    private void fetchInitialPhotos() {
        fetchPhotos(null);
    }

    private void fetchPhotos(Integer page) {
        mFlickrService.getRecentPhotos(page).enqueue(new Callback<FlickrRecentResponse>() {
            @Override
            public void onResponse(Call<FlickrRecentResponse> call, Response<FlickrRecentResponse> response) {
                mProgressBar.setVisibility(View.GONE);
                if (response.body() != null) {
                    FlickrRecentResponse photosResponse = response.body();

                    mTotalPages = photosResponse.photos.pages;
                    List<String> urls = parseFlickrPhotoToUrl(photosResponse.photos.photo);
                    mImageAdapter.addData(urls);
                } else {
                    if (response.errorBody() != null) {
                        try {
                            Log.e("KJ", response.errorBody().string());
                        } catch (IOException e) {
                            Log.e("KJ", "errorBody parsing error");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<FlickrRecentResponse> call, Throwable t) {
                Toast.makeText(FlickrGridViewActivity.this, "error: " + t.getMessage(), Toast.LENGTH_LONG)
                        .show();
                Log.e("KJ", "error:" + t.getMessage());
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }


    private List<String> parseFlickrPhotoToUrl(List<FlickrPhoto> photos) {

        List<String> urls = new ArrayList<>();
        for (FlickrPhoto photo : photos) {
            Uri uri = new Uri.Builder()
                    .scheme("https")
                    .authority("farm"+photo.farm+".staticflickr.com")
                    .appendPath(photo.server)
                    .appendPath(photo.id + "_" + photo.secret + "_" + FLICKR_PHOTO_SIZE_SUFFIX + ".jpg")
                    .build();
            urls.add(uri.toString());
        }

        return urls;
    }

    class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> mData = new ArrayList<>();

        public ImageAdapter(Context context) {
            mContext = context;
        }

        public void addData(List<String> urls) {
            mData.addAll(urls);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = (ImageView) convertView;
            if (imageView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(imageViewSizePx, imageViewSizePx));
                imageView.setPadding(2,2,2,2);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            String url = getItem(position);
            Picasso.with(mContext)
                    .load(url)
                    .resize(imageViewSizePx, imageViewSizePx)
                    .centerCrop()
                    .into(imageView);

            return imageView;
        }
    }
}
