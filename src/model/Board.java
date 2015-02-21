package model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import model.building.Building;
import model.building.BuildingType;
import model.gameobject.Cart;
import model.map.FluidSimulator;
import model.map.GameMap;
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
	List<Cart> carts;
	List<Cart> addCarts;
	
//	ArrayList<Light> lights;
	
//	Map<String,Creature> creatures;
//	List<Creature> addCreatures;
//	int creatureCount;
	
	int populationCapacity;
	int population;
	
	/* buildings */
	Map<Integer[], Building> buildings;
	List<Integer[]> removeBuildingList;
	
	/**
	 * Constructor for a brand new, basic game.
	 */
	public Board() {
		map = new GameMap(GRID_SIZE[0],GRID_SIZE[1]);

		buildings = new TreeMap<Integer[], Building>(IntegerArrayComparitor.getInstance());
		for(Building b: GameMap.startBuildings) {
			buildings.put(new Integer[] {b.getX(), b.getY()},b);
			b.place(this);
			b.activate(this);
		}
		
		carts = new ArrayList<Cart>();
		addCarts = new ArrayList<Cart>();
	}
	
	/** 
	 * Constructor with supplied map. This board may require additional setup.
	 * @param map	The map (visual only) to start with.
	 */
	public Board(GameMap map) {
		this.map = map;
		buildings = new TreeMap<Integer[], Building>(IntegerArrayComparitor.getInstance());
		carts = new ArrayList<Cart>();
		addCarts = new ArrayList<Cart>();
	}
			
	/* COPY METHOD */
	public Board clone() {
		
		Board board = new Board(map.clone());
//		board.setCreatureCount(creatureCount);

		// timers
		board.setTime(time);
		board.setGooTimer(gooTimer);
		
		// variables
		board.setPopulation(population);
		board.setPopulationCapacity(populationCapacity);

		// lists
//		for(Light l: lights) board.getLights().add(l.clone());
		for(Integer[] key: buildings.keySet()) board.getBuildings().put(key,buildings.get(key).clone());
		for(Cart c: addCarts) board.getAddCarts().add(c.clone());
		for(Cart c: carts) board.getCarts().add(c.clone());
//		for(String mid: creatures.keySet()) board.getCreatures().put(mid, creatures.get(mid).clone());
		
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
			
			// tick carts
			Iterator<Cart> cit = carts.iterator();
			while(cit.hasNext()) {
				Cart c = cit.next();
				c.move(this, dt);
			}
			
			// add carts
			for(Cart c: addCarts)
				carts.add(c);
			addCarts.clear();
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
		
		// check if valid location
		if(map.getTile(b.getX(),b.getY()) != BuildingType.BASE_HABITAT
				&& (map.getTile(b.getX(),b.getY()) != BuildingType.EMPTY || b.getTileType() != BuildingType.BASE_HABITAT))
			return;
		
		map.getMap()[b.getX()][b.getY()] = b.getTileType();
		
		if(b.getTileType().hasBuilding) {
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
		map.getMap()[position[0]][position[1]] = BuildingType.BASE_HABITAT;
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

	public Map<Integer[], Building> getBuildings() {
		return buildings;
	}

	public int getPopulation() {
		return population;
	}
	
	public void setPopulation(int population) {
		this.population = population;
	}
	
	public int getPopulationCapacity() {
		return populationCapacity;
	}
	
	public void setPopulationCapacity(int populationCapacity) {
		this.populationCapacity = populationCapacity;
	}
	
	public List<Cart> getCarts() {
		return carts;
	}
	
	public List<Cart> getAddCarts() {
		return addCarts;
	}
}
