package model.map;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;



public class GameMap {
	
	int[] size;
	TileType[][] map;
	
	// fluids
	int[][] gooMass;
	int[][] flowH;
	int[][] flowV;
	
	// power
	int[][] power;
	
	public GameMap(int x, int y) {
		size = new int[] {x,y};
		map = new TileType[x][y];
		gooMass = new int[x][y];
		flowH = new int[x][y];
		flowV = new int[x][y];
		power = new int[x][y];
//		for(int i=0;i<x;i++)
//		for(int j=0;j<y;j++)
//			map[i][j] = TileType.EMPTY;
//		for(int i=20;i<30;i++)
//		for(int j=20;j<30;j++)
//			map[i][j] = TileType.GROUND;
//		for(int i=20;i<29;i++)
//			map[i][21] = TileType.WALL;
//		gooMass[24][20] = 10000;
		
		readMap("images/map.bmp", map);
	}
	
	/**
	 * Clone constructor
	 * @param gameMap	the GameMap to clone
	 */
	public GameMap(GameMap gameMap) {
		size = new int[] {gameMap.size[0],gameMap.size[1]};
		map = new TileType[size[0]][size[1]];
		gooMass = new int[size[0]][size[1]];
		flowH = new int[size[0]][size[1]];
		flowV = new int[size[0]][size[1]];
		power = new int[size[0]][size[1]];
		
		for(int i=0;i<size[0];i++) {
//		for(int j=0;j<size[1];j++) {
			System.arraycopy(gameMap.getMap()[i], 0, map[i], 0, size[1]);
			System.arraycopy(gameMap.getGooMass()[i], 0, gooMass[i], 0, size[1]);
			System.arraycopy(gameMap.getGooFlowH()[i], 0, flowH[i], 0, size[1]);
			System.arraycopy(gameMap.getGooFlowV()[i], 0, flowV[i], 0, size[1]);
			System.arraycopy(gameMap.getPower()[i], 0, power[i], 0, size[1]);
//			map[i][j] = gameMap.getTile(i, j);
//			gooMass[i][j] = gameMap.getGooMass()[i][j];
//			flowH[i][j] = gameMap.getGooFlowH()[i][j];
//			flowV[i][j] = gameMap.getGooFlowV()[i][j];
//		}
		};
	}

	public GameMap clone() {
		return new GameMap(this);
	}
	
    public static void readMap(String fileName, TileType[][] map) {
    	
		try {
			
			File file = new File(fileName);
			BufferedImage image = ImageIO.read(file);
			
			for (int y = 0; y < map.length; y++) {
			for (int x = 0; x < map[y].length; x++) {
				
				int clr = image.getRGB(x, y);
				int red = (clr & 0x00ff0000) >> 16;
			
				switch(red) {
				case 0:
					map[x][y] = TileType.WALL;
					break;
				case 255:
					map[x][y] = TileType.GROUND;
					break;
				default:
					map[x][y] = TileType.EMPTY;
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
	
	public TileType getTile(int x, int y) {
		return map[x][y];
	}
	
	public void setTile(int x, int y, TileType tile) {
		map[x][y] = tile;
	}
	
	public TileType[][] getMap() {
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
	
	public int[][] getPower() {
		return power;
	}
}
