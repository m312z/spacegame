package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import model.building.Building;
import model.gameobject.Creature;
import model.map.FluidSimulator;
import model.map.GameMap;
import model.map.Light;
import model.map.TileType;
import utils.IntegerArrayComparitor;

/**
 * The main model.
 * @author Michael Cashmore
 */
public class Board {
	
	/* size of the board in terms of game size units */
	public static float[] BOARD_SIZE = {1500,1500};
	
	/* size of the board in terms of the grid */
	public static int[] GRID_SIZE = {50,50};
	public static float TILE_SIZE = BOARD_SIZE[0]/GRID_SIZE[0];
	
	/* epsilon distance for collision detection etc. */
	public static final float NUDGE = 0.01f;
	
	/* adjacency list for convenience */
	public static final Integer[][] ADJ_LIST = {{-1,0},{0,-1},{1,0},{0,1}};
	
	/* time-stamp for networking */
	public static final float MAX_TICK = 1f;
	float time;
	
	// timers
	public static final float GOO_TIME = 20f;
	float gooTimer = 0;
		
	/* internal game state */
	public enum BoardState {
		PLAY
	};
	BoardState state = BoardState.PLAY;
	BoardState lastState = BoardState.PLAY;
	
	/* model */
	GameMap map;
	ArrayList<Light> lights;
	
	Map<String,Creature> creatures;
	List<Creature> addCreatures;
	int creatureCount;
	
	/* buildings */
	Map<Integer[], Building> buildings;
	List<Integer[]> removeBuildingList;
	
	/**
	 * Constructor for a brand new, basic game.
	 */
	public Board() {
		map = new GameMap(GRID_SIZE[0],GRID_SIZE[1]);
		lights = new ArrayList<Light>();
		buildings = new TreeMap<Integer[], Building>(IntegerArrayComparitor.getInstance());
		creatures = new TreeMap<String, Creature>();
		addCreatures = new ArrayList<Creature>();
	}
	
	/** 
	 * Constructor with supplied map. This board may require additional setup.
	 * @param map	The map (visual only) to start with.
	 */
	public Board(GameMap map) {
		this.map = map;
		lights = new ArrayList<Light>();
		buildings = new TreeMap<Integer[], Building>(IntegerArrayComparitor.getInstance());
		creatures = new TreeMap<String, Creature>();
		addCreatures = new ArrayList<Creature>();
	}
			
	/* COPY METHOD */
	public Board clone() {
		
		Board board = new Board(map.clone());
		board.setCreatureCount(creatureCount);

		// timers
		board.setTime(time);
		board.setGooTimer(gooTimer);
		
		// lists
		for(Light l: lights) board.getLights().add(l.clone());
		for(Integer[] key: buildings.keySet()) board.getBuildings().put(key,buildings.get(key).clone());
		for(Creature c: addCreatures) board.getAddCreatures().add(c.clone());
		for(String mid: creatures.keySet()) board.getCreatures().put(mid, creatures.get(mid).clone());
		
		return board;
	}
	
	/*-------------*/
	/* main update */
	/*-------------*/
	
	/**
	 * @return false if the game is to continue.
	 */
	public boolean tick(float dt) {

		switch(state) {
		case PLAY:
			updateBoard(dt);
			break;
		}
		
		return false;
	}
	
	/**
	 * Update the board in a safe way by ticking between orders
	 * @param dt
	 */
	private void updateBoard(float dt) {

		while(dt>0) {
			
			float sdt = dt;
			if(sdt>MAX_TICK)
				sdt = MAX_TICK;
			
			dt -= sdt;
			time += sdt;
			
			// update goo
			gooTimer += sdt;
			while(gooTimer > GOO_TIME) {
				FluidSimulator.simulateStep(map);
				gooTimer = gooTimer - GOO_TIME;
			}

			// tick buildings
			Iterator<Integer[]> iit = buildings.keySet().iterator();
			while(iit.hasNext()) {
				Building b = buildings.get(iit.next());
				b.tick(this, sdt);
			}

			// tick creatures
			Iterator<Entry<String, Creature>> cit = creatures.entrySet().iterator();
			while(cit.hasNext()) {
				Entry<String, Creature> entry = cit.next();
				Creature c = entry.getValue();
				c.tick(this, sdt);
				if(c.isDead())
					cit.remove();
			}
			
			// add creatures
			for(Creature m: addCreatures) {
				creatures.put(m.getName()+"_"+creatureCount, m);
				creatureCount++;
			}
			addCreatures.clear();
		}
	}
	
	/*-----------*/
	/* buildings */
	/*-----------*/
	
	/**
	 * Attempt to build a building, first checking if the space is clear
	 * @param b	The building to build; containing its desired grid position.
	 */
	public void addBuilding(Building b) {
		
		// TODO remove debug
		Integer[] position = {b.getX(),b.getY()};
		if(buildings.containsKey(position)) {
			Building b1 = buildings.get(position);
			b1.setCurrentState((b1.getCurrentState()+1)%b1.getStateLabels().length);
		}
		
		// check if valid location
		if(map.getTile(b.getX(),b.getY()) != TileType.GROUND)
			return;
		
		map.getMap()[b.getX()][b.getY()] = b.getTileType();
		
		if(b.getTileType().hasTick) {
			Integer[] pos = new Integer[] {b.getX(), b.getY()};
			buildings.put(pos, b);
		}
		
		b.place(this);
		b.activate(this);
	}
	
	/**
	 * Attempt to remove a building, first deactivating it.
	 * @param position	the grid position of the building to remove.
	 */
	public void removeBuilding(Integer[] position) {
		if(buildings.containsKey(position)) {
			Building b = buildings.get(position);
			b.deActivate(this);
			b.remove(this);
			buildings.remove(position);
		}
		map.getMap()[position[0]][position[1]] = TileType.GROUND;
	}
	
	/**
	 * Attempt to activate/deactivate a building.  May have no
	 * effect it the costs are not met. 
	 * @param position	the grid position of the building to toggle.
	 */
	public void toggleBuilding(Integer[] position) {
		Building b = buildings.get(position);
		if(!b.isActive()) b.activate(this);
		else b.deActivate(this);
	}
	
	/*---------------------*/
	/* setters and getters */
	/*---------------------*/
		
	/* map and model */
	
	public float getTime() {
		return time;
	}
	
	public void setTime(float time) {
		this.time = time;
	}

	public void setGooTimer(float gooTimer) {
		this.gooTimer = gooTimer;
	}
	
	public GameMap getMap() {
		return map;
	}

	public ArrayList<Light> getLights() {
		return lights;
	}
	
	public Map<Integer[], Building> getBuildings() {
		return buildings;
	}
	
	public Map<String, Creature> getCreatures() {
		return creatures;
	}
	
	public List<Creature> getAddCreatures() {
		return addCreatures;
	}
	
	public void setCreatureCount(int creatureCount) {
		this.creatureCount = creatureCount;
	}
}
