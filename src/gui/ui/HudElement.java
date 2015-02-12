package gui.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import phys.Point2D;
import phys.Shape;

public abstract class HudElement {

	/* interaction types */
	public enum InteractionType
	{
		MOUSE_DOWN	(0),
		MOUSE_UP	(1),
		MOUSE_OVER	(2),
		MOUSE_OFF	(3);
		
		int id;
		private InteractionType(int id) {
			this.id = id;
		}
	}
	
	/* types */
	public enum HudElementType
	{
		BUTTON, PANEL, TEXTENTRY, TEXT
	}
	
	/* dynamic access */
	String name;
	
	/* display */
	HudElementType type;
	Shape shape;
	Point2D pos;
	boolean visible = true;
	boolean attachedToMouse = false;
	
	/* interaction */
	boolean focusable;
	boolean mouseOver;	

	Map<InteractionType,List<String>> mouseCommands;
	
	/* components */
	List<HudElement> elements;
	
	public HudElement(String name, Shape shape, Point2D pos) {
		this.name = name;
		this.shape = shape;
		this.pos = pos;
		this.elements = new ArrayList<HudElement>(0);
		
		mouseCommands = new HashMap<InteractionType,List<String>>();
		for(InteractionType it: InteractionType.values())
			mouseCommands.put(it,new ArrayList<String>(0));
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Shape getShape() {
		return shape;
	}
	
	public Point2D getPos() {
		return pos;
	}
	
	public HudElementType getType() {
		return type;
	};
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}
	
	public boolean isMouseOver() {
		return mouseOver;
	}
	
	public boolean isFocusable() {
		return focusable && visible;
	}
	
	public void addElement(HudElement e) {
		elements.add(e);
	}
	
	public List<HudElement> getElements() {
		return elements;
	}
	
	public boolean isAttachedToMouse() {
		return attachedToMouse;
	}
	
	public void setAttachedToMouse(boolean attachedToMouse) {
		this.attachedToMouse = attachedToMouse;
	}
	
	public List<String> getCommands(InteractionType it) {
		return mouseCommands.get(it);
	}
	
	public void addCommand(InteractionType it, String command) {
		mouseCommands.get(it).add(command);
	}
	
	public abstract String onKeyDown(int key, char c);
	public abstract String onKeyUp(int key, char c);
}
