package com.mafoglem.pacman.events;

import com.mafoglem.pacman.engine.Cell;
import com.mafoglem.pacman.engine.Player;
import com.mafoglem.pacman.engine.Position;

/**
 * @author Michael Fogleman
 */
public class EatEvent extends EngineEvent {
	
	public Player player;
	public Position position;
	public Cell cell;

}
