package model.building;

import model.building.buildingEffect.LifeSupportBuildingEffect;
import model.building.buildingEffect.PumpBuildingEffect;
import model.map.FluidSimulator;



public class BuildingFactory {
	
	public static Building makeBuilding(BuildingType type, int x, int y) {
		
		// construction
		Building building = new Building(type, x, y);
		switch (type) {
		case PUMP:
			PumpBuildingEffect pbe = new PumpBuildingEffect(10*FluidSimulator.minFlow);
			building.addEffect(pbe);
			break;
		case COMMAND_TOWER:
		case LIFE_SUPPORT:
			LifeSupportBuildingEffect lsbe = new LifeSupportBuildingEffect(3);
			building.addEffect(lsbe);
			break;
		default:
			break;
		}
		
		return building;
	}
	
}
