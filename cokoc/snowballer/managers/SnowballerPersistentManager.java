package cokoc.snowballer.managers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class SnowballerPersistentManager {
	public abstract void loadData();

	public abstract void saveData();

	protected void save(Object obj,String path) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}
	
	protected Object load(String path) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
		Object result = ois.readObject();
		ois.close();
		return result;
	}
}
