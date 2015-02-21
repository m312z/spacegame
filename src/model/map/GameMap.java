package model.map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import model.building.Building;
import model.building.BuildingFactory;
import model.building.BuildingType;

public class GameMap {

	// buildings from loading
	public static List<Building> startBuildings = new ArrayList<Building>();
	
	int[] size;
	BuildingType[][] map;
	
	// fluids
	int[][] gooMass;
	int[][] flowH;
	int[][] flowV;
	
	// power
	int[][] lifeSupport;
		
	public GameMap(int x, int y) {
		size = new int[] {x,y};
		map = new BuildingType[size[0]][size[1]];
		gooMass = new int[size[0]][size[1]];
		flowH = new int[size[0]][size[1]];
		flowV = new int[size[0]][size[1]];
		lifeSupport = new int[size[0]][size[1]];
		readMap("images/map.bmp", map);
	}
	
	/**
	 * Clone constructor
	 * @param gameMap	the GameMap to clone
	 */
	public GameMap(GameMap gameMap) {
		size = new int[] {gameMap.size[0],gameMap.size[1]};
		map = new BuildingType[size[0]][size[1]];
		gooMass = new int[size[0]][size[1]];
		flowH = new int[size[0]][size[1]];
		flowV = new int[size[0]][size[1]];
		lifeSupport = new int[size[0]][size[1]];
		
		for(int i=0;i<size[0];i++) {
			System.arraycopy(gameMap.getMap()[i], 0, map[i], 0, size[1]);
			System.arraycopy(gameMap.getGooMass()[i], 0, gooMass[i], 0, size[1]);
			System.arraycopy(gameMap.getGooFlowH()[i], 0, flowH[i], 0, size[1]);
			System.arraycopy(gameMap.getGooFlowV()[i], 0, flowV[i], 0, size[1]);
			System.arraycopy(gameMap.getLifeSupport()[i], 0, lifeSupport[i], 0, size[1]);
		};
	}

	public GameMap clone() {
		return new GameMap(this);
	}
	
    public static void readMap(String fileName, BuildingType[][] map) {
    	
		try {
			
			File file = new File(fileName);
			BufferedImage image = ImageIO.read(file);
			
			for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				
				int clr = image.getRGB(x, y);
				int red = (clr & 0x00ff0000) >> 16;
			
				switch(red) {
				case 0:
					map[x][y] = BuildingType.WALL;
					break;
				case 255:
					map[x][y] = BuildingType.BASE_HABITAT;
					break;
				case 163:
					map[x][y] = BuildingType.COMMAND_TOWER;
					startBuildings.add(BuildingFactory.makeBuilding(BuildingType.COMMAND_TOWER, x, y));
					break;
				default:
					map[x][y] = BuildingType.EMPTY;
					break;
				}
			}};
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	
	/*---------------------*/
	/* setters and getters */
	/*---------------------*/
	
	public int[] getSize() {
		return size;
	}
	
	public BuildingType getTile(int x, int y) {
		return map[x][y];
	}
	
	public void setTile(int x, int y, BuildingType tile) {
		map[x][y] = tile;
	}
	
	public BuildingType[][] getMap() {
		return map;
	}
	
	public int[][] getGooMass() {
		return gooMass;
	}
	
	public int[][] getGooFlowH() {
		return flowH;
	}
	
	public int[][] getGooFlowV() {
		return flowV;
	}
	
	public int[][] getLifeSupport() {
		return lifeSupport;
	}
}
