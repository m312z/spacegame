package core.controller;

import static core.Frame.SCREEN_SIZE;
import static gui.GameGUI.VIEW_SIZE;
import static model.Board.TILE_SIZE;
import gui.GameGUI;
import gui.HudFactory;
import gui.ui.HudElement;
import gui.ui.HudOverlay;

import java.util.List;

import model.Board;
import model.building.BuildingType;
import network.packet.PlayerInputPacket;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import phys.Point2D;
import core.Frame;

public class MouseController extends PlayerController {

	static final int MOUSE_MOVE = 1;
	static final int MOUSE_ACT = 0;
		
	Point2D oldCursorPos;	
	Point2D gameCursorPos;
	
	boolean[] hotMouseDown = new boolean[2];
	boolean[] hotMouseUp = new boolean[2];
	boolean dragging = false;
	
	BuildingType selectedBuilding;
	boolean building = false;
	boolean gooing = false;
	
	public MouseController(String playerID) {
		super(playerID);
		oldCursorPos = new Point2D();
		gameCursorPos = new Point2D();
	}
	
	@Override
	public void pollInput(Board board, HudOverlay overlay) {
		
		updateCursorPosition();
		hudInteraction(board, overlay);
				
		if(overlay.isEventEaten()) {
			for(int i=0;i<2;i++) {
				hotMouseDown[i] = false;
				hotMouseUp[i]= false;
			}
			dragging = false;
		} else {
			adjustView();
			if(!overlay.isOnHuD(cursorPosition.x, cursorPosition.y))
				gameInteraction(board,overlay);
		}
		
		if(!Mouse.isButtonDown(MOUSE_ACT)) hotMouseDown[MOUSE_ACT] = true;
		if(Mouse.isButtonDown(MOUSE_ACT)) hotMouseUp[MOUSE_ACT] = true;
		if(!Mouse.isButtonDown(MOUSE_MOVE)) hotMouseDown[MOUSE_MOVE] = true;
		if(Mouse.isButtonDown(MOUSE_MOVE)) hotMouseUp[MOUSE_MOVE] = true;
	}
	
	/**
	 * poll the mouse and get the new cursor position
	 */
	private void updateCursorPosition() {
		
		// cursor position on screen
		oldCursorPos.x = cursorPosition.x;
		oldCursorPos.y = cursorPosition.y;
		cursorPosition.x = Mouse.getX();
		cursorPosition.y = Frame.SCREEN_SIZE[1] - Mouse.getY();

		// cursor position in game
		gameCursorPos.x = cursorPosition.x/GameGUI.scale + GameGUI.viewAnchor.x;
		gameCursorPos.y = cursorPosition.y/GameGUI.scale + GameGUI.viewAnchor.y;
	}
	
	/**
	 * Interact with the HUD overlay
	 * @param board
	 * @param overlay
	 */
	private void hudInteraction(Board board, HudOverlay overlay) {
		
		BuildingType bt;
		HudElement e;
		
		// interacting with the overlay
		List<String> hudCommands = overlay.pollInput();
		for(String command: hudCommands) {
			String[] s = command.split("/");
			switch(s[0]) {
			case "hideTooltip":
				// hide building button tool-tip
				bt = BuildingType.valueOf(s[1]);
				e = overlay.getElement("toolTip_"+bt.name());
				if(e!=null) e.setVisible(false);
				break;

			case "showTooltip":
				// show building button tool-tip
				bt = BuildingType.valueOf(s[1]);
				e = overlay.getElement("toolTip_"+bt.name());
				if(e!=null) e.setVisible(true);
				break;

			case "showBuild":
				// open building category
				building = false;
				for(BuildingType type: BuildingType.values()) {
					e = overlay.getElement("buttonBuild_"+type.name());
					if(e!=null) e.setVisible(true);
				}
				break;

			case "build":
				// select building to build
				building = true;
				selectedBuilding = BuildingType.valueOf(s[1]);
				break;

			case "remove":
				// start deleting buildings
				building = false;
				gooing = false;
				for(BuildingType type: BuildingType.values()) {
					e = overlay.getElement("buttonBuild_"+type.name());
					if(e!=null) e.setVisible(false);
				}
				break;
				
			case "goo":
				// start deleting buildings
				building = false;
				gooing = true;
				for(BuildingType type: BuildingType.values()) {
					e = overlay.getElement("buttonBuild_"+type.name());
					if(e!=null) e.setVisible(false);
				}
				break;
			}	
		}
		
		// HUD has eaten the event
		if(overlay.isEventEaten()) {
			hotMouseDown[MOUSE_ACT] = false;
			hotMouseUp[MOUSE_ACT] = false;
			hotMouseDown[MOUSE_MOVE] = false;
			hotMouseUp[MOUSE_MOVE] = false;
			return;
		}
	}
	
	/**
	 * zoom and pan the view
	 */
	private void adjustView() {
				
		// adjusting the view zoom
		int scroll = 0;
		if(Mouse.hasWheel()) {
			scroll = Mouse.getDWheel();
			scroll = -20*Integer.signum(scroll);
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_EQUALS)) scroll = -10;
		if(Keyboard.isKeyDown(Keyboard.KEY_MINUS)) scroll = 10;
		if(Keyboard.isKeyDown(Keyboard.KEY_PRIOR)) scroll = -10;
		if(Keyboard.isKeyDown(Keyboard.KEY_NEXT)) scroll = 10;
		if(scroll!=0) {
			
			float centerX = GameGUI.viewAnchor.x + VIEW_SIZE[0]/2f;
			float centerY = GameGUI.viewAnchor.y + VIEW_SIZE[1]/2f;
			VIEW_SIZE[0] += scroll * SCREEN_SIZE[0]/1280f;
			if(VIEW_SIZE[0] < Board.TILE_SIZE*6)
				VIEW_SIZE[0] = Board.TILE_SIZE*6;
			if(VIEW_SIZE[0] > Board.TILE_SIZE*30)
				VIEW_SIZE[0] = Board.TILE_SIZE*30;
			VIEW_SIZE[1] = SCREEN_SIZE[1]/(SCREEN_SIZE[0]/VIEW_SIZE[0]);
			
			GameGUI.desiredViewAnchor.x = GameGUI.viewAnchor.x = centerX - VIEW_SIZE[0]/2f;
			GameGUI.desiredViewAnchor.y = GameGUI.viewAnchor.y = centerY - VIEW_SIZE[1]/2f;
		}
		
		// adjusting the view position
		scroll = 0;
		if((hotMouseDown[MOUSE_MOVE] || dragging) && Mouse.isButtonDown(MOUSE_MOVE)) {

			GameGUI.viewAnchor.x += (oldCursorPos.x - cursorPosition.x)/GameGUI.scale;
			GameGUI.viewAnchor.y += (oldCursorPos.y - cursorPosition.y)/GameGUI.scale;
			GameGUI.desiredViewAnchor.x = GameGUI.viewAnchor.x = 
					clampCursor(GameGUI.viewAnchor.x, -GameGUI.BORDER_SIZE, Board.BOARD_SIZE[0] + GameGUI.BORDER_SIZE - VIEW_SIZE[0]);
			GameGUI.desiredViewAnchor.y = GameGUI.viewAnchor.y = 
					clampCursor(GameGUI.viewAnchor.y, -GameGUI.BORDER_SIZE, Board.BOARD_SIZE[1] + GameGUI.BORDER_SIZE - VIEW_SIZE[1]);
			
			dragging = true;
			hotMouseDown[MOUSE_MOVE] = false;
			
		} else {
			dragging = false;
		}
	}
	
	/**
	 * Interact directly with the game board
	 * @param board
	 * @param overlay
	 */
	private void gameInteraction(Board board, HudOverlay overlay) {
		
//		Player player = null;
//		for(Player p: board.getPlayers()) {
//			if(p.getPlayerID().equals(playerID))
//				player = p;
//		}
//		
//		if(player==null) return;
		
		if(hotMouseDown[MOUSE_ACT] && Mouse.isButtonDown(MOUSE_ACT) && inScreen(gameCursorPos)) {

			int tileX = (int)(gameCursorPos.x / TILE_SIZE);
			int tileY = (int) (gameCursorPos.y / TILE_SIZE);
			
			if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && board.getMap().getTile(tileX, tileY)!=BuildingType.BASE_HABITAT) {
//				// toggle building
//				if(board.getBuildings().get(pos).getType().toggleable) {
//					PlayerInputPacket packet = new PlayerInputPacket(this, "A_"+selectedBuilding.id+"_"+tileX+"_"+tileY, board.getTime());
//					commands.add(packet);
//				}
			} else if(building) {
				// place building	
				PlayerInputPacket packet = new PlayerInputPacket(this, "B/"+selectedBuilding.id+"/"+tileX+"/"+tileY, board.getTime());
				commands.add(packet);
			} else if(gooing) {
				// test things
				PlayerInputPacket pip = new PlayerInputPacket(this, "GOO/"+tileX+"/"+tileY, board.getTime());
				commands.add(pip);
			} else if(board.getBuildings().containsKey(new Integer[] {tileX,tileY})) {
				// building menu
				overlay.setToolTip(HudFactory.makeBuildingToolTip(board, Mouse.getX(), SCREEN_SIZE[1]-Mouse.getY(), board.getBuildings().get(new Integer[] {tileX,tileY})));
			} else {
				// remove building
				PlayerInputPacket packet = new PlayerInputPacket(this, "R/"+tileX+"/"+tileY, board.getTime());
				commands.add(packet);
			}
			
			// mouse pressed
			hotMouseDown[MOUSE_ACT] = false;
			
		} else if(Mouse.isButtonDown(MOUSE_ACT) && inScreen(gameCursorPos)) {
									
		} else if(!Mouse.isButtonDown(MOUSE_ACT)) {
			
		}
	}

	/*---------------------*/
	/* setters and getters */
	/*---------------------*/
	
	@Override
	public boolean isSelecting() {
		return Mouse.isButtonDown(0);
	}
		
	@Override
	public boolean isPausing() {
		return false;
	}

	public boolean isBuilding() {
		return building;
	}
	
	@Override
	public Point2D getGameCursorPos() {
		return gameCursorPos;
	}
}
