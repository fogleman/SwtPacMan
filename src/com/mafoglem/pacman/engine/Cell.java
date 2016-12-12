package com.mafoglem.pacman.engine;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Michael Fogleman
 */
public class Cell {
	
	private static Map map = new HashMap();
	
	public final static Cell WALL = new Cell("WALL", 'x');
	public final static Cell FOOD = new Cell("FOOD", '.');
	public final static Cell PILL = new Cell("PILL", '*');
	public final static Cell SPACE = new Cell("SPACE", ' ');
	public final static Cell BOX = new Cell("BOX", '#');
	public final static Cell GATE = new Cell("GATE", '=');
	
	public static Cell getInstance(char c) {
		Character key = new Character(c);
		return (Cell)map.get(key);
	}
	
	private static void register(Cell block) {
		Character key = new Character(block.c);
		map.put(key, block);
	}
	
	private String name;
	private char c;
	
	private Cell(String name, char c) {
		this.name = name;
		this.c = c;
		register(this);
	}
	
	public boolean isWall() {
		return this == Cell.WALL || this == Cell.BOX;
	}
	
	public boolean isEatable() {
		return this == Cell.FOOD || this == Cell.PILL;
	}
	
	public String toString() {
		return name;
	}

}
