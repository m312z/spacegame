package core.game;

import gui.GameGUI;
import gui.HudFactory;

import java.util.Random;
import java.util.TreeMap;

import model.Board;
import model.Player.PlayerType;
import network.packet.Packet;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import core.Frame;
import core.Frame.GameState;
import core.controller.MouseController;
import core.controller.PlayerController;
import core.menu.Screen;

/**
 * The main loop for the game.  Also contains some static fields
 * that are useful everywhere.
 * @author Michael Cashmore
 *
 */
public class Game extends Screen {
	
	/* game loop statics */
	public static final float INTERVAL_FRAMES = 180;
	public static float GAMESPEED = 1f;
	public static int randomSeed = 1;
	public static Random randomGenerator;
		
	/* GUI */
	protected GameGUI gui;
	long lastFPS = 0;
	int fps;
	float dt;
	
	/* model */
	protected Board board;
	long lastTick;

	/* players playerID <-> Controller */
	TreeMap<String,PlayerController> controllers;
	
	/* player inputs */
	protected TreeMap<Float,Packet> tasks;
	
	/* board clone INTERVAL_FRAMES in the past (for synchronization) */
	protected Board syncBoard;
	
	/* tasks applied to local game, not yet applied to syncBoard */
	protected TreeMap<Float,Packet> syncTasks;
	
	public Game(Frame frame) {
		super(frame);		
		setup();
	}
	
	@Override
	public void cancel() {
		frame.state = GameState.MAINMENU;
		finished = true;
	}
	
	/*-------*/
	/* SETUP */
	/*-------*/
	
	@Override
	protected void makeOverlay() {
		menuOverlay = HudFactory.makeHUD(PlayerType.BUILDER);
	}

	public void setup() {
		
		// setup model
		randomGenerator = new Random(randomSeed);
		board = new Board();
		tasks = new TreeMap<Float,Packet>();
		syncBoard = board.clone();
		syncTasks = new TreeMap<Float, Packet>();
		
		// setup controllers
		controllers = new TreeMap<String,PlayerController>();
		controllers.put(
				frame.getPlayer().getPlayerID(),
				new MouseController(frame.getPlayer().getPlayerID()));
		
		// setup GUI / HUD
		gui = new GameGUI();
	}
	
	/*-----------*/
	/* Main loop */
	/*-----------*/
	
	@Override
	protected void setupLoop() {
		lastFPS = getTime();
		lastTick = getTime();
		dt = 1f;
	};
	
	@Override
	protected void pollInput() {
		
		// update FPS info
		dt = (getTime() - lastTick) / GAMESPEED;
		lastTick = getTime();
		
		// process game tasks
		collectTasks();
		processTasks();
		
		// update model
		finished = ( finished || board.tick(dt) );
	}

	protected void collectTasks() {
		
		// escape
		if(Keyboard.isKeyDown(Keyboard.KEY_ESCAPE))
			finished = true;
		
		// poll controllers
		for(PlayerController controller: controllers.values()) {
			controller.pollInput(board, menuOverlay);
			for(Packet p: controller.getCommands()) {
				tasks.put(p.getTime(),p);
			}
			controller.getCommands().clear();
		}
	}
	
	@Override
	protected void drawScreen() {
		// draw
		gui.draw(controllers.firstEntry().getValue(), board, frame.getPlayer(), dt);
		menuOverlay.draw();
		updateFPS();
	}

	/*--------------*/
	/* Model update */
	/*--------------*/
	
	/**
	 * Process tasks in the order they were received.  Keep a clone of the game set
	 * INTERVAL_FRAMES in the past to account for lag issues.
	 */
	protected void processTasks() {
		
		// ideal time for syncBoard
		float syncTime = board.getTime() - INTERVAL_FRAMES;
		Board newSyncBoard = null;
		float dt;
		
		// add tasks applied to Board, not applied to syncBoard
		for(Float f: syncTasks.keySet())
			addTask(syncTasks.get(f));
		syncTasks.clear();
		
		// Process tasks until the syncTime
		Packet packet = getTask(syncTime);
		while (packet!=null) {
						
			// process packet
			dt = packet.getTime() - syncBoard.getTime();
			if(dt>0) syncBoard.tick(dt);
			PacketHandler.applyPacket(syncBoard,packet);
			
			// get next task
			packet = getTask(syncTime);
		}
				
		// create new syncBoard
		dt = syncTime - syncBoard.getTime();
		if(dt>0) syncBoard.tick(dt);
		newSyncBoard = syncBoard.clone();
		
		// process remaining tasks (until current time)
		packet = getTask(Float.MAX_VALUE);
		while (packet!=null) {
			
			// process packet
			dt = packet.getTime() - syncBoard.getTime();
			if(dt>0) syncBoard.tick(dt);
			PacketHandler.applyPacket(syncBoard,packet);
			
			// add to syncTasks for next cycle
			syncTasks.put(packet.getTime(),packet);
			
			// get next task
			packet = getTask(board.getTime());
		}
		
		// increment to current time
		dt = board.getTime() - syncBoard.getTime();
		if(dt>0) syncBoard.tick(dt);
		
		// swap to new boards
		board = syncBoard;
		syncBoard = newSyncBoard;
	}

	public void addTask(Packet p) {
		tasks.put(p.getTime(), p);
	}
	
	/**
	 * @param threshold a time value.
	 * @return the earliest task, if it is before threshold, otherwise nothing.
	 */
	public Packet getTask(float threshold) {
		if(hasTasks()) {
			Packet p = null;
			if(tasks.firstKey()<threshold)
				p = tasks.remove(tasks.firstKey());
			return p;
		} else {
			return null;
		}
	}
	
	/**
	 * @return true if there are player inputs to process.
	 */
	private boolean hasTasks() {
		return (tasks.size()>0);
	}
	
	/*---------------------*/
	/* Setters and Getters */
	/*---------------------*/
	
	public Board getBoard() {
		return board;
	}
		
	public TreeMap<String, PlayerController> getControllers() {
		return controllers;
	}
	
	protected void updateFPS() {
		if (Game.getTime() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps + " Time: " + board.getTime());
			fps = 0;
			lastFPS = Game.getTime();
		}
		fps++;
	}
	
	public static long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public static void setSeed(int seed) {
		randomSeed = seed;
	}
	
	public static int getRandomSeed() {
		return randomSeed;
	}
	
	public static int getRandomInt(int max) {
		if(randomGenerator==null)
			randomGenerator = new Random(randomSeed);
		return randomGenerator.nextInt(max);
	}
}

