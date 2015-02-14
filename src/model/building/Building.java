package model.building;

import static model.Board.ADJ_LIST;

import java.util.ArrayList;
import java.util.List;

import model.Board;
import model.building.buildingEffect.BuildingEffect;
import phys.Point2D;

public class Building {

	BuildingType tileType;
	int x;
	int y;
	Point2D pos;
	boolean active;
	float maxHealth;
	float health;
	
	int noStates;
	int currentState;
	String[] stateLabels;
	
	int gooRequired;
	int gooDirection;
	int populationRequired;
		
	List<BuildingEffect> buildingEffects;
	
	public Building(BuildingType type, int x, int y) {
		
		this.tileType = type;
		this.x = x;
		this.y = y;
		this.pos = new Point2D((x+0.5f)*Board.TILE_SIZE,(y+0.5f)*Board.TILE_SIZE);
		this.active = false;
		
		this.maxHealth = this.health = 100;
		this.populationRequired = 0;
		this.gooRequired = 0;
		this.gooDirection = 0;
		
		this.noStates = 1;
		this.currentState = 0;
		this.stateLabels = new String[noStates];
		stateLabels[0] = "";
		
		buildingEffects = new ArrayList<BuildingEffect>();
	}

	/*---------*/
	/* cloning */
	/*---------*/
	
	/**
	 * Clone method for synchronization.
	 */
	public Building clone() {
		Building clone = new Building(tileType, x, y);
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
		
		clone.setPopulationRequired(populationRequired);
		clone.setGooRequired(gooRequired);
		clone.setGooDirection(gooDirection);
		
		for(BuildingEffect e: buildingEffects)
			clone.addEffect(e.clone());
	}
	
	/*----------*/
	/* activity */
	/*----------*/
	
	/**
	 * Update the building by dt
	 * @return whether the building should be removed
	 */
	public boolean tick(Board board, float dt) {
		
		if(active && hasPower(board)) {
			board.getMap().getGooMass()[x+ADJ_LIST[gooDirection][0]][y+ADJ_LIST[gooDirection][1]] -= gooRequired;
			return concreteTick(board, dt);
		} else if(active)
			deActivate(board);
		
		return false;
	}
	
	private boolean concreteTick(Board board, float dt) {
		for(BuildingEffect b: buildingEffects)
			b.tick(board,this,dt);
		return false;
	}
	
	/**
	 * Attempt to activate this tile
	 * @param board	the model
	 */
	public void activate(Board board) {
		if(active) return;
		for(BuildingEffect b: buildingEffects)
			b.activate(board,this);
		active = true;
	}
	
	/**
	 * Deactivate this tile;
	 * @param board	the model
	 */
	public void deActivate(Board board) {
		if(!active) return;
		for(BuildingEffect b: buildingEffects)
			b.deActivate(board,this);
		active = false;
	}
				
	/* might put a new mechanic here */
	public void place(Board board) { }
	public void remove(Board board) { }

	/*----------------------*/
	/* power and population */
	/*----------------------*/
	
	public boolean hasPower(Board board) {
		return (board.getMap().getGooMass()[x+ADJ_LIST[gooDirection][0]][y+ADJ_LIST[gooDirection][1]]>gooRequired);
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
	
	public BuildingType getTileType() {
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
	
	public int getGooRequired() {
		return gooRequired;
	}
	
	public int getPopulationRequired() {
		return populationRequired;
	}
	
	public void setPopulationRequired(int populationRequired) {
		this.populationRequired = populationRequired;
	}
	
	public void setGooRequired(int gooRequired) {
		this.gooRequired = gooRequired;
	}
	
	public int getGooDirection() {
		return gooDirection;
	}
	
	public void setGooDirection(int gooDirection) {
		this.gooDirection = gooDirection;
	}
	
	public void addEffect(BuildingEffect effect) {
		buildingEffects.add(effect);
	}
}
