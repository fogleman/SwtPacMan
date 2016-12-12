package com.mafoglem.pacman.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

import com.mafoglem.pacman.engine.Direction;
import com.mafoglem.pacman.engine.PlayerController;

/**
 * @author Michael Fogleman
 */
public class SwtPlayerController implements PlayerController, KeyListener {
	
	private Direction direction;
	
	public SwtPlayerController() {
		direction = Direction.LEFT;
	}
	
	public boolean isHumanControlled() {
		return true;
	}
	
	public void setDirection(Direction direction) {
		this.direction = direction;
	}

	public Direction getDirection() {
		return direction;
	}

	public void keyPressed(KeyEvent e) {
		int code = e.keyCode;
		if (code == SWT.ARROW_LEFT) direction = Direction.LEFT;
		else if (code == SWT.ARROW_RIGHT) direction = Direction.RIGHT;
		else if (code == SWT.ARROW_UP) direction = Direction.UP;
		else if (code == SWT.ARROW_DOWN) direction = Direction.DOWN;
	}

	public void keyReleased(KeyEvent e) {
		
	}

}
