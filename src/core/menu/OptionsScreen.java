package core.menu;

import static core.Frame.FULLSCREEN;
import static core.Frame.SCREEN_SIZE;
import static core.Frame.WINDOW_SIZE;
import gui.FontManager.FontType;
import gui.Pallete;
import gui.ui.ButtonElement;
import gui.ui.HudElement.InteractionType;
import gui.ui.HudOverlay;
import gui.ui.TextElement;

import java.awt.Color;
import java.util.List;

import phys.Point2D;
import phys.Shape;
import sound.SoundManager;
import core.Frame;
import core.Frame.GameState;

/**
 * Main menu controller.
 * @author Michael Cashmore
 */
public class OptionsScreen extends Screen {

	public OptionsScreen(Frame frame) {
		super(frame);
		makeOverlay();
	}

	@Override
	protected void makeOverlay() {
		
		// create menu UI
		menuOverlay = new HudOverlay();
		float hs = SCREEN_SIZE[1]/10;
		Shape bs = new Shape(new Point2D[] {
				new Point2D(-hs*2,-hs/2),
				new Point2D( hs*2,-hs/2),
				new Point2D( hs*2, hs/2),
				new Point2D(-hs*2, hs/2)
		});
				
		ButtonElement optionsButton = new ButtonElement("fullscreen_button", bs, new Point2D(SCREEN_SIZE[0]/2, 3*hs), Color.BLACK, Pallete.dull, Color.WHITE);
		optionsButton.addCommand(InteractionType.MOUSE_DOWN, "fullscreen");
		optionsButton.addElement(new TextElement("obt", bs, new Point2D(), "FULLSCREEN", FontType.FONT_32));
		
		ButtonElement soundButton = new ButtonElement("sound_button", bs, new Point2D(SCREEN_SIZE[0]/2, 4*hs), Color.BLACK, Pallete.dull, Color.WHITE);
		soundButton.addCommand(InteractionType.MOUSE_DOWN, "sound");
		soundButton.addElement(new TextElement("sbt_on", bs, new Point2D(), "SOUNDS ARE ON", FontType.FONT_32));
		soundButton.addElement(new TextElement("sbt_off", bs, new Point2D(), "SOUNDS ARE OFF", FontType.FONT_32));
		
		ButtonElement musicButton = new ButtonElement("music_button", bs, new Point2D(SCREEN_SIZE[0]/2, 5*hs), Color.BLACK, Pallete.dull, Color.WHITE);
		musicButton.addCommand(InteractionType.MOUSE_DOWN, "music");
		musicButton.addElement(new TextElement("mbt_on", bs, new Point2D(), "MUSIC IS OFF", FontType.FONT_32));
		musicButton.addElement(new TextElement("mbt_off", bs, new Point2D(), "MUSIC IS ON", FontType.FONT_32));
		
		ButtonElement quitButton = new ButtonElement("quit_button", bs, new Point2D(SCREEN_SIZE[0]/2, 6*hs), Color.BLACK, Pallete.dull, Color.WHITE);
		quitButton.addCommand(InteractionType.MOUSE_DOWN, "quit");
		quitButton.addElement(new TextElement("qbt", bs,new Point2D(), "BACK", FontType.FONT_32));
		
		menuOverlay.addElement(optionsButton);
		menuOverlay.addElement(soundButton);
		menuOverlay.addElement(musicButton);
		menuOverlay.addElement(quitButton);
		
		menuOverlay.getElement("mbt_on",menuOverlay.getElement("music_button")).setVisible(SoundManager.isMusicMute());
		menuOverlay.getElement("mbt_off",menuOverlay.getElement("music_button")).setVisible(!SoundManager.isMusicMute());
		menuOverlay.getElement("sbt_on",menuOverlay.getElement("sound_button")).setVisible(SoundManager.isSoundPlaying());
		menuOverlay.getElement("sbt_off",menuOverlay.getElement("sound_button")).setVisible(!SoundManager.isSoundPlaying());
	}

	protected void pollInput() {
		List<String> commands = menuOverlay.pollInput();
		for(String com: commands) {
			switch(com) {
			case "fullscreen": fullScreen(); break;
			case "music":
				SoundManager.setMusicMute(!SoundManager.isMusicMute());
				menuOverlay.getElement("mbt_on",menuOverlay.getElement("music_button")).setVisible(SoundManager.isMusicMute());
				menuOverlay.getElement("mbt_off",menuOverlay.getElement("music_button")).setVisible(!SoundManager.isMusicMute());
				break;
			case "sound":
				SoundManager.setSoundEffects(!SoundManager.isSoundPlaying());
				menuOverlay.getElement("sbt_on",menuOverlay.getElement("sound_button")).setVisible(SoundManager.isSoundPlaying());
				menuOverlay.getElement("sbt_off",menuOverlay.getElement("sound_button")).setVisible(!SoundManager.isSoundPlaying());
				break;
			case "quit": cancel(); break;
			}
		}
	}

	@Override
	public void cancel() {
		frame.state = GameState.END;
		finished = true;
	}

	@Override
	public void finish() {
		frame.state = GameState.MAINMENU;
		finished = true;
	}

	public void fullScreen() {
		FULLSCREEN = FULLSCREEN^true;
		frame.setDisplayMode(WINDOW_SIZE[0], WINDOW_SIZE[1], FULLSCREEN);
		makeOverlay();
	}
}
