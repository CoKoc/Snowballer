package cokoc.snowballer.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileIO {
	static public void copyFile(String path, String filename, InputStream content) {
		File file = new File("plugins" + path, filename);
		try {
			if(!file.exists()) {
				File dir = new File("plugins/", path);
				dir.mkdirs();
				file.createNewFile();
			}
			OutputStream out = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int len;
			while((len = content.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public boolean checkFileCreate(String path, String filename) {
		File file = new File("plugins" + path, filename);
		if (!(file.exists())) {
			try {
				File dir = new File("plugins/", path);
				dir.mkdirs();
				file.createNewFile();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	static public boolean checkFile(String path, String filename) {
		File file = new File("plugins" + path, filename);
		if (!(file.exists()))
			return false;
		return true;
	}
}
