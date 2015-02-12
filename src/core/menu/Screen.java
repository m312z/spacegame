package core.menu;

import gui.ui.HudOverlay;

import org.lwjgl.opengl.Display;

import sound.SoundManager;

import core.Frame;

public abstract class Screen {

	protected Frame frame;
	protected HudOverlay menuOverlay;
	public boolean finished = false;
	
	public Screen(Frame frame) {
		this.frame = frame;
		makeOverlay();
	}
	
	protected abstract void makeOverlay();
	
	public void start() {
		
		finished = false;
		setupLoop();
		
		while(!finished) {

			// pollInput
			pollInput();
						
			// draw view
			drawScreen();
			
			// opengl update
			Display.update();
			Display.sync(60);
			if (Display.wasResized()) {
	            frame.setDisplayMode(
	            		Display.getWidth(),
	            		Display.getHeight(),
	            		Frame.FULLSCREEN);
	            makeOverlay();
			}

			// queue buffers
			SoundManager.update();
			
			if(Display.isCloseRequested())
				cancel();
		}
		finish();
	}
	
	protected void setupLoop() {
		// nothing by default
	}

	protected abstract void pollInput();
	
	protected void drawScreen() {
		frame.getBackground().draw();
		menuOverlay.draw();
	}
	
	public abstract void cancel();
	
	public void finish() {
		finished = true;
	}
}
