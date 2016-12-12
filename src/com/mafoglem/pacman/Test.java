package com.mafoglem.pacman;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.mafoglem.pacman.engine.Game;
import com.mafoglem.pacman.engine.LevelConfig;
import com.mafoglem.pacman.view.SwtGameView;
import com.mafoglem.pacman.view.SwtPlayerController;
import com.mafoglem.pacman.view.SwtWindow;

/**
 * @author Michael Fogleman
 */
public class Test {
	
	public static void main(String[] args) {
		LevelConfig[] levels = null;
		if (args.length == 1) {
			levels = loadLevels(args[0]);
		}
		else {
			levels = loadLevels();
		}
		if (levels == null || levels.length < 1) return;
		
		SwtPlayerController controller = new SwtPlayerController();
		Game game = new Game(levels, controller);
		SwtGameView view = new SwtGameView(game);
		SwtWindow window = new SwtWindow();
		window.setTitle("Pac-Man");
		window.setCanvasSize(700, 700);
		window.addKeyListener(controller);
		window.setView(view);
		window.create();
	}
	
	private static LevelConfig[] loadLevels() {
		List list = new ArrayList();
		File levelDir = new File("levels");
		String[] files = new String[0];
		if (levelDir.exists() && levelDir.isDirectory()) {
			files = levelDir.list();
		}
		for (int i = 0; i < files.length; i++) {
			File levelFile = new File("levels/" + files[i]);
			if (levelFile.exists() && levelFile.isFile()) {
				String fileName = levelFile.getAbsolutePath();
				LevelConfig config = LevelConfig.createInstance(fileName);
				if (config != null) list.add(config);
			}
		}
		LevelConfig[] result = new LevelConfig[list.size()];
		list.toArray(result);
		return result;
	}
	
	private static LevelConfig[] loadLevels(String url) {
		List list = new ArrayList();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openConnection().getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				if (line.length() < 1) continue;
				LevelConfig config = loadLevel(line);
				if (config != null) {
					list.add(config);
				}
			}
			in.close();
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		LevelConfig[] result = new LevelConfig[list.size()];
		list.toArray(result);
		return result;
	}
	
	private static LevelConfig loadLevel(String url) {
		System.out.println("Loading level from " + url);
		LevelConfig result = null;
		try {
			result = LevelConfig.createInstance(new URL(url).openConnection().getInputStream());
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
}


