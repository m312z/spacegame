package model.gameobject;

import phys.Point2D;
import phys.Shape;

public abstract class GameObject {

	protected Point2D position;
	protected Point2D direction;
	protected Shape shape;
	protected float speed;
	
	public GameObject(Shape shape) {
		direction = new Point2D();
		position = new Point2D();
		this.shape = shape;
	}
	
	public void writeAttributesToClone(GameObject clone) {
		
		// gameObject attributes
		clone.setShape(shape.clone());
		clone.getPosition().x = position.x;
		clone.getPosition().y = position.y;
		clone.getDirection().x = direction.x;
		clone.getDirection().y = direction.y;
		clone.setSpeed(speed);
		
	}
	
	public Point2D getDirection() {
		return direction;
	}
	
	public Point2D getPosition() {
		return position;
	}
		
	public Shape getShape() {
		return shape;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setSpeed(float speed) {
		this.speed = speed;
	}
	
	public void setShape(Shape shape) {
		this.shape = shape;
	}
}
