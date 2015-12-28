package com.musicbox.mobilemusicbox;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import fragments.RetainFragment;

/**
 * Created by fescalona on 12/24/2015.
 */
public class MusicCardAdapter extends RecyclerView.Adapter<SongViewHolder> {

    private List<Song> songList;
    private LruCache<Float, Bitmap> mMemoryCache;
    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    final int cacheSize = maxMemory / 4;


    public MusicCardAdapter(List<Song> songList) {
        this.songList = songList;


    }


    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.music_card, parent, false);





        RetainFragment retainFragment = RetainFragment.findOrCreateRetainFragment(((Activity) parent.getContext()).getFragmentManager());
        mMemoryCache = retainFragment.mRetainedCache;
        if (mMemoryCache == null) {
            // Initialize cache here as usual
            mMemoryCache = new LruCache<Float, Bitmap>(cacheSize);
            retainFragment.mRetainedCache = mMemoryCache;
        }


        return new SongViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        Song song = songList.get(position);
        holder.title.setText(song.getTitle());
        holder.artist.setText(song.getArtist());
        holder.id = song.getId();
        holder.bitmap.setImageDrawable(null);

        //Picasso.with(holder.bitmap.getContext()).load("http://i.imgur.com/DvpvklR.png").into(holder.bitmap);

        //display img
        Object bitmap = mMemoryCache.get(song.getId());
        if (bitmap != null) {
            holder.bitmap.setImageBitmap((Bitmap) bitmap);
            //Log.v("cubanmusicbox", "setImageBitmap with mMemoryCache in MusicCardAdapter()");
        } else {
            SongAndView container = new SongAndView();
            container.song = song;
            container.holder = holder;

            Log.v("cubanmusicbox", "Calling ImageLoader in MusicCardAdapter()");
            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }
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


    class SongAndView {
        public Song song;
        public SongViewHolder holder;
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
            result.holder.bitmap.setImageDrawable(null);
            result.holder.bitmap.setImageBitmap(result.bitmap);
            mMemoryCache.put(result.song.getId(), result.bitmap);


        }
    }


}
