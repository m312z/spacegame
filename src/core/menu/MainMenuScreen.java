package core.menu;

import static core.Frame.SCREEN_SIZE;
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
import core.Frame;
import core.Frame.GameState;

/**
 * Main menu controller.
 * @author Michael Cashmore
 */
public class MainMenuScreen extends Screen {
		
	public MainMenuScreen(Frame frame) {
		super(frame);
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
		
		ButtonElement startButton = new ButtonElement("start_button", bs, new Point2D(SCREEN_SIZE[0]/2, 3*hs), Color.BLACK, Pallete.dull, Color.WHITE);
		startButton.addCommand(InteractionType.MOUSE_DOWN, "start");
		startButton.addElement(new TextElement("sbt", bs, new Point2D(0,0), "PLAY", FontType.FONT_32));
		
		ButtonElement hostButton = new ButtonElement("host_button", bs, new Point2D(SCREEN_SIZE[0]/2, 4*hs), Color.BLACK, Pallete.dull, Color.WHITE);
		hostButton.addCommand(InteractionType.MOUSE_DOWN, "host");
		hostButton.addElement(new TextElement("hbt", bs, new Point2D(0,0), "HOST", FontType.FONT_32));
		
		ButtonElement joinButton = new ButtonElement("join_button", bs, new Point2D(SCREEN_SIZE[0]/2, 5*hs), Color.BLACK, Pallete.dull, Color.WHITE);
		joinButton.addCommand(InteractionType.MOUSE_DOWN, "join");
		joinButton.addElement(new TextElement("jbt", bs, new Point2D(0,0), "JOIN", FontType.FONT_32));
		
		ButtonElement optionsButton = new ButtonElement("options_button", bs, new Point2D(SCREEN_SIZE[0]/2, 6*hs), Color.BLACK, Pallete.dull, Color.WHITE);
		optionsButton.addCommand(InteractionType.MOUSE_DOWN, "options");
		optionsButton.addElement(new TextElement("obt", bs, new Point2D(), "OPTIONS", FontType.FONT_32));
		
		ButtonElement quitButton = new ButtonElement("quit_button", bs, new Point2D(SCREEN_SIZE[0]/2, 7*hs), Color.BLACK, Pallete.dull, Color.WHITE);
		quitButton.addCommand(InteractionType.MOUSE_DOWN, "quit");
		quitButton.addElement(new TextElement("qbt", bs,new Point2D(), "QUIT", FontType.FONT_32));
		
		menuOverlay.addElement(startButton);
		menuOverlay.addElement(hostButton);
		menuOverlay.addElement(joinButton);
		menuOverlay.addElement(optionsButton);
		menuOverlay.addElement(quitButton);
	}

	protected void pollInput() {
		List<String> commands = menuOverlay.pollInput();
		for(String com: commands) {
			switch(com) {
			case "start":
				frame.state = GameState.LOCAL_GAME;
				finished = true;
				break;
			case "host":
				frame.state = GameState.HOST_GAME;
				finished = true;
				break;
			case "join":
				frame.state = GameState.CLIENT_GAME;
				finished = true;
				break;
			case "options":
				frame.state = GameState.OPTIONS;
				finished = true;
				break;
			case "quit":
				cancel();
				break;
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
		finished = true;
	}
}
