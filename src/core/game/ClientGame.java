package core.game;

import network.Client;
import network.packet.Packet;

import org.lwjgl.input.Keyboard;

import core.Frame;
import core.Frame.GameState;
import core.controller.PlayerController;

public class ClientGame extends Game {

	/* network connection */
	private Client clientConnection;
	
	/**
	 * Default Constructor.
	 * @param frame
	 */
	public ClientGame(Frame frame) {
		super(frame);
	}
	
	@Override
	public void finish() {
		finished = true;
		clientConnection.disconnect();
	}
	
	@Override
	public void cancel() {
		frame.state = GameState.MAINMENU;
		finish();
	}
	
	/*-------*/
	/* setup */
	/*-------*/
	
	/**
	 * Set's up client game.  Uses <code>Client</code> to attempt a connection
	 * and retrieves game details from the server.
	 * @param password Server password, possibly an empty String.
	 * @param ip Host's IP address as a String.
	 * @return	true if a game was successfully joined.
	 */
	public boolean setupClient(String password, String ip) {
		clientConnection = new Client(password,ip,frame.getPlayer());
		boolean success = clientConnection.isSuccessfulConnection();
		return success;
	}
		
	public Client getClientConnection() {
		return clientConnection;
	}
	
	/*-----------*/
	/* main loop */
	/*-----------*/
	
	@Override
	protected void setupLoop() {
		
		// setup FPS counter in Game
		super.setupLoop();
		
		board.setTime(clientConnection.getStartTime());
		syncBoard.setTime(clientConnection.getStartTime());
	}
	
	@Override
	protected void pollInput() {
		
		super.pollInput();
		
		if(!clientConnection.isSuccessfulConnection())
			finished = true;
	}
	
	@Override
	protected void collectTasks() {
		
		// escape
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			finished = true;
		
		// poll (local) controllers
		for(PlayerController controller: controllers.values()) {
			controller.pollInput(board, menuOverlay);
			for(Packet p: controller.getCommands()) {
				tasks.put(p.getTime(),p);
				
				// send task to server
				clientConnection.sendMessage(p);
			}
			controller.getCommands().clear();
		}
		
		// poll connection for tasks
		while(clientConnection.hasTasks()) {
			Packet task = clientConnection.getTask(Float.MAX_VALUE);
			tasks.put(task.getTime(), task);
		}
	}
}
