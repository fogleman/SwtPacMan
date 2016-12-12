package com.mafoglem.pacman.view;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import com.mafoglem.pacman.engine.Cell;
import com.mafoglem.pacman.engine.Direction;
import com.mafoglem.pacman.engine.Engine;
import com.mafoglem.pacman.engine.Grid;
import com.mafoglem.pacman.engine.LevelConfig;
import com.mafoglem.pacman.engine.Player;
import com.mafoglem.pacman.engine.PlayerType;

/**
 * @author Michael Fogleman
 */
public class SwtArenaView extends SwtView {
	
	private Engine engine;
	private LevelConfig config;
	private Grid grid;
	
	private Color background;
	private Color wallBorder;
	private Color wallFill;
	private Color boxColor;
	private Color gateColor;
	private Color foodColor;
	private Color pillColor;
	private Color pillFlashColor;
	private Color deadMonsterColor;
	private Color pupilColor;
	private Color vulnerableColor;
	private Color vulnerableFlashColor;
	private Color[] playerColors;
	private long pillWarning;
	
	private Image staticBuffer;
	private Image flashBuffer;
	private int cellSize;
	private int xOffset;
	private int yOffset;
	
	
	public SwtArenaView(Engine engine) {
		this.engine = engine;
	}
	
	public void init(int width, int height) {
		super.init(width, height);
		
		grid = engine.getGrid();
		config = engine.getLevelConfig();
		
		int gridWidth = grid.getWidth();
		int gridHeight = grid.getHeight();
		int s1 = width / gridWidth;
		int s2 = height / gridHeight;
		cellSize = Math.min(s1, s2);
		if (cellSize % 2 != 0) cellSize--;
		xOffset = (width - gridWidth * cellSize) / 2;
		yOffset = (height - gridHeight * cellSize) / 2;
		
		background = new Color(null, 0, 0, 0);
		wallBorder = new Color(null, 255, 255, 255);
		wallFill = new Color(null, 0, 0, 0);
		boxColor = createColor(config.getColor(LevelConfig.BOX_COLOR));
		gateColor = new Color(null, 0, 255, 255);
		foodColor = createColor(config.getColor(LevelConfig.FOOD_COLOR));
		pillColor = createColor(config.getColor(LevelConfig.PILL_COLOR));
		pillFlashColor = new Color(null, 0, 0, 0);
		deadMonsterColor = createColor(config.getColor(LevelConfig.MONSTER_DEAD_COLOR));
		pupilColor = new Color(null, 0, 0, 255);
		vulnerableColor = createColor(config.getColor(LevelConfig.MONSTER_VULNERABLE_COLOR));
		vulnerableFlashColor = createColor(config.getColor(LevelConfig.MONSTER_VULNERABLE_FLASH_COLOR));
		pillWarning = config.getLong(LevelConfig.PILL_WARNING);
		
		Player[] players = engine.getPlayers();
		playerColors = new Color[players.length];
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			playerColors[i] = createColor(player.getColor());
		}
		
		flashBuffer = createBackground();

		wallBorder.dispose();
		wallFill.dispose();
		wallBorder = createColor(config.getColor(LevelConfig.WALL_BORDER_COLOR));
		wallFill = createColor(config.getColor(LevelConfig.WALL_FILL_COLOR));
		
		staticBuffer = createBackground();
	}
	
	private Image createBackground() {
		Image buffer = new Image(null, getWidth(), getHeight());
		GC gc = new GC(buffer);
		gc.setBackground(background);
		gc.fillRectangle(0, 0, getWidth(), getHeight());
		drawGrid(gc, true, false);
		gc.dispose();
		return buffer;
	}
	
	public void destroy() {
		background.dispose();
		wallBorder.dispose();
		wallFill.dispose();
		boxColor.dispose();
		gateColor.dispose();
		foodColor.dispose();
		pillColor.dispose();
		pillFlashColor.dispose();
		deadMonsterColor.dispose();
		pupilColor.dispose();
		vulnerableColor.dispose();
		vulnerableFlashColor.dispose();
		staticBuffer.dispose();
		flashBuffer.dispose();
		
		for (int i = 0; i < playerColors.length; i++) {
			playerColors[i].dispose();
		}
		
		super.destroy();
	}
	
	public void redraw() {
		if (grid != engine.getGrid()) {
			resize(getWidth(), getHeight());
		}
		
		Image buffer = getBuffer();
		if (buffer == null) return;
		
		boolean center = false;
		
		GC gc = new GC(buffer);
		if (center) {
			cellSize = Math.max(getWidth(), getHeight()) / 8;
			cellSize += cellSize % 2;
			int index = getTimeTick(6000, engine.getPlayers().length);
			centerOn(engine.getPlayers()[0]);
			gc.setBackground(background);
			gc.fillRectangle(0, 0, getWidth(), getHeight());
			drawGrid(gc, true, true);
			drawPlayers(gc);
		}
		else {
			if (engine.getState() == Engine.STATE_WON) {
				int n = getTimeTick(250, 2);
				if (n == 0) {
					gc.drawImage(staticBuffer, 0, 0);
				}
				else {
					gc.drawImage(flashBuffer, 0, 0);
				}
			}
			else {
				gc.drawImage(staticBuffer, 0, 0);
				drawGrid(gc, false, true);
				drawPlayers(gc);
			}
		}
		gc.dispose();
	}
	
	private void centerOn(Player player) {
		int i = player.getPosition().getX();
		int j = player.getPosition().getY();
		
		int offset = (int)(player.getOffset() * cellSize);
		int xoff = offset * player.getDirection().getDx();
		int yoff = offset * player.getDirection().getDy();
		
		int x = cellSize * i + xoff;
		int y = cellSize * j + yoff;
		
		int w = getWidth() / 2 - cellSize / 2;
		int h = getHeight() / 2 - cellSize / 2;		
		if (x < w) x = w;
		if (y < h) y = h;
		
		w = grid.getWidth() * cellSize - getWidth() / 2 - cellSize / 2;
		h = grid.getHeight() * cellSize - getHeight() / 2 - cellSize / 2;
		if (x > w) x = w;
		if (y > h) y = h;
		
		xOffset = getWidth() / 2 - x - cellSize / 2;
		yOffset = getHeight() / 2 - y - cellSize / 2;
	}
	
	private void drawPlayers(GC gc) {
		Player[] players = engine.getPlayers();
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			Color color = playerColors[i];
			drawPlayer(gc, player, color);
		}
	}
	
	private void drawPlayer(GC gc, Player player, Color color) {
		int i = player.getPosition().getX();
		int j = player.getPosition().getY();
		
		int offset = (int)(player.getOffset() * cellSize);
		int xoff = offset * player.getDirection().getDx();
		int yoff = offset * player.getDirection().getDy();
		
		int x = cellSize * i + xOffset + xoff;
		int y = cellSize * j + yOffset + yoff;
		
		if (player.getType() == PlayerType.PACMAN) {
			drawPacMan(gc, color, player, x, y);
		}
		else if (player.getType() == PlayerType.MONSTER) {
			if (player.isDead()) {
				drawDeadMonster(gc, color, player, x, y);
			}
			else {
				drawMonster(gc, color, player, x, y);
			}
		}
		
//		gc.setForeground(vulnerableFlashColor);
//		gc.drawRectangle(x - xoff, y - yoff, cellSize, cellSize);
//		gc.drawRectangle(x, y, cellSize, cellSize);
	}


//		// fruit movement
//		double stop = 1;
//		double off = player.getOffset();
//		double xo = 0.0;
//		double yo = 0.0;
//		if (off >= stop) {
//			xo = 1.0;
//			yo = 0.0;
//		}
//		else {
//			yo = Math.sin(off * (3.14159 / stop)) * -0.25;
//			xo = off * (1.0 / stop);
//		}
//		int xoff = (int)(xo * cellSize) * player.getDirection().getDx();
//		int yoff = (int)(yo * cellSize);
//		x = cellSize * i + xOffset + xoff;
//		y = cellSize * j + yOffset + yoff;

	
	private void drawPacMan(GC gc, Color color, Player player, int x, int y) {
		double t = ((System.currentTimeMillis() / 1) % 31400) / 50;
		if (player.getEta() == 0 || player.isDead()) t = 0;
		int wide = (int)(20.0 * (Math.sin(t)+1)) + 10;
		int start = -180 + wide;
		int angle = start * -2;
		start += 90 * player.getDirection().intValue();
		
		if (player.isDead() || engine.getState() == Engine.STATE_DEAD) {
			start -= 90 * player.getDirection().intValue();
			start += getTimeTick(2, 360);
		}
		
		gc.setBackground(color);
		gc.fillArc(x, y, cellSize, cellSize, start, angle);
	}
	
	private void drawMonster(GC gc, Color color, Player player, int x, int y) {
		int size = cellSize;
		int x1 = x;
		int y1 = y;
		int x2 = x1 + size / 2;
		int y2 = y1 + size / 2;
		int x3 = x1 + size;
		int y3 = y1 + size;
		int y4 = y3 - size / 6;
		int x4 = x1 + size / 4;
		int x5 = x3 - size / 4;
		
		if (player.isVulnerable()) {
			long now = System.currentTimeMillis();
			long eta = player.getVulnerableEta();
			if (eta - now < pillWarning) {
				int n = getTimeTick(200, 2);
				if (n == 0) {
					color = vulnerableColor;
				}
				else {
					color = vulnerableFlashColor;
				}
			}
			else {
				color = vulnerableColor;
			}
		}
		
		gc.setBackground(color);
		gc.fillArc(x1, y1, size, size, 0, 180);
		gc.fillRectangle(x1, y2, size, y4-y2);
		
		int[] p = null;
		p = new int[] {x1,y4,x4,y4,x1,y3};
		gc.fillPolygon(p);
		p = new int[] {x4,y4,x2,y4,x2,y3};
		gc.fillPolygon(p);
		p = new int[] {x2,y4,x5,y4,x2,y3};
		gc.fillPolygon(p);
		p = new int[] {x5,y4,x3,y4,x3,y3};
		gc.fillPolygon(p);
		
		drawMonsterEyes(gc, color, player, x, y);
	}
	
	private void drawDeadMonster(GC gc, Color color, Player player, int x, int y) {
//		int size = cellSize;
//		
//		int x1 = x + size / 2;
//		int x2 = x1 - size / 6;
//		int x3 = x1 + size / 6;
//		int y1 = y + size / 2;
//		int y2 = y1 - size / 4;
//		int y3 = y1 + size / 4;
//		int w = size / 8;
//		
//		gc.setBackground(deadMonsterColor);
//		gc.fillOval(x2-w, y2, w*2, y3-y2);
//		gc.fillOval(x3-w, y2, w*2, y3-y2);
		drawMonsterEyes(gc, color, player, x, y);
	}
	
	
	/**
	 * @author helltoupee at ArsTechnica
	 */
	private void drawMonsterEyes(GC gc, Color color, Player player,	int x, int y) {
		int size = cellSize;

		int x1 = x + size / 2;
		int x2 = x1 - size / 5;
		int x3 = x1 + size / 5;
		int y1 = y + size / 2;
		int y2 = y1 - size / 4;
		int y3 = y1 + size / 4;
		int w = size / 6;
		int offset = size / 8;
		// Move the eyeballs slightly in the direction it's moving
		int lx, ly, rx, ry; // pupils' positions
		int rad = size / 9; // pupil size

		if (player.getDirection() == Direction.LEFT) {
			x2 -= offset;
			x3 -= offset;
			lx = x2 - w;
			ly = y1 - rad;
			rx = x3 - w;
			ry = y1 - rad;
		}
		else if (player.getDirection() == Direction.UP) {
			y2 -= offset;
			y3 -= offset;
			lx = x2 - rad;
			ly = y2;
			rx = x3 - rad;
			ry = y2;
		}
		else if (player.getDirection() == Direction.RIGHT) {
			x2 += offset;
			x3 += offset;
			lx = x2 + w - rad * 2;
			ly = y1 - rad;
			rx = x3 + w - rad * 2;
			ry = y1 - rad;
		}
		else /* player.getDirection() == Direction.DOWN */ {
			y2 += offset;
			y3 += offset;
			lx = x2 - rad;
			ly = y3 - rad * 2;
			rx = x3 - rad;
			ry = y3 - rad * 2;
		}

		gc.setBackground(deadMonsterColor);
		gc.fillOval(x2 - w, y2, w * 2, y3 - y2);
		gc.fillOval(x3 - w, y2, w * 2, y3 - y2);

		gc.setBackground(pupilColor);
		gc.fillOval(lx, ly, rad * 2, rad * 2);
		gc.fillOval(rx, ry, rad * 2, rad * 2);
	}
	
	private void drawGrid(GC gc, boolean staticBlocks, boolean dynamicBlocks) {
		long pillFlashRate = 250;
		
		int width = grid.getWidth();
		int height = grid.getHeight();
		int size = cellSize;
		
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				Cell block = grid.getCell(i, j);
				int x = size * i + xOffset;
				int y = size * j + yOffset;
				
//				if (staticBlocks && block == Cell.BOX) {
//					int x1 = x;
//					int y1 = y;
//					int x2 = x + size / 2;
//					int y2 = y + size / 2;
//					int x3 = x + size;
//					int y3 = y + size;
//					int w = size / 8;
//					int w2 = w * 2;
//					int s = size / 2;
//					
//					Cell n1 = grid.getCell(i, j-1);
//					Cell n2 = grid.getCell(i-1, j);
//					Cell n3 = grid.getCell(i, j+1);
//					Cell n4 = grid.getCell(i+1, j);
//					
//					gc.setBackground(wallFill);
//					gc.setForeground(wallBorder);
//					gc.fillRectangle(x2-w,y2-w,w2,w2);
//					if (n1 == Cell.BOX) {
//						gc.fillRectangle(x2-w,y1,w2,s);
//					}
//					if (n2 == Cell.BOX) {
//						gc.fillRectangle(x1,y2-w,s,w2);
//					}
//					if (n3 == Cell.BOX) {
//						gc.fillRectangle(x2-w,y2,w2,s);
//					}
//					if (n4 == Cell.BOX) {
//						gc.fillRectangle(x2,y2-w,s,w2);
//					}
//				}
				
				if (staticBlocks && block == Cell.BOX) {
					int x1 = x;
					int y1 = y;
					int x2 = x + size / 2;
					int y2 = y + size / 2;
					int x3 = x + size;
					int y3 = y + size;
					
					Cell n1 = grid.getCell(i, j-1);
					Cell n2 = grid.getCell(i-1, j);
					Cell n3 = grid.getCell(i, j+1);
					Cell n4 = grid.getCell(i+1, j);
					
					gc.setLineWidth(7);
					gc.setForeground(boxColor);
					
					if (n1 == Cell.BOX) {
						gc.drawLine(x2, y2, x2, y1);
					}
					if (n2 == Cell.BOX) {
						gc.drawLine(x1, y2, x2, y2);
					}
					if (n3 == Cell.BOX) {
						gc.drawLine(x2, y2, x2, y3+1);
					}
					if (n4 == Cell.BOX) {
						gc.drawLine(x2, y2, x3, y2);
					}
				}
				
				if (staticBlocks && block == Cell.WALL) {
					int padding = size / 4;
					int x1 = x;
					int y1 = y;
					int x2 = x1 + padding;
					int y2 = y1 + padding;
					int x3 = x1 + size / 2;
					int y3 = y1 + size / 2;
					int x5 = x1 + size;
					int y5 = y1 + size;
					int x4 = x5 - padding;
					int y4 = y5 - padding;
					int w1 = size;
					int h1 = size;
					int w2 = w1 - padding * 2;
					int h2 = h1 - padding * 2;
					int w3 = w1 / 2;
					int h3 = h1 / 2;
					int w4 = w2 / 2;
					int h4 = h2 / 2;
					int d = size / 2;
					
					gc.setForeground(wallBorder);
					gc.setBackground(wallFill);
					gc.setLineWidth(3);
					gc.fillRoundRectangle(x2,y2,w2,h2,d,d);
					gc.drawRoundRectangle(x2,y2,w2,h2,d,d);
			
					boolean n1, n2, n3;
			
					// left-bottom corner
					n1 = grid.getCell(i-1, j).isWall();
					n2 = grid.getCell(i, j+1).isWall();
					n3 = grid.getCell(i-1, j+1).isWall();
			
					if (n1 && n2 && n3) {
						gc.fillRectangle(x1, y3, w3, h3);
					}
					else if (n1 && n2) {
						gc.fillRectangle(x1, y3, w3, h4);
						gc.fillRectangle(x2, y3, w4, h3);
						gc.drawLine(x1, y4, x2, y4);
						gc.drawLine(x2, y4, x2, y5);
					}
					else if (n1) {
						gc.fillRectangle(x1, y3, w3, h4);
						gc.drawLine(x1, y4, x3, y4);
					}
					else if (n2) {
						gc.fillRectangle(x2, y3, w4, h3);
						gc.drawLine(x2, y3, x2, y5);
					}
			
					// right-bottom corner
					n1 = grid.getCell(i+1, j).isWall();
					n2 = grid.getCell(i, j+1).isWall();
					n3 = grid.getCell(i+1, j+1).isWall();
			
					if (n1 && n2 && n3) {
						gc.fillRectangle(x3, y3, w3, h3);
					}
					else if (n1 && n2) {
						gc.fillRectangle(x3, y3, w3, h4);
						gc.fillRectangle(x3, y3, w4, h3);
						gc.drawLine(x4, y4, x5, y4);
						gc.drawLine(x4, y4, x4, y5);
					}
					else if (n1) {
						gc.fillRectangle(x3, y3, w3, h4);
						gc.drawLine(x3, y4, x5, y4);
					}
					else if (n2) {
						gc.fillRectangle(x3, y3, w4, h3);
						gc.drawLine(x4, y3, x4, y5);
					}
			
					// right-top corner
					n1 = grid.getCell(i+1, j).isWall();
					n2 = grid.getCell(i, j-1).isWall();
					n3 = grid.getCell(i+1, j-1).isWall();
			
					if (n1 && n2 && n3) {
						gc.fillRectangle(x3, y1, w3, h3);
					}
					else if (n1 && n2) {
						gc.fillRectangle(x3, y2, w3, h4);
						gc.fillRectangle(x3, y1, w4, h3);
						gc.drawLine(x4, y1, x4, y2);
						gc.drawLine(x4, y2, x5, y2);
					}
					else if (n1) {
						gc.fillRectangle(x3, y2, w3, h4);
						gc.drawLine(x3, y2, x5, y2);
					}
					else if (n2) {
						gc.fillRectangle(x3, y1, w4, h3);
						gc.drawLine(x4, y1, x4, y3);
					}
			
					// left-top corner
					n1 = grid.getCell(i-1, j).isWall();
					n2 = grid.getCell(i, j-1).isWall();
					n3 = grid.getCell(i-1, j-1).isWall();
			
					if (n1 && n2 && n3) {
						gc.fillRectangle(x1, y1, w3, h3);
					}
					else if (n1 && n2) {
						gc.fillRectangle(x1, y2, w3, h4);
						gc.fillRectangle(x2, y1, w4, h3);
						gc.drawLine(x1, y2, x2, y2);
						gc.drawLine(x2, y1, x2, y2);
					}
					else if (n1) {
						gc.fillRectangle(x1, y2, w3, h4);
						gc.drawLine(x1, y2, x3, y2);
					}
					else if (n2) {
						gc.fillRectangle(x2, y1, w4, h3);
						gc.drawLine(x2, y1, x2, y3);
					}
				}
				
				if (dynamicBlocks && block == Cell.FOOD) {
					int s = size;
					x = x + s/2;
					y = y + s/2;
					s = s / 6;
					x = x - s/2;
					y = y - s/2;
					
					gc.setBackground(foodColor);
					gc.fillRectangle(x, y, s, s);
				}
				
				if (dynamicBlocks && block == Cell.PILL) {
					int s = size;
					x = x + s/2;
					y = y + s/2;
					s = s / 2;
					x = x - s/2;
					y = y - s/2;
					
					if (getTimeTick(pillFlashRate, 2) == 0) {
						gc.setBackground(pillColor);
					}
					else {
						gc.setBackground(pillFlashColor);
					}
					gc.fillOval(x, y, s, s);
				}
			}
		}
	}

}
