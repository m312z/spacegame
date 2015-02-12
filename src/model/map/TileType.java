package model.map;


public enum TileType {
	
	EMPTY(			0,  false,	false),
	GROUND(			1,  false,	false),
	
	WALL(			2,  true,	false),
	REINFORCEDWALL(	3,  true,	false),
	
	PUMP(			4,  true,	true),
	GRATING(		5,  true,	false),
	VARGRATING(		6,  true,	false),
	
	LIFESUPPORT(	7, 	true, 	true);
	
	public int id;
	public boolean solid;
	public boolean hasTick;
	
	private TileType(int id, boolean solid, boolean hasTick) {
		this.id = id;
		this.solid = solid;
		this.hasTick = hasTick;
	}
}