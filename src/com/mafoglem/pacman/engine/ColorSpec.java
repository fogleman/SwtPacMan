package com.mafoglem.pacman.engine;

/**
 * @author Michael Fogleman
 */
public class ColorSpec {
	
	private int r;
	private int g;
	private int b;
	
	public ColorSpec(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public int getRed() {
		return r;
	}
	
	public int getGreen() {
		return g;
	}
	
	public int getBlue() {
		return b;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(r).append(",");
		buffer.append(g).append(",");
		buffer.append(b);
		return buffer.toString();
	}

}
