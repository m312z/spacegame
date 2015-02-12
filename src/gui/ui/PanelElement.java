package gui.ui;

import java.awt.Color;

import phys.Point2D;
import phys.Shape;

public class PanelElement extends HudElement{

	static final Color TRANS = new Color(0,0,0,0);
	
	Color backColour;
	Color lineColour;
	String image;
	
	String imageOff;
	String imageHover;
		
	public PanelElement(String name, Shape shape, Point2D pos, Color back, Color line) {
		this(name, shape, pos, back, line, "", "");
	}
	
	public PanelElement(String name, Shape shape, Point2D pos, String image, String imageHover) {
		this(name, shape, pos, TRANS, TRANS, image, imageHover);
	}
	
	public PanelElement(String name, Shape shape, Point2D pos, Color back, Color line, String image, String imageHover) {
		super(name, shape, pos);
		this.image = image;
		this.imageOff = image;
		this.imageHover = imageHover;
		this.backColour = back;
		this.lineColour = line;
		type = HudElementType.PANEL;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	
	public Color getBackColour() {
		return backColour;
	}
	
	public void setBackColour(Color backColour) {
		this.backColour = backColour;
	}
	
	public Color getLineColour() {
		return lineColour;
	}
	
	public void setLineColour(Color lineColour) {
		this.lineColour = lineColour;
	}
	
	public void setImageHover(String imageHover) {
		this.imageHover = imageHover;
	}
	
	@Override
	public String onKeyDown(int key, char c) {
		return null;
	}

	@Override
	public String onKeyUp(int key, char c) {
		return null;
	}
}
