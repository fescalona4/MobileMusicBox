package com.musicbox.mobilemusicbox;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

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


        Float songId = getIntent().getFloatExtra(MainActivity.SONG_ID, 100);

        //Song song = DataProvider.songMap.get(songId);
        requestData(songId.toString());




        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    protected void updateDisplay(){

        if (song != null){

            TextView tv = (TextView) findViewById(R.id.nameText);
            tv.setText(song.getTitle());

            TextView artistText = (TextView) findViewById(R.id.artistText);
            artistText.setText(song.getArtist());

        }

    }


    private void requestData( String id ){
        pb.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MainActivity.ENDPOINT)
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


}
