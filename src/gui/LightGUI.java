package gui;

import static core.Frame.SCREEN_SIZE;
import static model.Board.TILE_SIZE;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.GL_FRAMEBUFFER_EXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glBindFramebufferEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glFramebufferTexture2DEXT;
import static org.lwjgl.opengl.EXTFramebufferObject.glGenFramebuffersEXT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.opengl.GL11.glTexImage2D;
import static org.lwjgl.opengl.GL11.glTexParameterf;
import model.Board;
import model.building.BuildingType;
import model.map.GameMap;
import model.map.Light;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import phys.Point2D;

public class LightGUI {

	private static boolean set = false;
	/** IDs for the FBO (single light) */
	private static int shadowBufferID;
	private static int shadowColorTextureID;
	/** IDs for the FBO (combined lights) */
	public static int combinedBufferID;
	public static int combinedColorTextureID;
	
	public LightGUI() {
		if (!GLContext.getCapabilities().GL_EXT_framebuffer_object) {
			System.out.println("FBO not supported!!!");
			System.exit(0);
		} else {
			shadowBufferID = glGenFramebuffersEXT();
			shadowColorTextureID = glGenTextures();
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, shadowBufferID);
			glBindTexture(GL_TEXTURE_2D, shadowColorTextureID);
			glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,SCREEN_SIZE[0],SCREEN_SIZE[1],0,GL_RGBA,GL_INT,(java.nio.ByteBuffer)null);
			glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, shadowColorTextureID, 0);
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
			
			combinedBufferID = glGenFramebuffersEXT();
			combinedColorTextureID = glGenTextures();
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, combinedBufferID);
			glBindTexture(GL_TEXTURE_2D, combinedColorTextureID);
			glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
			glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,SCREEN_SIZE[0],SCREEN_SIZE[1],0,GL_RGBA,GL_INT,(java.nio.ByteBuffer)null);
			glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, combinedColorTextureID, 0);
			glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		}
		set = true;
	}
	

	public static void setDisplay() {
		if(!set)
			return;
		
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, shadowBufferID);
		glBindTexture(GL_TEXTURE_2D, shadowColorTextureID);
		glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,SCREEN_SIZE[0],SCREEN_SIZE[1],0,GL_RGBA,GL_INT,(java.nio.ByteBuffer)null);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, shadowColorTextureID, 0);
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, combinedBufferID);
		glBindTexture(GL_TEXTURE_2D, combinedColorTextureID);
		glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,GL_NEAREST);
		glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA,SCREEN_SIZE[0],SCREEN_SIZE[1],0,GL_RGBA,GL_INT,(java.nio.ByteBuffer)null);
		glFramebufferTexture2DEXT(GL_FRAMEBUFFER_EXT,GL_COLOR_ATTACHMENT0_EXT,GL_TEXTURE_2D, combinedColorTextureID, 0);
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
	}
	
	public void drawLights(Board board, Point2D center, float darkness) {
		
		// switch to combined buffer
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, combinedBufferID);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
				
		// draw background
		{
			// black background
			GL11.glPushMatrix();
			GL11.glColor4f(0f,0f,0f,1f);	
			GL11.glTranslatef(0f,0f,0f);
		    GL11.glBegin(GL11.GL_QUADS);
			{
			      GL11.glVertex2f(0, 0);
			      GL11.glVertex2f(0, SCREEN_SIZE[1]);
			      GL11.glVertex2f(SCREEN_SIZE[0],SCREEN_SIZE[1]);
			      GL11.glVertex2f(SCREEN_SIZE[0],0);
			}
			GL11.glEnd();
			GL11.glPopMatrix();
		}
		
		// draw lights
//		for(Light l: board.getLights()) {
//			drawLight(board, l);
//		}
		
		// switch back to screen buffer
		glEnable(GL_TEXTURE_2D);
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable(GL11.GL_ALPHA_TEST);
		
		// draw shadow buffer to screen
		{
			GL11.glPushMatrix();
			GL11.glColor4f(1f,1f,1f,darkness);
			glBindTexture(GL_TEXTURE_2D, combinedColorTextureID);
		    GL11.glBegin(GL11.GL_QUADS);
			{
			    GL11.glTexCoord2f(0,1f);
			    GL11.glVertex2f(0,0);
			    GL11.glTexCoord2f(0,0);
			    GL11.glVertex2f(0,SCREEN_SIZE[1]);
			    GL11.glTexCoord2f(1f,0);
			    GL11.glVertex2f(SCREEN_SIZE[0],SCREEN_SIZE[1]);
			    GL11.glTexCoord2f(1f,1f);
			    GL11.glVertex2f(SCREEN_SIZE[0],0);
			}
			GL11.glEnd();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
			GL11.glPopMatrix();
		}
	}
	
	@SuppressWarnings("unused")
	private void drawLight(Board board, Light light) {
		
		// switch to shadow buffer
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, shadowBufferID);
		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		// setup
		{
			// black background
			GL11.glPushMatrix();
			GL11.glColor4f(0f,0f,0f,1f);
			GL11.glTranslatef(0f,0f,0f);
		    GL11.glBegin(GL11.GL_QUADS);
			{
			      GL11.glVertex2f(0, 0);
			      GL11.glVertex2f(0, SCREEN_SIZE[1]);
			      GL11.glVertex2f(SCREEN_SIZE[0],SCREEN_SIZE[1]);
			      GL11.glVertex2f(SCREEN_SIZE[0],0);
			}
			GL11.glEnd();
			GL11.glPopMatrix();
			
			// light circle
			GL11.glPushMatrix();
			GL11.glColor4f(0f,0f,0f,0f);
			GL11.glTranslatef(
					GameGUI.scale*(light.getPos().x - GameGUI.viewAnchor.x),
					GameGUI.scale*(light.getPos().y - GameGUI.viewAnchor.y), 0);
			GL11.glBegin(GL11.GL_TRIANGLE_FAN);
			{
				GL11.glVertex2f(0,0);
				for(int i=0;i<=360;i+=4) {
					float a = GameGUI.scale*((float)(light.getDistance()*Math.cos(Math.toRadians(i))));
					float b = GameGUI.scale*((float)(light.getDistance()*Math.sin(Math.toRadians(i))));
					GL11.glVertex2f(a,b);
				}
			}
			GL11.glEnd();
			GL11.glPopMatrix();
		}
		
		drawCellShadows(board.getMap(),light);
				
		// switch to combined buffer
		glEnable(GL_TEXTURE_2D);
		glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, combinedBufferID);
//		GL11.glBlendFunc(GL11.GL_ONE, GL11.GL_ZERO);
//		GL11.glDisable(GL11.GL_ALPHA_TEST);
		
		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
		GL11.glLogicOp(GL11.GL_AND);
		
		// draw shadow buffer to combined buffer
		{
			GL11.glPushMatrix();
			GL11.glColor4f(1f,1f,1f,1f);
			glBindTexture(GL_TEXTURE_2D, shadowColorTextureID);
		    GL11.glBegin(GL11.GL_QUADS);
			{
			    GL11.glTexCoord2f(0,1f);
			    GL11.glVertex2f(0,0);
			    GL11.glTexCoord2f(0,0);
			    GL11.glVertex2f(0,SCREEN_SIZE[1]);
			    GL11.glTexCoord2f(1f,0);
			    GL11.glVertex2f(SCREEN_SIZE[0],SCREEN_SIZE[1]);
			    GL11.glTexCoord2f(1f,1f);
			    GL11.glVertex2f(SCREEN_SIZE[0],0);
			}
			GL11.glEnd();
			GL11.glPopMatrix();
		}
		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
	}

	static final int[][] corner_list = {{0,1},{0,0},{1,0},{1,1}};
	private void drawCellShadows(GameMap map, Light light) {
		// for each cell
		int[] cell = new int[2];
		for(float i=-light.getDistance()-TILE_SIZE;i<light.getDistance()+TILE_SIZE;i+=TILE_SIZE) {
		for(float j=-light.getDistance()-TILE_SIZE;j<light.getDistance()+TILE_SIZE;j+=TILE_SIZE) {
			cell[0] = (int) ((light.getPos().x+i)/TILE_SIZE);
			cell[1] = (int) ((light.getPos().y+j)/TILE_SIZE);
			BuildingType tile = map.getTile(cell[0],cell[1]);
			if(tile.solid) {
				for(int v=0;v<4;v++)
					drawEdgeShadow(
							new float[] {
								corner_list[v][0]*TILE_SIZE + cell[0]*TILE_SIZE,
								corner_list[v][1]*TILE_SIZE + cell[1]*TILE_SIZE},
							new float[] {
								corner_list[(v+1)%4][0]*TILE_SIZE + cell[0]*TILE_SIZE,
								corner_list[(v+1)%4][1]*TILE_SIZE + cell[1]*TILE_SIZE},
							light.getPos(),light.getDistance());
			}
		}};
	}

//	private void drawObjectShadows(Board board, Light light) {
//		// for each object
//		for(GameObject g: board.getPlayers()) {
//			for(int v=0;v<g.getShape().getPoints().length;v++)
//				drawEdgeShadow(
//						new float[] {
//							g.getShape().getPoints()[v].x+g.getPos().x,
//							g.getShape().getPoints()[v].y+g.getPos().y},
//						new float[] {
//							g.getShape().getPoints()[(v+1)%g.getShape().getPoints().length].x+g.getPos().x,
//							g.getShape().getPoints()[(v+1)%g.getShape().getPoints().length].y+g.getPos().y},
//						light.getPos(),light.getDistance()*2);
//		}
//	}
	
	float dx,dy,distance;
	float[] c = new float[2];
	float[] d = new float[2];
	private void drawEdgeShadow(float[] a, float[] b, Point2D pos, float length) {
		
		dx = (a[0]-pos.x);
		dy = (a[1]-pos.y);
		distance = (float) Math.sqrt(dx*dx + dy*dy);
		
		c = new float[] {
				GameGUI.scale*(a[0]+length*dx/distance - GameGUI.viewAnchor.x),
				GameGUI.scale*(a[1]+length*dy/distance - GameGUI.viewAnchor.y)
		};
		
		dx = (b[0]-pos.x);
		dy = (b[1]-pos.y);
		distance = (float) Math.sqrt(dx*dx + dy*dy);
		
		d = new float[] {
				GameGUI.scale*(b[0]+length*dx/distance - GameGUI.viewAnchor.x),
				GameGUI.scale*(b[1]+length*dy/distance - GameGUI.viewAnchor.y)
		};
		
		fillQuad(
				GameGUI.scale*(a[0] - GameGUI.viewAnchor.x), GameGUI.scale*(a[1] - GameGUI.viewAnchor.y),
				GameGUI.scale*(b[0] - GameGUI.viewAnchor.x), GameGUI.scale*(b[1] - GameGUI.viewAnchor.y),
				d[0], d[1],
				c[0], c[1]);
	}
	
	// draw a black quad (shadow)
	public void fillQuad(
			float x1, float y1, float x2, float y2,
			float x3, float y3, float x4, float y4) {
		GL11.glPushMatrix();
		GL11.glColor3f(0,0,0);
		GL11.glTranslatef(x1, y1, 0);
		
//		GL11.glEnable(GL11.GL_COLOR_LOGIC_OP);
//		GL11.glLogicOp(GL11.GL_OR);
		
	    GL11.glBegin(GL11.GL_QUADS);
		{
		      GL11.glVertex2f(0,0);
		      GL11.glVertex2f(x2-x1,y2-y1);
		      GL11.glVertex2f(x3-x1,y3-y1);
		      GL11.glVertex2f(x4-x1,y4-y1);
		}
		GL11.glEnd();
		
//		GL11.glDisable(GL11.GL_COLOR_LOGIC_OP);
		
		GL11.glPopMatrix();
	}
}
