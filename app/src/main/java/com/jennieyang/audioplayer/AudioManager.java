package com.jennieyang.audioplayer;

import android.os.Environment;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;

public class AudioManager {
    final String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath() + "/Music";
    private ArrayList<HashMap<String, String>> audioList = new ArrayList<HashMap<String, String>>();

    public ArrayList<HashMap<String, String>> getPlayList() {
        File home = new File(MEDIA_PATH);
        if (home.listFiles(new FileExtensionFilter()).length > 0) {
            for (File file : home.listFiles(new FileExtensionFilter())) {
                HashMap<String, String> track = new HashMap<String, String>();
                track.put("audioTitle", file.getName().substring(0, file.getName().length() - 4)); // look into MediaMetadataRetriever
                track.put("audioPath", file.getPath());

                audioList.add(track);
            }
        }
        return audioList;
    }

    class FileExtensionFilter implements FilenameFilter {
        public boolean accept(File dir, String name) {
            return (name.toLowerCase().endsWith(".mp3"));
        }
    }
}
