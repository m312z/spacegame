package gui;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

public class FontManager {

	public static enum FontType
	{
		FONT_24 (24),
		FONT_32 (32);
		
		int size;
		private FontType(int size) {
			this.size = size;
		}
	}
	
	public static TrueTypeFont trueTypeFont_24;
	public static TrueTypeFont trueTypeFont_32;
	
	public static void makeFonts() {
		Font font24, font32;
		try {
			File fontfile = new File("resource/Impacted2.0.ttf");
			if(fontfile.exists()) { 
				font24 = Font.createFont(Font.TRUETYPE_FONT, fontfile);
				font24 = font24.deriveFont(Font.PLAIN, 24);
				font32 = font24.deriveFont(Font.PLAIN, 32);
			} else {
				font24 = new Font("Serif", Font.BOLD, 24);
				font32 = new Font("Serif", Font.BOLD, 32);
			}
		} catch (FontFormatException e) {
			e.printStackTrace();
			font24 = new Font("Serif", Font.BOLD, 24);
			font32 = new Font("Serif", Font.BOLD, 32);
		} catch (IOException e) {
			e.printStackTrace();
			font24 = new Font("Serif", Font.BOLD, 24);
			font32 = new Font("Serif", Font.BOLD, 32);
		} 
		trueTypeFont_24 = new TrueTypeFont(font24, true);
		trueTypeFont_32 = new TrueTypeFont(font32, true);
	}

	public static TrueTypeFont getFont(FontType type) {
		switch(type) {
		case FONT_24: return trueTypeFont_24;
		case FONT_32: return trueTypeFont_32;
		default: return trueTypeFont_32;
		}
	}
	
	public static void destroyFonts() {
		trueTypeFont_24.destroy();
		trueTypeFont_32.destroy();
	}
}
