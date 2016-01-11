package com.musicbox.mobilemusicbox;

import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import fragments.TopFragment;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class DetailActivity extends AppCompatActivity {

    ProgressBar pb;
    Song song;
    Intent playbackServiceIntent;
    MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound=false;

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

        final Button listenBtn = (Button) findViewById(R.id.listenBtn);
        listenBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.v("DetailActivity", "listen button clicked");

                if(musicBound) {
                    musicSrv.addSong(song);
                    musicSrv.playSong();
                    Log.v("DetailActivity", "musicSrv.playSong();");
                }
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            //get service
            musicSrv = binder.getService();
            //pass list
            //musicSrv.addSong(song);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };


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


    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            startService(playIntent);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);

        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv=null;
        super.onDestroy();
    }
}
