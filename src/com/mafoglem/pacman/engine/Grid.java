package com.mafoglem.pacman.engine;


/**
 * @author Michael Fogleman
 */
public class Grid {
	
	private Cell[][] data;
	private int width;
	private int height;
	private int foodCount;
	
	
	private Grid(int width, int height) {
		this.width = width;
		this.height = height;
		this.foodCount = 0;
		
		data = new Cell[width][height];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				data[x][y] = Cell.SPACE;
			}
		}
	}
	
	public static Grid createInstance(Grid source) {
		if (source == null) return null;
		int width = source.getWidth();
		int height = source.getHeight();
		Grid copy = new Grid(width, height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Cell cell = source.getCell(x, y);
				copy.setCell(x, y, cell);
			}
		}
		return copy;
	}
	
	public static Grid createInstance(String[] map) {
		if (map == null) return null;
		if (map.length == 0) return null;
		for (int i = 0; i < map.length; i++) {
			String row = map[i];
			if (row == null) return null;
			if (i < map.length - 1) {
				if (row.length() != map[i+1].length()) return null;
			}
		}
		
		int width = map[0].length();
		int height = map.length;
		
		Grid grid = new Grid(width, height);
		for (int y = 0; y < map.length; y++) {
			String row = map[y];
			for (int x = 0; x < row.length(); x++) {
				char c = row.charAt(x);
				Cell cell = Cell.getInstance(c);
				if (cell == null) return null;
				grid.setCell(x, y, cell);
			}
		}
		
		return grid;
	}
	
	public void setCell(Position p, Cell cell) {
		setCell(p.getX(), p.getY(), cell);
	}
	
	public void setCell(int x, int y, Cell cell) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return;
		}
		Cell previous = getCell(x, y);
		if (previous == Cell.FOOD || previous == Cell.PILL) {
			foodCount--;
		}
		data[x][y] = cell;
		if (cell == Cell.FOOD || cell == Cell.PILL) {
			foodCount++;
		}
	}
	
	public Cell getCell(Position p) {
		return getCell(p.getX(), p.getY());
	}
	
	public Cell getCell(int x, int y) {
		if (x < 0 || y < 0 || x >= width || y >= height) {
			return Cell.SPACE;
		}
		return data[x][y];
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getFoodCount() {
		return foodCount;
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("Width:  ").append(width).append('\n');
		b.append("Height: ").append(height).append('\n');
		b.append("Food:   ").append(foodCount).append('\n');
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				b.append(data[x][y]);
			}
			b.append('\n');
		}
		return b.toString();
	}

}
