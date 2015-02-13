package model.building;


public enum BuildingType {
	
	EMPTY(				0,  false,	false),
	BASE_HABITAT(		1,  false,	false),
	WALL(				2,  true,	false),
	REINFORCED_WALL(	3,  true,	false),
	PUMP(				4,  true,	true),
	GRATING(			5,  true,	false),
	VARIABLE_GRATING(	6,  true,	false),
	LIFE_SUPPORT(		7, 	true, 	true),
	COMMAND_TOWER(		8, 	true, 	true);
	
	public int id;
	public boolean solid;
	public boolean hasBuilding;
	
	private BuildingType(int id, boolean solid, boolean hasBuilding) {
		this.id = id;
		this.solid = solid;
		this.hasBuilding = hasBuilding;
	}
}