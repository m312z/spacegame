package core.menu;

import static core.Frame.SCREEN_SIZE;
import static gui.GameGUI.VIEW_SIZE;
import static gui.GameGUI.hudSize;
import static gui.GameGUI.scale;
import static gui.OpenGLDraw.fillRect;
import gui.GameGUI;

import java.awt.Color;


/**
 * The helpful background of the menu screen. 
 * @author Michael Cashmore
 *
 */
public class MenuBackground {
		
	public void draw() {
		
		scale = (SCREEN_SIZE[0]/GameGUI.VIEW_SIZE[0]);
		hudSize = (VIEW_SIZE[0]*scale)/10;
		
		// draw background
		fillRect(Color.BLACK, 0, 0, SCREEN_SIZE[0], SCREEN_SIZE[1]);

		/* background image
		Texture tex = TextureLoader.getInstance().getTexture("stars");
		OpenGLDraw.bindTexture(tex);
		OpenGLDraw.drawTexture(0, 0, SCREEN_SIZE[0], SCREEN_SIZE[1]);
		OpenGLDraw.unbindTexture();
		*/
		
		for(int i=0;i<=SCREEN_SIZE[0]/hudSize;i++) {
		for(int j=0;j<=SCREEN_SIZE[1]/hudSize;j++) {
			fillRect(new Color(
						i/(float)(SCREEN_SIZE[0]/hudSize),
						j/(float)(SCREEN_SIZE[1]/hudSize),
						j/(float)(SCREEN_SIZE[1]/hudSize)),
					i*hudSize, j*hudSize,
					hudSize, hudSize);
		}};
	}
}
