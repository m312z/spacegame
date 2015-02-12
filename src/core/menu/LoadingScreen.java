package core.menu;

import gui.FontManager;
import gui.LoadingScreenGUI;
import gui.TextureLoader;
import gui.ui.HudOverlay;
import sound.SoundManager;
import core.Frame;
import core.Frame.GameState;

/**
 * Loading bar controller.
 * @author Michael Cashmore
 */
public class LoadingScreen extends Screen {

	int state;
	Thread thread;
	LoadingScreenGUI gui;
		
	public LoadingScreen(Frame frame) {
		super(frame);
		FontManager.makeFonts();
		makeOverlay();
		
		gui = new LoadingScreenGUI();		
		state = 0;
		SoundLoader loader = new SoundLoader();
		thread = new Thread(loader);
		thread.start();
	}

	@Override
	protected void makeOverlay() {
		menuOverlay = new HudOverlay();
	}
	
	@Override
	protected void pollInput() {

		// load next object
		switch(state) {
		case 0:
			// images
			TextureLoader.getInstance().loadOneTexture();
			if(TextureLoader.getInstance().getLoadedImages()
					== TextureLoader.getInstance().getTotalImages().size()) {
				state++;
				/*
				((TextElement)menuOverlay.getElement("loading_string")).setText("Loading music");
				menuOverlay.getElement("loading_string").getPos().x = (Frame.SCREEN_SIZE[0] - FontManager.getFont(FontType.FONT_32).getWidth("Loading music"))/2f;
				*/
			}
			break;
		case 1: 
			// sounds
			if(!thread.isAlive()) finish();
			break;
		}
	}
	
	@Override
	protected void drawScreen() {
		// draw view
		gui.draw( (float)(TextureLoader.getInstance().getLoadedImages() + SoundManager.soundsLoaded*TextureLoader.getInstance().getTotalImages().size())
				/ (float)(TextureLoader.getInstance().getTotalImages().size()*3));
		menuOverlay.draw();
	}
		
	@Override
	public void cancel() {
		frame.state = GameState.MAINMENU;
		finished = true;
	}
	
	@Override
	public void finish() {
		frame.state = GameState.MAINMENU;
		finished = true;
	}
	
	class SoundLoader implements Runnable
	{
		@Override
		public void run() {
			SoundManager.init();
		}	
	}
}