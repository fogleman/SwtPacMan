package com.mafoglem.pacman.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author Michael Fogleman
 */
public class SwtWindow {
	
	private String title;
	private Display display;
	private Shell shell;
	private Canvas canvas;
	private Thread windowThread;
	private Thread redrawThread;
	private long refreshRate;
	private boolean needTitleUpdate;
	private boolean needRedraw;
	private boolean needResize;
	private boolean needDestroy;
	private boolean created;
	
	private int desiredWidth;
	private int desiredHeight;
	private int width;
	private int height;
	private Image canvasBuffer;
	
	private List keyListeners;
	private SwtView view;
	
	
	public SwtWindow() {
		keyListeners = new ArrayList();
		setTitle("SWT Application");
		setCanvasSize(800, 600);
		setRefreshRate(10);
	}
	
	
	public void setView(SwtView view) {
		this.view = view;
	}
	
	public SwtView getView() {
		return view;
	}
	
	
	public void addKeyListener(KeyListener listener) {
		keyListeners.add(listener);
	}
	
	public void removeKeyListener(KeyListener listener) {
		keyListeners.remove(listener);
	}
	
	
	public void create() {
		if (created) return;
		created = true;
		windowThread = new Thread(createWindowRunnable());
		windowThread.start();
		redrawThread = new Thread(createRedrawRunnable());
		redrawThread.start();
	}
	
	public void destroy() {
		if (display != null && !display.isDisposed()) {
			needDestroy = true;
			display.wake();
		}
	}
	
	public boolean isDisposed() {
		if (shell == null) return false;
		return shell.isDisposed();
	}
	
	private void redraw() {
		if (display != null && !display.isDisposed()) {
			needRedraw = true;
			display.wake();
		}
	}

	private Runnable createWindowRunnable() {
		Runnable runnable = new Runnable() {
			public void run() {
				SwtWindow.this.run();
			}
		};
		return runnable;
	}
	
	private Runnable createRedrawRunnable() {
		Runnable runnable = new Runnable() {
			public void run() {
				while (!SwtWindow.this.isDisposed()) {
					SwtWindow.this.redraw();
					try {
						long sleepTime = SwtWindow.this.getRefreshRate();
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		return runnable;
	}
	
	private void run() {
		display = new Display();
		shell = new Shell(display, SWT.SHELL_TRIM);
		canvas = new Canvas(shell, SWT.NO_BACKGROUND);
		
		DisposeListener disposeListener = new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				SwtWindow.this.widgetDisposed(e);
			}
		};
		shell.addDisposeListener(disposeListener);
		
		ControlListener controlListener = new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				SwtWindow.this.controlResized(e);
			}
		};
		shell.addControlListener(controlListener);
		
		PaintListener paintListener = new PaintListener() {
			public void paintControl(PaintEvent e) {
				SwtWindow.this.paintControl(e);
			}
		};
		canvas.addPaintListener(paintListener);
		
		MouseMoveListener mouseMoveListener = new MouseMoveListener() {
			public void mouseMove(MouseEvent e) {
				SwtWindow.this.mouseMove(e);
			}
		};
		canvas.addMouseMoveListener(mouseMoveListener);
		
		KeyListener keyListener = new KeyListener() {
			public void keyPressed(KeyEvent e) {
				for (Iterator i = keyListeners.iterator(); i.hasNext();) {
					KeyListener listener = (KeyListener)i.next();
					listener.keyPressed(e);
				}
			}
			public void keyReleased(KeyEvent e) {
				for (Iterator i = keyListeners.iterator(); i.hasNext();) {
					KeyListener listener = (KeyListener)i.next();
					listener.keyReleased(e);
				}
			}
		};
		canvas.addKeyListener(keyListener);
		
		canvas.setSize(desiredWidth, desiredHeight);
		shell.setText(title);
		shell.pack();
		shell.open();
		
		while (!shell.isDisposed()) {
			if (needTitleUpdate) {
				shell.setText(title);
				needTitleUpdate = false;
			}
			if (needRedraw) {
				canvas.redraw();
				needRedraw = false;
			}
			if (needResize) {
				canvas.setSize(desiredWidth, desiredHeight);
				shell.pack();
				needResize = false;
			}
			if (needDestroy) {
				shell.close();
				needDestroy = false;
			}
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
		display.dispose();
	}
	
	private void mouseMove(MouseEvent e) {
	}
	
	private void widgetDisposed(DisposeEvent e) {
		canvas.dispose();
		canvasBuffer.dispose();
	}
	
	private void controlResized(ControlEvent e) {
		Rectangle bounds = shell.getClientArea();
		resize(bounds.width, bounds.height);
	}
	
	private void resize(int width, int height) {
		if (width < 10) width = 10;
		if (height < 10) height = 10;
		
		this.width = width;
		this.height = height;

		canvas.setBounds(0, 0, width, height);
		if (canvasBuffer != null) canvasBuffer.dispose();
		canvasBuffer = new Image(null, width, height);
		
		if (view != null) {
			view.resize(width, height);
		}
	}
	
	private void paintControl(PaintEvent e) {
		if (canvasBuffer == null) return;
		GC gc = new GC(canvasBuffer);
		if (view != null) {
			view.redraw();
			Image buffer = view.getBuffer();
			if (buffer != null) gc.drawImage(buffer, 0, 0);
		}
		gc.dispose();
		e.gc.drawImage(canvasBuffer, 0, 0);
	}
	
	public void setCanvasSize(int width, int height) {
		desiredWidth = width;
		desiredHeight = height;
		if (display != null && !display.isDisposed()) {
			needResize = true;
			display.wake();
		}
	}
	
	public void setTitle(String title) {
		this.title = title;
		if (display != null && !display.isDisposed()) {
			needTitleUpdate = true;
			display.wake();
		}
	}

	public String getTitle() {
		return title;
	}
	
	public void setRefreshRate(long refreshRate) {
		this.refreshRate = refreshRate;
	}

	public long getRefreshRate() {
		return refreshRate;
	}

}
