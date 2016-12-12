package com.mafoglem.pacman.engine;

/**
 * @author Michael Fogleman
 */
public class PlayerType {
	
	public final static PlayerType PACMAN = new PlayerType();
	public final static PlayerType MONSTER = new PlayerType();
	public final static PlayerType FRUIT = new PlayerType();
	
	private PlayerType() {
	}

}
