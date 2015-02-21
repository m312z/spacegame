package model.gameobject;

import model.Board;
import model.building.BuildingType;
import model.building.RailBuilding;
import phys.DefaultShapes;
import phys.Point2D;
import phys.Shape;

public class Cart extends GameObject {

	public static final float BASE_CART_SPEED = 0.25f;
	
	protected Point2D destination;
	
	public Cart() {
		this(Shape.scale(DefaultShapes.basicSquare,Board.TILE_SIZE*4/5f));
		destination = new Point2D();
	}
	
	public Cart(Shape shape) {
		super(shape);
		speed = BASE_CART_SPEED;
	}
		
	public Cart clone() {
		Cart c = new Cart(shape.clone());
		writeAttributesToClone(c);
		return c;
	}
		
	public void writeAttributesToClone(Cart clone) {
		super.writeAttributesToClone(clone);
	}
				
	/*--------------------*/
	/* internal behaviour */
	/*--------------------*/
		
	/**
	 * Move the cart along rails
	 * @param board
	 * @param dt
	 * @return true if the cart is destroyed
	 */
	public boolean move(Board board, float dt) {
		
		// find current tile
		int x = (int) (position.x/Board.TILE_SIZE);
		int y = (int) (position.y/Board.TILE_SIZE);
		BuildingType t = board.getMap().getTile(x, y);
		
		// if not rail, return destroyed
		if(t!=BuildingType.RAIL) return true;
		
		// if at destination
		float d = Point2D.distance(destination, direction);
		if(d < speed*dt) {
			direction.x = destination.x;
			direction.y = destination.y;
			dt -= d/speed;
			
			// find rail exit direction
			Integer[] key = new Integer[] {x,y};
			RailBuilding b = (RailBuilding) board.getBuildings().get(key);
			key[0] = x + Board.ADJ_LIST[b.getExit()][0];
			key[1] = y + Board.ADJ_LIST[b.getExit()][1];
			
			// update goal position
			destination.x = (key[0] + 0.5f)*Board.TILE_SIZE;
			destination.y = (key[1] + 0.5f)*Board.TILE_SIZE;
		}
		
		// update direction
		direction.x = destination.x - position.x;
		direction.y = destination.y - position.y;
		Point2D.normalise(direction);
		
		// move cart
		position.x += dt*speed*direction.x;
		position.y += dt*speed*direction.y;
		
		return false;
	}
	
	/*---------------------*/
	/* setters and getters */
	/*---------------------*/
	
	public Point2D getGoalPosition() {
		return destination;
	}
}
