package com.musicbox.mobilemusicbox;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    public static final String SONG_ID = "SONG_ID";
    public static final String PHOTOS_BASE_URL = "http://cubanmusicbox.com/images/uploads/";
    public static final String ENDPOINT = "http://cubanmusicbox.com";

    ProgressBar pb;
    List<Song> songList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pb = (ProgressBar) findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);


        if(isOnline() && songList == null){
            Log.v("cubanmusicbox", "Calling requestData()");
            requestData();
        }
        else{
            Toast.makeText(this, "Network is not available", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            if(isOnline()){
                requestData();
            }
            else{
                Toast.makeText(this, "Network is not available", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void requestData(){

        pb.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SongsApi api = retrofit.create(SongsApi.class);
        api.getAllSongs().enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Response<List<Song>> response, Retrofit retrofit) {
                //Log.v("1", response.body().toString());
                songList = response.body();
                updateDisplay();
                pb.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Throwable t) {
                pb.setVisibility(View.INVISIBLE);
            }
        });

    }

    protected void updateDisplay(){

        if (songList != null){
            SongListAdapter adapter = new SongListAdapter(
                    this, R.layout.list_item, songList);

            ListView lv = (ListView) findViewById(R.id.listView);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);

                    Song song = songList.get(position);

                    intent.putExtra(SONG_ID, song.getId());

                    startActivity(intent);
                }
            });
        }

    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnectedOrConnecting()){
            return true;
        }
        else{
            return false;
        }
    }

}


