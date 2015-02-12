package model.building;

import static model.Board.ADJ_LIST;
import model.Board;
import model.map.TileType;
import phys.Point2D;

public class Building {
		
	TileType tileType;
	int x;
	int y;
	Point2D pos;
	boolean active;
	float maxHealth;
	float health;
	
	int noStates;
	int currentState;
	String[] stateLabels;
		
	public Building(TileType type, int x, int y) {
		this.tileType = type;
		this.x = x;
		this.y = y;
		this.pos = new Point2D((x+0.5f)*Board.TILE_SIZE,(y+0.5f)*Board.TILE_SIZE);
		this.active = false;
		this.maxHealth = this.health = 100;
		this.noStates = 1;
		this.currentState = 0;
		this.stateLabels = new String[noStates];
		stateLabels[0] = "";
	}

	/*---------*/
	/* cloning */
	/*---------*/
	
	/**
	 * Clone method for synchronization.
	 */
	public Building clone() {
		Building clone = new Building(tileType,x,y);
		setValues(clone);
		return clone;
	}
	
	/**
	 * Set building values, should be called by subclass clone methods
	 */
	public void setValues(Building clone) {
		clone.setActive(active);
		clone.setHealth(health);
		
		clone.setNoStates(noStates);
		clone.setCurrentState(currentState);
		String[] cloneStateLabels = new String[noStates];
		for(int i=0;i<noStates;i++) cloneStateLabels[i] = stateLabels[i];
		clone.setStateLabels(cloneStateLabels);
	}
	
	/*----------*/
	/* activity */
	/*----------*/
	
	/**
	 * Update the building by dt
	 * @return whether the building should be removed
	 */
	public boolean tick(Board board, float dt) {
		return false;
	}
	
	/**
	 * Attempt to activate this tile
	 * @param board	the model
	 */
	public void activate(Board board) {		
		if(active) return;
		active = true;
	}
	
	/**
	 * Deactivate this tile;
	 * @param board	the model
	 */
	public void deActivate(Board board) {
		if(!active) return;
		active = false;
	}
				
	/* might put a new mechanic here */
	public void place(Board board) { }
	public void remove(Board board) { }

	/*----------------------*/
	/* power and population */
	/*----------------------*/
	
	public boolean hasPower(Board board) {
		for(int i=0;i<ADJ_LIST.length;i++) {
			if(board.getMap().getGooMass()[x+ADJ_LIST[i][0]][y+ADJ_LIST[i][1]]>0)
				return true;
		}
		return false;
	}
		
	/*--------------*/
	/* state change */
	/*--------------*/
	
	public void outputState(Board board, String stateCommand) {
		// send signal
		for(int i=0;i<Board.ADJ_LIST.length;i++) {
			Integer[] position = {x+Board.ADJ_LIST[i][0],y+Board.ADJ_LIST[i][1]};
			if(board.getBuildings().containsKey(position)) {
				Building b = board.getBuildings().get(position);
				b.inputState(board, stateCommand);
			}	
		}
	}

	public void inputState(Board board, String stateCommand) {
		// received signal
		for(int i=0;i<noStates;i++) {
			if(stateLabels[i].equals(stateCommand))
				currentState = i;
		}
	}
	
	/*---------------------*/
	/* getters and setters */
	/*---------------------*/
	
	public TileType getTileType() {
		return tileType;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Point2D getPos() {
		return pos;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
		
	public void setHealth(float health) {
		this.health = health;
		if(this.health < 0)
			this.health = 0;
	}
	
	public float getHealth() {
		return health;
	}
	
	public float getMaxHealth() {
		return maxHealth;
	}
	
	public String[] getStateLabels() {
		return stateLabels;
	}
	
	public int getCurrentState() {
		return currentState;
	}
	
	public void setCurrentState(int currentState) {
		this.currentState = currentState;
	}
	
	public void setStateLabels(String[] stateLabels) {
		this.stateLabels = stateLabels;
	}
	
	public void setNoStates(int noStates) {
		this.noStates = noStates;
	}
}
