package com.mafoglem.pacman.engine;

import com.mafoglem.pacman.events.EatEvent;
import com.mafoglem.pacman.events.EngineAdapter;
import com.mafoglem.pacman.events.PlayerEvent;



/**
 * @author Michael Fogleman
 */
public class Game {
	
	private LevelConfig[] levels;
	private PlayerController controller;
	private Engine engine;
	private int index;
	
	private int score;
	private int lives;
	private int bonus;
	
	
	public Game(LevelConfig[] levels, PlayerController controller) {
		this.levels = levels;
		this.controller = controller;
		
		engine = new Engine(controller);
		engine.addEngineListener(new EngineAdapter() {
			public void cellEaten(EatEvent e) {
				Game.this.cellEaten(e);
			}
			public void playerLivenessChanged(PlayerEvent e) {
				Game.this.playerLivenessChanged(e);
			}
		});
		
		reset();
	}
	
	private void nextLevel() {
		index = (++index)%levels.length;
		engine.reset(levels[index]);
	}
	
	private void cellEaten(EatEvent e) {
		incrementScore(10);
		if (e.cell == Cell.PILL) {
			bonus = 200;
		}
	}
	
	private void playerLivenessChanged(PlayerEvent e) {
		Player p = e.player;
		if (p.getType() == PlayerType.MONSTER) {
			if (p.isDead()) {
				incrementScore(bonus);
				bonus *= 2;
			}
		}
		else {
			lives--;
		}
	}
	
	private void incrementScore(int amount) {
		int oldScore = score;
		score += amount;
		int newScore = score;
		
		int lifeBonus = 10000;
		int n1 = oldScore / lifeBonus;
		int n2 = newScore / lifeBonus;
		if (n1 != n2) {
			lives++;
		}
	}
	
	public void update() {
		engine.update();
		
		if (engine.getState() == Engine.STATE_DONE) {
			if (lives < 0) {
				reset();
			}
			else {
				if (engine.getGrid().getFoodCount() > 0) {
					engine.reset();
				}
				else {
					nextLevel();
				}
			}
		}
	}
	
	private void reset() {
		lives = 2;
		score = 0;
		index = -1;
//		index = (int)(System.currentTimeMillis() % levels.length) - 1;
		nextLevel();
	}
	
	public Engine getEngine() {
		return engine;
	}
	
	public int getScore() {
		return score;
	}
	
	public int getLives() {
		return lives;
	}
	
	public int getLevel() {
		return index+1;
	}


}
