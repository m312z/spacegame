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

import network.Server;
import network.packet.Packet;
import phys.Point2D;
import phys.Shape;
import core.Frame;

public class HostLobbySetup extends Screen {

	/* network connection */
	Server connection;
	
	/* true if the game is starting */
	boolean play = false;
		
	public HostLobbySetup(Frame frame, Server server) {
		super(frame);
		this.connection = server;
	}
	
	@Override
	protected void makeOverlay() {
		menuOverlay = new HudOverlay();
		float hs = SCREEN_SIZE[1]/10;
		Shape bs = new Shape(new Point2D[] {
				new Point2D(-hs*2,-hs/2),
				new Point2D(hs*2,-hs/2),
				new Point2D(hs*2,hs/2),
				new Point2D(-hs*2,hs/2)
		});
		
		ButtonElement startButton = new ButtonElement("start_button", bs, new Point2D(SCREEN_SIZE[0]/2 - hs*2, SCREEN_SIZE[1]/2 + hs*5/2f), Pallete.dull, Color.BLUE);
		startButton.addCommand(InteractionType.MOUSE_DOWN, "start");
		startButton.addElement(new TextElement("sbt", bs, new Point2D(), "PLAY", Color.WHITE, FontType.FONT_32));
		
		ButtonElement quitButton = new ButtonElement("quit_button", bs, new Point2D(SCREEN_SIZE[0]/2 + hs*2, SCREEN_SIZE[1]/2 + hs*5/2f), Pallete.dull, Color.BLUE);
		quitButton.addCommand(InteractionType.MOUSE_DOWN, "quit");
		quitButton.addElement(new TextElement("qbt", bs, new Point2D(), "QUIT", Color.WHITE, FontType.FONT_32));
		
		menuOverlay.addElement(startButton);
		menuOverlay.addElement(quitButton);
	}
	
//	private String getAddress() {
//		String s = "";
//		try {
//			Enumeration<NetworkInterface> nien = NetworkInterface.getNetworkInterfaces();
//			while(nien.hasMoreElements()) {
//				NetworkInterface ni = nien.nextElement();
//				Enumeration<InetAddress> aden = ni.getInetAddresses();
//				while(aden.hasMoreElements()) {
//					InetAddress add = aden.nextElement();
//					System.out.println(add.getHostAddress());
//					s += add.getHostAddress() + "\n";
//				}
//			}
//		} catch (SocketException e) {
//			e.printStackTrace();
//		}
//		return s;
//	}

	@Override
	protected void pollInput() {
		
		List<String> commands = menuOverlay.pollInput();
		for(String com: commands) {
			switch(com) {
			case "start": finish(); break;
			case "quit": cancel(); break;
			}
		}

		// messages
		getMessages();
	}
	
	private void getMessages() {
		// Process tasks
		Packet packet = connection.getTask(Float.MAX_VALUE);
		while (packet!=null) {
			System.out.println(packet.getType().toString());
		}
	}

	public boolean toPlay() {
		return play;
	}
	
	@Override
	public void cancel() {
		play = false;
		finished = true;
	}
	
	@Override
	public void finish() {
		play = true;
		finished = true;
	}
}
