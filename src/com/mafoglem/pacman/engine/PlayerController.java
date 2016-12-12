package com.mafoglem.pacman.engine;

/**
 * @author Michael Fogleman
 */
public interface PlayerController {
	
	public Direction getDirection();
	public void setDirection(Direction direction);
	public boolean isHumanControlled();

}
