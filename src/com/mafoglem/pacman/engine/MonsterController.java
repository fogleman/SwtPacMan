package com.mafoglem.pacman.engine;

import java.util.Random;


/**
 * @author Michael Fogleman
 */
public class MonsterController implements PlayerController {
	
	private static Random random = new Random();
	
	private Direction direction = Direction.LEFT;
	private GridPathFinder pathFinder;
	private Player source;
	private Player target;
	private double intelligence;
	
	public MonsterController(GridPathFinder pathFinder, Player source, Player target) {
		this.pathFinder = pathFinder;
		this.source = source;
		this.target = target;
		
		intelligence = random.nextDouble() / 2.0 + 0.5;
		intelligence = 0.4;
	}
	
	public boolean isHumanControlled() {
		return false;
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	public Direction getDirection() {
		Direction[] directions = pathFinder.getDirections(source.getPosition(), target.getPosition());
		int choices = directions.length;
		
		if (choices > 1) {
			for (int i = 0; i < choices; i++) {
				if (direction.getOpposite() == directions[i]) {
					for (int j = i; j < directions.length-1; j++) {
						directions[j] = directions[j+1];
					}
					choices--;
					break;
				}
			}
		}
		
		if (choices < 1) return Direction.LEFT;
		
		double roll = random.nextDouble();
		if (roll <= intelligence) {
			direction = directions[0];
		}
		else {
			int n = random.nextInt(choices);
			direction = directions[n];
		}
		
		return direction;
	}

}
