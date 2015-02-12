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
