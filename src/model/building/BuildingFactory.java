package model.building;



public class BuildingFactory {
	
	public static Building makeBuilding(BuildingType type, int x, int y) {
		
		// construction
		Building building;
		switch (type) {
		case PUMP:
			building = new PumpBuilding(BuildingType.PUMP, x, y);
			break;
		case LIFE_SUPPORT:
			building = new LifeSupportBuilding(BuildingType.LIFE_SUPPORT, x, y, 10);
			break;
		case COMMAND_TOWER:
			building = new LifeSupportBuilding(BuildingType.COMMAND_TOWER, x, y, 0);
			break;
		default:
			building = new LifeSupportBuilding(BuildingType.LIFE_SUPPORT, x, y, 10);
			break;
		}
		
		return building;
	}
	
}
