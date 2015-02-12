package core.controller;

import gui.ui.HudOverlay;

import java.util.ArrayList;
import java.util.List;

import model.Board;
import network.packet.Packet;
import phys.Point2D;

public abstract class PlayerController {

	String playerID;

	protected Point2D cursorPosition;
	protected List<Packet> commands;
	
	public PlayerController(String playerID) {
		this.playerID = playerID;
		this.cursorPosition = new Point2D();
		this.commands = new ArrayList<Packet>(2);
	}

	public abstract void pollInput(Board board, HudOverlay overlay);
	
	/*---------------------*/
	/* setters and getters */
	/*---------------------*/
	
	public abstract boolean isSelecting();
	public abstract boolean isPausing();
	
	public String getPlayerID() {
		return playerID;
	}
	
	public void setPlayer(String playerID) {
		this.playerID = playerID;
	}
		
	public Point2D getCursorPosition() {
		return cursorPosition;
	}
		
	public List<Packet> getCommands() {
		return commands;
	}
	
	public abstract Point2D getGameCursorPos();
	
	/*---------*/
	/* utility */
	/*---------*/
	
	protected boolean inScreen(Point2D pos) {
		return (pos.x>=0 && pos.x<Board.BOARD_SIZE[0] && pos.y>=0 && pos.y<Board.BOARD_SIZE[1]);
	}
	
	protected float clampCursor(float value, float min, float max) {
		if(value>max) return max;
		if(value<min) return min;
		return value;
	}
}
