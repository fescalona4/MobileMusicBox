package com.musicbox.mobilemusicbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fescalona on 12/24/2015.
 */
public final class DataProvider {

    public static List<Song> songList = new ArrayList<>();
    public static Map<Float, Song> songMap = new HashMap<>();

    static {

        addSong(Float.valueOf(100), "Traidora", "Gente De Zona ft. Marc Anthony");
        addSong(Float.valueOf(101), "Tienes Que Parar", "Yomil & El Dany");
        addSong(Float.valueOf(102), "Solos", "El Micha Ft. Maluma");
        addSong(Float.valueOf(103), "Soltera", "El Principe Ft. Srta Dayana");
    }

    private static void addSong(Float id, String title,
                                   String artist) {
        Song item = new Song(id, title, artist);
        songList.add(item);
        songMap.put(id, item);
    }

    public static List<String> getSongNames() {
        List<String> list = new ArrayList<>();
        for (Song song : songList) {
            list.add(song.getTitle());
        }
        return list;
    }

    public static List<Song> getFilteredList(String searchString) {

        List<Song> filteredList = new ArrayList<>();
        for (Song song : songList) {
            if (song.getFilename().contains(searchString)) {
                filteredList.add(song);
            }
        }

        return filteredList;

    }
}
