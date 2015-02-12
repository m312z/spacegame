package phys;

import utils.StringParser;

public class Point2D {

	public static final Point2D nullVector = new Point2D();
	
	public float x;
	public float y;
	
	public Point2D() {
		this(0,0);
	}
	
	public Point2D(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public static float magnitude(Point2D p) {
		return (float) Math.sqrt(p.x*p.x + p.y*p.y);
	}
	
	public static float distance(Point2D a, Point2D b) {
		return (float) Math.sqrt(
				(a.x - b.x)*(a.x - b.x)
				+ (a.y - b.y)*(a.y - b.y));
	}

	public void rotate(float angle) {
		float temp = x;
		x = (float) (x*Math.cos(Math.toRadians(angle)) - y*Math.sin(Math.toRadians(angle)));
		y = (float) (temp*Math.sin(Math.toRadians(angle)) + y*Math.cos(Math.toRadians(angle)));;
	}
	
	public float getDistance(Point2D p) {
		return (float)Math.sqrt((p.x - x) * (p.x - x) + (p.y - y) * (p.y - y));
	}

	public float getAngle() {
		return (float)Math.atan2(y, x) + (float)Math.PI / 2;
	}
	
	public static Point2D normalise(Point2D p) {
		float mag = magnitude(p);
		if(mag>0) {
			p.y = p.y/mag;
			p.x = p.x/mag;
		}
		return p;
	}
		
	public static Point2D invert(Point2D pos) {
		return new Point2D(-pos.x,-pos.y);
	}
	
	public static float angleBetween(Point2D a, Point2D b) {
		// using dot product
		float cosa = (a.y*b.y + a.x*b.x)/(Point2D.magnitude(a)*Point2D.magnitude(b));
		return (float) Math.toDegrees(Math.acos(cosa));
	}
	
	public String write() {
		return "{"+x+"}{"+y+"}";
	}
	
	public static Point2D read(String line) {
		Point2D p = new Point2D();
		p.x = Float.parseFloat(StringParser.getBracket(line));
		line = StringParser.removeBracket(line);
		p.y = Float.parseFloat(StringParser.getBracket(line));
		return p;
	}
	
	@Override
	public String toString() {
		return "{"+x+" "+y+"}";
	}
}
