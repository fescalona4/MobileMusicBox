package parsers;

import com.musicbox.mobilemusicbox.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fescalona on 12/24/2015.
 */
public class SongJSONParser {

    public static List<Song> parseFeed(String content) {

        JSONArray ar = null;
        try {
            ar = new JSONArray(content);
            List<Song> songList = new ArrayList<>();

            for (int i = 0; i < ar.length(); i++) {

                JSONObject obj = ar.getJSONObject(i);
                Song song = new Song();

                song.setId(Float.valueOf(obj.getLong("id")));
                song.setTitle(obj.getString("title"));
                song.setArtist(obj.getString("artist"));
                song.setFilename(obj.getString("filename"));
                song.setImage(obj.getString("image"));
                song.setUrl(obj.getString("url"));

                songList.add(song);
            }
            return songList;


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
