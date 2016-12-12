package com.mafoglem.pacman.events;

/**
 * @author Michael Fogleman
 */
public interface EngineListener {
	
	public void stateChanged(StateEvent e);
	public void cellEaten(EatEvent e);
	public void playerPositionChanged(PlayerEvent e);
	public void playerSpeedChanged(PlayerEvent e);
	public void playerDirectionChanged(PlayerEvent e);
	public void playerLivenessChanged(PlayerEvent e);
	public void playerVulnerabilityChanged(PlayerEvent e);
	public void collisionOccurred(CollisionEvent e);
	
}
