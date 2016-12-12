package com.mafoglem.pacman.engine;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Michael Fogleman
 */
public class LevelConfig {
	
	public final static String PACMAN_POSITIONS = "PACMAN_POSITIONS";
	public final static String PACMAN_DIRECTIONS = "PACMAN_DIRECTIONS";
	public final static String MONSTER_POSITIONS = "MONSTER_POSITIONS";
	public final static String MONSTER_DIRECTIONS = "MONSTER_DIRECTIONS";
	public final static String SPAWN_POSITION = "SPAWN_POSITION";
	public final static String FRUIT_PATH = "FRUIT_PATH";
	public final static String PACMAN_SPEED = "PACMAN_SPEED";
	public final static String MONSTER_SPEED = "MONSTER_SPEED";
	public final static String MONSTER_VULNERABLE_SPEED = "MONSTER_VULNERABLE_SPEED";
	public final static String MONSTER_DEAD_SPEED = "MONSTER_DEAD_SPEED";
	public final static String PILL_DURATION = "PILL_DURATION";
	public final static String PILL_WARNING = "PILL_WARNING";
	public final static String PACMAN_COLORS = "PACMAN_COLORS";
	public final static String MONSTER_COLORS = "MONSTER_COLORS";
	public final static String MONSTER_VULNERABLE_COLOR = "MONSTER_VULNERABLE_COLOR";
	public final static String MONSTER_VULNERABLE_FLASH_COLOR = "MONSTER_VULNERABLE_FLASH_COLOR";
	public final static String MONSTER_DEAD_COLOR = "MONSTER_DEAD_COLOR";
	public final static String WALL_FILL_COLOR = "WALL_FILL_COLOR";
	public final static String WALL_BORDER_COLOR = "WALL_BORDER_COLOR";
	public final static String BOX_COLOR = "BOX_COLOR";
	public final static String FOOD_COLOR = "FOOD_COLOR";
	public final static String PILL_COLOR = "PILL_COLOR";
	
	
	
	public static LevelConfig createInstance(String fileName) {
		LevelConfig instance = new LevelConfig();
		try {
			instance.loadFile(fileName);
		}
		catch (Exception e) {
			e.printStackTrace();
			instance = null;
		}
		if (instance.grid == null) {
			instance = null;
		}
		return instance;
	}
	
	public static LevelConfig createInstance(InputStream stream) {
		LevelConfig instance = new LevelConfig();
		try {
			instance.loadFile(stream);
		}
		catch (Exception e) {
			e.printStackTrace();
			instance = null;
		}
		if (instance.grid == null) {
			instance = null;
		}
		return instance;
	}

	
	private Grid grid;
	private Map parameters;
	
	private LevelConfig() {
		parameters = new HashMap();
	}
	
	public Grid getGrid() {
		return grid;
	}
	
	public long getLong(String key) {
		Long value = (Long)get(key);
		return value.longValue();
	}
	
	public long[] getLongList(String key) {
		Long[] list = (Long[])get(key);
		long[] result = new long[list.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = list[i].longValue();
		}
		return result;
	}
	
	public int getInt(String key) {
		Integer value = (Integer)get(key);
		return value.intValue();
	}
	
	public int[] getIntList(String key) {
		Integer[] list = (Integer[])get(key);
		int[] result = new int[list.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = list[i].intValue();
		}
		return result;
	}
	
	public ColorSpec getColor(String key) {
		return (ColorSpec)get(key);
	}
	
	public ColorSpec[] getColorList(String key) {
		return (ColorSpec[])get(key);
	}
	
	public Position getPosition(String key) {
		return (Position)get(key);
	}
	
	public Position[] getPositionList(String key) {
		return (Position[])get(key);
	}
	
	public Direction getDirection(String key) {
		return (Direction)get(key);
	}
	
	public Direction[] getDirectionList(String key) {
		return (Direction[])get(key);
	}
	
	private Object get(String key) {
		return parameters.get(key);
	}
	
	private void put(String key, Object value) {
		parameters.put(key, value);
	}
	
	
	
	private void loadFile(String fileName) throws Exception {
		InputStream stream = new FileInputStream(fileName);
		loadFile(stream);
	}
	
	private void loadFile(InputStream stream) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(stream));
		String line = null;
		while ((line = in.readLine()) != null) {
			if (line.startsWith("#")) continue;

			if (line.startsWith("START_MAP")) {
				grid = loadGrid(in);
			}
			
			int n = line.indexOf("=");
			if (n > 0 && n < line.length()-1) {
				String name = line.substring(0, n);
				String value = line.substring(n+1);
				put(name, loadParameter(name, value));
			}
		}
		in.close();
	}
	
	private Grid loadGrid(BufferedReader in) throws IOException {
		List list = new ArrayList();
		String line = null;
		while ((line = in.readLine()) != null) {
			if (line.startsWith("#")) continue;
			if (line.startsWith("END_MAP")) break;
			list.add(line);
		}
		String[] map = new String[list.size()];
		list.toArray(map);
		Grid grid = Grid.createInstance(map);
		return grid;
	}
	
	private Object loadParameter(String name, String value) {
		String[] s = null;

		s = new String[] {
			PACMAN_POSITIONS,
			MONSTER_POSITIONS,
			FRUIT_PATH
		};
		if (contains(s, name)) {
			return parsePositionList(value);
		}

		s = new String[] {
			PACMAN_DIRECTIONS,
			MONSTER_DIRECTIONS
		};
		if (contains(s, name)) {
			return parseDirectionList(value);
		}

		s = new String[] {
			SPAWN_POSITION
		};
		if (contains(s, name)) {
			return parsePosition(value);
		}

		s = new String[] {
			PACMAN_SPEED,
			MONSTER_SPEED,
			MONSTER_VULNERABLE_SPEED,
			MONSTER_DEAD_SPEED,
			PILL_DURATION,
			PILL_WARNING
		};
		if (contains(s, name)) {
			return parseLong(value);
		}

		s = new String[] {
			PACMAN_COLORS,
			MONSTER_COLORS
		};
		if (contains(s, name)) {
			return parseColorList(value);
		}

		s = new String[] {
			MONSTER_VULNERABLE_COLOR,
			MONSTER_VULNERABLE_FLASH_COLOR,
			MONSTER_DEAD_COLOR,
			WALL_FILL_COLOR,
			WALL_BORDER_COLOR,
			BOX_COLOR,
			FOOD_COLOR,
			PILL_COLOR
		};
		if (contains(s, name)) {
			return parseColor(value);
		}
		
		return value;
	}
	
	private boolean contains(String[] list, String s) {
		for (int i = 0; i < list.length; i++) {
			String string = list[i];
			if (string.equals(s)) return true;
		}
		return false;
	}
	

	
	private Long parseLong(String s) {
		return new Long(s);	
	}
	
	private Long[] parseLongList(String s) {
		String[] list = parseList(s);
		int size = list.length;
		Long[] result = new Long[size];
		for (int i = 0; i < size; i++) {
			result[i] = parseLong(list[i]);
		}
		return result;
	}
	
	private Integer parseInt(String s) {
		return new Integer(s);
	}
	
	private Integer[] parseIntList(String s) {
		String[] list = parseList(s);
		int size = list.length;
		Integer[] result = new Integer[size];
		for (int i = 0; i < size; i++) {
			result[i] = parseInt(list[i]);
		}
		return result;
	}
	
	private ColorSpec parseColor(String s) {
		int[] n = splitInts(s, ",");
		if (n.length != 3) return null;
		return new ColorSpec(n[0], n[1], n[2]);
	}
	
	private ColorSpec[] parseColorList(String s) {
		String[] list = parseList(s);
		int size = list.length;
		ColorSpec[] result = new ColorSpec[size];
		for (int i = 0; i < size; i++) {
			result[i] = parseColor(list[i]);
		}
		return result;
	}
	
	private Position parsePosition(String s) {
		int[] n = splitInts(s, ",");
		if (n.length != 2) return null;
		return new Position(n[0], n[1]);
	}
	
	private Position[] parsePositionList(String s) {
		String[] list = parseList(s);
		int size = list.length;
		Position[] result = new Position[size];
		for (int i = 0; i < size; i++) {
			result[i] = parsePosition(list[i]);
		}
		return result;
	}
	
	private Direction parseDirection(String s) {
		s = s.toUpperCase();
		if (s.equals("UP")) return Direction.UP;
		if (s.equals("DOWN")) return Direction.DOWN;
		if (s.equals("LEFT")) return Direction.LEFT;
		if (s.equals("RIGHT")) return Direction.RIGHT;
		return null;
	}
	
	private Direction[] parseDirectionList(String s) {
		String[] list = parseList(s);
		int size = list.length;
		Direction[] result = new Direction[size];
		for (int i = 0; i < size; i++) {
			result[i] = parseDirection(list[i]);
		}
		return result;
	}
	
	private String[] parseList(String s) {
		return splitStrings(s, ";");
	}
	
	private String[] splitStrings(String s, String delimiters) {
		StringTokenizer t = new StringTokenizer(s, delimiters);
		int size = t.countTokens();
		String[] result = new String[size];
		for (int i = 0; i < size; i++) {
			result[i] = t.nextToken();
		}
		return result;
	}
	
	private int[] splitInts(String s, String delimiters) {
		String[] strings = splitStrings(s, delimiters);
		int[] result = new int[strings.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(strings[i]);
		}
		return result;
	}

}
