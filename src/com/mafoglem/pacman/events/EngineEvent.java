package com.mafoglem.pacman.events;


/**
 * @author Michael Fogleman
 */
public abstract class EngineEvent {
	
	private static int counter = 0;
	
	public long time;
	public int id;
	
	public EngineEvent() {
		time = System.currentTimeMillis();
		id = counter++;
	}

}
