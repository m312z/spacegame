package model.map;

import static model.Board.GRID_SIZE;
import model.building.BuildingType;

public class FluidSimulator {

	// fluid properties
	public final static int maxMass = 1000;
	public final static int maxCompress = 280;
	public final static int minDraw = 1;
	public final static int maxDraw = 2000;

	// units of fluid moved out of one block to another, per tick
	public final static int maxFlow = 1000;
	public final static int minFlow  = 1;
	public final static int viscosity = 20;
	
	static int[][] new_mass = new int[GRID_SIZE[0]][GRID_SIZE[1]];
	static int tX = 0;
	static int tY = 0;

	// simulate all fluids
	public final static void simulateStep(GameMap map) {

		int[][] old_mass = map.getGooMass();
		
		// set up newMass
		for(int x = 0; x < GRID_SIZE[0]; x++) {
		for(int y = 0; y < GRID_SIZE[1]; y++) {
			map.getGooFlowH()[x][y] = 0;
			map.getGooFlowV()[x][y] = 0;
			if (map.getTile(x,y)==BuildingType.EMPTY
					|| (map.getTile(x,y).solid
		    		&& map.getTile(x,y)!=BuildingType.GRATING
		    		&& map.getTile(x,y)!=BuildingType.VARIABLE_GRATING))
				new_mass[x][y] = 0;
			else
				new_mass[x][y] = old_mass[x][y];
		}};

		int flow;
		int remaining_mass;
		boolean vargrating = false;
		
		// calculate and apply flow for each block
		for(int x = 0; x < GRID_SIZE[0]; x++) {
		for(int y = GRID_SIZE[1]-1; y >=0 ; y--) {
			
			// skip solid tiles
		    if (old_mass[x][y] == 0 ||
		    		(map.getTile(x,y).solid
		    		&& map.getTile(x,y)!=BuildingType.GRATING
		    		&& map.getTile(x,y)!=BuildingType.VARIABLE_GRATING))
		    	continue;
		       
		    // custom push-only flow
		    remaining_mass = old_mass[x][y];
		    flow = 0;

		    // if the current tile is a VARGRATING only minFlow is possible.
		    vargrating = (map.getTile(x,y) == BuildingType.VARIABLE_GRATING);
		    		    
		    // left and right
		    for(int i=-1;i<3;i+=2) {
		    	tX = x+i;
			    if( tX>=0 && tX<GRID_SIZE[0] ) {
			    	if(!map.getTile(tX,y).solid
				    		|| map.getTile(tX,y)==BuildingType.GRATING
				    		|| map.getTile(tX,y)==BuildingType.VARIABLE_GRATING) {
			    		// equalize the amount of water in this block and it's neighbour
				        flow = (remaining_mass - map.getGooMass()[tX][y])/viscosity;
				        if(flow>minFlow) {
				        	if(vargrating) {
				        		flow = minFlow*flow/Math.abs(flow);
				        	} else {
					        	flow = Math.max(flow,minFlow);
					        	flow = Math.min(flow, Math.min(maxFlow, remaining_mass));
				        	}
					        new_mass[x][y]  -= flow;
					        new_mass[tX][y] += flow;
					        remaining_mass  -= flow;
					        map.getGooFlowH()[x][y] = (int)-flow;
				        }
			    	}
			    }
		    	if ( remaining_mass <= 0 ) break;
		    }
		       
		    if ( remaining_mass <= 0 ) continue;
		    
		    // up and down
		    for(int i=-1;i<3;i+=2) {
		    	tY = y+i;
			    if ( tY>=0 && tY<GRID_SIZE[1] ) {
				    if(!map.getTile(x,tY).solid
				    		|| map.getTile(x,tY)==BuildingType.GRATING
				    		|| map.getTile(x,tY)==BuildingType.VARIABLE_GRATING) {
				    	// equalize the amount of water in this block and it's neighbour
				        flow = (remaining_mass - map.getGooMass()[x][tY])/viscosity;
				        if ( flow > minFlow ) {
				        	if(vargrating) {
				        		flow = minFlow*flow/Math.abs(flow);
				        	} else {
					        	flow = Math.max(flow,minFlow);
					        	flow = Math.min(flow, Math.min(maxFlow, remaining_mass));
				        	}
					        new_mass[x][y]  -= flow;
				        	new_mass[x][tY] += flow;
					        remaining_mass  -= flow;
					        map.getGooFlowV()[x][y] = (int)-flow;
				        }
				    }
			    }
		    	if ( remaining_mass <= 0 ) break;
		    }
		}};
		  
		
		// copy the new mass values to the mass array
		for(int x = 0; x < GRID_SIZE[0]; x++)
//		for(int y = 0; y < GRID_SIZE[1]; y++)
//			map.getGooMass()[x][y] = new_mass[x][y];
			System.arraycopy(new_mass[x], 0, map.getGooMass()[x], 0,new_mass[x].length);
	}
		 
	/**
	 * Take an amount of water and calculate how it should be split among two
	 * vertically adjacent cells. Returns
	 * @param total_mass
	 * @return the amount of water that should be in the bottom cell.
	 */
	public final static int get_stable_state_b ( int total_mass ) {
		if ( total_mass <= maxMass ) {
			return maxMass;
		} else if ( total_mass < 2*maxMass + maxCompress ) {
			return (maxMass*maxMass + total_mass*maxCompress)/(maxMass + maxCompress);
		} else {
			return (total_mass + maxCompress)/2;
		}
	}
	
}
