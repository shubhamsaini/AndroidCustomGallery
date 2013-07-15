package in.ernet.iisc.cps;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class SongsManager {
	// SDCard Path
	final String MEDIA_PATH1 = "/mnt/sdcard/cnsng/";
	private ArrayList<HashMap<String, String>> songsList = new ArrayList<HashMap<String, String>>();

	// Constructor
	public SongsManager() {
	}

	public ArrayList<HashMap<String, String>> getPlayList() {

		getList(MEDIA_PATH1);
		return songsList;
	}

	public void getList(String path) {
		File home = new File(path);

		for (File file : home.listFiles()) {

			if (file.isDirectory()) {
				if (file.canRead())
					getList(file.getPath());
			} else {
				String filename = file.getName();
				String ext = filename.substring(filename.lastIndexOf('.') + 1,
						filename.length());
				if (ext.equals("MP3") || ext.equals("mp3")) {
					HashMap<String, String> song = new HashMap<String, String>();
					song.put(
							"songTitle",
							file.getName().substring(0,
									(file.getName().length() - 4)));
					song.put("songPath", file.getPath());

					// Adding each song to SongList
					songsList.add(song);
				}
			}
		}
	}
}
