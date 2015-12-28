package fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.musicbox.mobilemusicbox.DetailActivity;
import com.musicbox.mobilemusicbox.HomeActivity;
import com.musicbox.mobilemusicbox.MusicCardAdapter;
import com.musicbox.mobilemusicbox.R;
import com.musicbox.mobilemusicbox.Song;
import com.musicbox.mobilemusicbox.SongListAdapter;
import com.musicbox.mobilemusicbox.SongsApi;

import java.util.List;

import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by fescalona on 12/26/2015.
 */
public class DashboardFragment extends Fragment {

    ProgressBar pb;
    List<Song> songList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("Dashboard");
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);


        pb = (ProgressBar) rootView.findViewById(R.id.progressBar1);
        pb.setVisibility(View.INVISIBLE);


        if (isOnline() && songList == null) {
            Log.v("cubanmusicbox", "Calling requestData()");
            requestData();
        } else {
            Toast.makeText(rootView.getContext(), "Network is not available", Toast.LENGTH_LONG).show();
        }


        //See more button click
        final Button newBtn = (Button) rootView.findViewById(R.id.newBtn);
        newBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.v("cubanmusicbox", "see more clicked");
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.content_frame, new NewReleasesFragment()).addToBackStack(null).commit();
            }
        });

        final Button topBtn = (Button) rootView.findViewById(R.id.topBtn);
        topBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                Log.v("cubanmusicbox", "see more clicked");
                FragmentManager fm = getFragmentManager();
                fm.beginTransaction().replace(R.id.content_frame, new TopFragment()).addToBackStack(null).commit();
            }
        });


        return rootView;
    }


    private void requestData() {

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


    protected void updateDisplay() {

        if (songList != null) {


            RecyclerView view = (RecyclerView) getView().findViewById(R.id.newSongsGridView);
            view.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.HORIZONTAL);
            view.setLayoutManager(llm);

            MusicCardAdapter adapter = new MusicCardAdapter(songList);
            view.setAdapter(adapter);


            RecyclerView view2 = (RecyclerView) getView().findViewById(R.id.topSongsRecyclerView);
            view2.setHasFixedSize(true);
            LinearLayoutManager llm2 = new LinearLayoutManager(getActivity());
            llm2.setOrientation(LinearLayoutManager.HORIZONTAL);
            view2.setLayoutManager(llm2);

            MusicCardAdapter adapter2 = new MusicCardAdapter(songList);
            view2.setAdapter(adapter2);

        }

    }



    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}
