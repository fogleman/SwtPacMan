package com.mafoglem.pacman.engine;

/**
 * @author Michael Fogleman
 */
public class Position {
	
	private int x;
	private int y;
	
	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Position(Position p, Direction d) {
		this(p.getX() + d.getDx(), p.getY() + d.getDy());
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(x);
		b.append(',');
		b.append(y);
		return b.toString();
	}
	
	public boolean equals(Object that) {
		if (that == null) return false;
		if (this == that) return true;
		if (!(that instanceof Position)) return false;
		Position p = (Position)that;
		if (p.x != x) return false;
		if (p.y != y) return false;
		return true;
	}
	
	public int hashCode() {
		return (x << 8) | y;
	}

}
