package com.mafoglem.pacman.events;


/**
 * @author Michael Fogleman
 */
public class EventPrinter extends EngineAdapter {
	
	private static long START = System.currentTimeMillis();

	public void stateChanged(StateEvent e) {
		StringBuffer b = new StringBuffer();
		b.append("Old State: ");
		b.append(e.oldState);
		b.append(", New State: ");
		b.append(e.newState);
		print(e, b);
	}
	
	public void cellEaten(EatEvent e) {
		StringBuffer b = new StringBuffer();
		b.append("Player: ");
		b.append(e.player);
		b.append(", Position: ");
		b.append(e.position);
		b.append(", Cell: ");
		b.append(e.cell);
		print(e, b);
	}
	
	public void playerPositionChanged(PlayerEvent e) {
		StringBuffer b = new StringBuffer();
		b.append("Player: ");
		b.append(e.player);
		b.append(", Position: ");
		b.append(e.player.getPosition());
		print(e, b);
	}
	
	public void playerSpeedChanged(PlayerEvent e) {
		StringBuffer b = new StringBuffer();
		b.append("Player: ");
		b.append(e.player);
		b.append(", Speed: ");
		b.append(e.player.getSpeed());
		print(e, b);
	}
	
	public void playerDirectionChanged(PlayerEvent e) {
		StringBuffer b = new StringBuffer();
		b.append("Player: ");
		b.append(e.player);
		b.append(", Direction: ");
		b.append(e.player.getDirection());
		print(e, b);
	}
	
	public void playerLivenessChanged(PlayerEvent e) {
		StringBuffer b = new StringBuffer();
		b.append("Player: ");
		b.append(e.player);
		b.append(", Dead: ");
		b.append(e.player.isDead());
		print(e, b);
	}
	
	public void playerVulnerabilityChanged(PlayerEvent e) {
		StringBuffer b = new StringBuffer();
		b.append("Player: ");
		b.append(e.player);
		b.append(", Vulnerable: ");
		b.append(e.player.isVulnerable());
		print(e, b);
	}
	
	public void collisionOccurred(CollisionEvent e) {
		StringBuffer b = new StringBuffer();
		b.append("Player1: ");
		b.append(e.player1);
		b.append(", Player2: ");
		b.append(e.player2);
		print(e, b);
	}
	
	private void print(EngineEvent e, Object message) {
		StringBuffer b = new StringBuffer();
		
		b.append("E");
		String id = Integer.toString(e.id);
		int pad = 5 - id.length();
		for (int i = 0; i < pad; i++) {
			b.append("0");
		}
		b.append(id);
		b.append(" T");
		
		long now = System.currentTimeMillis();
		long time = now - START;
		
		long millis = time % 1000;
		time /= 1000;
		long seconds = time % 60;
		time /= 60;
		long minutes = time % 60;
		time /= 60;
		long hours = time % 24;
		
		b.append(hours);
		b.append(":");
		
		if (minutes < 10) b.append("0");
		b.append(minutes);
		b.append(":");
		
		if (seconds < 10) b.append("0");
		b.append(seconds);
		b.append(".");
		
		if (millis < 100) b.append("0");
		if (millis < 10) b.append("0");
		b.append(millis);
		
		String clazz = e.getClass().getName();
		int index = clazz.lastIndexOf('.');
		if (index > 0) clazz = clazz.substring(index+1);
		
		b.append(" - ");
		b.append(clazz);
		
		String msg = message == null ? null : message.toString();
		if (msg != null && msg.length() > 0) {
			b.append(": ");
			b.append(msg);
		}
		
		System.out.println(b);
	}

}
