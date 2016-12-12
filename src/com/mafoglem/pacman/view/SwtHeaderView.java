package com.mafoglem.pacman.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import com.mafoglem.pacman.engine.Game;

/**
 * @author Michael Fogleman
 */
public class SwtHeaderView extends SwtView {
	
	private Game game;
	private Color background;
	
	private Font scoreFont;
	private Color scoreColor;
	
	
	public SwtHeaderView(Game game) {
		this.game = game;
	}
	
	public void init(int width, int height) {
		super.init(width, height);
		background = new Color(null, 0, 0, 0);
		scoreFont = new Font(null, "Helvetica", (int)(height/2), SWT.BOLD);
		scoreColor = new Color(null, 255, 255, 255);
	}
	
	public void destroy() {
		background.dispose();
		scoreFont.dispose();
		scoreColor.dispose();
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
		
		int score = game.getScore();
		drawScore(gc, score);
		
		gc.dispose();
	}
	
	private void drawScore(GC gc, int score) {
		gc.setFont(scoreFont);
		gc.setForeground(scoreColor);
		gc.setBackground(background);
		
		String s = Integer.toString(score);
		int w = gc.stringExtent(s).x;
		int x = getWidth() - getWidth()/2 - w/2;
		int y = getHeight()/3;
		
		gc.drawString(s, x, y);
	}

}
