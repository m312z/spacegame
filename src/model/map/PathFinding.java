package model.map;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TreeMap;

import model.Board;
import utils.IntegerArrayComparitor;

public class PathFinding {

	public static List<Integer[]> search(GameMap map, Integer[] source, Integer[] target) {

	   	List<Integer[]> path = new ArrayList<Integer[]>();
		
		Map<Integer[], AStarNode> nodeSet = new TreeMap<Integer[], AStarNode>(IntegerArrayComparitor.getInstance());
		Queue<AStarNode> openSet = new PriorityQueue<AStarNode>(1,AStarNodeComparitor.getInstance());

		AStarNode startNode = new AStarNode(source,null,0,getEstimate(source,target));
		openSet.add(startNode);

		while(!openSet.isEmpty()) {

			AStarNode current = openSet.poll();

			// check for goal
			if(current.h==0) {
				reconstructPath(path, current);
				break;
			}

			// add new nodes, re-add nodes with better G-values
			for(AStarNode node: getNeighbours(map,nodeSet,current,target))
				if(!openSet.contains(node)) openSet.add(node);
		}

		return path;
	}
	
	private static List<AStarNode> getNeighbours(GameMap map, Map<Integer[], AStarNode> nodeSet, AStarNode current, Integer[] target) {
		
		List<AStarNode> neighbours = new ArrayList<AStarNode>();
		
		Integer[] pos = new Integer[2];
		for(int i=0;i<4;i++) {
			pos[0] = current.position[0] + Board.ADJ_LIST[i][0];
			pos[1] = current.position[1] + Board.ADJ_LIST[i][1];
			
			if(nodeSet.containsKey(pos)) {
				AStarNode checkNode = nodeSet.get(pos);
				float d = getEstimate(current.position, pos);
				if(checkNode.g > current.g + d) {
					checkNode.g = current.g + d;
					neighbours.add(checkNode);
				}
			} else {
				AStarNode newNode = new AStarNode(
						new Integer[] {pos[0],pos[1]},
						current,
						current.g + getEstimate(current.position, pos),
						getEstimate(pos,target));
				nodeSet.put(newNode.position, newNode);
				neighbours.add(newNode);
			}
		}

		return neighbours;
	}

	private static float getEstimate(Integer[] source, Integer[] target) {
		return (float) Math.sqrt((source[0]-target[0])*(source[0]-target[0]) + (source[1]-target[1])*(source[1]-target[1]));
	}
	
	private static void reconstructPath(List<Integer[]> path, AStarNode current) {
	   	if(current.cameFrom!=null) 
	   		reconstructPath(path, current.cameFrom);
	   	path.add(current.position);
	}
	
	/*------------------*/
	/* Internal classes */
	/*------------------*/
		
	public static class AStarNode
	{
	    private AStarNode(Integer[] position, AStarNode cameFrom, double g, double h) {
			this.position = position;
			this.cameFrom = cameFrom;
			this.g = g;
			this.h = h;
		}
		public Integer[] position;
	    public AStarNode cameFrom;
	    public double g;
	    public double h;
	}
	
	public static class AStarNodeComparitor implements Comparator<AStarNode>
	{
		@Override
		public int compare(AStarNode o1, AStarNode o2) {
			if(o1.g+o1.h>o2.h+o2.h)
				return 1;
			if(o2.h+o2.h>o1.g+o1.h)
				return -1;
			return 0;
		}

		private static AStarNodeComparitor singleton;
		
		public static AStarNodeComparitor getInstance() {
			if(singleton==null)
				singleton = new AStarNodeComparitor(); 
			return singleton;

		}
	}
}
