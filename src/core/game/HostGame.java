package core.game;

import gui.ServerFrame;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import network.Server;
import network.packet.Packet;
import network.packet.StartGamePacket;

import org.lwjgl.input.Keyboard;

import core.Frame;
import core.Frame.GameState;
import core.controller.PlayerController;


public class HostGame extends Game implements WindowListener {

	/* game info */
	private String gameName = "World";
	
	/* debug info for the host */ 
	private ServerFrame serverFrame;
	private boolean displayServerFrame = true;
	
	/* network connection */
	private Server server;
	
	public HostGame(Frame frame) {
		super(frame);
		// set up server
		resetServer();
	}
	
	@Override
	public void finish() {
		server.finish();
		server.disconnectAll();
		
		if(displayServerFrame) {
			serverFrame.setVisible(false);
			serverFrame.dispose();
		}
		
		finished = true;
	}
	
	@Override
	public void cancel() {
		frame.state = GameState.MAINMENU;
		finish();
	}
	
	/*--------------*/
	/* Setup server */
	/*--------------*/

	private void resetServer() {
		
		// display debug info pane
		if(displayServerFrame) {
			serverFrame = new ServerFrame(gameName);
			serverFrame.addWindowListener(this);
			serverFrame.setVisible(true);
		}
		
		// create connection
		server = new Server(gameName,serverFrame);
		if(displayServerFrame)
			serverFrame.output("GameServer started: "+gameName);
	}
	
	public Server getServer() {
		return server;
	}
		
	/*-----------*/
	/* main loop */
	/*-----------*/

	@Override
	protected void setupLoop() {
		
		// setup FPS counter in Game
		super.setupLoop();

		server.sendMessageToAll(new StartGamePacket());
	}
	
	@Override
	protected void collectTasks() {
		
		// escape
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			finished = true;
				
		// poll (local) controllers
		for(PlayerController controller: controllers.values()) {
			controller.pollInput(board, menuOverlay);
			// create input packet
			for(Packet p: controller.getCommands()) {
				tasks.put(p.getTime(),p);
				// send task to all (other) remote players
				server.sendMessageToAll(p);
			}
			controller.getCommands().clear();
		}
		
		// poll server connection
		while(server.hasTasks()) {
			Packet task = server.getTask(Float.MAX_VALUE);
			tasks.put(task.getTime(), task);
		}
	}
	
	/*-------------------------*/
	/* debug info pane methods */
	/*-------------------------*/

	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
		finished = true;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		finished = true;
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {	
	}

}
