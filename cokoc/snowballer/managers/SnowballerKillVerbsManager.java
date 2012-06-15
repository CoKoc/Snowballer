package cokoc.snowballer.managers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class SnowballerKillVerbsManager {
	private static ArrayList<String> killVerbs;
	
	public SnowballerKillVerbsManager() {
		killVerbs = new ArrayList<String>();
	}
	
	public static String getRandomKillVerb() {
		Random generator = new Random();
		int random = generator.nextInt(killVerbs.size());
		return killVerbs.get(random);
	}
	
	public static void loadMessages() {
		if(killVerbs == null)
			killVerbs = new ArrayList<String>();
		File translationFile = new File("plugins/Snowballer/killverbs.txt");
		Scanner fileScanner;
		try {
			fileScanner = new Scanner(translationFile);
			while(fileScanner.hasNextLine()) {
				String currentLine = fileScanner.nextLine();
				if(currentLine != null)
					if(! currentLine.isEmpty())
					killVerbs.add(currentLine);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
