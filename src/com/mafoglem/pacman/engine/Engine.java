package com.mafoglem.pacman.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.mafoglem.pacman.events.CollisionEvent;
import com.mafoglem.pacman.events.EatEvent;
import com.mafoglem.pacman.events.EngineListener;
import com.mafoglem.pacman.events.PlayerEvent;
import com.mafoglem.pacman.events.StateEvent;


/**
 * @author Michael Fogleman
 */
public class Engine {
	
	public final static int STATE_READY = 1;
	public final static int STATE_RUNNING = 2;
	public final static int STATE_DEAD = 3;
	public final static int STATE_WON = 4;
	public final static int STATE_DONE = 5;
	
	private LevelConfig config;
	private Grid grid;
	private GridPathFinder pathFinder;
	private PlayerController controller;
	private Player[] players;
	private Map controllers;
	private List listeners;
	
	private int state;
	private int nextState;
	private long stateEta;
	private long pauseEta;
	
	private Position spawnPosition;
	private long pacmanSpeed;
	private long monsterSpeed;
	private long monsterVulnerableSpeed;
	private long monsterDeadSpeed;
	private long pillDuration;
	
	public Engine(PlayerController controller) {
		this.controller = controller;
		listeners = new ArrayList();
		controllers = new HashMap();
	}
	
	public void reset(LevelConfig config) {
		this.config = config;
		grid = Grid.createInstance(config.getGrid());
		pathFinder = new GridPathFinder(grid);
		reset();
	}
	
	public void reset() {
		players = createPlayers(config);
		
		controllers.clear();
		setPlayerController(players[0], controller);
		for (int i = 1; i < players.length; i++) {
			Player player = players[i];
			PlayerController controller = new MonsterController(pathFinder, players[i], players[0]);
			setPlayerController(player, controller);
		}
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			PlayerController controller = getPlayerController(player);
			controller.setDirection(player.getDirection());
		}
		
		spawnPosition = config.getPosition(LevelConfig.SPAWN_POSITION);
		pacmanSpeed = config.getLong(LevelConfig.PACMAN_SPEED);
		monsterSpeed = config.getLong(LevelConfig.MONSTER_SPEED);
		monsterVulnerableSpeed = config.getLong(LevelConfig.MONSTER_VULNERABLE_SPEED);
		monsterDeadSpeed = config.getLong(LevelConfig.MONSTER_DEAD_SPEED);
		pillDuration = config.getLong(LevelConfig.PILL_DURATION);
		
		setState(STATE_READY, STATE_RUNNING, 1500);
	}
	
	private static Player[] createPlayers(LevelConfig config) {
		List list = new ArrayList();
		int playerId = 0;
		
		Position[] positions = config.getPositionList(LevelConfig.PACMAN_POSITIONS);
		Direction[] directions = config.getDirectionList(LevelConfig.PACMAN_DIRECTIONS);
		ColorSpec[] colors = config.getColorList(LevelConfig.PACMAN_COLORS);
		long speed = config.getLong(LevelConfig.PACMAN_SPEED);
		
		int count = positions.length;
		for (int i = 0; i < count; i++) {
			Position p = positions[i];
			Direction d = directions[i];
			ColorSpec c = colors[i];
			Player player = new Player(playerId++, PlayerType.PACMAN, p);
			player.setDirection(d);
			player.setColor(c);
			player.setSpeed(speed);
			list.add(player);
		}
		
		positions = config.getPositionList(LevelConfig.MONSTER_POSITIONS);
		directions = config.getDirectionList(LevelConfig.MONSTER_DIRECTIONS);
		colors = config.getColorList(LevelConfig.MONSTER_COLORS);
		speed = config.getLong(LevelConfig.MONSTER_SPEED);
		
		long waitTime = 0;
		long now = System.currentTimeMillis();
		count = positions.length;
		for (int i = 0; i < count; i++) {
			waitTime += 2000;
			Position p = positions[i];
			Direction d = directions[i];
			ColorSpec c = colors[i];
			Player player = new Player(playerId++, PlayerType.MONSTER, p);
			player.setDirection(d);
			player.setColor(c);
			player.setSpeed(speed);
			player.setPenaltyEta(now + waitTime);
			list.add(player);
		}
		
		Player[] result = new Player[list.size()];
		list.toArray(result);
		return result;
	}
	
	private void setState(int state) {
		StateEvent e = new StateEvent();
		e.oldState = this.state;
		e.newState = state;
		
		this.state = state;
		stateEta = 0;
		
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			EngineListener listener = (EngineListener)i.next();
			listener.stateChanged(e);
		}
	}
	
	private void setState(int state, int nextState, long delay) {
		setState(state);
		this.nextState = nextState;
		this.stateEta = System.currentTimeMillis() + delay;
	}
	
	private void pause(long duration) {
		pauseEta = System.currentTimeMillis() + duration;
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			player.shiftEtas(duration);
		}
	}
	
	public void update() {
		long now = System.currentTimeMillis();
		
		if (stateEta != 0) {
			if (now < stateEta) {
				return;
			}
			setState(nextState);
		}
		
		if (state == STATE_RUNNING) {
			
			if (pauseEta != 0) {
				if (now < pauseEta) {
					return;
				}
				pauseEta = 0;
			}
			
			updatePlayers();
			if (grid.getFoodCount() == 0) {
				stopPlayers();
				setState(STATE_WON, STATE_DONE, 3000);
			}
		}
	}
	
	private void updatePlayers() {
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			updatePlayerPosition(player);
			updatePlayerOffset(player);
			updatePlayerSpeed(player);
		}
		checkForCollisions();
	}
	
	private void updatePlayerPosition(Player player) {
		PlayerController controller = getPlayerController(player);
		long now = System.currentTimeMillis();
		long eta = player.getEta();
		
		if (eta == 0) {
			Direction direction = getPlayerDirection(player, controller);
			setPlayerDirection(player, direction);
			Cell nextCell = getNextCell(player);
			if (isPassable(player, nextCell)) {
				eta = now + player.getSpeed();
				player.setEta(eta);
			}
		}
		else if (now > eta) {
			setPlayerPosition(player, getNextPosition(player));
			eatCell(player);
			if (true) { //(player.getType() == PlayerType.MONSTER) {
				if (player.isDead() && player.getPosition().equals(spawnPosition)) {
					controller.setDirection(Direction.LEFT);
					setPlayerDirection(player, Direction.LEFT);
					setPlayerDead(player, false);
					setPlayerPenaltyEta(player, now + 2000);
				}
			}
			Direction direction = getPlayerDirection(player, controller);
			setPlayerDirection(player, direction);
			long diff = now - player.getEta();
			if (diff > player.getSpeed()) diff = player.getSpeed();
			eta = now + player.getSpeed() - diff;
			Cell nextCell = getNextCell(player);
			if (!isPassable(player, nextCell)) eta = 0;
			player.setEta(eta);
		}
		else {
			if (controller != null && controller.isHumanControlled()) {
				Direction direction = getPlayerDirection(player, controller);
				if (direction.getOpposite() == player.getDirection()) {
					reversePlayerDirection(player);
				}
			}
		}
	}
	
	private void updatePlayerOffset(Player player) {
		double offset = 0.0;
		
		long eta = player.getEta();
		if (eta != 0) {
			long now = System.currentTimeMillis();
			long diff = eta - now;
			long rate = player.getSpeed();
			offset = (double)(rate - diff) / (double)rate;
		}
		
		player.setOffset(offset);
	}
	
	private void updatePlayerSpeed(Player player) {
		long speed = 1000;
		
		if (player.getType() == PlayerType.PACMAN) {
			speed = pacmanSpeed;
			if (player.isDead()) {
				speed = monsterDeadSpeed;
			}
		}
		else if (player.getType() == PlayerType.MONSTER) {
			speed = monsterSpeed;
			if (player.isDead()) {
				speed = monsterDeadSpeed;
			}
			if (player.isVulnerable()) {
				speed = monsterVulnerableSpeed;
			}
		}
		
		setPlayerSpeed(player, speed);
	}
	
	private void eatCell(Player player) {
		if (player.getType() == PlayerType.PACMAN) {
			Cell cell = grid.getCell(player.getPosition());
			if (cell.isEatable() && !player.isDead()) {
				if (cell == Cell.PILL) {
					makeMonstersVulnerable();
				}
				grid.setCell(player.getPosition(), Cell.SPACE);
					
				EatEvent e = new EatEvent();
				e.cell = cell;
				e.player = player;
				e.position = player.getPosition();
				for (Iterator i = listeners.iterator(); i.hasNext();) {
					EngineListener listener = (EngineListener)i.next();
					listener.cellEaten(e);
				}
			}
		}
	}
	
	private void setPlayerSpeed(Player player, long newSpeed) {
		long oldSpeed = player.getSpeed();
		if (oldSpeed == newSpeed) return;
		
		player.setSpeed(newSpeed);
		
		long eta = player.getEta();
		if (eta != 0) {
			double d = 1.0 - player.getOffset();
			eta = System.currentTimeMillis() + (long)(d * newSpeed);
			player.setEta(eta);
		}
		
		PlayerEvent e = new PlayerEvent();
		e.player = player;
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			EngineListener listener = (EngineListener)i.next();
			listener.playerSpeedChanged(e);
		}
	}
	
	private void setPlayerDirection(Player player, Direction newDirection) {
		Direction oldDirection = player.getDirection();
		if (oldDirection.equals(newDirection)) return;
		
		player.setDirection(newDirection);
		
		PlayerEvent e = new PlayerEvent();
		e.player = player;
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			EngineListener listener = (EngineListener)i.next();
			listener.playerDirectionChanged(e);
		}
	}
	
	private void setPlayerPosition(Player player, Position newPosition) {
		Position oldPosition = player.getPosition();
		if (oldPosition.equals(newPosition)) return;
		
		player.setPosition(newPosition);
		
		PlayerEvent e = new PlayerEvent();
		e.player = player;
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			EngineListener listener = (EngineListener)i.next();
			listener.playerPositionChanged(e);
		}
	}
	
	private void setPlayerVulnerableEta(Player player, long newEta) {
		long oldEta = player.getVulnerableEta();
		if (oldEta == newEta) return;
		
		player.setVulnerableEta(newEta);
		
		PlayerEvent e = new PlayerEvent();
		e.player = player;
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			EngineListener listener = (EngineListener)i.next();
			listener.playerVulnerabilityChanged(e);
		}
	}
	
	private void setPlayerPenaltyEta(Player player, long newEta) {
		long oldEta = player.getPenaltyEta();
		if (oldEta == newEta) return;
		
		player.setPenaltyEta(newEta);
		
//		PlayerEvent e = new PlayerEvent();
//		e.player = player;
//		for (Iterator i = listeners.iterator(); i.hasNext();) {
//			EngineListener listener = (EngineListener)i.next();
//			listener.playerVulnerabilityChanged(e);
//		}
	}
	
	private void setPlayerDead(Player player, boolean newDead) {
		boolean oldDead = player.isDead();
		if (oldDead == newDead) return;
		
		player.setDead(newDead);
		
		PlayerEvent e = new PlayerEvent();
		e.player = player;
		for (Iterator i = listeners.iterator(); i.hasNext();) {
			EngineListener listener = (EngineListener)i.next();
			listener.playerLivenessChanged(e);
		}
	}
	
	private void reversePlayerDirection(Player player) {
		if (player.getEta() > 0) {
			setPlayerPosition(player, getNextPosition(player));
			
			if (player.getOffset() > 0.5) {
				eatCell(player);
			}
		} 
		setPlayerDirection(player, player.getDirection().getOpposite());
		long speed = player.getSpeed();
		long now = System.currentTimeMillis();
		long eta = player.getEta();
		long start = eta - speed;
		long diff = now - start;
		eta = now + diff;
		player.setEta(eta);
	}
	
	private void checkForCollisions() {
		for (int i = 0; i < players.length; i++) {
			Player c1 = players[i];
			for (int j = i+1; j < players.length; j++) {
				Player c2 = players[j];
				if (playersCollide(c1, c2)) {
					boolean publish = false;
					
					if ((c1.getType() == PlayerType.PACMAN) && (c2.getType() == PlayerType.MONSTER)) {
						handleCollision(c1, c2);
						publish = true;
					}
					else if ((c2.getType() == PlayerType.PACMAN) && (c1.getType() == PlayerType.MONSTER)) {
						handleCollision(c2, c1);
						publish = true;
					}
					
					if (publish) {
						CollisionEvent e = new CollisionEvent();
						e.player1 = c1;
						e.player2 = c2;
						for (Iterator k = listeners.iterator(); k.hasNext();) {
							EngineListener listener = (EngineListener)k.next();
							listener.collisionOccurred(e);
						}
					}
				}
			}
		}
	}
	
	private boolean playersCollide(Player c1, Player c2) {
		Rectangle r1 = getPlayerBounds(c1);
		Rectangle r2 = getPlayerBounds(c2);
		return r1.intersects(r2);
	}
	
	private void handleCollision(Player pacman, Player monster) {
		if (monster.isVulnerable()) {
			setPlayerDead(monster, true);
			setPlayerVulnerableEta(monster, 0);
			
//			pause(1000);
		}
		else if (!monster.isDead()) {
			setPlayerDead(pacman, true);
			stopPlayers();
			setState(STATE_DEAD, STATE_DONE, 3000);
		}
	}
	
	private Direction getPlayerDirection(Player player, PlayerController controller) {
		Direction direction = player.getDirection();
		
		if (controller != null) {
			direction = controller.getDirection();
		}
		
		if (true) { //(player.getType() == PlayerType.MONSTER) {
			if (player.isDead()) {
				direction = pathFinder.getDirection(player.getPosition(), spawnPosition);
			}
		}
		
		if (!isLegalDirection(player, direction)) {
			direction = player.getDirection();
		}
		
		return direction;
	}
	
	private void setPlayerController(Player player, PlayerController controller) {
		controllers.put(player, controller);
	}
	
	private PlayerController getPlayerController(Player player) {
		return (PlayerController)controllers.get(player);
	}
	
	private void stopPlayers() {
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			player.setEta(0);
		}
	}
	
	private void resetPlayerPositions() {
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			setPlayerPosition(player, player.getStartPosition());
		}
	}
	
	private void makeMonstersVulnerable() {
		long eta = System.currentTimeMillis() + pillDuration;
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			if (player.getType() != PlayerType.MONSTER) continue;
			if (!player.isDead()) {
				setPlayerVulnerableEta(player, eta);
				
				PlayerController controller = getPlayerController(player);
				if (controller != null) {
					controller.setDirection(player.getDirection().getOpposite());
				}
				reversePlayerDirection(player);
			}
		}
	}
	
	private Rectangle getPlayerBounds(Player player) {
		int cellSize = 100;
		
		int i = player.getPosition().getX();
		int j = player.getPosition().getY();
		
		int offset = (int)(player.getOffset() * cellSize);
		int xoff = offset * player.getDirection().getDx();
		int yoff = offset * player.getDirection().getDy();
		
		int x = cellSize * i + xoff;
		int y = cellSize * j + yoff;
		
		return new Rectangle(x, y, cellSize, cellSize);
	}
	
	private boolean isLegalDirection(Player player, Direction direction) {
		Position p = new Position(player.getPosition(), direction);
		Cell cell = grid.getCell(p);
		if (!isPassable(player, cell)) return false;
		return true;
	}
	
	private boolean isPassable(Player player, Cell cell) {
		if (player.getType() == PlayerType.PACMAN) {
			if (cell == Cell.GATE) return false;
		}
		if (player.getType() == PlayerType.MONSTER) {
			if (cell == Cell.GATE) {
				long now = System.currentTimeMillis();
				long eta = player.getPenaltyEta();
				if (eta > now) {
					return false;
				}
			}
		}
		return !cell.isWall();
	}
	
	private Cell getNextCell(Player player) {
		return grid.getCell(getNextPosition(player));
	}
	
	private Position getNextPosition(Player player) {
		Position p = new Position(player.getPosition(), player.getDirection());
		int x = p.getX();
		int y = p.getY();
		int w = grid.getWidth();
		int h = grid.getHeight();
		if (x < 0) x = w-1;
		if (y < 0) y = h-1;
		if (x >= w) x = 0;
		if (y >= h) y = 0;
		p = new Position(x, y);
		return p;
	}
	
	
	
	public int getState() {
		return state;
	}
	
	public LevelConfig getLevelConfig() {
		return config;
	}
	
	public Grid getGrid() {
		return grid;
	}
	
	public Player[] getPlayers() {
		return players;
	}
	
	
	
	public void addEngineListener(EngineListener listener) {
		listeners.add(listener);
	}
	
	public void removeEngineListener(EngineListener listener) {
		listeners.remove(listener);
	}
	
	
	
	private static class Rectangle {
		int x;
		int y;
		int w;
		int h;
		
		public Rectangle(int x, int y, int w, int h) {
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
		}
		
		public boolean intersects(Rectangle r) {
			int cx1 = x + w / 2;
			int cy1 = y + h / 2;
			int cx2 = r.x + r.w / 2;
			int cy2 = r.y + r.h / 2;
			
			int dx = cx1 - cx2;
			int dy = cy1 - cy2;
			
			int dist = (int)Math.sqrt(dx*dx + dy*dy);
			
			return (dist < 75);
			
//			int left1 = x;
//			int right1 = x+w;
//			int top1 = y;
//			int bottom1 = y+h;
//			
//			int left2 = r.x;
//			int right2 = r.x+r.w;
//			int top2 = r.y;
//			int bottom2 = r.y+r.h;
//			
//			int left3 = Math.max(left1, left2);
//			int right3 = Math.min(right1, right2);
//			int top3 = Math.max(top1, top2);
//			int bottom3 = Math.min(bottom1, bottom2);
//			
//			int w3 = right3 - left3;
//			int h3 = bottom3 - top3;
//			
//			return (w3 >= 30) && (h3 >= 30);
		}
	}

}
