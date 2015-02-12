package gui.ui;

import java.awt.Color;

import org.lwjgl.input.Mouse;

import phys.Point2D;
import phys.Shape;

public class ButtonElement extends PanelElement {

	Color hoverColour;
	
	public ButtonElement(String name, Shape shape, Point2D pos, Color back, Color line) {
		this(name, shape, pos, back, line, "", "");
	}
	
	public ButtonElement(String name, Shape shape, Point2D pos, Color back, Color hover, Color line) {
		this(name, shape, pos, back, line, "", "");
		hoverColour = hover;
	}
	
	public ButtonElement(String name, Shape shape, Point2D pos, String image, String imageHover) {
		this(name, shape, pos, TRANS, TRANS, image, imageHover);
	}
	
	public ButtonElement(String name, Shape shape, Point2D pos, Color back, Color line, String image, String imageHover) {
		super(name, shape, pos, back, line, image, imageHover);
		hoverColour = back;
		type = HudElementType.BUTTON;
	}
	
	@Override
	public Color getBackColour() {
		if(hoverColour!=null && !Mouse.isGrabbed() && mouseOver)
			return hoverColour;
		return backColour;
	}
}