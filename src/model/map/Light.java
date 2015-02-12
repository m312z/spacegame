package model.map;

import phys.Point2D;

public class Light {
	
	public float r,g,b;
	Point2D position;
	float distance;
	
	public Light(float r, float g, float b, Point2D position, float distance) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.position = position;
		this.distance = distance;
	}

	public Light clone() {
		return new Light(r,g,b,new Point2D(position.x,position.y),distance);
	}

	public Point2D getPos() {
		return position;
	}

	public float getDistance() {
		return distance;
	}
}
