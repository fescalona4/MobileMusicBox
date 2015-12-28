package com.musicbox.mobilemusicbox;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by fescalona on 12/24/2015.
 */
public class MusicCardAdapter extends RecyclerView.Adapter<SongViewHolder> {

    private List<Song> songList;

    public MusicCardAdapter(List<Song> songList) {
        this.songList = songList;

    }


    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_card, parent, false);


        return new SongViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        holder.id = song.getId();
        holder.bitmap.setImageDrawable(null);


        String imageUrl = HomeActivity.PHOTOS_BASE_URL + song.getImage();
        Picasso.with(holder.bitmap.getContext()).load(imageUrl).into(holder.bitmap);

    }


    @Override
    public int getItemCount() {
        return songList.size();
    }


    @Override
    public long getItemId(int position) {
        Song song = songList.get(position);
        return song.getId().longValue();
    }


}
