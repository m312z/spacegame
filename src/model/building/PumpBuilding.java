package model.building;

import static model.Board.ADJ_LIST;
import model.Board;
import model.map.FluidSimulator;
import model.map.TileType;

public class PumpBuilding extends Building {

	static final float PUMP_DURATION = 3000f;
	static final float PUMP_START = 2000f;
	
	int amount;
	float internal;
	float timer;
	
	public PumpBuilding(TileType type, int x, int y) {
		super(TileType.PUMP, x, y);
		internal = 0f;
		timer = 0.0f;
		amount = FluidSimulator.minFlow;
		noStates = 12;
		currentState = 0;
		stateLabels = new String[12];
		stateLabels[0] = "left_to_top";
		stateLabels[1] = "left_to_right";
		stateLabels[2] = "left_to_bottom";
		stateLabels[3] = "top_to_right";
		stateLabels[4] = "top_to_bottom";
		stateLabels[5] = "top_to_left";
		stateLabels[6] = "right_to_bottom";
		stateLabels[7] = "right_to_left";
		stateLabels[8] = "right_to_top";
		stateLabels[9] = "bottom_to_left";
		stateLabels[10] = "bottom_to_top";
		stateLabels[11] = "bottom_to_right";
	}

	/**
	 * Clone method for synchronization.
	 */
	public Building clone() {
		
		PumpBuilding clone = new PumpBuilding(tileType,x,y);
		setValues(clone);
		
		clone.setAmount(amount);
		clone.setInternal(internal);
		clone.setTimer(timer);
		return clone;
	}

	@Override
	public boolean tick(Board board, float dt) {
		timer += dt;
		if(timer > PUMP_START)
			pump(board, dt);
		if(timer > PUMP_DURATION)
			timer -= PUMP_DURATION;
		return false;
	}
	
	private void pump(Board board, float dt) {
				
		if(!hasPower(board)) return;
		
		// calculate cells for transfer
		int intake = currentState/3;
		int output = (intake + 1 + currentState%3) % 4;
		
		int inCellX = x + ADJ_LIST[intake][0];
		int inCellY = y + ADJ_LIST[intake][1];
		int outCellX = x + ADJ_LIST[output][0];
		int outCellY = y + ADJ_LIST[output][1];
		
		// make sure everything is on map
		if(inCellX<0 || inCellX>=Board.GRID_SIZE[0]) return;
		if(inCellY<0 || inCellY>=Board.GRID_SIZE[1]) return;
		if(outCellX<0 || outCellX>=Board.GRID_SIZE[0]) return;
		if(outCellY<0 || outCellY>=Board.GRID_SIZE[1]) return;
		
		// make sure everything is empty space
		if(board.getMap().getTile(inCellX,inCellY).solid
	    		&& board.getMap().getTile(inCellX,inCellY)!=TileType.GRATING
	    		&& board.getMap().getTile(inCellX,inCellY)!=TileType.VARGRATING)
			return;
		if(board.getMap().getTile(outCellX,outCellY).solid
	    		&& board.getMap().getTile(outCellX,outCellY)!=TileType.GRATING
	    		&& board.getMap().getTile(outCellX,outCellY)!=TileType.VARGRATING)
			return;
		
		// move goo
		if(board.getMap().getGooMass()[inCellX][inCellY]>amount*dt) {
			
			board.getMap().getGooMass()[outCellX][outCellY] += (int)(amount*dt);
			board.getMap().getGooMass()[inCellX][inCellY] -= (int)(amount*dt);
			
			// check in case of small ticks
			internal += (amount*dt) - (int)(amount*dt);
			while(internal>1) {
				board.getMap().getGooMass()[inCellX][inCellY]++;
				internal--;
			}
		}
	}
	
	/*---------------------*/
	/* getters and setters */
	/*---------------------*/
		
	public int getAmount() {
		return amount;
	}
	
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	public float getInternal() {
		return internal;
	}
	
	public void setInternal(float internal) {
		this.internal = internal;
	}
	
	public void setTimer(float timer) {
		this.timer = timer;
	}
}
