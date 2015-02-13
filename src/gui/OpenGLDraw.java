package gui;

import java.awt.Color;

import org.lwjgl.opengl.GL11;

import phys.Point2D;
import phys.Shape;

/**
 * This class contains all the methods required for drawing onto the screen.
 * Everything here should be static - it is just a set of tools.
 * @author Michael Cashmore
 *
 */
public class OpenGLDraw {
	
	private static Texture tex;
	
	/*------------------*/
	/* BINDING TEXTURES */
	/*------------------*/
	
	public static void bindTexture(Texture texture) {
		if(texture==null || tex == texture)
			return;
		// bind to the appropriate texture
		texture.bind();
		tex = texture;
	}
	
	public static void unbindTexture() {
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		tex = null;
	}
	
	/*---------------------*/
	/* ACTUAL DRAW METHODS */
	/*---------------------*/
	
	/**
	 * Draw a solid coloured rectangle
	 * @param colour
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void fillRect(Color colour, float x, float y, float width, float height) {
		// store the current model matrix
		GL11.glPushMatrix();
			
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	
		// set correct colour
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, 0);		
			
		// draw a quad
	    GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2f(0, 0);
			GL11.glVertex2f(0, height);
			GL11.glVertex2f(width,height);
			GL11.glVertex2f(width,0);
		}
		GL11.glEnd();
			
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw a solid coloured rectangle with depth for 3D
	 * @param colour
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param depth [0,1]
	 */
	public static void fillRect(Color colour, float x, float y, float width, float height, float depth) {
		// store the current model matrix
		GL11.glPushMatrix();
			
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	
		// set correct colour
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, depth);		
			
		// draw a quad
	    GL11.glBegin(GL11.GL_QUADS);
		{
			GL11.glVertex2f(0, 0);
			GL11.glVertex2f(0, height);
			GL11.glVertex2f(width,height);
			GL11.glVertex2f(width,0);
		}
		GL11.glEnd();
			
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}

	/**
	 * Draw a solid coloured polygon
	 * @param colour
	 * @param x
	 * @param y
	 */
	public static void fillPoly(Color colour, float[] x, float[] y) {
		// store the current model matrix
		GL11.glPushMatrix();
			
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	
		// set correct colour
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(0,0,0);		
			
		// draw a quad
	    GL11.glBegin(GL11.GL_QUADS);
		{
			for(int i=0;i<x.length;i++)
				GL11.glVertex2f(x[i],y[i]);
		}
		GL11.glEnd();
			
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw a solid coloured polygon
	 * @param colour
	 * @param x
	 * @param y
	 * @param shape
	 * @param scale
	 */
	public static void fillPoly(Color colour, float x, float y, Shape shape, float scale) {
		// store the current model matrix
		GL11.glPushMatrix();
			
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	
		// set correct colour
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, 0);		
			
		// draw a poly
	    GL11.glBegin(GL11.GL_POLYGON);
		{
			for(Point2D p: shape.getPoints()) {
		      GL11.glVertex2f(p.x*scale, p.y*scale);
			}
		}
		GL11.glEnd();
			
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw an unfilled polygon
	 * @param colour
	 * @param x
	 * @param y
	 * @param shape
	 * @param scale
	 */
	public static void drawPoly(Color colour, float x, float y, Shape shape, float scale) {
		// store the current model matrix
		GL11.glPushMatrix();
			
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	
		// set correct colour
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, 0);		
			
		// draw a poly
	    GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			for(Point2D p: shape.getPoints()) {
		      GL11.glVertex2f(p.x*scale, p.y*scale);
			}
		}
		GL11.glEnd();
			
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
		
	/**
	 * Draw an unfilled polygon
	 * @param colour
	 * @param x
	 * @param y
	 * @param shape
	 * @param scale
	 */
	public static void drawPoly(Color colour, float x, float y, Shape shape, float scale, float depth) {
		// store the current model matrix
		GL11.glPushMatrix();
			
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	
		// set correct colour
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, depth);		
			
		// draw a poly
	    GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			for(Point2D p: shape.getPoints()) {
		      GL11.glVertex2f(p.x*scale, p.y*scale);
			}
		}
		GL11.glEnd();
			
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw an unfilled rectangle of a specified line-colour.
	 * @param colour
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void drawRect(Color colour, float x, float y, float width, float height) {
		// store the current model matrix
		GL11.glPushMatrix();
		
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		// set correct colour
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);
	    
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, 0);		
			
		// draw a quad
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
		      GL11.glVertex2f(0, 0);
		      GL11.glVertex2f(0, height);
		      GL11.glVertex2f(width,height);
		      GL11.glVertex2f(width,0);
		}
		GL11.glEnd();
			
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw an unfilled rectangle of a specified line-colour with depth
	 * @param colour
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public static void drawRect(Color colour, float x, float y, float width, float height, float depth) {
		// store the current model matrix
		GL11.glPushMatrix();
		
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		// set correct colour
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);
	    
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, depth);		
			
		// draw a quad
		GL11.glBegin(GL11.GL_LINE_LOOP);
		{
		      GL11.glVertex2f(0, 0);
		      GL11.glVertex2f(0, height);
		      GL11.glVertex2f(width,height);
		      GL11.glVertex2f(width,0);
		}
		GL11.glEnd();
			
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw an unfilled arc of a specified line-colour with depth.
	 * @param colour	The line-colour
	 * @param x			The xcoord of the center
	 * @param y			The ycoord of the center
	 * @param r			The radius of the arc
	 * @param start		The starting angle of the arc in degrees (clockwise from the rightmost point of the circle.)
	 * @param angle		The size of the arc to draw, in degrees. (360 will draw a complete circle)
	 * @param depth		The depth of the arc
	 */
	public static void drawArc(Color colour, float x, float y, float r, int start, int angle, float depth) {
		// store the current model matrix
		GL11.glPushMatrix();
		
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		// set correct colour
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);
	    		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, depth);				

		// draw as a line loop
		GL11.glBegin(GL11.GL_LINE_LOOP);
		for(int i=0;i<angle;i++) {
			float a = (float)(r*Math.cos(Math.toRadians(start+i)));
			float b = (float)(r*Math.sin(Math.toRadians(start+i)));
			GL11.glVertex2f(a,-b);
		}
		GL11.glEnd();
		
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw an unfilled arc of a specified line-colour.
	 * @param colour	The line-colour
	 * @param x			The xcoord of the center
	 * @param y			The ycoord of the center
	 * @param r			The radius of the arc
	 * @param start		The starting angle of the arc in degrees (clockwise from the rightmost point of the circle.)
	 * @param angle		The size of the arc to draw, in degrees. (360 will draw a complete circle)
	 */
	public static void drawArc(Color colour, float x, float y, float r, int start, int angle) {
		// store the current model matrix
		GL11.glPushMatrix();
		
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		// set correct colour
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);
	    		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, 0);				

		// draw as a line loop
		GL11.glBegin(GL11.GL_LINE_LOOP);
		for(int i=0;i<angle;i++) {
			float a = (float)(r*Math.cos(Math.toRadians(start+i)));
			float b = (float)(r*Math.sin(Math.toRadians(start+i)));
			GL11.glVertex2f(a,-b);
		}
		GL11.glEnd();
		
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw a filled circle segment of a specified colour.
	 * @param colour	The colour
	 * @param x			The xcoord of the center
	 * @param y			The ycoord of the center
	 * @param r			The radius of the circle
	 * @param start		The starting angle of the arc in degrees (clockwise from the rightmost point of the circle.)
	 * @param angle		The size of the arc to draw, in degrees. (360 will draw a complete circle)
	 */
	public static void fillArc(Color colour, float x, float y, float r, int start, int angle) {
		// store the current model matrix
		GL11.glPushMatrix();
		
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		// set correct colour
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);
	    		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, 0);	

		// draw as triangle fan
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex2f(0,0);
		for(int i=0;i<angle;i++) {
			float a = (float)(r*Math.cos(Math.toRadians(start+i)));
			float b = (float)(r*Math.sin(Math.toRadians(start+i)));
			GL11.glVertex2f(a,-b);
		}
		GL11.glEnd();
		
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw a filled circle segment of a specified colour with depth.
	 * @param colour	The colour
	 * @param x			The xcoord of the center
	 * @param y			The ycoord of the center
	 * @param r			The radius of the circle
	 * @param start		The starting angle of the arc in degrees (clockwise from the rightmost point of the circle.)
	 * @param angle		The size of the arc to draw, in degrees. (360 will draw a complete circle)
	 * @param depth		The depth of the arc
	 */
	public static void fillArc(Color colour, float x, float y, float r, int start, int angle, float depth) {
		// store the current model matrix
		GL11.glPushMatrix();
		
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		
		// set correct colour
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);
	    		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, depth);	

		// draw as triangle fan
		GL11.glBegin(GL11.GL_TRIANGLE_FAN);
		GL11.glVertex2f(0,0);
		for(int i=0;i<angle;i++) {
			float a = (float)(r*Math.cos(Math.toRadians(start+i)));
			float b = (float)(r*Math.sin(Math.toRadians(start+i)));
			GL11.glVertex2f(a,-b);
		}
		GL11.glEnd();
		
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw a square texture scaled to fit the destination box
	 * @param x			The xcoord
	 * @param y			The ycoord
	 * @param width		The width of the destination box
	 * @param height	The height of the destination box
	 */
	public static void drawTexture(float x, float y, float width, float height) {
		
		if(tex==null) {
			System.out.println("No texture is bound");
			return;
		}
		
		// store the current model matrix
		GL11.glPushMatrix();
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, 0);
		
		GL11.glColor4f(1,1,1,1);

		// draw a box
	    GL11.glBegin(GL11.GL_QUADS);
		{
		    GL11.glTexCoord2f(0, 0);
		    GL11.glVertex2f(0, 0);
		    GL11.glTexCoord2f(0,tex.getHeight());
		    GL11.glVertex2f(0, height);
		    GL11.glTexCoord2f(tex.getWidth(), tex.getHeight());
		    GL11.glVertex2f(width,height);
		    GL11.glTexCoord2f(tex.getWidth(), 0);
		    GL11.glVertex2f(width,0);
		}
		GL11.glEnd();
		
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw a square texture scaled to fit the destination box with depth
	 * @param x			The xcoord
	 * @param y			The ycoord
	 * @param width		The width of the destination box
	 * @param height	The height of the destination box
	 */
	public static void drawTexture(float x, float y, float width, float height, float depth) {
		
		if(tex==null) {
			System.out.println("No texture is bound");
			return;
		}
		
		// store the current model matrix
		GL11.glPushMatrix();
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, depth);
		
		GL11.glColor4f(1,1,1,1);

		// draw a box
	    GL11.glBegin(GL11.GL_QUADS);
		{
		    GL11.glTexCoord2f(0, 0);
		    GL11.glVertex2f(0, 0);
		    GL11.glTexCoord2f(0,tex.getHeight());
		    GL11.glVertex2f(0, height);
		    GL11.glTexCoord2f(tex.getWidth(), tex.getHeight());
		    GL11.glVertex2f(width,height);
		    GL11.glTexCoord2f(tex.getWidth(), 0);
		    GL11.glVertex2f(width,0);
		}
		GL11.glEnd();
		
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw a square texture scaled to fit the destination box with depth and alpha
	 * @param x			The xcoord
	 * @param y			The ycoord
	 * @param width		The width of the destination box
	 * @param height	The height of the destination box
	 */
	public static void drawTexture(float x, float y, float width, float height, float depth, float alpha) {
		
		if(tex==null) {
			System.out.println("No texture is bound");
			return;
		}
		
		// store the current model matrix
		GL11.glPushMatrix();
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, depth);
		
		GL11.glColor4f(1,1,1,alpha);

		// draw a box
	    GL11.glBegin(GL11.GL_QUADS);
		{
		    GL11.glTexCoord2f(0, 0);
		    GL11.glVertex2f(0, 0);
		    GL11.glTexCoord2f(0,tex.getHeight());
		    GL11.glVertex2f(0, height);
		    GL11.glTexCoord2f(tex.getWidth(), tex.getHeight());
		    GL11.glVertex2f(width,height);
		    GL11.glTexCoord2f(tex.getWidth(), 0);
		    GL11.glVertex2f(width,0);
		}
		GL11.glEnd();
		
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw a square texture scaled to fit the destination box with depth
	 * @param x			The xcoord
	 * @param y			The ycoord
	 * @param width		The width of the destination box
	 * @param height	The height of the destination box
	 */
	public static void drawTexture(float x, float y, float width, float height, float sx, float sy, float sw, float sh, float depth) {
		
		if(tex==null) {
			System.out.println("No texture is bound");
			return;
		}
		
		// store the current model matrix
		GL11.glPushMatrix();
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, depth);
		
		GL11.glColor4f(1,1,1,1);

		// draw a box
	    GL11.glBegin(GL11.GL_QUADS);
		{
		    GL11.glTexCoord2f(sx, sy);
		    GL11.glVertex2f(0, 0);
		    GL11.glTexCoord2f(sx,sy+sh*tex.getHeight());
		    GL11.glVertex2f(0, height);
		    GL11.glTexCoord2f(sx+sw*tex.getWidth(), sy+sh*tex.getHeight());
		    GL11.glVertex2f(width,height);
		    GL11.glTexCoord2f(sx+sw*tex.getWidth(), sy);
		    GL11.glVertex2f(width,0);
		}
		GL11.glEnd();
		
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw a square texture scaled to fit the destination box
	 * @param x			The xcoord
	 * @param y			The ycoord
	 * @param width		The width of the destination box
	 * @param height	The height of the destination box
	 */
	public static void drawTexture(Color colour, float x, float y, float width, float height) {
		
		if(tex==null) {
			System.out.println("No texture is bound");
			return;
		}
		
		// store the current model matrix
		GL11.glPushMatrix();
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, 0);
		
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);

		// draw a box
	    GL11.glBegin(GL11.GL_QUADS);
		{
		    GL11.glTexCoord2f(0, 0);
		    GL11.glVertex2f(0, 0);
		    GL11.glTexCoord2f(0,tex.getHeight());
		    GL11.glVertex2f(0, height);
		    GL11.glTexCoord2f(tex.getWidth(), tex.getHeight());
		    GL11.glVertex2f(width,height);
		    GL11.glTexCoord2f(tex.getWidth(), 0);
		    GL11.glVertex2f(width,0);
		}
		GL11.glEnd();
		
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw a square texture scaled to fit the destination box
	 * @param x			The xcoord
	 * @param y			The ycoord
	 * @param width		The width of the destination box
	 * @param height	The height of the destination box
	 */
	public static void drawTexture(float x, float y, float width, float height, float sx, float sy, float sw, float sh) {
		
		if(tex==null) {
			System.out.println("No texture is bound");
			return;
		}
		
		sx = sx/tex.getImageWidth();
		sw = sw/tex.getImageWidth();
		sh = sh/tex.getImageHeight();
		sy = sy/tex.getImageHeight();
		
		// store the current model matrix
		GL11.glPushMatrix();
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, 0);
		
		GL11.glColor4f(1,1,1,1);

		// draw a box
	    GL11.glBegin(GL11.GL_QUADS);
		{
		    GL11.glTexCoord2f(sx, sy);
		    GL11.glVertex2f(0, 0);
		    GL11.glTexCoord2f(sx,sy+sh);
		    GL11.glVertex2f(0, height);
		    GL11.glTexCoord2f(sx+sw,sy+sh);
		    GL11.glVertex2f(width,height);
		    GL11.glTexCoord2f(sx+sw,sy);
		    GL11.glVertex2f(width,0);
		}
		GL11.glEnd();
		
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	/**
	 * Draw a square texture scaled to fit the destination box, with an alpha
	 * @param x			The xcoord
	 * @param y			The ycoord
	 * @param width		The width of the destination box
	 * @param height	The height of the destination box
	 */
	public static void drawTransparentTexture(float x, float y, float width, float height, float opacity) {
		
		if(tex==null) {
			System.out.println("No texture is bound");
			return;
		}
		
		// store the current model matrix
		GL11.glPushMatrix();
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, 0);
		
		GL11.glColor4f(1,1,1,opacity);

		// draw a box
	    GL11.glBegin(GL11.GL_QUADS);
		{
		    GL11.glTexCoord2f(0, 0);
		    GL11.glVertex2f(0, 0);
		    GL11.glTexCoord2f(0,tex.getHeight());
		    GL11.glVertex2f(0, height);
		    GL11.glTexCoord2f(tex.getWidth(), tex.getHeight());
		    GL11.glVertex2f(width,height);
		    GL11.glTexCoord2f(tex.getWidth(), 0);
		    GL11.glVertex2f(width,0);
		}
		GL11.glEnd();
		
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	
	/**
	 * Draw a square texture scaled to fit the destination box, with some rotation
	 * @param x			The xcoord
	 * @param y			The ycoord
	 * @param width		The width of the destination box
	 * @param height	The height of the destination box
	 * @param rotation  The rotation of the texture, in degrees
	 */
	public static void drawTextureWithRotation(Color colour, float x, float y, float width, float height, float arx, float ary, float rotation) {
		
		if(tex==null) {
			System.out.println("No texture is bound");
			return;
		}
		
		// store the current model matrix
		GL11.glPushMatrix();
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x, y, 0);
		GL11.glRotatef(rotation,0.0f,0.0f,1.0f);
		GL11.glTranslatef(arx,ary,0.0f);
				
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);

		// draw a box
	    GL11.glBegin(GL11.GL_QUADS);
		{
		    GL11.glTexCoord2f(0, 0);
		    GL11.glVertex2f(0, 0);
		    GL11.glTexCoord2f(0,tex.getHeight());
		    GL11.glVertex2f(0, height);
		    GL11.glTexCoord2f(tex.getWidth(), tex.getHeight());
		    GL11.glVertex2f(width,height);
		    GL11.glTexCoord2f(tex.getWidth(), 0);
		    GL11.glVertex2f(width,0);
		}
		GL11.glEnd();

		GL11.glRotatef(-rotation,0.0f,0.0f,1.0f);

		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
	}
	
	public static void drawLine(Color colour, float x1, float y1, float x2, float y2) {
		// store the current model matrix
		GL11.glPushMatrix();
			
		// ensure the texture is unbound
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
	
		// set correct colour
		GL11.glColor4f(colour.getRed()/255f,colour.getGreen()/255f,
				colour.getBlue()/255f,colour.getAlpha()/255f);
		
		// translate to the right location and prepare to draw
		GL11.glTranslatef(x1, y1, 0);		
			
		// draw a quad
	    GL11.glBegin(GL11.GL_LINE_LOOP);
		{
			GL11.glVertex2f(0,0);
			GL11.glVertex2f(x2-x1,y2-y1);
		}
		GL11.glEnd();
			
		// restore the model view matrix to prevent contamination
		GL11.glPopMatrix();
		
	}	
}
