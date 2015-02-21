package model.building;

public class RailBuilding extends Building {

	int exit;
	
	public RailBuilding(BuildingType type, int x, int y) {
		super(type, x, y);
		exit = 0;
	}
	
	@Override
	public Building clone() {
		RailBuilding clone = new RailBuilding(tileType, x, y);
		setValues(clone);
		clone.setExit(exit);
		return clone;
	};

	/*---------------------*/
	/* setters and getters */
	/*---------------------*/
	
	public int getExit() {
		return exit;
	}

	public void setExit(int exit) {
		this.exit = exit;
	}
}
