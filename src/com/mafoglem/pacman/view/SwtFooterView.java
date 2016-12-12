package com.mafoglem.pacman.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import com.mafoglem.pacman.engine.Game;
import com.mafoglem.pacman.engine.Player;
import com.mafoglem.pacman.engine.PlayerType;

/**
 * @author Michael Fogleman
 */
public class SwtFooterView extends SwtView {
	
	private Game game;
	private Color background;
	private Font foodCountFont;
	private Color foodCountColor;
	private Font levelFont;
	private Color levelColor;
	private Color pacmanColor;
	
	
	public SwtFooterView(Game game) {
		this.game = game;
	}
	
	public void init(int width, int height) {
		super.init(width, height);
		background = new Color(null, 0, 0, 0);
		foodCountFont = new Font(null, "Arial", (int)(height/2.5), SWT.BOLD);
		foodCountColor = new Color(null, 255, 255, 255);
		
		levelFont = new Font(null, "Arial", (int)(height/2.5), SWT.NORMAL);
		levelColor = new Color(null, 255, 255, 255);
		
		pacmanColor = new Color(null, 255, 255, 0);
	}
	
	public void destroy() {
		background.dispose();
		foodCountFont.dispose();
		foodCountColor.dispose();
		levelFont.dispose();
		levelColor.dispose();
		pacmanColor.dispose();
		super.destroy();
	}
	
	public void redraw() {
		Image buffer = getBuffer();
		if (buffer == null) return;
		
		int width = getWidth();
		int height = getHeight();
		
		GC gc = new GC(buffer);
		gc.setBackground(background);
		gc.fillRectangle(0, 0, width, height);
		
		int foodCount = game.getEngine().getGrid().getFoodCount();
//		drawFoodCount(gc, foodCount);

		long countdown = getVulnerableCountdown();
		if (countdown > 0) {
			drawFoodCount(gc, countdown);
		}
		
		int lives = game.getLives();
		drawLives(gc, lives);
		
		int level = game.getLevel();
		drawLevel(gc, level);
		
		gc.dispose();
	}
	
	private long getVulnerableCountdown() {
		long result = 0;
		long now = System.currentTimeMillis();
		Player[] players = game.getEngine().getPlayers();
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			if (player.getType() != PlayerType.MONSTER) continue;
			long eta = player.getVulnerableEta();
			if (eta > now) {
				result = eta - now;
			}
		}
		return result;
	}
	
	private void drawPacMan(GC gc, Color color, int x, int y, int size) {
		double t = 0;
		int wide = (int)(20.0 * (Math.sin(t)+1)) + 10;
		int start = -180 + wide;
		int angle = start * -2;
		start += 90 * 2;
		
		gc.setBackground(color);
		gc.fillArc(x, y, size, size, start, angle);
	}
	
	private void drawLives(GC gc, int lives) {
		int x = getWidth()/6;
		int y = getHeight()/8;
		int size = getHeight()/2;
		int threshold = 5;
		
		for (int i = 0; i < lives; i++) {
			drawPacMan(gc, pacmanColor, x, y, size);
			x += size + size / 2;
			if (lives > threshold) break;
		}
		
		if (lives > threshold) {
			gc.setFont(levelFont);
			gc.setForeground(levelColor);
			gc.setBackground(background);
			
			String s = Integer.toString(lives);
			gc.drawString(s, x, y);
		}
	}
	
	private void drawLevel(GC gc, int level) {
		gc.setFont(levelFont);
		gc.setForeground(levelColor);
		gc.setBackground(background);
		
		String s = "Level " + Integer.toString(level);
		int w = gc.stringExtent(s).x;
		int x = getWidth() - getWidth()/6 - w;
		int y = getHeight()/8;
		
		gc.drawString(s, x, y);
	}
	
	private void drawFoodCount(GC gc, long foodCount) {
		gc.setFont(foodCountFont);
		gc.setForeground(foodCountColor);
		
		String s = Long.toString(foodCount);
		int w = gc.stringExtent(s).x;
		int x = getWidth() / 2 - w / 2;
		int y = 0;
		
		gc.drawString(s, x, y);
	}

}
