package gui.ui;

import gui.FontManager.FontType;
import gui.TrueTypeFont.FontAlign;

import java.awt.Color;

import phys.Point2D;
import phys.Shape;

public class TextElement extends HudElement {
	
	String text = "";
	Color colour;
	FontType fontType;
	FontAlign alignment;
	
	public TextElement(String name, Shape shape, Point2D pos, String text, FontType fontType) {
		this(name, shape, pos, text, Color.WHITE, fontType);
	}
	
	public TextElement(String name, Shape shape, Point2D pos, String text, Color colour, FontType fontType) {
		this(name, shape, pos, text, colour, fontType, FontAlign.ALIGN_LEFT);
	}
	
	public TextElement(String name, Shape shape, Point2D pos, String text, Color colour, FontType fontType, FontAlign alignment) {
		super(name, shape, pos);
		this.focusable = false;
		this.text = text;
		this.colour = colour;
		this.fontType = fontType;
		this.alignment = alignment;
		this.type = HudElementType.TEXT;
	}
			
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public Color getColour() {
		return colour;
	}
	
	public void setColour(Color colour) {
		this.colour = colour;
	}
	
	public FontType getFontType() {
		return fontType;
	}

	public FontAlign getAlignment() {
		return alignment;
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
