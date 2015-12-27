package fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.musicbox.mobilemusicbox.DetailActivity;
import com.musicbox.mobilemusicbox.HomeActivity;
import com.musicbox.mobilemusicbox.R;
import com.musicbox.mobilemusicbox.Song;
import com.musicbox.mobilemusicbox.SongListAdapter;
import com.musicbox.mobilemusicbox.SongsApi;

import java.util.List;

import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;


public class MusicFragment extends Fragment {

    ProgressBar pb;
    List<Song> songList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_music, container, false);



        pb = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);


        if(isOnline() && songList == null){
            Log.v("cubanmusicbox", "Calling requestData()");
            requestData();
        }
        else{
            Toast.makeText(rootView.getContext(), "Network is not available", Toast.LENGTH_LONG).show();
        }










        return rootView;
    }



    private void requestData(){

        pb.setVisibility(View.VISIBLE);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HomeActivity.ENDPOINT)
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
            SongListAdapter adapter = new SongListAdapter(getActivity(), R.layout.list_item, songList);

            ListView lv = (ListView) getView().findViewById(R.id.listView);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);

                    Song song = songList.get(position);

                    intent.putExtra(HomeActivity.SONG_ID, song.getId());

                    startActivity(intent);
                }
            });
        }

    }


    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if(netInfo != null && netInfo.isConnectedOrConnecting()){
            return true;
        }
        else{
            return false;
        }
    }




}
