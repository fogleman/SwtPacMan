package com.mafoglem.pacman.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Fogleman
 */
public class GridPathFinder {
	
	private Map distances;
	
	public GridPathFinder(Grid grid) {
		distances = new HashMap();
		run(createMap(grid));
	}
	
	public Direction getDirection(Position source, Position target) {
		Direction[] directions = getDirections(source, target);
		if (directions.length == 0) return null;
		return directions[0];
	}
	
	public Direction[] getDirections(Position source, Position target) {
		Position t = target;
		Position s = source;
		
		int choices = 4;
		
		s = new Position(source, Direction.LEFT);
		int left = getDistance(s, t);
		if (left < 0) {
			left = Integer.MAX_VALUE;
			choices--;
		}
		
		s = new Position(source, Direction.RIGHT);
		int right = getDistance(s, t);
		if (right < 0) {
			right = Integer.MAX_VALUE;
			choices--;
		}
		
		s = new Position(source, Direction.UP);
		int up = getDistance(s, t);
		if (up < 0) {
			up = Integer.MAX_VALUE;
			choices--;
		}
		
		s = new Position(source, Direction.DOWN);
		int down = getDistance(s, t);
		if (down < 0) {
			down = Integer.MAX_VALUE;
			choices--;
		}
		
		int[] distances = new int[4];
		Direction[] directions = new Direction[4];
		
		distances[0] = left;
		distances[1] = right;
		distances[2] = up;
		distances[3] = down;
		
		Arrays.sort(distances);
		for (int i = 0; i < distances.length; i++) {
			int distance = distances[i];
			if (left == distance) {
				directions[i] = Direction.LEFT;
				left = -1;
			}
			else if (right == distance) {
				directions[i] = Direction.RIGHT;
				right = -1;
			}
			else if (up == distance) {
				directions[i] = Direction.UP;
				up = -1;
			}
			else if (down == distance) {
				directions[i] = Direction.DOWN;
				down = -1;
			}
		}
		
		Direction[] result = new Direction[choices];
		for (int i = 0; i < choices; i++) {
			result[i] = directions[i];
		}
		
		return result;
	}
	
	public int getDistance(Position a, Position b) {
		Pair pair = new Pair(a, b);
		Integer distance = (Integer)distances.get(pair);
		if (distance == null) return -1;
		return distance.intValue();
	}
	
	private void setDistance(Position a, Position b, int distance) {
		Pair pair = new Pair(a, b);
		distances.put(pair, new Integer(distance));
	}
	
	private void enqueue(List queue, Position p) {
		queue.add(p);
	}
	
	private Position dequeue(List queue) {
		if (queue.size() == 0) return null;
		return (Position)queue.remove(0);
	}
	
	private void run(Vertex[][] map) {
		List queue = new ArrayList();
		int w = map.length;
		int h = map[0].length;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				Vertex vertex = map[x][y];
				if (!vertex.passable) continue;
				vertex.distance = 0;
				vertex.visited = true;
				Position a = new Position(x, y);
				enqueue(queue, a);
				run(map, a, queue, w, h);
				clear(map);
			}
		}
	}
	
	private void run(Vertex[][] map, Position a, List queue, int w, int h) {
		Position b = null;
		while ((b = dequeue(queue)) != null) {
			int x = b.getX();
			int y = b.getY();
			
			Vertex vertex = map[x][y];
			setDistance(a, b, vertex.distance);
			
			add(map, queue, x+1, y, w, h, vertex.distance+1);
			add(map, queue, x-1, y, w, h, vertex.distance+1);
			add(map, queue, x, y+1, w, h, vertex.distance+1);
			add(map, queue, x, y-1, w, h, vertex.distance+1);
		}
	}
	
	private void add(Vertex[][] map, List queue, int x, int y, int w, int h, int distance) {
		if (x < 0) return;
		if (y < 0) return;
		if (x >= w) return;
		if (y >= h) return;
		Vertex vertex = map[x][y];
		if (vertex.visited) return;
		if (!vertex.passable) return;
		vertex.visited = true;
		vertex.distance = distance;
		enqueue(queue, new Position(x, y));
	}
	
	private void clear(Vertex[][] map) {
		int w = map.length;
		int h = map[0].length;
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				Vertex vertex = map[x][y];
				vertex.visited = false;
			}
		}
	}
	
	private Vertex[][] createMap(Grid grid) {
		int w = grid.getWidth();
		int h = grid.getHeight();
		Vertex[][] map = new Vertex[w][h];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				Vertex vertex = new Vertex();
				Cell cell = grid.getCell(x, y);
				vertex.passable = !cell.isWall();
				vertex.visited = false;
				map[x][y] = vertex;
			}
		}
		return map;
	}
	
	private static class Vertex {
		boolean visited;
		int distance;
		boolean passable;
	}
	
	private static class Pair {
		Position p1;
		Position p2;
		
		public Pair(Position p1, Position p2) {
			this.p1 = p1;
			this.p2 = p2;
		}
		
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append(p1);
			buffer.append(':');
			buffer.append(p2);
			return buffer.toString();
		}
		
		public boolean equals(Object that) {
			if (that == null) return false;
			if (this == that) return true;
			if (!(that instanceof Pair)) return false;
			Pair p = (Pair)that;
			if (!p1.equals(p.p1)) return false;
			if (!p2.equals(p.p2)) return false;
			return true;
		}
	
		public int hashCode() {
			return (p1.hashCode() << 16) | p2.hashCode();
		}
	}

}

