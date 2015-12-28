package fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.musicbox.mobilemusicbox.HomeActivity;
import com.musicbox.mobilemusicbox.ItemOffsetDecoration;
import com.musicbox.mobilemusicbox.MusicCardAdapter;
import com.musicbox.mobilemusicbox.NewReleasesAdapter;
import com.musicbox.mobilemusicbox.R;
import com.musicbox.mobilemusicbox.Song;
import com.musicbox.mobilemusicbox.SongsApi;

import java.util.List;

import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by fescalona on 12/26/2015.
 */
public class NewReleasesFragment extends Fragment {

    ProgressBar pb;
    List<Song> songList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getActivity().setTitle("New Releases");

        View rootView = inflater.inflate(R.layout.fragment_new_releases, container, false);


        pb = (ProgressBar) rootView.findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);




        if (isOnline() && songList == null) {
            Log.v("cubanmusicbox", "Calling requestData in NewReleasesFragment()");
            requestData();
        } else {
            Toast.makeText(rootView.getContext(), "Network is not available", Toast.LENGTH_LONG).show();
        }



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


            RecyclerView view = (RecyclerView) getView().findViewById(R.id.newReleasesRecyclerView);
            view.setHasFixedSize(true);
            view.setLayoutManager(new GridLayoutManager(getView().getContext(), 2));

            //ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getView().getContext(), R.dimen.item_offset);
            //view.addItemDecoration(itemDecoration);

            NewReleasesAdapter adapter = new NewReleasesAdapter(songList);
            view.setAdapter(adapter);



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
