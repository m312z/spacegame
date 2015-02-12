package model.building;

import model.map.TileType;


public class BuildingFactory {
	
	public static Building makeBuilding(TileType type, int x, int y) {
		
		// construction
		Building building;
		switch (type) {
		case PUMP: building = new PumpBuilding(TileType.PUMP, x, y); break;
		default:	building = new Building(type, x, y);
		}
		
		// resource
		switch (type) {
		case WALL:
			break;
		}
		
		return building;
	}
	
}
