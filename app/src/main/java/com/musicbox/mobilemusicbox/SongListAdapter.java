package com.musicbox.mobilemusicbox;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.okhttp.internal.DiskLruCache;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import fragments.RetainFragment;

/**
 * Created by fescalona on 12/24/2015.
 */
public class SongListAdapter extends ArrayAdapter<Song> {

    private List<Song> songs;

    private LruCache<Float, Bitmap> mMemoryCache;
    private DiskLruCache mDiskLruCache;
    private final Object mDiskCacheLock = new Object();
    private boolean mDiskCacheStarting = true;
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
    private static final String DISK_CACHE_SUBDIR = "thumbnails";


    public SongListAdapter(Context context, int resource, List<Song> objects) {
        super(context, resource, objects);
        songs = objects;

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 4;

        RetainFragment retainFragment = RetainFragment.findOrCreateRetainFragment(((Activity) context).getFragmentManager());
        mMemoryCache = retainFragment.mRetainedCache;
        if (mMemoryCache == null) {
            // Initialize cache here as usual
            mMemoryCache = new LruCache<Float, Bitmap>(cacheSize);
            retainFragment.mRetainedCache = mMemoryCache;
        }
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.list_item, parent, false);
        }

        Song song = songs.get(position);

        TextView titleText = (TextView) convertView.findViewById(R.id.titleText1);
        titleText.setText(song.getTitle());

        TextView artistText = (TextView) convertView.findViewById(R.id.artistText);
        artistText.setText(song.getArtist());

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView1);
        imageView.setImageDrawable(null);

        //display img
        Object bitmap = mMemoryCache.get(song.getId());
        if (bitmap != null) {
            ImageView iv = (ImageView) convertView.findViewById(R.id.imageView1);
            iv.setImageBitmap((Bitmap) bitmap);
        } else {
            SongAndView container = new SongAndView();
            container.song = song;
            container.view = convertView;

            Log.v("cubanmusicbox", "Calling ImageLoader in SongListAdapter()");
            ImageLoader loader = new ImageLoader();
            loader.execute(container);
        }


        return convertView;
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
            ImageView imageView = (ImageView) result.view.findViewById(R.id.imageView1);
            imageView.setImageDrawable(null);
            imageView.setImageBitmap(result.bitmap);
            mMemoryCache.put(result.song.getId(), result.bitmap);
        }
    }
}
