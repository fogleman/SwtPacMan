package com.mafoglem.pacman.view;

/**
 * Defines the API for a generic "view"
 * 
 * For the PacMan game, there will be just a few views:
 *  - Intro view which will display a PacMan logo and info
 *    such as number of players, level setting, etc
 *  - Game view which will actually be a composite of other
 *    views.. grid view, header view, footer view
 *  - The grid view shows the game grid and anything on
 *    the grid (pacman, monsters, etc)
 *  - The header view will show score, high score, etc
 *  - The footer view will show lives, level, etc
 *  - Other views (e.g. cartoons between levels)
 * 
 * This interface doesn't define much API and may very
 * well be modified soon
 * 
 * Each graphics toolkit will define an abstract view,
 * e.g. SwtView, AwtView
 * 
 * Concrete views will require model references in the constructors
 * For example, the GridView requires a reference to the Grid
 * to be viewed
 * 
 * @author Michael Fogleman
 */
public interface View {
	
	public void init(int width, int height);
	public void resize(int width, int height);
	public void destroy();
	public void redraw();

}
