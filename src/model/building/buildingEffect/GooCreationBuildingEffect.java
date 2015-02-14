package model.building.buildingEffect;

import static model.Board.ADJ_LIST;
import model.Board;
import model.building.Building;
import model.building.BuildingType;

public class GooCreationBuildingEffect extends BuildingEffect {
	
	int amount;
	int output;
	
	public GooCreationBuildingEffect(int amount) {
		this.amount = amount;
		this.output = 2;
	}

	@Override
	public BuildingEffect clone() {
		PumpBuildingEffect pbe = new PumpBuildingEffect(amount);
		pbe.setAmount(amount);
		pbe.setOutput(output);
		return pbe;
	}

	@Override
	public void tick(Board board, Building b, float dt) {

		int x = b.getX();
		int y = b.getY();
		
		int outCellX = x + ADJ_LIST[output][0];
		int outCellY = y + ADJ_LIST[output][1];
		
		// make sure everything is on map
		if(outCellX<0 || outCellX>=Board.GRID_SIZE[0]) return;
		if(outCellY<0 || outCellY>=Board.GRID_SIZE[1]) return;
		
		// make sure everything is empty space
		if(board.getMap().getTile(outCellX,outCellY).solid
	    		&& board.getMap().getTile(outCellX,outCellY)!=BuildingType.GRATING
	    		&& board.getMap().getTile(outCellX,outCellY)!=BuildingType.VARIABLE_GRATING)
			return;
		
		// make goo
		board.getMap().getGooMass()[outCellX][outCellY] += (int)(amount*dt);
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

	public void setOutput(int output) {
		this.output = output;
	}
}
