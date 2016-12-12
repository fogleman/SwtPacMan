package com.mafoglem.pacman.view;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import com.mafoglem.pacman.engine.ColorSpec;

/**
 * Abstract View for SWT
 * See View interface for more information
 * 
 * TODO getTimeTick methods need to be moved to a Util class 
 * 
 * @author Michael Fogleman
 */
public abstract class SwtView implements View {
	
	private Image buffer;

	public void init(int width, int height) {
		if (width < 1) width = 1;
		if (height < 1) height = 1;
		buffer = new Image(null, width, height);
	}
	
	public void resize(int width, int height) {
		if (buffer != null) destroy();
		init(width, height);
	}

	public void destroy() {
		buffer.dispose();
		buffer = null;
	}

	public void redraw() {
	}
	
	public Image getBuffer() {
		return buffer;
	}
	
	public int getWidth() {
		if (buffer == null) return 0;
		return buffer.getBounds().width;
	}
	
	public int getHeight() {
		if (buffer == null) return 0;
		return buffer.getBounds().height;
	}
	
	protected static Color createColor(ColorSpec spec) {
		Color color = new Color(null, spec.getRed(), spec.getGreen(), spec.getBlue());
		return color;
	}
	
	protected static int getTimeTick(long rate, int range) {
		return getTimeTick(rate, range, 0);
	}
	
	protected static int getTimeTick(long rate, int range, int offset) {
		return (int)((System.currentTimeMillis() / rate) % range) + offset;
	}

}
