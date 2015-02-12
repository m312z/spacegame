package model.building;

import model.Board;
import model.map.TileType;

public class LifeSupportBuilding extends Building {

	int range;
	
	public LifeSupportBuilding(TileType type, int x, int y) {
		super(TileType.LIFESUPPORT, x, y);
		
		noStates = 1;
		currentState = 0;
		stateLabels = new String[1];
		stateLabels[0] = "generate";
	}

	/**
	 * Clone method for synchronization.
	 */
	public Building clone() {
		LifeSupportBuilding clone = new LifeSupportBuilding(tileType,x,y);
		clone.setRange(range);
		setValues(clone);
		return clone;
	}
	

	@Override
	public void activate(Board board) {
		if(active) return;
		
		for(int i=-range+1;i<range;i++) {
		for(int j=-range+1;j<range;j++) {
			if(x+i<0 || x+i>=board.getMap().getSize()[0]) continue;
			if(y+j<0 || y+j>=board.getMap().getSize()[1]) continue;
			board.getMap().getPower()[x+i][y+j]++;
		}};
		
		active = true;
	}
	

	@Override
	public void deActivate(Board board) {
		if(!active) return;
		
		for(int i=-range+1;i<range;i++) {
			for(int j=-range+1;j<range;j++) {
				if(x+i<0 || x+i>=board.getMap().getSize()[0]) continue;
				if(y+j<0 || y+j>=board.getMap().getSize()[1]) continue;
				if(board.getMap().getPower()[x+i][y+j]>0) 
					board.getMap().getPower()[x+i][y+j]--;
			}};
		
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
