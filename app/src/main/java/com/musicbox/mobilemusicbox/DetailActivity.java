package com.musicbox.mobilemusicbox;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import fragments.RetainFragment;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class DetailActivity extends AppCompatActivity {

    ProgressBar pb;
    Song song;
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory / 4;
    private LruCache<Float, Bitmap> mMemoryCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);


        Float songId = getIntent().getFloatExtra(HomeActivity.SONG_ID, 100);
        requestData(songId.toString());


        RetainFragment retainFragment = RetainFragment.findOrCreateRetainFragment(getFragmentManager());
        mMemoryCache = retainFragment.mRetainedCache;
        if (mMemoryCache == null) {
            // Initialize cache here as usual
            mMemoryCache = new LruCache<Float, Bitmap>(cacheSize);
            retainFragment.mRetainedCache = mMemoryCache;
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    protected void updateDisplay(){

        if (song != null){

            TextView tv = (TextView) findViewById(R.id.titleText);
            tv.setText(song.getTitle());

            TextView artistText = (TextView) findViewById(R.id.artistText);
            artistText.setText(song.getArtist());


            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageDrawable(null);

            //display img
            Object bitmap = mMemoryCache.get(song.getId());
            if (bitmap != null) {
                ImageView iv = (ImageView) findViewById(R.id.imageView);
                iv.setImageBitmap((Bitmap)bitmap);
                Log.v("cubanmusicbox", "Calling mMemoryCache...");
            } else {
                SongAndView container = new SongAndView();
                container.song = song;

                Log.v("cubanmusicbox", "Calling ImageLoader...");
                ImageLoader loader = new ImageLoader();
                loader.execute(container);
            }
        }

    }


    private void requestData( String id ){
        pb.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HomeActivity.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SongsApi api = retrofit.create(SongsApi.class);
        api.getSongDetails(id).enqueue(new Callback<Song>() {
            @Override
            public void onResponse(Response<Song> response, Retrofit retrofit) {
                //Log.v("1", response.body().toString());
                song = response.body();
                updateDisplay();
                pb.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Throwable t) {
                pb.setVisibility(View.INVISIBLE);
            }
        });

    }



    class SongAndView {
        public Song song;
        public View view;
        public Bitmap bitmap;

    }

    private class ImageLoader extends AsyncTask<SongAndView, Void, SongAndView> {


        @Override
        protected SongAndView doInBackground(SongAndView... params) {

            SongAndView container = params[0];
            Song song = container.song;

            try {
                String imageUrl = HomeActivity.PHOTOS_BASE_URL + song.getImage();
                InputStream in = (InputStream) new URL(imageUrl).getContent();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                song.setBitmap(bitmap);
                in.close();
                container.bitmap = bitmap;
                return container;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(SongAndView result) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageDrawable(null);
            imageView.setImageBitmap(result.bitmap);
            mMemoryCache.put(result.song.getId(), result.bitmap);
        }
    }

}
