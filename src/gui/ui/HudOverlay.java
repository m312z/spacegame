package gui.ui;

import static core.Frame.SCREEN_SIZE;
import gui.FontManager;
import gui.FontManager.FontType;
import gui.TrueTypeFont.FontAlign;
import gui.OpenGLDraw;
import gui.Texture;
import gui.TextureLoader;
import gui.TrueTypeFont;
import gui.ui.HudElement.InteractionType;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import core.Frame;

import phys.Point2D;
import phys.Shape;

public class HudOverlay {
		
	List<HudElement> elements;
	HudElement toolTip;
	
	/* interaction */
	HudElement focus = null;
	boolean hotMouseDown = false;
	boolean hotMouseUp = false;
	boolean hotRightMouseDown = false;
	boolean hotRightMouseUp = false;
	
	/* true if the GUI has eaten an event that is !onHud */
	boolean eventEaten = false;
	
	public HudOverlay() {
		elements = new ArrayList<HudElement>();
	}
	
	public void addElement(HudElement e) {
		elements.add(e);
	}
	
	public HudElement getElement(String name) {
		for(HudElement e: elements) {
			if(e.getName().equals(name))
				return e;
			HudElement child = getElement(name,e);
			if(child!=null) return child;
		}
		return null;
	}
	
	public HudElement getElement(String name, HudElement parent) {
		for(HudElement e: parent.getElements()) {
			if(e.getName().equals(name))
				return e;
			HudElement child = getElement(name,e);
			if(child!=null) return child;
		}
		return null;
	}
	
	public HudElement getElement(Point2D pos) {
		// find top level element under position
		for(int i=elements.size()-1; i>=0; i--) {
			HudElement e = elements.get(i);
			if(!e.isVisible())
				continue;
			if(Shape.shapeContainsPoint(pos, e.getShape(), e.getPos())!=Point2D.nullVector)
				return e;
		}
		return null;
	}

	public void setToolTip(HudElement tip) {
		toolTip = tip;
	}
	
	public boolean isOnHuD(float x, float y) {
		Point2D mousePos = new Point2D(x,y);
		for(int i=elements.size()-1; i>=0; i--) {
			HudElement e = elements.get(i);
			if(!e.isVisible()) continue;
			if(Shape.shapeContainsPoint(mousePos, e.getShape(), e.getPos())!=Point2D.nullVector) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isEventEaten() {
		return eventEaten;
	}
	
	/**
	 * @return the list of commands generated from the overlay.
	 */
	public List<String> pollInput() {
		
		eventEaten = false;
		
		List<String> commands = new ArrayList<String>();
		
		pollMouse(commands);
		
		pollKeyboard(commands);
		
		return commands;
	}

	private void pollKeyboard(List<String> commands) {
		// keyboard input on focus element
		if(focus!=null && focus.isFocusable()) {
			while (Keyboard.next()) {
				if (Keyboard.getEventKeyState()) {
					String command = focus.onKeyDown(Keyboard.getEventKey(), Keyboard.getEventCharacter()); 
					if(command!=null) commands.add(command);
				}
				else {
					String command = focus.onKeyUp(Keyboard.getEventKey(), Keyboard.getEventCharacter()); 
					if(command!=null) commands.add(command);
				}
			}
		}
	}

	private void pollMouse(List<String> commands) {
		// set mouse state
		Point2D mousePos = new Point2D(Mouse.getX(),SCREEN_SIZE[1]-Mouse.getY());
		
		// move elements attached to mouse
		for(HudElement e: elements) {
			if(e.isAttachedToMouse()) {
				e.getPos().x = mousePos.x;
				e.getPos().y = mousePos.y;
			}
		}
		
		if(Mouse.isButtonDown(0))
			hotMouseUp = true;
		else hotMouseDown = true;

		if(Mouse.isButtonDown(1))
			hotRightMouseUp = true;
		else hotRightMouseDown = true;
		
		// dismiss tips
		if(toolTip!=null && Mouse.isButtonDown(0) && hotMouseDown) {
			toolTip = null;
			hotMouseDown = false;
			eventEaten = true;
		}
		
		if(toolTip!=null && Mouse.isButtonDown(1) && hotRightMouseDown) {
			toolTip = null;
			hotRightMouseDown = false;
			eventEaten = true;
		}
		
		// don't allow drags to affect buttons
		if(Mouse.isButtonDown(0) && !isOnHuD(mousePos.x,mousePos.y))
			hotMouseDown = false;
		if(Mouse.isButtonDown(1) && !isOnHuD(mousePos.x,mousePos.y))
			hotRightMouseDown = false;
				
		// mouse input for each element
		for(int i=elements.size()-1; i>=0; i--) {
			HudElement e = elements.get(i);
			Point2D origin = new Point2D();
			interact(commands, mousePos, e, origin);
		}
	}

	private void interact(List<String> commands, Point2D mousePos, HudElement e, Point2D origin) {
		
		if(!e.isVisible())
			return;
		Point2D realPos = new Point2D(e.getPos().x + origin.x, e.getPos().y + origin.y);
		if(Shape.shapeContainsPoint(mousePos, e.getShape(), realPos)!=Point2D.nullVector) {
			// mouse over
			if(!e.isMouseOver()) {
				e.setMouseOver(true);
				List<String> cs = e.getCommands(InteractionType.MOUSE_OVER);
				for(String command: cs)
					commands.add(command);
			}
			// mouse down
			if(Mouse.isButtonDown(0) && hotMouseDown) {
				if(e.isFocusable()) {
					focus = e;
				}	
				List<String> cs = e.getCommands(InteractionType.MOUSE_DOWN);
				for(String command: cs) {
					commands.add(command);
					hotMouseDown = false;
				}
			}
			// mouse up
			if(!Mouse.isButtonDown(0) && hotMouseUp) {
				List<String> cs = e.getCommands(InteractionType.MOUSE_UP);
				for(String command: cs) {
					commands.add(command);
					hotMouseUp = false;
				}
			}
		} else {
			// mouse off
			if(e.isMouseOver()) {
				e.setMouseOver(false);
				List<String> cs = e.getCommands(InteractionType.MOUSE_OFF);
				for(String command: cs)
					commands.add(command);
			}
		}
		
		for(HudElement child: e.getElements())
			interact(commands, mousePos, child, e.getPos());
	}
	
	public void draw() {
		Point2D origin = new Point2D();
		for(HudElement e: elements) {
			if(!e.isVisible()) continue;
			if(e.isAttachedToMouse()) continue;
			drawElement(e, origin);
		}
		for(HudElement e: elements) {
			if(!e.isVisible()) continue;
			if(!e.isAttachedToMouse()) continue;
			drawElement(e, origin);
		}
		if(toolTip!=null && toolTip.isVisible())
			drawElement(toolTip, origin);
	}

	public List<String> interact(Point2D pos, InteractionType interactionType) {
		HudElement e = getElement(pos);
		if(e!=null)
			return e.getCommands(interactionType);
		return new ArrayList<String>(0);
	}
	
	public void drawElement(HudElement e, Point2D origin) {
		Texture tex;
		if(!e.isVisible())
			return;
		switch(e.getType()) {
		case BUTTON:
		case PANEL:
			PanelElement p = (PanelElement)e;
			OpenGLDraw.fillPoly(p.getBackColour(),
					origin.x + e.getPos().x,
					origin.y + e.getPos().y,
					e.getShape(), 1);
			tex = TextureLoader.getInstance().getTexture(p.getImage());
			if(tex!=null) {
				OpenGLDraw.bindTexture(tex);
				OpenGLDraw.drawTexture(
						origin.x + e.getPos().x + e.getShape().getMinX(),
						origin.y + e.getPos().y + e.getShape().getMinY(),
						e.getShape().getMaxX() - e.getShape().getMinX(),
						e.getShape().getMaxY() - e.getShape().getMinY());
				OpenGLDraw.unbindTexture();
			}
			OpenGLDraw.drawPoly(p.getLineColour(),
					origin.x + e.getPos().x,
					origin.y + e.getPos().y,
					e.getShape(), 1);
			break;
		case TEXTENTRY:
			float baseLine = origin.y + e.getPos().y + e.getShape().getMinY() + FontManager.getFont(FontType.FONT_32).getHeight()*Frame.FONT_SCALE;
			String text = ((TextEntryElement)e).getText();
			if(e == focus) text += "|";
			FontManager.getFont(FontType.FONT_32).drawString(Color.WHITE, origin.x + e.getPos().x + e.getShape().getMinX(),
					baseLine, text, Frame.FONT_SCALE,-Frame.FONT_SCALE,FontAlign.ALIGN_LEFT);
			break;
		case TEXT:
			TextElement te = ((TextElement)e);
			TrueTypeFont font = FontManager.getFont(te.getFontType());
			baseLine = origin.y + te.getPos().y + te.getShape().getMinY() + font.getHeight()*Frame.FONT_SCALE;
			font.drawString(te.getColour(),
					origin.x + te.getPos().x + te.getShape().getMinX(),
					baseLine, te.getText(), Frame.FONT_SCALE,-Frame.FONT_SCALE,te.getAlignment());	
			break;
		}
		
		for(HudElement child: e.getElements()) {
			if(!child.isVisible()) continue;
			drawElement(child, e.getPos());
		}
	}
}
