package com.mafoglem.pacman.view;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

import com.mafoglem.pacman.engine.Game;
import com.mafoglem.pacman.engine.Engine;

/**
 * @author Michael Fogleman
 */
public class SwtGameView extends SwtView {
	
	private Game game;
	private Engine engine;
	
	private SwtHeaderView headerView;
	private Point headerLocation;
	private SwtArenaView arenaView;
	private Point arenaLocation;
	private SwtFooterView footerView;
	private Point footerLocation;
	private int arenaHeight;
	private int headerHeight;
	private int footerHeight;
	
	
	public SwtGameView(Game game) {
		this.game = game;
		engine = game.getEngine();
		
		arenaView = new SwtArenaView(engine);
		headerView = new SwtHeaderView(game);
		footerView = new SwtFooterView(game);
	}
	
	public void init(int width, int height) {
		super.init(width, height);
		
		headerHeight = height / 15;
		footerHeight = height / 15;
		
		arenaHeight = height - headerHeight - footerHeight;
		if (arenaHeight < 50) {
			arenaHeight = 50;
		}
		
		headerLocation = new Point(0, 0);
		headerView.init(width, headerHeight);
		
		arenaLocation = new Point(0, headerHeight);
		arenaView.init(width, arenaHeight);
		
		footerLocation = new Point(0, headerHeight + arenaHeight);
		footerView.init(width, footerHeight);
	}
	
	public void destroy() {
		arenaView.destroy();
		headerView.destroy();
		footerView.destroy();
		super.destroy();
	}
	
	public void redraw() {
		Image buffer = getBuffer();
		if (buffer == null) return;
		
		game.update();
		
//		if (game.getEngine() != engine) {
//			engine = game.getEngine();
//			arenaView.destroy();
//			arenaView = new SwtArenaView(engine);
//			arenaView.init(getWidth(), roundHeight);
//		}
		
		headerView.redraw();
		arenaView.redraw();
		footerView.redraw();
		
		GC gc = new GC(buffer);
		Image headerBuffer = headerView.getBuffer();
		Image roundBuffer = arenaView.getBuffer();
		Image footerBuffer = footerView.getBuffer();
		gc.drawImage(headerBuffer, headerLocation.x, headerLocation.y);
		gc.drawImage(roundBuffer, arenaLocation.x, arenaLocation.y);
		gc.drawImage(footerBuffer, footerLocation.x, footerLocation.y);
		gc.dispose();
	}

}
