package core.menu;

import static core.Frame.SCREEN_SIZE;
import gui.FontManager.FontType;
import gui.ui.ButtonElement;
import gui.ui.HudElement.InteractionType;
import gui.ui.HudOverlay;
import gui.ui.PanelElement;
import gui.ui.TextElement;
import gui.ui.TextEntryElement;

import java.awt.Color;
import java.util.List;

import phys.Point2D;
import phys.Shape;
import core.Frame;

public class ClientSetup extends Screen {

	/* true if to connect */
	boolean join = false;
	
	/* connection details */
	String name;
	String IP;
	String pass;
	
	/* GUI */
	TextEntryElement nameEntry;
	TextEntryElement addEntry;
	TextEntryElement passEntry;
	
	public ClientSetup(Frame frame) {
		super(frame);
	}
	
	@Override
	protected void makeOverlay() {
		
		menuOverlay = new HudOverlay();
		float hs = SCREEN_SIZE[1]/16;
		Shape ps = new Shape(new Point2D[] {
				new Point2D(-hs*4,-hs*3),
				new Point2D(hs*4,-hs*3),
				new Point2D(hs*4,hs*3),
				new Point2D(-hs*4,hs*3)
		});
		Shape bs = new Shape(new Point2D[] {
				new Point2D(-hs*2,-hs/2),
				new Point2D(hs*2,-hs/2),
				new Point2D(hs*2,hs/2),
				new Point2D(-hs*2,hs/2)
		});	
		Shape ts = new Shape(new Point2D[] {
				new Point2D(-hs*4,-hs/2),
				new Point2D(hs*4,-hs/2),
				new Point2D(hs*4,hs/2),
				new Point2D(-hs*4,hs/2)
		});	
		PanelElement joinPanel = new PanelElement("join_panel", ps, new Point2D(SCREEN_SIZE[0]/2, SCREEN_SIZE[1]/2), Color.BLACK, Color.BLUE);
		joinPanel.addElement(new TextElement("pnt", bs, new Point2D(0, ps.getMinY() + hs/2f), "USERNAME", Color.WHITE, FontType.FONT_32));
		joinPanel.addElement(new TextElement("pat", bs, new Point2D(0, ps.getMinY() + hs*5/2f), "IP", Color.WHITE, FontType.FONT_32));
		joinPanel.addElement(new TextElement("ppt", bs, new Point2D(0, ps.getMinY() + hs*9/2f), "PASSWORD", Color.WHITE, FontType.FONT_32));
		joinPanel.addElement(new TextEntryElement("name_entry", ts, new Point2D(0, ps.getMinY() + hs*3/2f),""));
		joinPanel.addElement(new TextEntryElement("address_entry", ts, new Point2D(0, ps.getMinY() + hs*7/2f),""));
		joinPanel.addElement(new TextEntryElement("password_entry", ts, new Point2D(0, ps.getMinY() + hs*11/2f),""));
		ButtonElement joinButton = new ButtonElement("join_button", bs, new Point2D(SCREEN_SIZE[0]/2 - hs*2, SCREEN_SIZE[1]/2 + hs*7/2f), Color.BLACK, Color.BLUE);
		joinButton.addCommand(InteractionType.MOUSE_DOWN, "join");
		joinButton.addElement(new TextElement("qbt", bs,new Point2D(), "JOIN", Color.WHITE, FontType.FONT_32));
		ButtonElement quitButton = new ButtonElement("quit_button", bs, new Point2D(SCREEN_SIZE[0]/2 + hs*2, SCREEN_SIZE[1]/2 + hs*7/2f), Color.BLACK, Color.BLUE);
		quitButton.addCommand(InteractionType.MOUSE_DOWN, "quit");
		quitButton.addElement(new TextElement("qbt", bs,new Point2D(), "QUIT", Color.WHITE, FontType.FONT_32));
		menuOverlay.addElement(joinPanel);
		menuOverlay.addElement(joinButton);
		menuOverlay.addElement(quitButton);
		
		((TextEntryElement)menuOverlay.getElement("name_entry")).setText("Senoir");
		((TextEntryElement)menuOverlay.getElement("address_entry")).setText("127.0.0.1");
	}
	

	protected void pollInput() {
		List<String> commands = menuOverlay.pollInput();
		for(String com: commands) {
			switch(com) {
			case "join":
				name = ((TextEntryElement)menuOverlay.getElement("name_entry")).getText();
				IP = ((TextEntryElement)menuOverlay.getElement("address_entry")).getText();
				pass = ((TextEntryElement)menuOverlay.getElement("password_entry")).getText();
				join = true;
				finish();
				break;
			case "quit":
				cancel();
				break;
			}
		}
	}
	
	public String getName() {
		return name;
	}
	
	public String getPass() {
		return pass;
	}
	
	public String getIP() {
		return IP;
	}
		
	public boolean toJoin() {
		return join;
	}
	
	@Override
	public void cancel() {
		join = false;
		finished = true;
	}
}
