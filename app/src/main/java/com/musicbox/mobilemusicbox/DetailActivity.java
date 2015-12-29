package com.musicbox.mobilemusicbox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class DetailActivity extends AppCompatActivity {

    ProgressBar pb;
    Song song;

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


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    protected void updateDisplay() {

        if (song != null) {

            TextView tv = (TextView) findViewById(R.id.titleText);
            tv.setText(song.getTitle());

            TextView artistText = (TextView) findViewById(R.id.artistText);
            artistText.setText(song.getArtist());


            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageDrawable(null);

            String imageUrl = HomeActivity.PHOTOS_BASE_URL + song.getImage();
            Picasso.with(imageView.getContext()).load(imageUrl).into(imageView);
        }

    }


    private void requestData(String id) {
        pb.setVisibility(View.VISIBLE);

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HomeActivity.ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create(gson))
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


}
