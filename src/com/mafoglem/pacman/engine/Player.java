package com.mafoglem.pacman.engine;

/**
 * @author Michael Fogleman
 */
public class Player {
	
	private int id;
	private PlayerType type;
	private Position startPosition;
	private Position position;
	private Direction direction;
	private ColorSpec color;
	private long eta;
	private long speed;
	private double offset;
	private long vulnerableEta;
	private long penaltyEta;
	private boolean dead;
	
	
	public Player(int id, PlayerType type, Position startPosition) {
		setId(id);
		setType(type);
		setStartPosition(startPosition);
		setPosition(startPosition);
		setDirection(Direction.LEFT);
		setColor(new ColorSpec(255, 255, 255));
		setEta(0);
		setSpeed(250);
		setOffset(0.0);
		setVulnerableEta(0);
		setPenaltyEta(0);
		setDead(false);
	}
	
	public void shiftEtas(long amount) {
		if (eta != 0) eta += amount;
		if (vulnerableEta != 0) vulnerableEta += amount;
		if (penaltyEta != 0) penaltyEta += amount;
	}

	private void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	private void setType(PlayerType type) {
		this.type = type;
	}

	public PlayerType getType() {
		return type;
	}
	
	public void setPosition(int x, int y) {
		setPosition(new Position(x, y));
	}
	
	public void setPosition(Position p) {
		this.position = p;
	}
	
	public Position getPosition() {
		return position;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setEta(long eta) {
		this.eta = eta;
	}

	public long getEta() {
		return eta;
	}

	public void setSpeed(long speed) {
		this.speed = speed;
	}

	public long getSpeed() {
		return speed;
	}

	public void setStartPosition(Position startPosition) {
		this.startPosition = startPosition;
	}

	public Position getStartPosition() {
		return startPosition;
	}

	public void setColor(ColorSpec color) {
		this.color = color;
	}

	public ColorSpec getColor() {
		return color;
	}

	public void setOffset(double offset) {
		this.offset = offset;
	}

	public double getOffset() {
		return offset;
	}
	
	public boolean isVulnerable() {
		long now = System.currentTimeMillis();
		return (vulnerableEta > now);
	}

	public void setVulnerableEta(long vulnerableEta) {
		this.vulnerableEta = vulnerableEta;
	}

	public long getVulnerableEta() {
		return vulnerableEta;
	}

	public void setPenaltyEta(long penaltyEta) {
		this.penaltyEta = penaltyEta;
	}

	public long getPenaltyEta() {
		return penaltyEta;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public boolean isDead() {
		return dead;
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("P");
		b.append(id);
		return b.toString();
	}

}
