package model.building.buildingEffect;

import static model.Board.ADJ_LIST;
import model.Board;
import model.building.Building;
import model.building.BuildingType;

public class PumpBuildingEffect extends BuildingEffect {

	static final float PUMP_DURATION = 3000f;
	static final float PUMP_START = 2000f;
	
	int amount;
	float internal;
	float timer;
	int intake;
	int output;
	
	public PumpBuildingEffect(int amount) {
		this.amount = amount;
		this.internal = 0;
		this.timer = 0;
		this.intake = 0;
		this.output = 2;
	}

	@Override
	public BuildingEffect clone() {
		PumpBuildingEffect pbe = new PumpBuildingEffect(amount);
		pbe.setAmount(amount);
		pbe.setInternal(internal);
		pbe.setTimer(timer);
		pbe.setIntake(intake);
		pbe.setOutput(output);
		return pbe;
	}

	@Override
	public void tick(Board board, Building b, float dt) {
		timer += dt;
		if(timer > PUMP_START)
			pump(board, b, dt);
		if(timer > PUMP_DURATION)
			timer -= PUMP_DURATION;
	}
	
	private void pump(Board board, Building b, float dt) {

		int x = b.getX();
		int y = b.getY();
		
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
	    		&& board.getMap().getTile(inCellX,inCellY)!=BuildingType.GRATING
	    		&& board.getMap().getTile(inCellX,inCellY)!=BuildingType.VARIABLE_GRATING)
			return;
		if(board.getMap().getTile(outCellX,outCellY).solid
	    		&& board.getMap().getTile(outCellX,outCellY)!=BuildingType.GRATING
	    		&& board.getMap().getTile(outCellX,outCellY)!=BuildingType.VARIABLE_GRATING)
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

	@Override
	public void deActivate(Board board, Building b) {
			
	}

	@Override
	public void activate(Board board, Building b) {
		
	}

	/*---------------------*/
	/* getters and setters */
	/*---------------------*/
	
	public void setAmount(int amount) {
		this.amount = amount;
	}

	public void setInternal(float internal) {
		this.internal = internal;
	}

	public void setTimer(float timer) {
		this.timer = timer;
	}

	public void setIntake(int intake) {
		this.intake = intake;
	}

	public void setOutput(int output) {
		this.output = output;
	}
}
