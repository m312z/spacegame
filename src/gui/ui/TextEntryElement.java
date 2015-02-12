package gui.ui;

import org.lwjgl.input.Keyboard;

import phys.Point2D;
import phys.Shape;

public class TextEntryElement extends HudElement {

	String text = "";
	int size = 16;

	public TextEntryElement(String name, Shape shape, Point2D pos, String image) {
		super(name, shape, pos);
		focusable = true;
		type = HudElementType.TEXTENTRY;
	}

	public String getText() {
		return text;
	}

	public void setText(String string) {
		text = string;
	}

	@Override
	public String onKeyDown(int key, char c) {
		if(Character.isLetterOrDigit(c) || c=='.') {
			if(text.length() < size) text += c;
		} else {
			switch(key) {
			case Keyboard.KEY_BACK:
				if(text.length()>0)
					text = text.substring(0,text.length()-1);
				break;
			}
		}
		return null;
	}

	@Override
	public String onKeyUp(int key, char c) {
		return null;
	}
}
