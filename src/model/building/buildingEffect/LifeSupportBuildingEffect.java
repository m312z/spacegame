package model.building.buildingEffect;

import model.Board;
import model.building.Building;

public class LifeSupportBuildingEffect extends BuildingEffect {

	int range;
	
	public LifeSupportBuildingEffect(int range) {
		this.range = range;
	}
	
	@Override
	public BuildingEffect clone() {
		LifeSupportBuildingEffect clone = new LifeSupportBuildingEffect(range);
		return clone;
	}

	@Override
	public void tick(Board board, Building b, float dt) {
		
	}

	@Override
	public void deActivate(Board board, Building b) {
		int x = b.getX();
		int y = b.getY();
		int popDecrease = 0;
		for(int i=-range+1;i<range;i++) {
		for(int j=-range+1;j<range;j++) {
			if(x+i<0 || x+i>=board.getMap().getSize()[0]) continue;
			if(y+j<0 || y+j>=board.getMap().getSize()[1]) continue;
			if(board.getMap().getLifeSupport()[x+i][y+j]>0) 
				board.getMap().getLifeSupport()[x+i][y+j]--;
			if(board.getMap().getLifeSupport()[x+i][y+j]==0) popDecrease++;
		}};
		board.setPopulationCapacity(board.getPopulationCapacity() - popDecrease);
	}

	@Override
	public void activate(Board board, Building b) {
		int x = b.getX();
		int y = b.getY();
		int popIncrease = 0;
		for(int i=-range+1;i<range;i++) {
		for(int j=-range+1;j<range;j++) {
			if(x+i<0 || x+i>=board.getMap().getSize()[0]) continue;
			if(y+j<0 || y+j>=board.getMap().getSize()[1]) continue;
			if(board.getMap().getLifeSupport()[x+i][y+j]==0) popIncrease++;
			board.getMap().getLifeSupport()[x+i][y+j]++;
		}};
		board.setPopulationCapacity(board.getPopulationCapacity() + popIncrease);
	}

	/*---------------------*/
	/* getters and setters */
	/*---------------------*/
	
	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}
}
