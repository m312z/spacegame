package gui;

import static core.Frame.SCREEN_SIZE;
import static gui.GameGUI.VIEW_SIZE;
import static gui.GameGUI.hudSize;
import static gui.GameGUI.scale;
import static gui.OpenGLDraw.fillRect;

import java.awt.Color;

import phys.Point2D;

public class LoadingScreenGUI {

	public void draw(float completion) {
	
		scale = (SCREEN_SIZE[0]/GameGUI.VIEW_SIZE[0]);
		GameGUI.VIEW_SIZE[1] = SCREEN_SIZE[1]/scale;
		GameGUI.desiredViewAnchor = new Point2D();
		GameGUI.viewAnchor = new Point2D();
		hudSize = (VIEW_SIZE[0]*scale)/10;
		
		// draw background
		fillRect(Color.BLACK, 0, 0, SCREEN_SIZE[0], SCREEN_SIZE[1]);
	}
}
