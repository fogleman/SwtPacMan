package com.mafoglem.pacman.engine;

/**
 * @author Michael Fogleman
 */
public class Direction {
	
	public final static Direction UP = new Direction("UP", 3, 0, -1);
	public final static Direction DOWN = new Direction("DOWN", 1, 0, 1);
	public final static Direction LEFT = new Direction("LEFT", 0, -1, 0);
	public final static Direction RIGHT = new Direction("RIGHT", 2, 1, 0);
	
	private String s;
	private int dx;
	private int dy;
	private int value;
	
	private Direction(String s, int value, int dx, int dy) {
		this.s = s;
		this.value = value;
		this.dx = dx;
		this.dy = dy;
	}
	
	public int getDx() {
		return dx;
	}
	
	public int getDy() {
		return dy;
	}
	
	public int intValue() {
		return value;
	}
	
	public Direction getOpposite() {
		if (this == UP) return DOWN;
		if (this == DOWN) return UP;
		if (this == LEFT) return RIGHT;
		if (this == RIGHT) return LEFT;
		return null;
	}
	
	public String toString() {
		return s;
	}

}
