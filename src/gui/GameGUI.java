package gui;

import static core.Frame.SCREEN_SIZE;
import static gui.OpenGLDraw.fillRect;
import static model.Board.TILE_SIZE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.awt.Color;

import model.Board;
import model.Player;
import model.gameobject.Creature;
import model.map.FluidSimulator;
import model.map.TileType;

import org.lwjgl.opengl.GL11;

import phys.Point2D;
import core.controller.MouseController;
import core.controller.PlayerController;

/**
 * Draws the game to the screen.
 * Feel free to replace with a prettier version.
 * @author Michael Cashmore
 */
public class GameGUI {
	
	/* size of the view window in terms of game size units */
	public static float BORDER_SIZE = Board.TILE_SIZE*10;
	public static float[] VIEW_SIZE = {
		Board.TILE_SIZE*20,
		Board.TILE_SIZE*20
	};
	
	/* colour pallete */
	static final Color back = new Color(80,80,80);
		
	/* ratio of SCREEN_SIZE/VIEW_SIZE */
	public static float scale;
	
	/* offset to center the screen when full-screen */
	public static Point2D viewAnchor = new Point2D();
	public static Point2D desiredViewAnchor = new Point2D();
	public static float hudSize = 0;
	
	/* depth layers */
	int num_layers = 5;
	int layer_ground = 1;
	int layer_ground_hud = 2;
	int layer_goo = 3;
	int layer_object = 4;
	int layer_effect = 5;
	
	/* visual stuff */
	float timer=0;
	LightGUI lightGUI = new LightGUI();
	
	/**
	 * Draw the game.
	 * @param board	the model to be drawn
	 * @param drawHUD	true if the size-bars are to be drawn
	 */
	public void draw(PlayerController controller, Board board, Player p, float dt) {
		
		scale = (SCREEN_SIZE[0]/VIEW_SIZE[0]);
		VIEW_SIZE[1] = SCREEN_SIZE[1]/scale;
		hudSize = SCREEN_SIZE[1]/12;
		
		// draw background
		fillRect(Color.BLACK, 0, 0, SCREEN_SIZE[0], SCREEN_SIZE[1]);
		fillRect(Pallete.dull,
				scale*(-viewAnchor.x), scale*(-viewAnchor.y),
				scale*Board.BOARD_SIZE[0], scale*Board.BOARD_SIZE[1]);
		
		// update dynamic elements
		updateElements(dt);
		
		// enable depth testing for 3D
		glEnable(GL_DEPTH_TEST);
		glClear(GL_DEPTH_BUFFER_BIT);
		
		drawGoo(board);
		drawObjects(board);
		drawMap(board);
//		lightGUI.drawLights(board, viewAnchor, 0.75f);
		
		// switch back to painters algorithm for HUD
		glDisable(GL_DEPTH_TEST);
				
		// words
//		WordManager.tick();
//		for(Word w: WordManager.getWords()) {
//			FontManager.getFont(w.fontType).drawString(w.colour,
//					(int)(scale*(w.pos.x - viewAnchor.x)) - FontManager.getFont(w.fontType).getWidth(w.word)/2,
//					(int)(scale*(w.pos.y - viewAnchor.y)),
//					w.word, 1, -1);
//		}
//		
		drawHUD(controller,board,p);
		
		timer = (timer+dt)%720;
	}

	private void updateElements(float dt) {
		
		// viewAnchor moves to desired location smoothly
		float d = Point2D.distance(viewAnchor, desiredViewAnchor);
		if(d > 1f) {
			viewAnchor.x += 6*dt*(desiredViewAnchor.x- viewAnchor.x)/VIEW_SIZE[0];
			viewAnchor.y += 6*dt*(desiredViewAnchor.y- viewAnchor.y)/VIEW_SIZE[0];
		}
		
	}

	/*----------*/
	/* TILE MAP */
	/*----------*/
	
	/**
	 * Draw the underlying tile map.
	 * @param board
	 * @param p
	 */
	private void drawMap(Board board) {
		
		float depth_far_ground = 1 - layer_ground/(float)num_layers;
		float depth_far_wall = 1 - layer_object/(float)num_layers;
		float depth_distance = 1/(float)num_layers;
		float tileX, tileY;
		
		for(int i=0;i<board.getMap().getSize()[0];i++) {
			
			tileX = scale*(-viewAnchor.x + i*TILE_SIZE);
			if(tileX>SCREEN_SIZE[0] || tileX+scale*TILE_SIZE<0) continue;
			
		for(int j=0;j<board.getMap().getSize()[1];j++) {
			
			tileY = scale*(-viewAnchor.y + (j - 0.5f)*TILE_SIZE);
			if(tileY>SCREEN_SIZE[1] || tileY+scale*TILE_SIZE*2<0) continue;
					
			// default floor
			if(board.getMap().getTile(i,j)!=TileType.EMPTY) {
				OpenGLDraw.bindTexture(TextureLoader.getInstance().getTexture("Plain Block"));
				OpenGLDraw.drawTexture(
						tileX,
						tileY,
						scale*TILE_SIZE, scale*TILE_SIZE*2,
						depth_far_ground - depth_distance*((j+1)*TILE_SIZE/Board.BOARD_SIZE[1]) );
				OpenGLDraw.unbindTexture();
			}
			
			// pick texture
			switch(board.getMap().getTile(i,j)) {
			case EMPTY:
			case GROUND:
				break;
			case WALL:
				OpenGLDraw.bindTexture(TextureLoader.getInstance().getTexture("Plain Block"));
				break;
			case REINFORCEDWALL:
				OpenGLDraw.bindTexture(TextureLoader.getInstance().getTexture("Brown Block"));
				break;
			default :
				OpenGLDraw.bindTexture(TextureLoader.getInstance().getTexture("Stone Block"));
				break;
			}
			
			// draw building
			if(board.getMap().getTile(i,j)!=TileType.EMPTY && board.getMap().getTile(i,j)!=TileType.GROUND) {
				OpenGLDraw.drawTexture(
						tileX,
						scale*(-viewAnchor.y + (j - 1f)*TILE_SIZE),
						scale*TILE_SIZE, scale*TILE_SIZE*2,
						depth_far_wall - depth_distance*((j+1)*TILE_SIZE/Board.BOARD_SIZE[1]) );
				OpenGLDraw.unbindTexture();
			}
		}};
		
	}
	
	/*--------------*/
	/* GAME OBJECTS */
	/*--------------*/
	
	/**
	 * Draw all game objects in painters-algorithm order.
	 * @param board
	 * @param p
	 */
	private void drawObjects(Board board) {
		
		float depth_far = 1 - layer_object/(float)num_layers;
		float depth_distance = 1/(float)num_layers;
		
		
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		OpenGLDraw.bindTexture(TextureLoader.getInstance().getTexture("Character"));
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		for(Creature g: board.getCreatures().values()) {
			
			int i = (int)(g.getPosition().x / TILE_SIZE);
			int j = (int) (g.getPosition().y / TILE_SIZE);
			float top = 0.75f * board.getMap().getGooMass()[i][j]/(float)(2*FluidSimulator.maxMass+FluidSimulator.maxCompress);				
			if(top>0.75f)top=0.75f;
			float creatureHeight = (g.getShape().getMaxX() - g.getShape().getMinX())*1.71f;
			float drawHeight = Math.max(0.0f, creatureHeight - top*TILE_SIZE);
			
//			OpenGLDraw.fillRect(Color.RED,
//					scale*(-viewAnchor.x + g.getPosition().x + g.getShape().getMinX()),
//					scale*(-viewAnchor.y + g.getPosition().y - creatureHeight),
//					scale*( g.getShape().getMaxX() - g.getShape().getMinX()),
//					scale*( drawHeight ),
//					depth_far - depth_distance*((g.getPosition().y+g.getShape().getMaxY())/Board.BOARD_SIZE[1]));
			OpenGLDraw.drawTexture(
					scale*(-viewAnchor.x + g.getPosition().x + g.getShape().getMinX()),
					scale*(-viewAnchor.y + g.getPosition().y - creatureHeight),
					scale*( g.getShape().getMaxX() - g.getShape().getMinX()),
					scale*( drawHeight ),
					0,0,
					1f,drawHeight/creatureHeight,
					depth_far - depth_distance*((g.getPosition().y+g.getShape().getMaxY())/Board.BOARD_SIZE[1]));
		}
			
		OpenGLDraw.unbindTexture();
		for(Creature g: board.getCreatures().values())
			OpenGLDraw.drawPoly(Color.GREEN,
					scale*(-viewAnchor.x + g.getPosition().x),
					scale*(-viewAnchor.y + g.getPosition().y),
					g.getShape(),
					scale,
					depth_far - depth_distance*((g.getPosition().y+g.getShape().getMaxY())/Board.BOARD_SIZE[1]));
	}
	
	/*-----*/
	/* GOO */
	/*-----*/
	
	/**
	 * Draw the goo layer
	 * @param board
	 * @param p
	 */
	private void drawGoo(Board board) {
		
		float depth_far = 1 - layer_object/(float)num_layers;
		float depth_distance = 1/(float)num_layers;
		float tileX, tileY;
		
		for(int i=0;i<board.getMap().getSize()[0];i++) {
			
			tileX = scale*(-viewAnchor.x + i*TILE_SIZE);
			if(tileX>SCREEN_SIZE[0] || tileX+scale*TILE_SIZE<0) continue;
			
		for(int j=0;j<board.getMap().getSize()[1];j++) {
			
			if(board.getMap().getGooMass()[i][j]>0) {
				
				float top = 0.75f * board.getMap().getGooMass()[i][j]/(float)(2*FluidSimulator.maxMass+FluidSimulator.maxCompress);				
				if(top>0.75f)top=0.75f;
				
				tileY = scale*((j-top)*TILE_SIZE - viewAnchor.y);
				if(tileY>SCREEN_SIZE[1] || tileY+scale*TILE_SIZE*(1+top)<0) continue;
				
				Color gc = getGooColor(board.getMap().getGooMass()[i][j]);
				OpenGLDraw.fillRect(gc,
						tileX,
						tileY,
						scale*TILE_SIZE, scale*(TILE_SIZE),
						depth_far - depth_distance*((j)*TILE_SIZE/Board.BOARD_SIZE[1]) );
				Color gcs = getGooSideColor(board.getMap().getGooMass()[i][j]);
				OpenGLDraw.fillRect(gcs,
						tileX,
						tileY + scale*TILE_SIZE,
						scale*TILE_SIZE, scale*top*TILE_SIZE,
						depth_far - depth_distance*((j)*TILE_SIZE/Board.BOARD_SIZE[1]) );
			}
		}};
		OpenGLDraw.unbindTexture();
	}
	
	// get colour based on water mass
	public final static Color getGooColor(int m) {
	    int o = (50-50*m/(FluidSimulator.maxMass+FluidSimulator.maxCompress));
	    int b = (205-155*m/(FluidSimulator.maxMass+FluidSimulator.maxCompress));
	    if(b<50) b=50;
	    if(o<0) o=0;
	    return new Color(
	    		(int)(o),
	    		(int)(b),
	    		(int)(o));
	}
	
	// get colour based on water mass
	public final static Color getGooSideColor(int m) {
	    int o = (75-50*m/(FluidSimulator.maxMass+FluidSimulator.maxCompress));
	    int b = (230-155*m/(FluidSimulator.maxMass+FluidSimulator.maxCompress));
	    if(b<75) b=75;
	    if(o<25) o=25;
	    return new Color(
	    		(int)(o),
	    		(int)(b),
	    		(int)(o));
	}
	
	
	/*-----*/
	/* HUD */
	/*-----*/
	
	/**
	 * Draw the HUD
	 * @param controller
	 * @param board
	 * @param p
	 */
	private void drawHUD(PlayerController controller, Board board, Player p) {
		
		float tileX, tileY;
		if(((MouseController)controller).isBuilding()) {
			for(int i=0;i<board.getMap().getSize()[0];i++) {
				tileX = scale*(-viewAnchor.x + i*TILE_SIZE);
				if(tileX>SCREEN_SIZE[0] || tileX+scale*TILE_SIZE<0) continue;
				OpenGLDraw.fillRect(Pallete.dim_red, tileX, 0, scale*TILE_SIZE/20f, SCREEN_SIZE[1]);
			}
			for(int j=0;j<board.getMap().getSize()[1];j++) {
				tileY = scale*(-viewAnchor.y + j*TILE_SIZE);
				if(tileY>SCREEN_SIZE[1] || tileY+scale*TILE_SIZE*2<0) continue;
				OpenGLDraw.fillRect(Pallete.dim_red, 0, tileY, SCREEN_SIZE[0], scale*TILE_SIZE/20f);
			}
		}
	}
}
