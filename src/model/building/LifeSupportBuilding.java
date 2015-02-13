package model.building;

import model.Board;

public class LifeSupportBuilding extends Building {

	int range;
	
	public LifeSupportBuilding(BuildingType type, int x, int y, int gooRequired) {
		super(BuildingType.LIFE_SUPPORT, x, y);
		range = 3;
		noStates = 1;
		currentState = 0;
		stateLabels = new String[1];
		stateLabels[0] = "generate";
		this.gooRequired = gooRequired;
	}

	/**
	 * Clone method for synchronization.
	 */
	public Building clone() {
		LifeSupportBuilding clone = new LifeSupportBuilding(tileType,x,y,gooRequired);
		clone.setRange(range);
		setValues(clone);
		return clone;
	}	
	
	@Override
	protected boolean concreteTick(Board board, float dt) {
		return false;
	}
	
	@Override
	public void activate(Board board) {
		if(active) return;
		
		int popIncrease = 0;
		
		for(int i=-range+1;i<range;i++) {
		for(int j=-range+1;j<range;j++) {
			if(x+i<0 || x+i>=board.getMap().getSize()[0]) continue;
			if(y+j<0 || y+j>=board.getMap().getSize()[1]) continue;
			if(board.getMap().getLifeSupport()[x+i][y+j]==0) popIncrease++;
			board.getMap().getLifeSupport()[x+i][y+j]++;
		}};
		
		board.setPopulationCapacity(board.getPopulationCapacity() + popIncrease);
		
		active = true;
	}
	
	@Override
	public void deActivate(Board board) {
		if(!active) return;
		
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
		
		active = false;
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
