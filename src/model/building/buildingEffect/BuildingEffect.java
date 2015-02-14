package model.building.buildingEffect;

import model.Board;
import model.building.Building;

public abstract class BuildingEffect {

	public abstract BuildingEffect clone();
	public abstract void tick(Board board, Building b, float dt);
	public abstract void deActivate(Board board, Building b);
	public abstract void activate(Board board, Building b);	
}
