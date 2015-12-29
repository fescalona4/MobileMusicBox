package com.musicbox.mobilemusicbox;

import android.graphics.Bitmap;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by fescalona on 12/24/2015.
 */
public class Song {
    private Float id;
    private String title;
    private String artist;
    private String image;
    private String filename;
    private String url;
    private Bitmap bitmap;

    private int playCount;
    private String downloadCount;
    private Date dateAdded;
    private String filter;

    public Song() {
    }

    public Song( Float id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
    }

    public Float getId() {
        return id;
    }

    public void setId(Float id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public String getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(String downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Date getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(Date dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public static Comparator<Song> SongPlayCountComparator = new Comparator<Song>() {
        @Override
        public int compare(Song lhs, Song rhs) {
            return rhs.getPlayCount() - lhs.getPlayCount();
        }
    };

    public static Comparator<Song> SongDateComparator = new Comparator<Song>() {
        @Override
        public int compare(Song lhs, Song rhs) {
            return rhs.getDateAdded().compareTo(lhs.dateAdded);
        }
    };

}
